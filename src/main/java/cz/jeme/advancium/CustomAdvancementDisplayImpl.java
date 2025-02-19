package cz.jeme.advancium;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

final class CustomAdvancementDisplayImpl implements CustomAdvancementDisplay {
    public static final CustomAdvancementDisplayImpl EMPTY = new CustomAdvancementDisplayImpl(new Builder());

    private final ItemStack icon;
    private final Component title;
    private final Component description;
    private final Frame frame;
    private final boolean showToast;
    private final boolean announceToChat;
    private final boolean hidden;

    private CustomAdvancementDisplayImpl(final Builder builder) {
        icon = builder.icon.clone();
        title = builder.title;
        description = builder.description;
        frame = builder.frame;
        showToast = builder.showToast;
        announceToChat = builder.announceToChat;
        hidden = builder.hidden;
    }

    @Override
    public ItemStack icon() {
        return icon.clone();
    }

    @Override
    public Component title() {
        return title;
    }

    @Override
    public Component description() {
        return description;
    }

    @Override
    public Frame frame() {
        return frame;
    }

    @Override
    public boolean showToast() {
        return showToast;
    }

    @Override
    public boolean announceToChat() {
        return announceToChat;
    }

    @Override
    public boolean hidden() {
        return hidden;
    }

    static final class Builder implements CustomAdvancementDisplay.Builder {
        private static final ItemStack DEFAULT_ICON = ItemStack.of(Material.GRASS_BLOCK);

        private ItemStack icon = DEFAULT_ICON;
        private Component title = Component.empty();
        private Component description = Component.empty();
        private Frame frame = Frame.TASK;
        private boolean showToast = true;
        private boolean announceToChat = true;
        private boolean hidden = false;

        @Override
        public CustomAdvancementDisplay.Builder icon(final ItemStack icon) {
            this.icon = icon;
            return this;
        }

        @Override
        public CustomAdvancementDisplay.Builder title(final Component title) {
            this.title = title;
            return this;
        }

        @Override
        public CustomAdvancementDisplay.Builder description(final Component description) {
            this.description = description;
            return this;
        }

        @Override
        public CustomAdvancementDisplay.Builder frame(final Frame frame) {
            this.frame = frame;
            return this;
        }

        @Override
        public CustomAdvancementDisplay.Builder showToast(final boolean showToast) {
            this.showToast = showToast;
            return this;
        }

        @Override
        public CustomAdvancementDisplay.Builder announceToChat(final boolean announceToChat) {
            this.announceToChat = announceToChat;
            return this;
        }

        @Override
        public CustomAdvancementDisplay.Builder hidden(final boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        @Override
        public CustomAdvancementDisplay build() {
            return new CustomAdvancementDisplayImpl(this);
        }
    }
}
