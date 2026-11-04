package obs07.chroma;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// Parsed .chroma file: profile info, new files, hashed hunks and key operations
public final class PatchData {

	static final class Hunk {
		final String[] oldExact;
		final String[] oldLoose;
		// >= 0 copies an old line, otherwise text from added
		final int[] newCopy;
		final String[] added;
		int lead;
		int trail;

		Hunk(int oldCount, int newCount) {
			oldExact = new String[oldCount];
			oldLoose = new String[oldCount];
			newCopy = new int[newCount];
			added = new String[newCount];
		}
	}

	static final class FileChange {
		final String path;
		final boolean created;
		final List<String> content = new ArrayList<String>();
		final List<Hunk> hunks = new ArrayList<Hunk>();
		final List<String> props = new ArrayList<String>();
		// null when the final newline is left alone
		Boolean endsWithNewline;
		String baseSha1;

		FileChange(String path, boolean created) {
			this.path = path;
			this.created = created;
		}
	}

	final Properties info = new Properties();
	// SHA-1 of the .chroma file, so a changed patch rebuilds the pack even with the same version
	String sha1 = "";
	final List<FileChange> files = new ArrayList<FileChange>();

	public String id() {
		return info.getProperty("id");
	}

	public String version() {
		return info.getProperty("version");
	}

	public String name() {
		return info.getProperty("name", id());
	}

	public String changelog() {
		return info.getProperty("changelog", "");
	}

	public int engine() {
		return Integer.parseInt(info.getProperty("engine"));
	}

	// any malformed input ends up as IllegalArgumentException
	public static PatchData parse(String text) {
		try {
			PatchData data = parseRaw(text);
			data.check();
			return data;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException("broken patch data: " + e);
		}
	}

	static PatchData parseRaw(String text) throws Exception {
		String[] lines = text.split("\n", -1);
		if (!lines[0].trim().equals("CHROMA 1")) throw new IllegalArgumentException("unknown patch format");
		PatchData data = new PatchData();
		int max = lines.length;
		int i = 1;
		while (i < lines.length) {
			String[] head = lines[i++].split(" ");
			if (head[0].equals("@info")) {
				int count = count(head[1], max);
				StringBuilder sb = new StringBuilder();
				for (int k = 0; k < count; k++) sb.append(lines[i++]).append('\n');
				data.info.load(new StringReader(sb.toString()));
			} else if (head[0].equals("@new")) {
				FileChange f = new FileChange(safePath(head[1]), true);
				int count = count(head[2], max);
				for (int k = 0; k < count; k++) f.content.add(lines[i++].substring(1));
				f.endsWithNewline = Boolean.valueOf(head[3].equals("1"));
				data.files.add(f);
			} else if (head[0].equals("@mod")) {
				FileChange f = new FileChange(safePath(head[1]), false);
				int hunks = count(head[2], max);
				if (!head[3].equals("-")) f.endsWithNewline = Boolean.valueOf(head[3].equals("1"));
				f.baseSha1 = head[4];
				for (int k = 0; k < hunks; k++) {
					String[] hh = lines[i++].split(" ");
					Hunk hunk = new Hunk(count(hh[1], max), count(hh[2], max));
					for (int o = 0; o < hunk.oldExact.length; o++) {
						String[] parts = lines[i++].split(" ");
						hunk.oldExact[o] = parts[1];
						hunk.oldLoose[o] = parts[2];
					}
					for (int n = 0; n < hunk.newCopy.length; n++) {
						String l = lines[i++];
						if (l.charAt(0) == '=') {
							hunk.newCopy[n] = count(l.substring(1), max);
						} else {
							hunk.newCopy[n] = -1;
							hunk.added[n] = l.substring(1);
						}
					}
					// leading/trailing context, trimmed when fuzzing
					int lead = 0;
					while (lead < hunk.newCopy.length && hunk.newCopy[lead] == lead) lead++;
					int trail = 0;
					while (trail < hunk.newCopy.length - lead
						&& hunk.newCopy[hunk.newCopy.length - 1 - trail] == hunk.oldExact.length - 1 - trail) trail++;
					hunk.lead = Math.min(lead, hunk.oldExact.length);
					hunk.trail = trail;
					f.hunks.add(hunk);
				}
				data.files.add(f);
			} else if (head[0].equals("@props")) {
				FileChange f = data.find(head[1]);
				int count = count(head[2], max);
				for (int k = 0; k < count; k++) f.props.add(lines[i++]);
			} else if (head[0].isEmpty()) {
				continue;
			} else {
				throw new IllegalArgumentException("bad patch line " + head[0]);
			}
		}
		return data;
	}

	// counts never exceed the number of lines, so bad data cannot ask for huge arrays
	static int count(String s, int max) {
		int n = Integer.parseInt(s);
		if (n < 0 || n > max) throw new IllegalArgumentException("bad count " + s);
		return n;
	}

	FileChange find(String path) {
		for (FileChange f : files) if (f.path.equals(path)) return f;
		throw new IllegalArgumentException("no file " + path);
	}

	void check() {
		String id = id();
		if (id == null || !id.matches("[a-z0-9_]{1,32}")) throw new IllegalArgumentException("bad profile id " + id);
		if (version() == null || !version().matches("[0-9]{1,4}(\\.[0-9]{1,4}){0,3}")) throw new IllegalArgumentException("bad version " + version());
		engine();
		String output = info.getProperty("output", "");
		// the output is a file name inside shaderpacks, nothing else
		if (!output.matches("[A-Za-z0-9 _+()\\[\\]-]{1,100}") || output.trim().isEmpty() || !output.equals(output.trim())
			|| output.toUpperCase().matches("(CON|PRN|AUX|NUL|COM[0-9]|LPT[0-9])( .*)?"))
			throw new IllegalArgumentException("bad output name " + output);
		safePath(info.getProperty("detect.file", ""));
		for (String f : info.getProperty("detect.excludes.in", "shaders/x").split(",")) safePath(f.trim());
		if (info.getProperty("detect.contains", "").isEmpty()) throw new IllegalArgumentException("no detect.contains");
		for (FileChange f : files) {
			if (!f.created && (f.baseSha1 == null || !f.baseSha1.matches("[0-9a-f]{40}"))) throw new IllegalArgumentException("bad sha1 for " + f.path);
			for (Hunk h : f.hunks) {
				for (int n : h.newCopy) if (n >= h.oldExact.length) throw new IllegalArgumentException("bad hunk in " + f.path);
			}
		}
	}

	// patches may only touch files inside shaders/
	static String safePath(String path) {
		if (!path.matches("shaders/[A-Za-z0-9_./-]{1,200}") || path.contains("..") || path.contains("//"))
			throw new IllegalArgumentException("refused path " + path);
		return path;
	}
}
