package obs07.chroma.forge1710;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

// Angelica draws antialiased text with its own GL shader, outside the shader pack: with a lower render
// resolution (FSR2_SCALE) signs and Nuclear Control panels lose their text. With font AA off, text in
// the world goes through the pack, so AA is turned off while the world is drawn, back on for the HUD and menus.
public final class TextAA {

	private static Field mode;
	private static int saved = -1;

	private TextAA() {}

	static void init() {
		try {
			mode = Class.forName("com.gtnewhorizons.angelica.config.FontConfig").getField("fontAAMode");
		} catch (Throwable t) {
			// no Angelica, or an Angelica without font AA
			return;
		}
		TextAA handler = new TextAA();
		FMLCommonHandler.instance().bus().register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
	}

	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent e) {
		if (e.phase == TickEvent.Phase.START) {
			// theWorld
			if (Minecraft.func_71410_x().field_71441_e != null && RenderScale.packScale() > 0f) off();
		} else {
			restore();
		}
	}

	@SubscribeEvent
	public void hud(RenderGameOverlayEvent.Pre e) {
		if (e.type == RenderGameOverlayEvent.ElementType.ALL) restore();
	}

	private static void off() {
		if (saved >= 0) return;
		try {
			int current = mode.getInt(null);
			if (current == 0) return;
			saved = current;
			mode.setInt(null, 0);
		} catch (Throwable t) {
			mode = null;
		}
	}

	private static void restore() {
		if (saved < 0) return;
		try {
			// the player may have changed it in the meantime
			if (mode.getInt(null) == 0) mode.setInt(null, saved);
		} catch (Throwable t) {
			// leave it
		}
		saved = -1;
	}
}
