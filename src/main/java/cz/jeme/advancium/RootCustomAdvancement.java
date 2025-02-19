package cz.jeme.advancium;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class RootCustomAdvancement extends AbstractCustomAdvancement {
    private final CustomAdvancementTab tab;

    @ApiStatus.Internal
    RootCustomAdvancement(final Builder builder, final CustomAdvancementTab tab) {
        super(builder, tab.plugin());

        this.tab = tab;
    }

    @Override
    public CustomAdvancementTab tab() {
        return tab;
    }

    @Override
    public CustomAdvancement parent() {
        throw new UnsupportedOperationException("Root advancement has no parent");
    }

    @Override
    public NamespacedKey parentKey() {
        throw new UnsupportedOperationException("Root advancement has no parent");
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean hasCustomParent() {
        return false;
    }

    @Override
    public boolean hasCustomTab() {
        return true;
    }
}
