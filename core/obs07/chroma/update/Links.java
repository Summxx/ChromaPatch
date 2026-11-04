package obs07.chroma.update;

import java.awt.Desktop;
import java.net.URI;

// Opens a fixed https link in the browser (1.7.10 has no link screen helper)
public final class Links {

	public static void open(String url) {
		if (!url.startsWith("https://")) return;
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
				return;
			}
		} catch (Throwable ignored) {
		}
		String os = System.getProperty("os.name", "").toLowerCase();
		String[] cmd = os.contains("win") ? new String[] {"rundll32", "url.dll,FileProtocolHandler", url}
			: os.contains("mac") ? new String[] {"open", url} : new String[] {"xdg-open", url};
		try {
			new ProcessBuilder(cmd).start();
		} catch (Exception e) {
			obs07.chroma.Patcher.log("could not open " + url + ": " + e);
		}
	}
}
