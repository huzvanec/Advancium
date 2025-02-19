package cz.jeme.advancium;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@NullMarked
sealed abstract class AbstractCustomAdvancement implements CustomAdvancement permits BaseCustomAdvancement, RootCustomAdvancement, BukkitCustomAdvancement {
    protected final Plugin plugin;
    protected final NamespacedKey key;
    protected final CustomAdvancementDisplay display;
    protected final CustomAdvancementRewards rewards;
    protected final Set<String> criteria;
    protected final Set<Set<String>> requirements;

    private @Nullable Advancement lazyBukkit;

    protected AbstractCustomAdvancement(final Builder builder, final Plugin plugin) {
        this.plugin = plugin;

        key = builder.key;
        display = builder.display;
        rewards = builder.rewards;
        criteria = builder.criteria;
        requirements = builder.requirements.stream()
                .map(Collections::unmodifiableSet)
                .collect(Collectors.toSet());

        builder.eventRegistrations.forEach(reg ->
                EventManager.forPlugin(plugin).subscribe(
                        reg.eventClass(),
                        reg.eventPriority(),
                        event -> {
                            @SuppressWarnings("unchecked") final BiConsumer<Event, CustomAdvancement> handler = (BiConsumer<Event, CustomAdvancement>) reg.handler();
                            handler.accept(event, this);
                        }
                )
        );

        AdvancementLoader.INSTANCE.load(this);
    }

    @Override
    public Advancement asBukkit() {
        if (lazyBukkit == null)
            lazyBukkit = Objects.requireNonNull(
                    Bukkit.getAdvancement(key),
                    "Could not obtain Bukkit representation of this custom advancement, this advancement is not loaded to the server"
            );
        return lazyBukkit;
    }

    @Override
    public @Unmodifiable Set<String> criteria() {
        return criteria;
    }

    @Override
    public CustomAdvancementDisplay display() {
        return display;
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public AdvancementProgress progress(final Player player) {
        return player.getAdvancementProgress(asBukkit());
    }

    @Override
    public @Unmodifiable Set<Set<String>> requirements() {
        return requirements;
    }

    @Override
    public CustomAdvancementRewards rewards() {
        return rewards;
    }

    @Override
    public NamespacedKey key() {
        return key;
    }


    static final class Builder implements CustomAdvancement.Builder {
        private final NamespacedKey key;

        private CustomAdvancementDisplay display = CustomAdvancementDisplay.empty();
        private CustomAdvancementRewards rewards = CustomAdvancementRewards.empty();
        private @Unmodifiable Set<String> criteria = Set.of("dummy");
        private Set<Set<String>> requirements = Set.of(criteria);

        private final List<EventRegistration<? extends Event>> eventRegistrations = new ArrayList<>();
        private final List<BiConsumer<PlayerAdvancementCriterionGrantEvent, CustomAdvancement>> criterionGrantedHandlers = new ArrayList<>();
        private final List<BiConsumer<PlayerAdvancementCriterionGrantEvent, CustomAdvancement>> advancementCompletedHandlers = new ArrayList<>();

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
        public CustomAdvancement.Builder requirements(final Set<Set<String>> requirements) {
            this.criteria = requirements.stream()
                    .flatMap(Set::stream)
                    .peek(criterion -> {
                        if (criterion.isBlank())
                            throw new IllegalArgumentException("Empty criterion name");
                    })
                    .collect(Collectors.toSet());
            if (criteria.isEmpty()) throw new IllegalArgumentException("No criteria provided");
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
            criterionGrantedHandlers.add(handler);
            return this;
        }

        @Override
        public CustomAdvancement.Builder onAdvancementCompleted(final BiConsumer<PlayerAdvancementCriterionGrantEvent, CustomAdvancement> handler) {
            advancementCompletedHandlers.add(handler);
            return this;
        }

        private void registerCriterionEvents() {
            // Accessing loot directly
            // If not done with enough care, this could mutate CustomAdvancementRewards!
            final List<ItemStack> unsafeLootRewards = ((CustomAdvancementRewardsImpl) rewards).loot;
            if (
                    criterionGrantedHandlers.isEmpty() &&
                    advancementCompletedHandlers.isEmpty() &&
                    unsafeLootRewards.isEmpty()
            ) return; // Just a slight optimization
            on(
                    PlayerAdvancementCriterionGrantEvent.class,
                    EventPriority.NORMAL,
                    (event, advancement) -> {
                        if (!event.getAdvancement().getKey().equals(key)) return;
                        criterionGrantedHandlers.forEach(
                                handler -> handler.accept(event, advancement)
                        );
                        if (!event.getAdvancementProgress().isDone()) return;
                        advancementCompletedHandlers.forEach(
                                handler -> handler.accept(event, advancement)
                        );
                        if (event.isCancelled()) return;
                        final Player player = event.getPlayer();
                        final Collection<ItemStack> overflow = player.getInventory().addItem(
                                unsafeLootRewards.toArray(new ItemStack[0])
                        ).values();
                        for (final ItemStack item : overflow)
                            player.getWorld().dropItem(player.getLocation(), item);
                    }
            );
        }

        @Override
        public CustomAdvancement buildAndBindTo(final CustomAdvancement parent) {
            registerCriterionEvents();
            return new BaseCustomAdvancement(this, parent);
        }

        @Override
        public CustomAdvancement buildAndBindToBukkit(final NamespacedKey key, final Plugin plugin) {
            registerCriterionEvents();
            return new BukkitCustomAdvancement(this, key, plugin);
        }

        @ApiStatus.Internal
        CustomAdvancement buildRoot(final CustomAdvancementTab tab) {
            registerCriterionEvents();
            return new RootCustomAdvancement(this, tab);
        }
    }
}
