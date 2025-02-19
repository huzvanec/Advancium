package cz.jeme.advancium;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Represents the display properties of a custom advancement.
 * <p>
 * Custom advancements can have custom icons, titles, descriptions, and other visual and behavioral settings
 * shown to players. This interface provides a set of methods to access and configure such display settings.
 * <p>
 * The {@link #icon()} and the {@link #title()} of the root advancement are used in the tab's display data.
 */
sealed public interface CustomAdvancementDisplay permits CustomAdvancementDisplayImpl {
    /**
     * Creates a new builder for configuring and building a {@link CustomAdvancementDisplay}.
     *
     * @return the builder instance
     */
    static Builder display() {
        return new CustomAdvancementDisplayImpl.Builder();
    }

    /**
     * Returns an empty implementation of {@link CustomAdvancementDisplay}, containing default values.
     *
     * @return an empty custom advancement display
     */
    static CustomAdvancementDisplay empty() {
        return CustomAdvancementDisplayImpl.EMPTY;
    }

    /**
     * Gets the icon associated with the advancement.
     *
     * @return the {@link ItemStack} icon
     */
    ItemStack icon();

    /**
     * Gets the title of the advancement.
     *
     * @return the title as a {@link Component}
     */
    Component title();

    /**
     * Gets the description of the advancement.
     *
     * @return the description as a {@link Component}
     */
    Component description();

    /**
     * Gets the frame type of the advancement, which informs the visual style and importance.
     *
     * @return the {@link Frame} type
     */
    Frame frame();

    /**
     * Checks whether a notification toast is shown to the player when the advancement is achieved.
     *
     * @return {@code true} if the toast is shown, otherwise {@code false}
     */
    boolean showToast();

    /**
     * Checks whether the advancement is announced in the chat when achieved.
     *
     * @return {@code true} if the achievement is announced, otherwise {@code false}
     */
    boolean announceToChat();

    /**
     * Checks whether the advancement is hidden until it is unlocked.
     *
     * @return {@code true} if hidden, otherwise {@code false}
     */
    boolean hidden();

    /**
     * Builder interface for creating and configuring {@link CustomAdvancementDisplay} instances.
     */
    sealed interface Builder permits CustomAdvancementDisplayImpl.Builder {
        /**
         * Sets the icon for the advancement.
         *
         * @param icon the {@link ItemStack} to use as the icon
         * @return this builder instance for chaining
         */
        Builder icon(final ItemStack icon);

        /**
         * Sets the icon for the advancement using a material type.
         * <p>
         * This is a convenience method that creates an {@link ItemStack} from the given {@link Material}
         * and adds it as a reward.
         * </p>
         *
         * @param icon the {@link Material} to use as the icon
         * @return this builder instance for chaining
         */
        default Builder icon(final Material icon) {
            return icon(ItemStack.of(icon));
        }

        /**
         * Sets the icon for the advancement after applying modifications via a consumer.
         * <p>
         * This method allows modifications to be applied to the {@link ItemStack} before it is set.
         * The provided {@code consumer} can modify the item, for example, by setting enchantments, or other metadata.
         * </p>
         *
         * @param item     the {@link ItemStack} to be used as the icon
         * @param consumer a consumer to modify the {@link ItemStack}
         * @return this builder instance for chaining
         */
        default Builder icon(final ItemStack item, final Consumer<ItemStack> consumer) {
            consumer.accept(item);
            return icon(item);
        }

        /**
         * Sets the title of the advancement.
         *
         * @param title the title as a {@link Component}
         * @return this builder instance for chaining
         */
        Builder title(final Component title);

        /**
         * Sets the description of the advancement.
         *
         * @param description the description as a {@link Component}
         * @return this builder instance for chaining
         */
        Builder description(final Component description);

        /**
         * Sets the frame type for the advancement.
         * <p>
         * This controls the visual style and purpose of the advancement.
         *
         * @param frame the {@link Frame} type
         * @return this builder instance for chaining
         */
        Builder frame(final Frame frame);

        /**
         * Sets whether a notification toast should be shown when the advancement is achieved.
         *
         * @param showToast {@code true} to show the toast, otherwise {@code false}
         * @return this builder instance for chaining
         */
        Builder showToast(final boolean showToast);

        /**
         * Sets whether the advancement should be announced in chat when achieved.
         *
         * @param announceToChat {@code true} to announce in chat, otherwise {@code false}
         * @return this builder instance for chaining
         */
        Builder announceToChat(final boolean announceToChat);

        /**
         * Sets whether the advancement should remain hidden until achieved.
         *
         * @param hidden {@code true} if hidden, otherwise {@code false}
         * @return this builder instance for chaining
         */
        Builder hidden(final boolean hidden);

        /**
         * Builds and returns the {@link CustomAdvancementDisplay} instance.
         *
         * @return the built custom advancement display
         */
        CustomAdvancementDisplay build();
    }

    /**
     * Enum representing the frame types of a custom advancement.
     * <p>
     * The frame type determines the visual style and emphasis when the advancement is displayed.
     */
    enum Frame {
        /**
         * Represents a task advancement. This is the default and simplest frame type.
         */
        TASK("task"),

        /**
         * Represents a goal advancement. A step above task with increased importance.
         */
        GOAL("goal"),

        /**
         * Represents a challenge advancement. The most significant and challenging type.
         */
        CHALLENGE("challenge");

        private final String id;

        /**
         * Constructs a {@link Frame} with the given identifier.
         *
         * @param id the {@link String} identifier of the frame
         */
        Frame(final String id) {
            this.id = id;
        }

        /**
         * Gets the {@link String} identifier of the frame.
         *
         * @return the frame identifier
         */
        public String id() {
            return id;
        }
    }
}