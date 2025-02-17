package cz.jeme.advancium;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@NullMarked
final class CustomAdvancementImpl implements CustomAdvancement {
    private final NamespacedKey key;
    private final CustomAdvancementDisplay display;
    private final CustomAdvancementRewards rewards;
    private final Set<String> criteria;
    private final Set<Set<String>> requirements;
    private final @Nullable NamespacedKey parentKey;
    private final @Nullable CustomAdvancement parent;
    private final @Nullable CustomAdvancementTab tab;
    private final Plugin plugin;

    // bind a custom advancement to a bukkit advancement
    private CustomAdvancementImpl(final Builder builder, final NamespacedKey parentKey, final Plugin plugin) {
        this.parent = null;
        this.tab = null;
        this.parentKey = parentKey;
        this.plugin = plugin;

        key = builder.key;
        display = builder.display;
        rewards = builder.rewards;
        criteria = Collections.unmodifiableSet(builder.criteria);
        requirements = builder.requirements.stream()
                .map(Collections::unmodifiableSet)
                .collect(Collectors.toSet());

        register(builder);
    }

    // bind a custom advancement to another custom advancement
    // this is the only implementation that will not throw any exception
    // when accessing its methods
    private CustomAdvancementImpl(final Builder builder, final CustomAdvancement parent) {
        this.parent = parent;
        this.tab = parent.tab();
        this.parentKey = parent.key();
        this.plugin = parent.plugin();

        key = builder.key;
        display = builder.display;
        rewards = builder.rewards;
        criteria = Collections.unmodifiableSet(builder.criteria);
        requirements = builder.requirements.stream()
                .map(Collections::unmodifiableSet)
                .collect(Collectors.toSet());

        register(builder);
    }

    // bind to tab (a root advancemnet)
    private CustomAdvancementImpl(final Builder builder, final CustomAdvancementTab tab) {
        this.parent = null;
        this.tab = tab;
        this.parentKey = null;
        this.plugin = tab.plugin();

        key = builder.key;
        display = builder.display;
        rewards = builder.rewards;
        criteria = Collections.unmodifiableSet(builder.criteria);
        requirements = builder.requirements.stream()
                .map(Collections::unmodifiableSet)
                .collect(Collectors.toSet());

        register(builder);
    }

    private void register(final Builder builder) {
        // register events
        builder.eventRegistrations.forEach(reg -> EventManager.forPlugin(plugin).subscribe(
                reg.eventClass(),
                reg.eventPriority(),
                event -> {
                    @SuppressWarnings("unchecked") final BiConsumer<Event, CustomAdvancement> handler = (BiConsumer<Event, CustomAdvancement>) reg.handler();
                    handler.accept(event, this);
                }
        ));

        AdvancementLoader.INSTANCE.load(this);
    }

    @Override
    public CustomAdvancementDisplay display() {
        return display;
    }

    @Override
    public CustomAdvancementRewards rewards() {
        return rewards;
    }

    @Override
    public @Unmodifiable Set<String> criteria() {
        return criteria;
    }

    @Override
    public @Unmodifiable Set<Set<String>> requirements() {
        return requirements;
    }

    @Override
    public AdvancementProgress progress(final Player player) {
        return player.getAdvancementProgress(asBukkit());
    }

    @Override
    public CustomAdvancementTab tab() {
        if (tab == null) throw new UnsupportedOperationException(
                "Advancement is bound to a bukkit advancement and therefore does not belong to a custom tab."
        );
        return tab;
    }

    @Override
    public CustomAdvancement parent() {
        if (parent == null) throw new UnsupportedOperationException(
                "" // TODO split impl
        );
        return parent;
    }

    @Override
    public NamespacedKey parentKey() {
        if (parentKey == null) throw new UnsupportedOperationException(
                "" // TODO split impl
        );
        return parentKey;
    }

    @Override
    public Advancement asBukkit() {
        return Objects.requireNonNull(Bukkit.getAdvancement(key), "This advancement is not loaded to the server");
    }

    @Override
    public boolean isRoot() {
        return parentKey == null;
    }

    @Override
    public boolean hasCustomParent() {
        return parent != null;
    }

    @Override
    public boolean hasCustomTab() {
        return tab != null;
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    static final class Builder implements CustomAdvancement.Builder {
        private final NamespacedKey key;
        private final List<EventRegistration<? extends Event>> eventRegistrations = new ArrayList<>();

        private CustomAdvancementDisplay display = CustomAdvancementDisplay.empty();
        private CustomAdvancementRewards rewards = CustomAdvancementRewards.empty();
        private Set<String> criteria = Set.of("dummy");
        private Set<Set<String>> requirements = Set.of(Set.of("dummy"));

        public Builder(final NamespacedKey key) {
            this.key = key;
        }

        @Override
        public CustomAdvancement.Builder display(final CustomAdvancementDisplay display) {
            this.display = display;
            return this;
        }

        @Override
        public CustomAdvancement.Builder rewards(final CustomAdvancementRewards rewards) {
            this.rewards = rewards;
            return this;
        }

        @Override
        public CustomAdvancement.Builder criteria(final Set<String> criteria) {
            this.criteria = criteria;
            requirements = criteria.stream()
                    .map(Set::of)
                    .collect(Collectors.toSet());
            return this;
        }

        @Override
        public CustomAdvancement.Builder requirements(final Set<Set<String>> requirements) {
            this.requirements = requirements;
            return this;
        }

        @Override
        public <T extends Event> CustomAdvancement.Builder on(final Class<T> eventClass, final EventPriority priority, final BiConsumer<T, CustomAdvancement> handler) {
            eventRegistrations.add(new EventRegistration<>(
                    eventClass,
                    priority,
                    handler
            ));
            return this;
        }

        @Override
        public CustomAdvancement.Builder onCriterionGranted(final BiConsumer<PlayerAdvancementCriterionGrantEvent, CustomAdvancement> handler) {
            return on(
                    PlayerAdvancementCriterionGrantEvent.class,
                    EventPriority.NORMAL,
                    (event, advancement) -> {
                        if (event.getAdvancement().getKey().equals(key))
                            handler.accept(event, advancement);
                    }
            );
        }

        @Override
        public CustomAdvancement.Builder onAdvancementCompleted(final BiConsumer<PlayerAdvancementCriterionGrantEvent, CustomAdvancement> handler) {
            return onCriterionGranted((event, advancement) -> {
                if (event.getAdvancementProgress().isDone())
                    handler.accept(event, advancement);
            });
        }

        @Override
        public CustomAdvancement buildAndBindTo(final CustomAdvancement parent) {
            return new CustomAdvancementImpl(this, parent);
        }

        @Override
        public CustomAdvancement buildAndBindToBukkit(final NamespacedKey key, final Plugin plugin) {
            return new CustomAdvancementImpl(this, key, plugin);
        }

        @ApiStatus.Internal
        CustomAdvancement buildRoot(final CustomAdvancementTab tab) {
            return new CustomAdvancementImpl(this, tab);
        }
    }
}
