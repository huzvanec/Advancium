package cz.jeme.advancium;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Represents a custom advancement.
 * <p>
 * A custom advancement defines a unique milestone or task for players to achieve.
 */
@NullMarked
public sealed interface CustomAdvancement extends Keyed permits AbstractCustomAdvancement {
    /**
     * Creates a new builder for a {@link CustomAdvancement} using the given {@link NamespacedKey}.
     *
     * @param key the namespaced key of the advancement
     * @return the builder to configure and build the advancement
     * @see NamespacedKey#NamespacedKey(Plugin, String)
     */
    static Builder advancement(final NamespacedKey key) {
        return new AbstractCustomAdvancement.Builder(key);
    }

    /**
     * Gets the display properties of the custom advancement.
     *
     * @return the advancement's display settings
     * @see CustomAdvancementDisplay
     */
    CustomAdvancementDisplay display();

    /**
     * Gets the rewards granted upon completing the advancement.
     *
     * @return the advancement's rewards
     * @see CustomAdvancementRewards
     */
    CustomAdvancementRewards rewards();

    /**
     * Gets the {@link Set} of criteria.
     * <p>
     * Which criteria must be met to complete this advancement is defined in the {@link #requirements()}.
     * </p>
     *
     * @return an unmodifiable {@link Set} of criteria
     * @see #requirements()
     */
    @Unmodifiable
    Set<String> criteria();

    /**
     * Gets the {@link Set} of criteria groups that define the requirements for advancement completion.
     * <p>
     * Some advancements require players to fulfill multiple groups of criteria, where completing
     * any criterion within a group satisfies that group. The advancement is completed when all
     * groups have been satisfied.
     * </p>
     * Example:
     * <pre>{@code
     * Set.of(
     *     Set.of("diamond_axe", "diamond_sword"),
     *     Set.of("iron_shovel", "iron_hoe")
     * )
     * }</pre>
     * This means the advancement consists of two groups. To complete the first group, the player
     * must obtain either the {@code "diamond_axe"} or the {@code "diamond_sword"} criterion.
     * To complete the second group, they must obtain either the {@code "iron_shovel"}
     * or the {@code "iron_hoe"} criterion.
     *
     * @return an unmodifiable {@link Set} of requirement groups, where each group is a {@link Set} of criteria
     */
    @Unmodifiable
    Set<Set<String>> requirements();


    /**
     * Gets the {@link AdvancementProgress} associated with the given player.
     *
     * @param player the player for whom the progress is fetched
     * @return the player's advancement progress
     * @see AdvancementProgress
     */
    AdvancementProgress progress(final Player player);

    /**
     * Gets the {@link CustomAdvancementTab} where this advancement is displayed.
     *
     * @return the custom advancement tab this advancement belongs to
     * @throws UnsupportedOperationException if this advancement does not belong to a custom tab
     *                                       (if this advancement was created using {@link Builder#buildAndBindToBukkit})
     * @see CustomAdvancementTab
     * @see #hasCustomTab()
     */
    CustomAdvancementTab tab();

    /**
     * Gets the parent {@link CustomAdvancement}.
     *
     * @return the parent advancement
     * @throws UnsupportedOperationException if this advancement is a root advancement
     *                                       or it does not have a {@link CustomAdvancement} parent
     * @see #isRoot()
     * @see #hasCustomParent()
     */
    CustomAdvancement parent();

    /**
     * Gets the {@link NamespacedKey} of the parent advancement.
     *
     * @return The parent advancement's namespaced key.
     * @throws UnsupportedOperationException if this advancement is a root advancement
     * @see #isRoot()
     */
    NamespacedKey parentKey();

    /**
     * Converts this custom advancement into a Bukkit {@link Advancement} instance.
     *
     * @return the corresponding Bukkit advancement
     */
    Advancement asBukkit();

    /**
     * Checks whether this custom advancement is a root custom advancement.
     * <p>
     * Returns {@code false} if this advancement has a custom advancement parent.
     * <p>
     * Returns {@code true} if this advancement is a root custom advancement.
     * <p>
     * Returns {@code false} if this advancement has a Bukkit parent.
     *
     * @return {@code true} if this advancement is a root advancement, otherwise {@code false}.
     */
    boolean isRoot();

