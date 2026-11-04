package net.minecraft.client.gui.screens;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
public abstract class Screen {
	public int f_96543_;
	public int f_96544_;
	protected Font f_96547_;
	protected Screen(Component title) {}
	protected void m_7856_() {}
	public void m_88315_(GuiGraphics g, int x, int y, float t) {}
	public void m_280273_(GuiGraphics g) {}
	public void m_7379_() {}
	protected <T extends GuiEventListener & Renderable & NarratableEntry> T m_142416_(T w) { return w; }
}
