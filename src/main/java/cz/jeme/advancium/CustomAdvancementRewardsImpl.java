package cz.jeme.advancium;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NullMarked
final class CustomAdvancementRewardsImpl implements CustomAdvancementRewards {
    public static final CustomAdvancementRewardsImpl EMPTY = new CustomAdvancementRewardsImpl(new Builder());

    private final int experience;
    private final List<NamespacedKey> recipeKeys;
    private final List<NamespacedKey> lootTableKeys;
    /**
     * A {@link List} of {@link ItemStack}s that represents the concrete loot rewards of an advancement.
     * <p>
     * This list is package-private to optimize loot giving (defensive copies are not created).
     * </p>
     *
     * @see BaseCustomAdvancement.Builder#Builder(NamespacedKey)
     */
    @ApiStatus.Internal
    final List<ItemStack> loot;

    private CustomAdvancementRewardsImpl(final Builder builder) {
        experience = builder.experience;
        recipeKeys = Collections.unmodifiableList(builder.recipeKeys);
        lootTableKeys = Collections.unmodifiableList(builder.lootTableKeys);
        loot = builder.loot.stream()
                .map(ItemStack::clone) // defensive copy to ensure immutability
                .toList();
    }

    @Override
    public int experience() {
        return experience;
    }

    @Override
    public @Unmodifiable List<NamespacedKey> recipeKeys() {
        return recipeKeys;
    }

    @Override
    public @Unmodifiable List<NamespacedKey> lootTableKeys() {
        return lootTableKeys;
    }

    @Override
    public @Unmodifiable List<ItemStack> loot() {
        // defensive copy to ensure immutability
        return loot.stream().map(ItemStack::clone).toList();
    }

    static final class Builder implements CustomAdvancementRewards.Builder {
        private int experience = 0;
        private final List<NamespacedKey> recipeKeys = new ArrayList<>();
        private final List<NamespacedKey> lootTableKeys = new ArrayList<>();
        private final List<ItemStack> loot = new ArrayList<>();

        @Override
        public CustomAdvancementRewards.Builder experience(final int experience) {
            this.experience = experience;
            return this;
        }

        @Override
        public CustomAdvancementRewards.Builder addRecipe(final NamespacedKey key) {
            recipeKeys.add(key);
            return this;
        }

        @Override
        public CustomAdvancementRewards.Builder addLootTable(final NamespacedKey key) {
            lootTableKeys.add(key);
            return this;
        }

        @Override
        public CustomAdvancementRewards.Builder addLoot(final ItemStack item) {
            loot.add(item);
            return this;
        }

        @Override
        public CustomAdvancementRewards build() {
            return new CustomAdvancementRewardsImpl(this);
        }
    }
}
