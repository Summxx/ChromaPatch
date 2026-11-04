package net.neoforged.bus.api;
import java.util.function.Consumer;
public interface IEventBus { <T extends Event> void addListener(EventPriority p, boolean c, Class<T> t, Consumer<T> l); }
