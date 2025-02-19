package cz.jeme.advancium;

import net.kyori.adventure.key.KeyPattern;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Represents a custom advancement tab.
 * <p>
 * Custom advancement tabs group advancements under a single category.
 * <p>
 * All display data for a tab are copied from its root advancement.
 */
public sealed interface CustomAdvancementTab extends Keyed permits CustomAdvancementTabImpl {

    /**
     * Creates a new builder for configuring and constructing a {@link CustomAdvancementTab}.
     *
     * @param plugin the plugin associated with this tab
     * @return a new builder instance
     */
    static Builder tab(final Plugin plugin) {
        return new CustomAdvancementTabImpl.Builder(plugin);
    }

    /**
     * Gets the root advancement of this tab.
     * <p>
     * The root advancement is considered the primary or starting point of this tab.
     * <p>
     * All display data for this tab are copied from this root advancement.
     *
     * @return the root {@link CustomAdvancement}
     */
    CustomAdvancement root();

    /**
     * Gets the background of the tab as a {@link NamespacedKey}.
     * <p>
     * The background determines the texture or image displayed on this tab.
     * <p>
     * For example: {@code minecraft:textures/gui/advancements/backgrounds/stone.png}
     *
     * @return the {@link NamespacedKey} for the tab's background texture
     */
    NamespacedKey background();

    /**
     * Gets the plugin associated with this custom advancement tab.
     *
     * @return the plugin associated with this tab
     */
    Plugin plugin();

    /**
     * Builder for creating and configuring instances of {@link CustomAdvancementTab}.
     */
    sealed interface Builder permits CustomAdvancementTabImpl.Builder {

        /**
         * Sets the root advancement for the tab.
         * <p>
         * <strong>This is a mandatory parameter of this builder!</strong>
         * </p>
         *
         * @param builder a builder for the root {@link CustomAdvancement}
         * @return this builder instance for chaining
         */
        Builder root(final CustomAdvancement.Builder builder);

        /**
         * Sets the background texture for the tab using a {@link NamespacedKey}.
         * <p>
         * <strong>Default: {@code minecraft:textures/gui/advancements/backgrounds/stone.png}</strong>
         * </p>
         *
         * @param background the {@link NamespacedKey} of the background texture
         * @return this builder instance for chaining
         */
        Builder background(final NamespacedKey background);

        /**
         * Sets the background texture for the tab using a string representation of a {@link NamespacedKey}.
         * <p>
         * The string key is converted into a {@link NamespacedKey}. If the key is invalid, an exception is thrown.
         * <p>
         * <strong>Default: {@code minecraft:textures/gui/advancements/backgrounds/stone.png}</strong>
         * </p>
         *
         * @param background the string representation of the background key
         * @return this builder instance for chaining
         * @throws NullPointerException if the key is invalid
         */
        default Builder background(final @KeyPattern String background) {
            return background(Objects.requireNonNull(NamespacedKey.fromString(background), "Invalid key: \"" + background + "\""));
        }

        /**
         * Builds and registers the {@link CustomAdvancementTab}.
         * <p>
         * This also loads the tab into the server, making it visible in the game.
         *
         * @return the built {@link CustomAdvancementTab}
         */
        CustomAdvancementTab buildAndLoad();
    }
}