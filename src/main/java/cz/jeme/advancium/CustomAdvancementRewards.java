package cz.jeme.advancium;

import net.kyori.adventure.key.KeyPattern;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents the rewards granted upon completing a custom advancement.
 * <p>
 * Custom advancements can reward players with experience, recipes, items or loot tables.
 * This interface provides methods to access and configure these rewards.
 * </p>
 */
public interface CustomAdvancementRewards {

    /**
     * Creates a new builder for configuring and constructing {@link CustomAdvancementRewards}.
     *
     * @return a new builder instance
     */
    static Builder rewards() {
        return new CustomAdvancementRewardsImpl.Builder();
    }

    /**
     * Returns an empty instance of {@link CustomAdvancementRewards} with no rewards.
     *
     * @return an empty rewards object
     */
    static CustomAdvancementRewards empty() {
        return CustomAdvancementRewardsImpl.EMPTY;
    }

    /**
     * Gets the amount of experience granted upon completion of the advancement.
     *
     * @return the amount of experience points
     */
    int experience();

    /**
     * Gets a {@link List} of {@link NamespacedKey}s for recipes granted as rewards.
     *
     * @return an unmodifiable {@link List} of recipe keys
     */
    @Unmodifiable
    List<NamespacedKey> recipeKeys();

    /**
     * Gets a {@link List} of recipes granted as rewards.
     * <p>
     * This method resolves recipe keys into actual {@link Recipe} objects available on the server.
     * If a recipe key is invalid or unresolved, an exception will be thrown.
     *
     * @return an unmodifiable {@link List} of {@link Recipe} objects
     * @throws NullPointerException if resolving any of the recipe keys fails
     */
    default @Unmodifiable List<Recipe> recipes() {
        return recipeKeys().stream()
                .map(Bukkit::getRecipe)
                .map(Objects::requireNonNull)
                .toList();
    }

    /**
     * Gets a {@link List} of {@link NamespacedKey}s for loot tables granted as rewards.
     *
     * @return an unmodifiable {@link List} of loot table keys.
     */
    @Unmodifiable
    List<NamespacedKey> lootTableKeys();

    /**
     * Gets a {@link List} of loot tables granted as rewards.
     * <p>
     * This method resolves loot table keys into actual {@link LootTable} objects available on the server.
     * If a loot table key is invalid or unresolved, an exception will be thrown.
     *
     * @return an unmodifiable {@link List} of {@link LootTable} objects
     * @throws NullPointerException if resolving any of the loot table keys fails
     */
    default @Unmodifiable List<LootTable> lootTables() {
        return lootTableKeys().stream()
                .map(Bukkit::getLootTable)
                .map(Objects::requireNonNull)
                .toList();
    }

    /**
     * Returns a deep-copied, immutable list of loot granted as rewards.
     * <p>
     * Each {@link ItemStack} in the returned list is cloned to maintain immutability.
     * </p>
     *
     * @return an unmodifiable list of cloned {@link ItemStack} rewards
     */
    @Unmodifiable
    List<ItemStack> loot();

    /**
     * Builder for constructing instances of {@link CustomAdvancementRewards}.
     */
    interface Builder {
        /**
         * Sets the amount of experience granted upon completion of the advancement.
         *
         * @param experience the amount of experience points
         * @return this builder instance for chaining
         */
        Builder experience(final int experience);

        /**
         * Adds a recipe reward to the advancement.
         *
         * @param key the {@link NamespacedKey} of the recipe
         * @return this builder instance for chaining
         */
        Builder addRecipe(final NamespacedKey key);

        /**
         * Adds a recipe reward to the advancement using a {@link Recipe} object.
         *
         * @param recipe the recipe to add as a reward
         * @param <R>    the type of the recipe, which implements {@link Recipe} and {@link Keyed}
         * @return this builder instance for chaining
         */
        default <R extends Recipe & Keyed> Builder addRecipe(final @NotNull R recipe) {
            return addRecipe(recipe.getKey());
        }

        /**
         * Adds a recipe reward using a {@link String} key.
         * <p>
         * The string is converted into a {@link NamespacedKey}. If the key is invalid, an exception is thrown.
         *
         * @param key the string representation of the recipe key
         * @return this builder instance for chaining
         * @throws NullPointerException if the key is invalid
         */
        default Builder addRecipe(final @KeyPattern String key) {
            return addRecipe(Objects.requireNonNull(NamespacedKey.fromString(key), "Invalid key: \"" + key + "\""));
        }

        /**
         * Adds a loot table reward to the advancement.
         *
         * @param key the {@link NamespacedKey} of the loot table
         * @return this builder instance for chaining
         */
        Builder addLootTable(final NamespacedKey key);

        /**
         * Adds a loot table reward using a {@link LootTable} object.
         *
         * @param lootTable the loot table to add as a reward
         * @return this builder instance for chaining
         */
        default Builder addLootTable(final LootTable lootTable) {
            return addLootTable(lootTable.getKey());
        }

        /**
         * Adds a loot table reward using a {@link String} key.
         * <p>
         * The string is converted into a {@link NamespacedKey}. If the key is invalid, an exception is thrown.
         *
         * @param key the string representation of the loot table key
         * @return this builder instance for chaining
         * @throws NullPointerException if the key is invalid
         */
        default Builder addLootTable(final @KeyPattern String key) {
            return addLootTable(Objects.requireNonNull(NamespacedKey.fromString(key), "Invalid key: \"" + key + "\""));
        }

        /**
         * Adds an {@link ItemStack} to the list of loot rewards for this advancement.
         * <p>
         * The provided {@link ItemStack} is added as a reward when this advancement is granted.
         * </p>
         *
         * @param item the {@link ItemStack} to add as loot
         * @return this builder instance for chaining
         */
        Builder addLoot(final ItemStack item);

        /**
         * Adds an {@link ItemStack} of the specified {@link Material} to the loot rewards.
         * <p>
         * This is a convenience method that creates an {@link ItemStack} from the given {@link Material}
         * and adds it as a reward.
         * </p>
         *
         * @param material the {@link Material} to create an {@link ItemStack} from
         * @return this builder instance for chaining
         */
        default Builder addLoot(final Material material) {
            return addLoot(ItemStack.of(material));
        }

        /**
         * Adds an {@link ItemStack} to the loot rewards after applying modifications via a consumer.
         * <p>
         * This method allows modifications to be applied to the {@link ItemStack} before it is added.
         * The provided {@code consumer} can modify the item, for example, by setting enchantments,
         * custom names, or other metadata.
         * </p>
         *
         * @param item     the {@link ItemStack} to add as loot
         * @param consumer a {@link Consumer} that modifies the {@link ItemStack} before it is added
         * @return this builder instance for chaining
         */
        default Builder addLoot(final ItemStack item, final Consumer<ItemStack> consumer) {
            consumer.accept(item);
            return addLoot(item);
        }

        /**
         * Builds and returns the configured {@link CustomAdvancementRewards}.
         *
         * @return the built rewards object
         */
        CustomAdvancementRewards build();
    }
}