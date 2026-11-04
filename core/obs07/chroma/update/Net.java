package obs07.chroma.update;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

// HTTPS downloads from the one host we trust, with size limits
final class Net {

	static final String HOST = "raw.githubusercontent.com";
	static final String BASE = "https://" + HOST + "/Summxx/ChromaPatch/main/updates/";

	interface Fetcher {
		byte[] get(String file, int maxBytes) throws IOException;
	}

	static final Fetcher GITHUB = new Fetcher() {
		public byte[] get(String file, int maxBytes) throws IOException {
			if (!file.matches("[A-Za-z0-9_.-]{1,80}") || file.contains("..")) throw new IOException("bad file name " + file);
			URL url = new URL(BASE + file);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			if (!(c instanceof HttpsURLConnection)) throw new IOException("not https");
			c.setInstanceFollowRedirects(false);
			c.setUseCaches(false);
			c.setConnectTimeout(5000);
			c.setReadTimeout(10000);
			c.setRequestProperty("User-Agent", "ChromaPatch/" + Updates.MOD_VERSION);
			c.setRequestProperty("Accept-Encoding", "identity");
			try {
				int code = c.getResponseCode();
				if (code != 200) throw new IOException("HTTP " + code);
				if (!HOST.equals(c.getURL().getHost())) throw new IOException("unexpected host");
				long length = c.getContentLengthLong();
				if (length > maxBytes) throw new IOException("too large");
				InputStream in = c.getInputStream();
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buf = new byte[16384];
					int n;
					while ((n = in.read(buf)) > 0) {
						if (out.size() + n > maxBytes) throw new IOException("too large");
						out.write(buf, 0, n);
					}
					return out.toByteArray();
				} finally {
					in.close();
				}
			} finally {
				c.disconnect();
			}
		}
	};
}
