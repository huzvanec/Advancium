package cz.jeme.programu.advancium;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
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
    private final List<NamespacedKey> lootKeys;

    private CustomAdvancementRewardsImpl(final Builder builder) {
        experience = builder.experience;
        recipeKeys = Collections.unmodifiableList(builder.recipeKeys);
        lootKeys = Collections.unmodifiableList(builder.lootKeys);
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
        return lootKeys;
    }

    static final class Builder implements CustomAdvancementRewards.Builder {
        private int experience = 0;
        private final List<NamespacedKey> recipeKeys = new ArrayList<>();
        private final List<NamespacedKey> lootKeys = new ArrayList<>();

        @Override
        public CustomAdvancementRewards.Builder experience(final int experience) {
            this.experience = experience;
            return this;
        }

        @Override
        public CustomAdvancementRewards.Builder addRecipe(final @NotNull NamespacedKey key) {
            recipeKeys.add(key);
            return this;
        }

        @Override
        public CustomAdvancementRewards.Builder addLootTable(final NamespacedKey key) {
            lootKeys.add(key);
            return this;
        }

        @Override
        public CustomAdvancementRewards build() {
            return new CustomAdvancementRewardsImpl(this);
        }
    }
}
