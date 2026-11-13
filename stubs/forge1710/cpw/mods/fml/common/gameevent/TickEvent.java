package cpw.mods.fml.common.gameevent;
import cpw.mods.fml.common.eventhandler.Event;
public class TickEvent extends Event {
	public enum Phase { START, END }
	public final Phase phase = null;
	public static class ClientTickEvent extends TickEvent {}
	public static class RenderTickEvent extends TickEvent {}
}
