package cz.jeme.advancium;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class BaseCustomAdvancement extends AbstractCustomAdvancement {
    private final CustomAdvancement parent;
    private final CustomAdvancementTab tab;

    @ApiStatus.Internal
    BaseCustomAdvancement(final Builder builder, final CustomAdvancement parent) {
        super(builder, parent.plugin());

        this.parent = parent;
        this.tab = parent.tab();
    }

    @Override
    public CustomAdvancementTab tab() {
        return tab;
    }

    @Override
    public CustomAdvancement parent() {
        return parent;
    }

    @Override
    public NamespacedKey parentKey() {
        return parent.key();
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean hasCustomParent() {
        return true;
    }

    @Override
    public boolean hasCustomTab() {
        return true;
    }
}
