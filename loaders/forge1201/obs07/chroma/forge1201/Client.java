package obs07.chroma.forge1201;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import obs07.chroma.update.Prompts;
import obs07.chroma.update.Updates;

// Chroma Patch pages on the title screen (SRG names: Minecraft.getInstance/screen/setScreen)
final class Client implements Prompts.Ui {

	final Screen parent;

	Client(Screen parent) {
		this.parent = parent;
	}

	static void init(File dir) {
		Updates.start(dir);
		if (Updates.get() == null) return;
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, e -> {
			if (e.phase == TickEvent.Phase.END) tick();
		});
	}

	static void tick() {
		Minecraft mc = Minecraft.m_91087_();
		if (mc.f_91080_ instanceof TitleScreen) Prompts.onTitleScreen(new Client(mc.f_91080_));
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
		ConfirmLinkScreen.m_274480_(url, parent, false);
	}

	public void back() {
		show(parent);
	}

	static void show(Screen screen) {
		Minecraft.m_91087_().m_91152_(screen);
	}
}
