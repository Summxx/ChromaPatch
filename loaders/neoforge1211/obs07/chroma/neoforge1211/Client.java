package obs07.chroma.neoforge1211;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import obs07.chroma.update.Prompts;
import obs07.chroma.update.Updates;

// Chroma Patch pages on the title screen
final class Client implements Prompts.Ui {

	final Screen parent;

	Client(Screen parent) {
		this.parent = parent;
	}

	static void init(File dir) {
		Updates.start(dir);
		if (Updates.get() == null) return;
		NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ClientTickEvent.Post.class, e -> tick());
	}

	static void tick() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen instanceof TitleScreen) Prompts.onTitleScreen(new Client(mc.screen));
	}

	public void confirm(String title, String message, String yes, String no, Prompts.Answer answer, Runnable escape) {
		show(new ChromaScreen(title, message, yes, no, null, null, () -> answer.accept(true), () -> answer.accept(false), escape));
	}

	public void alert(String title, String message) {
		Runnable done = () -> {
			Prompts.alertClosed();
			back();
		};
		show(new ChromaScreen(title, message, "OK", null, null, null, done, done, done));
	}

	public void page(String title, String message, String linkLabel, String url, String close, Runnable closed) {
		show(new ChromaScreen(title, message, close, null, linkLabel, url, closed, closed, closed));
	}

	public void openLink(String url) {
		ConfirmLinkScreen.confirmLinkNow(parent, url);
	}

	public void back() {
		show(parent);
	}

	static void show(Screen screen) {
		Minecraft.getInstance().setScreen(screen);
	}
}
