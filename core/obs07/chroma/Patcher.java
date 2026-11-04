package obs07.chroma;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

// Finds supported shaderpack zips and writes a patched copy next to them
public final class Patcher {

	public static final Charset RAW = Charset.forName("ISO-8859-1");
	static final String MARKER = "shaders/chroma_patch.properties";
	// limits for zips we did not make: one text file, and everything copied into a patched pack
	static final int MAX_TEXT = 16 << 20;
	static final long MAX_PACK = 1L << 30;

	// highest .chroma format feature this build understands
	public static final int ENGINE = 1;

	static final class Profile {
		final String id;
		final Properties info;
		final PatchData data;

		Profile(PatchData data) {
			this.data = data;
			this.id = data.id();
			this.info = data.info;
		}

		String version() {
			return data.version();
		}

		String output() {
			return info.getProperty("output");
		}

		boolean matches(ZipFile zip, String root) throws IOException {
			String text = read(zip, root + info.getProperty("detect.file"));
			if (text == null || !text.contains(info.getProperty("detect.contains"))) return false;
			String excludes = info.getProperty("detect.excludes", "");
			if (excludes.isEmpty()) return true;
			// already patched packs are left alone
			for (String file : info.getProperty("detect.excludes.in", info.getProperty("detect.file")).split(",")) {
				String other = read(zip, root + file.trim());
				if (other != null && other.contains(excludes)) return false;
			}
			return true;
		}
	}

	final List<Profile> profiles = new ArrayList<Profile>();

	Patcher(File gameDir) throws IOException {
		// patch data ships inside the jar, new patches come with new jars
		for (PatchData d : bundled()) if (d.engine() <= ENGINE) profiles.add(new Profile(d));
	}

	public static List<PatchData> bundled() throws IOException {
		Properties list = new Properties();
		list.load(resource("chromapatch/profiles.properties"));
		List<PatchData> out = new ArrayList<PatchData>();
		for (String id : list.getProperty("profiles").split(",")) {
			byte[] bytes = readAll(resource("chromapatch/" + id.trim() + ".chroma"));
			PatchData d = PatchData.parse(new String(bytes, RAW));
			try {
				d.sha1 = sha1(bytes);
			} catch (Exception e) {
				throw new IOException(e.toString());
			}
			if (!d.id().equals(id.trim())) throw new IOException("profile id mismatch " + id);
			out.add(d);
		}
		return out;
	}

	// versions in use, read again on each call
	public static Map<String, String> currentVersions(File gameDir) {
		Map<String, String> out = new LinkedHashMap<String, String>();
		for (PatchData d : active(gameDir)) out.put(d.id(), d.version());
		return out;
	}

	// patch data in use
	public static List<PatchData> active(File gameDir) {
		List<PatchData> out = new ArrayList<PatchData>();
		try {
			for (Profile p : new Patcher(gameDir).profiles) out.add(p.data);
		} catch (Exception e) {
			log("could not read patch data: " + e);
		}
		return out;
	}

	public static int compare(String a, String b) {
		String[] x = a.split("\\.");
		String[] y = b.split("\\.");
		for (int i = 0; i < Math.max(x.length, y.length); i++) {
			int u = i < x.length ? Integer.parseInt(x[i]) : 0;
			int v = i < y.length ? Integer.parseInt(y[i]) : 0;
			if (u != v) return u < v ? -1 : 1;
		}
		return 0;
	}

	// result of the last run, one line per profile, for the welcome screen
	static volatile List<String> report = new ArrayList<String>();
	List<String> lines = new ArrayList<String>();

	public static List<String> report() {
		return report;
	}

	// Entry point used by every loader
	public static synchronized void run(File gameDir) {
		File packs = new File(gameDir, "shaderpacks");
		if (!packs.isDirectory()) return;
		Patcher patcher = null;
		try {
			patcher = new Patcher(gameDir);
			File[] zips = packs.listFiles();
			if (zips == null) return;
			// newest supported zip per profile
			Map<Profile, File> sources = new LinkedHashMap<Profile, File>();
			for (File zip : zips) {
				String name = zip.getName();
				if (!zip.isFile() || !name.toLowerCase().endsWith(".zip") || name.startsWith(".") || patcher.isOutput(name)) continue;
				try {
					Profile profile = patcher.detect(zip);
					if (profile == null) continue;
					File previous = sources.get(profile);
					if (previous == null || zip.lastModified() > previous.lastModified()) sources.put(profile, zip);
				} catch (Exception e) {
					log("could not read " + name + ": " + e);
				}
			}
			for (Map.Entry<Profile, File> e : sources.entrySet()) {
				try {
					patcher.patch(e.getValue(), e.getKey());
				} catch (Exception ex) {
					log("could not patch " + e.getValue().getName() + ": " + ex);
					patcher.fail(e.getKey(), e.getValue().getName() + " could not be read.");
				}
			}
			for (Profile p : patcher.profiles) {
				if (sources.containsKey(p)) continue;
				String name = p.info.getProperty("name");
				if (new File(packs, p.output() + ".zip").isFile()) patcher.ok(p);
				else patcher.lines.add("No " + name + " zip found in shaderpacks. Download " + name + " from its official page and restart the game.");
			}
			report = patcher.lines;
		} catch (Exception e) {
			log("disabled: " + e);
			List<String> r = new ArrayList<String>();
			r.add("\u00a7cChroma Patch could not start: " + e.getMessage() + "\u00a7r");
			report = r;
		}
	}

