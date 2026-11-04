package obs07.chroma.update;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import obs07.chroma.PatchData;
import obs07.chroma.Patcher;

// New version notices: only with consent, signed by the release key, nothing is ever downloaded but the notice
public final class Updates {

	public static final String MOD_VERSION = "@VERSION@";

	static final String VERSION = "[0-9]{1,4}(\\.[0-9]{1,4}){0,3}";
	static final int MAX_MANIFEST = 64 * 1024;
	static final int MAX_SIG = 512;

	// a newer mod jar exists: the player downloads it from the official pages
	public static final class Offer {
		public final String version;
		public final String current;
		public final String changelog;

		Offer(String version, String current, String changelog) {
			this.version = version;
			this.current = current;
			this.changelog = changelog.replace("\r", "");
		}
	}

	static volatile Updates instance;

	final File gameDir;
	final File configFile;
	final File cacheDir;
	final Net.Fetcher net;
	String mode = "ask";
	final Set<String> declined = new LinkedHashSet<String>();
	// versions whose changelog was already shown, "mod:1.0.0" or "<profile>:<version>"
	final Set<String> seen = new LinkedHashSet<String>();
	final List<Offer> offers = Collections.synchronizedList(new ArrayList<Offer>());

	Updates(File gameDir, Net.Fetcher net) {
		this.gameDir = gameDir;
		this.configFile = new File(gameDir, "config/chromapatch.properties");
		this.cacheDir = new File(gameDir, "config/chromapatch");
		this.net = net;
		loadSettings();
	}

	// called once by the loaders after the first patch run, client only
	public static void start(File gameDir) {
		if (!new File(gameDir, "shaderpacks").isDirectory()) return;
		Updates u = new Updates(gameDir, Net.GITHUB);
		instance = u;
		if (u.mode.equals("on")) u.checkLater();
	}

	public static Updates get() {
		return instance;
	}

	// ---- for the in-game screens

	public boolean needsConsent() {
		return Trust.enabled() && mode.equals("ask");
	}

	public void setConsent(boolean allowed) {
		mode = allowed ? "on" : "off";
		saveSettings();
		if (allowed) checkLater();
	}

	// next offer to show, each one is shown once per launch
	public Offer nextOffer() {
		synchronized (offers) {
			return offers.isEmpty() ? null : offers.remove(0);
		}
	}

	public void decline(Offer offer) {
		declined.add("mod:" + offer.version);
		saveSettings();
	}

	// changelogs of versions not shown yet: the mod first, then each patch in use
	public List<String[]> whatsNew() {
		List<String[]> out = new ArrayList<String[]>();
		if (MOD_VERSION.matches(VERSION) && !seen.contains("mod:" + MOD_VERSION)) {
			out.add(new String[] {"mod:" + MOD_VERSION, "Chroma Patch " + MOD_VERSION, modChangelog(MOD_VERSION)});
		}
		for (PatchData d : Patcher.active(gameDir)) {
			String key = d.id() + ":" + d.version();
			if (!seen.contains(key)) out.add(new String[] {key, d.name() + " patch " + d.version(), d.changelog()});
		}
		return out;
	}

	public void markSeen(String key) {
		if (seen.add(key)) saveSettings();
	}

	// the "## Chroma Patch <version>" section of the bundled CHANGELOG.md
	static String modChangelog(String version) {
		try {
			String text = new String(Patcher.readAll(Patcher.resource("chromapatch/CHANGELOG.md")), "UTF-8");
			StringBuilder sb = new StringBuilder();
			boolean inside = false;
			for (String line : text.split("\n")) {
				if (line.startsWith("## ")) {
					inside = line.substring(3).trim().equals("Chroma Patch " + version);
					continue;
				}
				if (inside && !line.trim().isEmpty()) sb.append(sb.length() == 0 ? "" : "\n").append(line.trim());
			}
			return sb.toString();
		} catch (IOException e) {
			return "";
		}
	}

	// ---- checking

