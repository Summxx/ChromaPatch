package net.minecraft.client.gui.screens;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
public abstract class Screen {
	public int width;
	public int height;
	protected Font font;
	protected Screen(Component title) {}
	protected void init() {}
	public void render(GuiGraphics g, int x, int y, float t) {}
	public void renderBackground(GuiGraphics g, int x, int y, float t) {}
	public void onClose() {}
	protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T w) { return w; }
}