	void ok(Profile p) {
		lines.add("\u00a7a" + p.info.getProperty("name") + " patch " + p.version() + " applied.\u00a7r\nSelect in the shader menu:\n\"" + p.output() + "\"");
	}

	void fail(Profile p, String why) {
		lines.add("\u00a7c" + p.info.getProperty("name") + " patch " + p.version() + " not applied.\u00a7r " + why);
	}

	boolean isOutput(String name) {
		for (Profile p : profiles) if (name.equals(p.output() + ".zip")) return true;
		return name.contains(" [Chroma Patch]");
	}

	Profile detect(File source) throws IOException {
		ZipFile zip = new ZipFile(source);
		try {
			String root = findRoot(zip);
			if (root == null || zip.getEntry(root + MARKER) != null) return null;
			for (Profile p : profiles) if (p.matches(zip, root)) return p;
			return null;
		} finally {
			zip.close();
		}
	}

	void patch(File source, Profile profile) throws Exception {
		ZipFile zip = new ZipFile(source);
		try {
			String root = findRoot(zip);
			File target = new File(source.getParentFile(), profile.output() + ".zip");
			String sourceSha1 = sha1(source);
			if (upToDate(target, profile, sourceSha1)) {
				ok(profile);
				return;
			}

			Map<String, byte[]> changed = new LinkedHashMap<String, byte[]>();
			List<String> failed = new ArrayList<String>();
			List<String> merged = new ArrayList<String>();
			int exact = 0;
			int modified = 0;
			int fuzzy = 0;

			for (PatchData.FileChange file : profile.data.files) {
				if (file.created) {
					changed.put(file.path, Applier.create(file).getBytes(RAW));
					continue;
				}
				modified++;
				ZipEntry entry = zip.getEntry(root + file.path);
				if (entry == null) {
					failed.add(file.path + " (missing)");
					continue;
				}
				byte[] bytes = readEntry(zip, entry, MAX_TEXT);
				if (sha1(bytes).equals(file.baseSha1)) exact++;
				String original = new String(bytes, RAW);

				Applier.Result result = Applier.apply(file, original);
				String text = result.text;
				if (!result.failed.isEmpty() && !file.props.isEmpty()) {
					text = PropsMerge.apply(original, file.props);
					merged.add(file.path);
				} else {
					failed.addAll(result.failed);
				}
				fuzzy += result.fuzzy;
				changed.put(file.path, text.getBytes(RAW));
			}

			String summary = profile.info.getProperty("name") + " patch " + profile.version() + " on " + source.getName()
				+ " (" + exact + "/" + modified + " files match " + profile.info.getProperty("base") + ", " + fuzzy + " fuzzy hunks"
				+ (merged.isEmpty() ? "" : ", merged by key: " + merged) + ")";
			if (!failed.isEmpty()) {
				List<String> report = new ArrayList<String>();
				report.add(summary);
				report.add("failed hunks:");
				for (String f : failed) report.add("  " + f);
				report.add("no pack written: this version is not supported yet");
				writeLog(target, report);
				log("could not patch " + source.getName() + ", see " + logFile(target).getName());
				fail(profile, "This " + profile.info.getProperty("name") + " version is not supported yet, see " + logFile(target).getName() + " in shaderpacks.");
				return;
			}

			File temp = new File(target.getParentFile(), "." + target.getName() + ".tmp");
			try {
				writePack(zip, root, changed, temp, profile, sourceSha1, source.getName());
			} catch (Exception e) {
				temp.delete();
				throw e;
			}
			if (target.exists() && !target.delete()) {
				temp.delete();
				log("could not replace " + target.getName() + " (in use?), close the game and retry");
				fail(profile, "The previous pack is in use, restart the game.");
				return;
			}
			if (!temp.renameTo(target)) throw new IOException("rename failed");
			logFile(target).delete();
			log("wrote " + target.getName() + ": " + summary);
			ok(profile);
		} finally {
			zip.close();
		}
	}

