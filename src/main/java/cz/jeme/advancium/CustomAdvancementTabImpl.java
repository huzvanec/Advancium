package cz.jeme.advancium;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

final class CustomAdvancementTabImpl implements CustomAdvancementTab {
    private final Plugin plugin;
    private final CustomAdvancement root;
    private final NamespacedKey background;

    private CustomAdvancementTabImpl(final Builder builder) {
        plugin = builder.plugin;
        background = builder.background;
        root = ((BaseCustomAdvancement.Builder) Objects.requireNonNull(
                builder.rootBuilder,
                "You must specify root advancement when creating an advancement tab"
        )).buildRoot(this);
    }

    @Override
    public CustomAdvancement root() {
        return root;
    }

    @Override
    public NamespacedKey background() {
        return background;
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public NamespacedKey key() {
        return root.key();
    }

    static final class Builder implements CustomAdvancementTab.Builder {
        private static final NamespacedKey DEFAULT_BACKGROUND = NamespacedKey.minecraft("textures/gui/advancements/backgrounds/stone.png");

        private final Plugin plugin;
        private NamespacedKey background = DEFAULT_BACKGROUND;
        private @Nullable CustomAdvancement.Builder rootBuilder;

        public Builder(final Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public CustomAdvancementTab.Builder root(final CustomAdvancement.Builder builder) {
            this.rootBuilder = builder;
            return this;
        }

        @Override
        public CustomAdvancementTab.Builder background(final NamespacedKey background) {
            this.background = background;
            return this;
        }

        @Override
        public CustomAdvancementTab buildAndLoad() {
            return new CustomAdvancementTabImpl(this);
        }
    }
}
