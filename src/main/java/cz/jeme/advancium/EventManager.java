package cz.jeme.advancium;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
final class EventManager implements Listener {
    private final Plugin plugin;
    private final Map<Class<? extends Event>, Map<EventPriority, List<Consumer<? extends Event>>>> eventMap = new HashMap<>();

    private EventManager(final Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin plugin() {
        return plugin;
    }

    public <T extends Event> void subscribe(final Class<T> eventClass,
                                            final Consumer<T> handler) {
        subscribe(eventClass, EventPriority.NORMAL, handler);
    }

    public <T extends Event> void subscribe(final Class<T> eventClass,
                                            final EventPriority priority,
                                            final Consumer<T> handler) {
        final Map<EventPriority, List<Consumer<? extends Event>>> priorityMap = eventMap.computeIfAbsent(
                eventClass,
                $ -> new HashMap<>()
        );

        priorityMap.computeIfAbsent(
                priority,
                $ -> {
                    final List<Consumer<? extends Event>> list = new ArrayList<>();
                    Bukkit.getPluginManager().registerEvent(
                            eventClass,
                            this,
                            priority,
                            ($$, event) -> {
                                if (!eventClass.isInstance(event)) return;
                                for (final Consumer<? extends Event> h : list) {
                                    @SuppressWarnings("unchecked") final Consumer<Event> cH = (Consumer<Event>) h;
                                    cH.accept(event);
                                }
                            },
                            plugin
                    );
                    return list;
                }
        ).add(handler);
    }

    private static final Map<String, EventManager> PLUGIN_EVENT_MANAGERS = new HashMap<>();

    public static EventManager forPlugin(final Plugin plugin) {
        return PLUGIN_EVENT_MANAGERS.computeIfAbsent(
                plugin.getName(),
                $ -> new EventManager(plugin)
        );
    }
}
