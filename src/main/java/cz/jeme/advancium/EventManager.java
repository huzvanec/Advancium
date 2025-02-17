package cz.jeme.advancium;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
final class EventManager implements Listener {
    private final Plugin plugin;

    private EventManager(final Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin plugin() {
        return plugin;
    }

    public <T extends Event> void subscribe(final Class<T> eventClass, final @NotNull Consumer<T> handler) {
        subscribe(eventClass, EventPriority.NORMAL, handler);
    }

    public <T extends Event> void subscribe(final Class<T> eventClass, final EventPriority priority, final @NotNull Consumer<T> handler) {
        Bukkit.getPluginManager().registerEvent(
                eventClass,
                this,
                priority,
                ($, event) -> {
                    if (!eventClass.isInstance(event)) return;
                    handler.accept(eventClass.cast(event));
                },
                plugin
        );
    }

    private static final Map<String, EventManager> PLUGIN_EVENT_MANAGERS = new HashMap<>();

    public static EventManager forPlugin(final Plugin plugin) {
        return PLUGIN_EVENT_MANAGERS.computeIfAbsent(
                plugin.getName(),
                $ -> new EventManager(plugin)
        );
    }
}
