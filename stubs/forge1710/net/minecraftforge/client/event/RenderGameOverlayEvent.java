package net.minecraftforge.client.event;
import cpw.mods.fml.common.eventhandler.Event;
public class RenderGameOverlayEvent extends Event {
	public enum ElementType { ALL }
	public final ElementType type = null;
	public static class Pre extends RenderGameOverlayEvent {}
}
