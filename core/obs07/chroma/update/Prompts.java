package obs07.chroma.update;

// What to ask on the title screen, shared by all loaders
public final class Prompts {

	public interface Answer {
		void accept(boolean yes);
	}

	// implemented by each loader with its own screens
	public interface Ui {
		// escape closes the page without an answer
		void confirm(String title, String message, String yes, String no, Answer answer, Runnable escape);

		void alert(String title, String message);

		// one page with a link button (the loader asks before opening it) and a close button
		void page(String title, String message, String linkLabel, String url, String close, Runnable closed);

		// asks before opening the link, then goes back to the title screen
		void openLink(String url);

		void back();
	}

	static final int MAX_CHANGELOG_LINES = 12;
	public static final String DISCORD = "https://discord.gg/ZgXeQqePy3";
	public static final String RELEASES = "https://github.com/Summxx/ChromaPatch/releases/latest";

	static boolean consentAsked;
	static java.util.List<String[]> news;
	static boolean open;

	// called every client tick while the title screen is shown
	public static void onTitleScreen(final Ui ui) {
		final Updates u = Updates.get();
		if (u == null || open) return;

		// first launch: thanks, Discord, patch result and what the patch adds
		if (!u.seen.contains("welcome")) {
			open = true;
			ui.page("Thanks for downloading Chroma Patch!", welcome(u), "Discord", DISCORD, "Continue", new Runnable() {
				public void run() {
					// the welcome page already lists the changes of this version
					for (String[] n : u.whatsNew()) u.markSeen(n[0]);
					u.markSeen("welcome");
					news = null;
					open = false;
					ui.back();
				}
			});
			return;
		}

		if (!consentAsked && u.needsConsent()) {
			consentAsked = true;
			show(ui, "Chroma Patch",
				"Check GitHub for new versions of Chroma Patch at startup?\n\n"
					+ "Only a signed version notice is read.\nThe mod never downloads or installs anything.\nYou download new versions yourself from the official pages.\n"
					+ "Setting: updates in config/chromapatch.properties.",
				"Allow", "Don't allow", new Answer() {
					public void accept(boolean yes) {
						u.setConsent(yes);
					}
				});
			return;
		}

		// what's new, once per version
		if (news == null) news = u.whatsNew();
		if (!news.isEmpty()) {
			String[] n = news.remove(0);
			u.markSeen(n[0]);
			open = true;
			ui.alert("What's new: " + n[1], changelog(n[2]));
			return;
		}

		final Updates.Offer offer = u.nextOffer();
		if (offer == null) return;
		open = true;
		ui.confirm("Chroma Patch " + offer.version + " is available",
			"Installed: " + offer.current + "\n\n" + changelog(offer.changelog) + "\n\nOK opens the release page to download the new version.", "OK",
			"Skip this version", new Answer() {
				public void accept(boolean yes) {
					open = false;
					if (yes) {
						ui.openLink(RELEASES);
					} else {
						u.decline(offer);
						ui.back();
					}
				}
			}, close(ui));
	}

	// close the dialog after the answer
	static void show(final Ui ui, String title, String message, String yes, String no, final Answer answer) {
		open = true;
		ui.confirm(title, message, yes, no, new Answer() {
			public void accept(boolean value) {
				open = false;
				answer.accept(value);
				if (!open) ui.back();
			}
		}, close(ui));
	}

	static Runnable close(final Ui ui) {
		return new Runnable() {
			public void run() {
				open = false;
				ui.back();
			}
		};
	}

	static String welcome(Updates u) {
		StringBuilder sb = new StringBuilder();
		for (String line : obs07.chroma.Patcher.report()) sb.append(line).append('\n');
		for (obs07.chroma.PatchData d : obs07.chroma.Patcher.active(u.gameDir)) {
			sb.append("\nWhat the ").append(d.name()).append(" patch adds:\n").append(changelog(d.changelog())).append('\n');
		}
		sb.append("\nNews, help and feedback on the Discord.");
		return sb.toString();
	}

	public static void alertClosed() {
		open = false;
	}

	static String changelog(String text) {
		if (text.trim().isEmpty()) return "No changelog.";
		String[] lines = text.split("\n");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length && i < MAX_CHANGELOG_LINES; i++) {
			if (i > 0) sb.append('\n');
			// no formatting codes from the outside
			sb.append(lines[i].replace("§", ""));
		}
		if (lines.length > MAX_CHANGELOG_LINES) sb.append("\n...");
		return sb.toString();
	}
}
