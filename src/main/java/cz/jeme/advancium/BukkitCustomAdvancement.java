package cz.jeme.advancium;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class BukkitCustomAdvancement extends AbstractCustomAdvancement {
    private final NamespacedKey parentKey;

    @ApiStatus.Internal
    BukkitCustomAdvancement(final Builder builder,
                            final NamespacedKey parentKey,
                            final Plugin plugin) {
        super(builder, plugin);

        this.parentKey = parentKey;
    }

    @Override
    public CustomAdvancementTab tab() {
        throw new UnsupportedOperationException(
                "Advancement with a Bukkit parent does not belong to a custom advancement tab"
        );
    }

    @Override
    public CustomAdvancement parent() {
        throw new UnsupportedOperationException(
                "Advancement with a Bukkit parent does not have a custom advancement parent"
        );
    }

    @Override
    public NamespacedKey parentKey() {
        return parentKey;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean hasCustomParent() {
        return false;
    }

    @Override
    public boolean hasCustomTab() {
        return false;
    }
}
