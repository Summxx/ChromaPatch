package net.minecraft.client.gui.components;
import net.minecraft.network.chat.Component;
public class Button extends AbstractButton {
	public interface OnPress { void onPress(Button b); }
	public static class Builder { public Builder bounds(int x, int y, int w, int h) { return this; } public Button build() { return null; } }
	public static Builder builder(Component c, OnPress p) { return null; }
}
