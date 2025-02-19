package cz.jeme.advancium;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.function.BiConsumer;

record EventRegistration<T extends Event>(
        Class<T> eventClass,
        EventPriority eventPriority,
        BiConsumer<T, CustomAdvancement> handler
) {
    public EventRegistration(final Class<T> eventClass, final BiConsumer<T, CustomAdvancement> handler) {
        this(eventClass, EventPriority.NORMAL, handler);
    }
}