    /**
     * Checks whether this custom advancement has a {@link CustomAdvancement} parent.
     * <p>
     * Returns {@code true} if this advancement has a custom advancement parent.
     * <p>
     * Returns {@code false} if this advancement is a root custom advancement.
     * <p>
     * Returns {@code false} if this advancement has a Bukkit parent.
     *
     * @return {@code true} if this advancement has a parent and it is a {@link CustomAdvancement}, otherwise {@code false}
     */
    boolean hasCustomParent();

    /**
     * Checks whether this custom advancement belongs to a {@link CustomAdvancementTab}.
     * <p>
     * Returns {@code true} if this advancement has a custom advancement parent.
     * <p>
     * Returns {@code true} if this advancement is a root custom advancement.
     * <p>
     * Returns {@code false} if this advancement has a Bukkit parent.
     *
     * @return {@code} true if this advancement has a custom advancement tab, otherwise {@code false}
     */
    boolean hasCustomTab();

    /**
     * Gets the plugin associated with this custom advancement.
     *
     * @return the plugin that this advancement belongs to
     */
    Plugin plugin();

    /**
     * Builder for creating and configuring instances of {@link CustomAdvancement}.
     */
    sealed interface Builder permits AbstractCustomAdvancement.Builder {
        /**
         * Sets the display properties of the advancement.
         * <p>
         * <strong>Default:</strong> {@link CustomAdvancementDisplay#empty()}
         * </p>
         *
         * @param display the display properties for the advancement
         * @return this builder instance for chaining
         * @see CustomAdvancementDisplay
         * @see CustomAdvancementDisplay#display()
         */
        Builder display(final CustomAdvancementDisplay display);

        /**
         * Builds the display properties for the advancement before setting them.
         * <p>
         * <strong>Default:</strong> {@link CustomAdvancementDisplay#empty()}
         * </p>
         *
         * @param builder a builder for the display settings
         * @return this builder instance for chaining
         */
        default Builder display(final CustomAdvancementDisplay.Builder builder) {
            return display(builder.build());
        }

        /**
         * Sets the rewards for the advancement.
         * <p>
         * <strong>Default:</strong> {@link CustomAdvancementRewards#empty()}
         * </p>
         *
         * @param rewards the rewards for completing the advancement
         * @return this builder instance for chaining
         * @see CustomAdvancementRewards
         * @see CustomAdvancementRewards#rewards()
         */
        Builder rewards(final CustomAdvancementRewards rewards);

        /**
         * Builds the rewards for the advancement before setting them.
         * <p>
         * <strong>Default:</strong> {@link CustomAdvancementRewards#empty()}
         * </p>
         *
         * @param builder a builder for the rewards settings
         * @return this builder instance for chaining
         */
        default Builder rewards(final CustomAdvancementRewards.Builder builder) {
            return rewards(builder.build());
        }

        /**
         * Sets the criteria for this advancement.
         * <p>
         * This method automatically updates the requirements so that each criterion forms its own group.
         * If this behavior is not desired, a subsequent call to {@link #requirements(Set)} should be made
         * to override the default grouping.
         * </p>
         * <p>
         * <strong>Default:</strong> {@code Set.of("dummy")}
         * </p>
         *
         * @param criteria a {@link Set} of criteria names
         * @return this builder instance for chaining
         * @deprecated in favour of using only {@link #requirements(Set)}, criteria are now auto-generated
         */
        @Deprecated
        Builder criteria(final Set<String> criteria);

        /**
         * Sets the requirements for this advancement by grouping criteria.
         * <p>
         * The format and behavior of requirements are explained in detail in {@link CustomAdvancement#requirements()}.
         * Each inner set represents a requirement group, where completing any criterion within a group
         * satisfies that group. The advancement is completed when all groups are satisfied.
         * </p>
         * <p>
         * A call to this method must be made <i>after</i> {@link #criteria(Set)},
         * as calling {@link #criteria(Set)} afterward will overwrite the requirements.
         * </p>
         * <p>
         * <strong>Default:</strong> {@code Set.of(Set.of("dummy"))}
         * </p>
         *
         * @param requirements a set of grouped criteria requirements
         * @return this builder instance for chaining
         * @see CustomAdvancement#requirements()
         */
        Builder requirements(final Set<Set<String>> requirements);

