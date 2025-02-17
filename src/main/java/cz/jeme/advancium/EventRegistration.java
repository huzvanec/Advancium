package cz.jeme.advancium;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

@NullMarked
record EventRegistration<T extends Event>(
        Class<T> eventClass,
        EventPriority eventPriority,
        BiConsumer<T, CustomAdvancement> handler
) {
    public EventRegistration(final Class<T> eventClass, final BiConsumer<T, CustomAdvancement> handler) {
        this(eventClass, EventPriority.NORMAL, handler);
    }
}