	void checkLater() {
		if (!Trust.enabled()) return;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					check();
				} catch (Throwable e) {
					Patcher.log("update check failed: " + e);
				}
			}
		}, "Chroma Patch update check");
		t.setDaemon(true);
		t.start();
	}

	void check() throws Exception {
		byte[] m = net.get("manifest.properties", MAX_MANIFEST);
		byte[] s = net.get("manifest.sig", MAX_SIG);
		Properties props = verifyManifest(m, s);
		if (props == null) throw new IOException("manifest signature is not valid");
		long serial = Long.parseLong(props.getProperty("serial"));
		// an older signed manifest must not bring back older data
		if (serial < lastSerial()) throw new IOException("manifest is older than one already seen");
		saveSerial(serial);

		String mod = props.getProperty("mod.version", "");
		if (mod.matches(VERSION) && MOD_VERSION.matches(VERSION) && Patcher.compare(mod, MOD_VERSION) > 0 && !declined.contains("mod:" + mod)) {
			offers.add(new Offer(mod, MOD_VERSION, props.getProperty("mod.changelog", "")));
			Patcher.log("Chroma Patch " + mod + " is available (installed " + MOD_VERSION + ")");
		}

	}

	static Properties verifyManifest(byte[] m, byte[] s) throws IOException {
		if (m == null || s == null || !Trust.verify(m, s)) return null;
		Properties props = new Properties();
		props.load(new ByteArrayInputStream(m));
		if (!"1".equals(props.getProperty("format"))) throw new IOException("unknown manifest format");
		return props;
	}

	// ---- files

	void loadSettings() {
		Properties p = new Properties();
		if (configFile.isFile()) {
			try {
				FileInputStream in = new FileInputStream(configFile);
				try {
					p.load(in);
				} finally {
					in.close();
				}
			} catch (IOException ignored) {
			}
		}
		String m = p.getProperty("updates", "ask").trim();
		mode = m.equals("on") || m.equals("off") ? m : "ask";
		for (String d : p.getProperty("declined", "").split(",")) if (!d.trim().isEmpty()) declined.add(d.trim());
		for (String d : p.getProperty("seen", "").split(",")) if (!d.trim().isEmpty()) seen.add(d.trim());
	}

	void saveSettings() {
		StringBuilder sb = new StringBuilder();
		sb.append("# Chroma Patch\n");
		sb.append("# updates: ask = ask in game, on = check for a new Chroma Patch version at startup (notice only), off = no network access\n");
		sb.append("updates=").append(mode).append('\n');
		sb.append("# skipped versions\n");
		sb.append("declined=");
		boolean first = true;
		for (String d : declined) {
			if (!first) sb.append(',');
			sb.append(d);
			first = false;
		}
		sb.append('\n');
		sb.append("# changelogs already shown\n");
		sb.append("seen=");
		first = true;
		for (String d : seen) {
			if (!first) sb.append(',');
			sb.append(d);
			first = false;
		}
		sb.append('\n');
		try {
			configFile.getParentFile().mkdirs();
			write(configFile, sb.toString().getBytes("ISO-8859-1"));
		} catch (IOException e) {
			Patcher.log("could not save settings: " + e);
		}
	}

	long lastSerial() {
		try {
			Properties p = new Properties();
			p.load(new ByteArrayInputStream(read(new File(cacheDir, "state.properties"), 4096)));
			return Long.parseLong(p.getProperty("serial", "0"));
		} catch (Exception e) {
			return 0;
		}
	}

	void saveSerial(long serial) throws IOException {
		if (serial <= lastSerial()) return;
		if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) throw new IOException("cannot create " + cacheDir);
		write(new File(cacheDir, "state.properties"), ("serial=" + serial + "\n").getBytes("ISO-8859-1"));
	}

	static byte[] read(File f, int max) throws IOException {
		if (!f.isFile()) throw new IOException("missing " + f.getName());
		if (f.length() > max) throw new IOException("too large " + f.getName());
		return Patcher.readAll(new FileInputStream(f));
	}

	// temp file + rename, never a half written file
	static void write(File target, byte[] bytes) throws IOException {
		File temp = new File(target.getParentFile(), "." + target.getName() + ".tmp");
		FileOutputStream out = new FileOutputStream(temp);
		try {
			out.write(bytes);
			out.getFD().sync();
		} finally {
			out.close();
		}
		if (target.exists() && !target.delete()) {
			temp.delete();
			throw new IOException("cannot replace " + target.getName());
		}
		if (!temp.renameTo(target)) throw new IOException("cannot write " + target.getName());
	}
}