	void writePack(ZipFile zip, String root, Map<String, byte[]> changed, File out, Profile profile, String sourceSha1, String sourceName) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(out));
		try {
			long budget = MAX_PACK;
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();
				if (!name.startsWith(root) || name.length() == root.length()) continue;
				String path = name.substring(root.length());
				if (changed.containsKey(path) || path.equals(MARKER)) continue;
				zos.putNextEntry(new ZipEntry(path));
				if (!entry.isDirectory()) budget -= copyLimited(zip.getInputStream(entry), zos, budget);
				zos.closeEntry();
			}
			for (Map.Entry<String, byte[]> e : changed.entrySet()) {
				zos.putNextEntry(new ZipEntry(e.getKey()));
				zos.write(e.getValue());
				zos.closeEntry();
			}
			Properties marker = new Properties();
			marker.setProperty("profile", profile.id);
			marker.setProperty("patch", profile.version());
			marker.setProperty("patchSha1", profile.data.sha1);
			marker.setProperty("source", sourceName);
			marker.setProperty("sourceSha1", sourceSha1);
			zos.putNextEntry(new ZipEntry(MARKER));
			marker.store(zos, "Chroma Patch");
			zos.closeEntry();
		} finally {
			zos.close();
		}
	}

	boolean upToDate(File target, Profile profile, String sourceSha1) {
		if (!target.isFile()) return false;
		try {
			ZipFile zip = new ZipFile(target);
			try {
				ZipEntry entry = zip.getEntry(MARKER);
				if (entry == null) return false;
				Properties marker = new Properties();
				marker.load(new java.io.ByteArrayInputStream(readEntry(zip, entry, 65536)));
				return profile.id.equals(marker.getProperty("profile"))
					&& profile.version().equals(marker.getProperty("patch"))
					&& profile.data.sha1.equals(marker.getProperty("patchSha1", ""))
					&& sourceSha1.equals(marker.getProperty("sourceSha1"));
			} finally {
				zip.close();
			}
		} catch (IOException e) {
			return false;
		}
	}

	// "" for packs zipped from the shaders folder, "Folder/" for GitHub style zips
	static String findRoot(ZipFile zip) {
		String best = null;
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			String name = entries.nextElement().getName();
			if (name.endsWith("shaders/shaders.properties")) {
				String root = name.substring(0, name.length() - "shaders/shaders.properties".length());
				if (best == null || root.length() < best.length()) best = root;
			}
		}
		return best;
	}

	static String read(ZipFile zip, String path) throws IOException {
		ZipEntry entry = zip.getEntry(path);
		return entry == null ? null : new String(readEntry(zip, entry, MAX_TEXT), RAW);
	}

	// stops reading past max, whatever size the zip claims
	static byte[] readEntry(ZipFile zip, ZipEntry entry, int max) throws IOException {
		if (entry.getSize() > max) throw new IOException(entry.getName() + " is too large");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyLimited(zip.getInputStream(entry), out, max);
		return out.toByteArray();
	}

	static long copyLimited(InputStream in, OutputStream out, long max) throws IOException {
		try {
			byte[] buf = new byte[65536];
			long total = 0;
			int n;
			while ((n = in.read(buf)) > 0) {
				total += n;
				if (total > max) throw new IOException("zip content too large");
				out.write(buf, 0, n);
			}
			return total;
		} finally {
			in.close();
		}
	}

	static File logFile(File target) {
		String n = target.getName();
		return new File(target.getParentFile(), n.substring(0, n.length() - 4) + ".log");
	}

	static void writeLog(File target, List<String> lines) {
		try {
			PrintWriter w = new PrintWriter(logFile(target), "UTF-8");
			for (String l : lines) w.println(l);
			w.close();
		} catch (IOException ignored) {
		}
	}

	public static void log(String message) {
		System.out.println("[Chroma Patch] " + message);
	}

	public static InputStream resource(String path) throws IOException {
		InputStream in = Patcher.class.getClassLoader().getResourceAsStream(path);
		if (in == null) throw new IOException("missing " + path);
		return in;
	}

	public static byte[] readAll(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		in.close();
		return out.toByteArray();
	}

	static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[65536];
		int n;
		while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
	}

	static String sha1(byte[] bytes) throws Exception {
		return hex(MessageDigest.getInstance("SHA-1").digest(bytes));
	}

	static String sha1(File file) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		FileInputStream in = new FileInputStream(file);
		try {
			byte[] buf = new byte[65536];
			int n;
			while ((n = in.read(buf)) > 0) md.update(buf, 0, n);
		} finally {
			in.close();
		}
		return hex(md.digest());
	}

	public static String hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) sb.append(String.format("%02x", b));
		return sb.toString();
	}

	// offline use: java -cp chromapatch.jar obs07.chroma.Patcher <game dir or shaderpacks dir>
	public static void main(String[] args) {
		File dir = new File(args.length > 0 ? args[0] : ".");
		if (dir.getName().equals("shaderpacks")) dir = dir.getParentFile();
		run(dir.getAbsoluteFile());
	}
}
