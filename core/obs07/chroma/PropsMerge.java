package obs07.chroma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Key by key operations for .properties and .lang files, used when text hunks fail
final class PropsMerge {

	static final class Entry {
		final String key;
		final List<String> tokens;
		final List<String> raw;
		final int start;

		Entry(String key, List<String> tokens, List<String> raw, int start) {
			this.key = key;
			this.tokens = tokens;
			this.raw = raw;
			this.start = start;
		}
	}

	static String apply(String upstream, List<String> ops) {
		boolean crlf = upstream.contains("\r\n");
		List<String> lines = Applier.split(upstream);

		for (int i = 0; i < ops.size(); i++) {
			String[] op = ops.get(i).split(" ", 4);
			String kind = op[0];
			Map<String, Entry> entries = parse(lines);

			if (kind.equals("del")) {
				Entry e = entries.get(op[1]);
				if (e != null) replace(lines, e, new ArrayList<String>());
			} else if (kind.equals("add") || kind.equals("val")) {
				int count = Integer.parseInt(op[2]);
				List<String> raw = new ArrayList<String>();
				for (int k = 0; k < count; k++) raw.add(ops.get(++i).substring(1));
				String key = parse(raw).keySet().iterator().next();
				Entry existing = entries.get(key);
				if (existing != null) replace(lines, existing, raw);
				else insertAfter(lines, kind.equals("add") ? entries.get(op[1]) : null, raw);
			} else if (kind.equals("rem") || kind.equals("ins")) {
				String key = op[1];
				Entry e = entries.get(key);
				List<String> tokens = e == null ? new ArrayList<String>() : new ArrayList<String>(e.tokens);
				if (kind.equals("rem")) {
					tokens.remove(op[2]);
				} else if (!tokens.contains(op[3])) {
					int at = op[2].equals("-") ? 0 : tokens.lastIndexOf(op[2]) + 1;
					if (at == 0 && !op[2].equals("-")) at = tokens.size();
					tokens.add(at, op[3]);
				}
				List<String> raw = single(key, tokens);
				if (e != null) replace(lines, e, raw);
				else insertAfter(lines, null, raw);
			}
		}
		return Applier.join(lines, crlf, true);
	}

	static List<String> single(String key, List<String> tokens) {
		StringBuilder sb = new StringBuilder(key).append('=');
		for (int i = 0; i < tokens.size(); i++) {
			if (i > 0) sb.append(' ');
			sb.append(tokens.get(i));
		}
		return Arrays.asList(sb.toString());
	}

	static void replace(List<String> lines, Entry e, List<String> raw) {
		for (int i = 0; i < e.raw.size(); i++) lines.remove(e.start);
		lines.addAll(e.start, raw);
	}

	static void insertAfter(List<String> lines, Entry previous, List<String> raw) {
		int at = previous == null ? lines.size() : previous.start + previous.raw.size();
		lines.addAll(at, raw);
	}

	// last definition wins, like the loaders
	static Map<String, Entry> parse(List<String> lines) {
		Map<String, Entry> map = new LinkedHashMap<String, Entry>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String trimmed = line.trim();
			if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) continue;
			int eq = trimmed.indexOf('=');
			if (eq <= 0) continue;

			int start = i;
			List<String> raw = new ArrayList<String>();
			StringBuilder value = new StringBuilder();
			String part = trimmed.substring(eq + 1);
			raw.add(line);
			while (part.endsWith("\\") && i + 1 < lines.size()) {
				value.append(part, 0, part.length() - 1).append(' ');
				part = lines.get(++i).trim();
				raw.add(lines.get(i));
			}
			value.append(part.endsWith("\\") ? part.substring(0, part.length() - 1) : part);

			List<String> tokens = new ArrayList<String>();
			for (String t : value.toString().trim().split("\\s+")) if (!t.isEmpty()) tokens.add(t);
			String key = trimmed.substring(0, eq).trim();
			map.remove(key);
			map.put(key, new Entry(key, tokens, raw, start));
		}
		return map;
	}
}
