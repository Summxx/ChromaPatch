package net.minecraftforge.event;
import net.minecraftforge.eventbus.api.Event;
public class TickEvent extends Event {
	public enum Phase { START, END }
	public final Phase phase = null;
	public static class ClientTickEvent extends TickEvent {}
}