        /**
         * Adds an event handler for this advancement.
         *
         * @param eventClass the event class to listen to
         * @param priority   the priority of the event handler
         * @param handler    the handler executed when the event occurs
         * @param <T>        the type of the event
         * @return this builder instance for chaining
         */
        <T extends Event> Builder on(final Class<T> eventClass, final EventPriority priority, final BiConsumer<T, CustomAdvancement> handler);

        /**
         * Adds an event handler for this advancement with default normal priority ({@link EventPriority#NORMAL}).
         *
         * @param eventClass the event class to listen to
         * @param handler    the handler executed when the event occurs
         * @param <T>        the type of the event
         * @return this builder instance for chaining
         */
        default <T extends Event> Builder on(final Class<T> eventClass, final BiConsumer<T, CustomAdvancement> handler) {
            return on(eventClass, EventPriority.NORMAL, handler);
        }

        /**
         * Adds an event handler that fires when a player is granted any criterion of this advancement.
         * <p>
         * This event is registered with the priority {@link EventPriority#NORMAL}.
         * </p>
         *
         * @param handler the handler executed when the event occurs
         * @return this builder instance for chaining
         */
        Builder onCriterionGranted(final BiConsumer<PlayerAdvancementCriterionGrantEvent, CustomAdvancement> handler);

        /**
         * Adds an event handler that fires when a player completes this advancement
         * (i.e., when they have acquired all required criteria).
         * <p>
         * This event is registered with the priority {@link EventPriority#NORMAL}.
         * </p>
         *
         * @param handler the handler executed when the event occurs
         * @return this builder instance for chaining
         */
        Builder onAdvancementCompleted(final BiConsumer<PlayerAdvancementCriterionGrantEvent, CustomAdvancement> handler);

        /**
         * Builds this advancement and binds it to a specified {@link CustomAdvancement} parent.
         *
         * @param parent the parent custom advancement
         * @return the built custom advancement
         */
        CustomAdvancement buildAndBindTo(final CustomAdvancement parent);

        /**
         * Builds this advancement and binds it to the root custom advancement in a specified tab.
         *
         * @param tab the {@link CustomAdvancementTab} where the advancement belongs
         * @return the built custom advancement
         */
        default CustomAdvancement buildAndBindTo(final CustomAdvancementTab tab) {
            return buildAndBindTo(tab.root());
        }

        /**
         * Builds this advancement and binds it to a Bukkit {@link Advancement} instance.
         *
         * @param advancement the parent Bukkit advancement
         * @param plugin      the plugin associated with the advancement
         * @return the built custom advancement
         */
        default CustomAdvancement buildAndBindToBukkit(final Advancement advancement, final Plugin plugin) {
            return buildAndBindToBukkit(advancement.getKey(), plugin);
        }

        /**
         * Builds this advancement and binds it to a Bukkit {@link Advancement} using a {@link String} key.
         *
         * @param key    the string key of the parent advancement
         * @param plugin the plugin associated with the advancement
         * @return the configured custom advancement
         * @throws NullPointerException if the key pattern is invalid
         * @see NamespacedKey
         */
        default CustomAdvancement buildAndBindToBukkit(final @KeyPattern String key, final Plugin plugin) {
            return buildAndBindToBukkit(Objects.requireNonNull(NamespacedKey.fromString(key), "Invalid key: \"" + key + "\""), plugin);
        }

        /**
         * Builds this advancement and binds it to a Bukkit {@link Advancement} using a {@link NamespacedKey}.
         *
         * @param key    the key of the parent advancement
         * @param plugin the plugin associated with the advancement
         * @return the built custom advancement
         */
        CustomAdvancement buildAndBindToBukkit(final NamespacedKey key, final Plugin plugin);
    }
}