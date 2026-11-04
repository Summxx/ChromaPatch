package obs07.chroma;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Finds each hunk by the hashes of its original lines, like patch with a small fuzz
final class Applier {

	static final int MAX_FUZZ = 2;

	static final class Result {
		String text;
		int fuzzy;
		final List<String> failed = new ArrayList<String>();
	}

	static Result apply(PatchData.FileChange file, String original) throws Exception {
		Result result = new Result();
		boolean crlf = original.contains("\r\n");
		boolean endsWithNewline = original.isEmpty() || original.endsWith("\n");
		List<String> lines = split(original);

		int cursor = 0;
		int index = 0;
		for (PatchData.Hunk hunk : file.hunks) {
			index++;
			List<String> exact = hashes(lines, false);
			List<String> loose = hashes(lines, true);
			boolean done = false;
			for (int fuzz = 0; fuzz <= MAX_FUZZ && !done; fuzz++) {
				int cutLead = Math.min(fuzz, hunk.lead);
				int cutTrail = Math.min(fuzz, hunk.trail);
				if (fuzz > 0 && cutLead == 0 && cutTrail == 0) continue;
				int from = cutLead;
				int to = hunk.oldExact.length - cutTrail;

				int at = find(exact, hunk.oldExact, from, to, cursor);
				if (at < 0) at = find(exact, hunk.oldExact, from, to, 0);
				if (at < 0) at = find(loose, hunk.oldLoose, from, to, 0);
				if (at < 0) continue;

				List<String> replaced = new ArrayList<String>();
				for (int n = 0; n < hunk.newCopy.length; n++) {
					int copy = hunk.newCopy[n];
					if (copy < 0) replaced.add(hunk.added[n]);
					else if (copy >= from && copy < to) replaced.add(lines.get(at + copy - from));
				}
				for (int k = from; k < to; k++) lines.remove(at);
				lines.addAll(at, replaced);
				cursor = at + replaced.size();
				if (fuzz > 0) result.fuzzy++;
				done = true;
			}
			if (!done) result.failed.add(file.path + " #" + index);
		}

		if (file.endsWithNewline != null) endsWithNewline = file.endsWithNewline.booleanValue();
		result.text = join(lines, crlf, endsWithNewline);
		return result;
	}

	static String create(PatchData.FileChange file) {
		return join(file.content, false, file.endsWithNewline == null || file.endsWithNewline.booleanValue());
	}

	private static int find(List<String> fileHashes, String[] block, int from, int to, int start) {
		int size = to - from;
		if (size <= 0) return Math.min(start, fileHashes.size());
		outer:
		for (int i = start; i + size <= fileHashes.size(); i++) {
			for (int j = 0; j < size; j++) if (!fileHashes.get(i + j).equals(block[from + j])) continue outer;
			return i;
		}
		return -1;
	}

	static List<String> split(String text) {
		List<String> lines = new ArrayList<String>(Arrays.asList(text.replace("\r\n", "\n").split("\n", -1)));
		if (text.isEmpty() || text.endsWith("\n")) lines.remove(lines.size() - 1);
		return lines;
	}

	static String join(List<String> lines, boolean crlf, boolean endsWithNewline) {
		StringBuilder sb = new StringBuilder();
		String eol = crlf ? "\r\n" : "\n";
		for (int i = 0; i < lines.size(); i++) {
			if (i > 0) sb.append(eol);
			sb.append(lines.get(i));
		}
		if (endsWithNewline && !lines.isEmpty()) sb.append(eol);
		return sb.toString();
	}

	static List<String> hashes(List<String> lines, boolean loose) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		List<String> out = new ArrayList<String>(lines.size());
		for (String l : lines) out.add(hash(md, loose ? l.trim() : l));
		return out;
	}

	static String hash(MessageDigest md, String line) {
		byte[] d = md.digest(line.getBytes(Patcher.RAW));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) sb.append(String.format("%02x", d[i]));
		return sb.toString();
	}
}
