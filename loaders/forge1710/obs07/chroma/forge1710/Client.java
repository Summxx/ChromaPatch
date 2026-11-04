package obs07.chroma.forge1710;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import obs07.chroma.update.Links;
import obs07.chroma.update.Prompts;
import obs07.chroma.update.Updates;

// Chroma Patch pages on the main menu (SRG names, see Dialog)
public final class Client implements Prompts.Ui {

	final GuiScreen parent;

	Client(GuiScreen parent) {
		this.parent = parent;
	}

	static void init(File dir) {
		Updates.start(dir);
		if (Updates.get() == null) return;
		FMLCommonHandler.instance().bus().register(new Ticker());
	}

	public static final class Ticker {
		@SubscribeEvent
		public void tick(TickEvent.ClientTickEvent e) {
			if (e.phase != TickEvent.Phase.END) return;
			// getMinecraft, currentScreen, theWorld
			Minecraft mc = Minecraft.func_71410_x();
			GuiScreen screen = mc.field_71462_r;
			if (screen == null || mc.field_71441_e != null) return;
			// GTNH replaces the main menu with CustomMainMenu
			if (screen instanceof GuiMainMenu || screen.getClass().getName().equals("lumien.custommainmenu.gui.GuiCustom"))
				Prompts.onTitleScreen(new Client(screen));
		}
	}

	public void confirm(String title, String message, String yes, String no, final Prompts.Answer answer, Runnable escape) {
		show(new Dialog(title, message, yes, no, null, null, new Runnable() {
			public void run() {
				answer.accept(true);
			}
		}, new Runnable() {
			public void run() {
				answer.accept(false);
			}
		}, escape));
	}

	public void alert(String title, String message) {
		Runnable done = new Runnable() {
			public void run() {
				Prompts.alertClosed();
				back();
			}
		};
		show(new Dialog(title, message, "OK", null, null, null, done, done, done));
	}

	public void page(String title, String message, String linkLabel, String url, String close, Runnable closed) {
		show(new Dialog(title, message, close, null, linkLabel, url, closed, closed, closed));
	}

	public void openLink(final String url) {
		Runnable back = new Runnable() {
			public void run() {
				back();
			}
		};
		show(new Dialog("Open this link in your browser?", url, "Open", "Cancel", null, null, new Runnable() {
			public void run() {
				Links.open(url);
				back();
			}
		}, back, back));
	}

	public void back() {
		show(parent);
	}

	// displayGuiScreen
	static void show(GuiScreen screen) {
		Minecraft.func_71410_x().func_147108_a(screen);
	}
}
