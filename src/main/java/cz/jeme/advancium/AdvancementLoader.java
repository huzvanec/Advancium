package cz.jeme.advancium;

import com.google.gson.Gson;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.advancement.Advancement;

import java.util.Map;
import java.util.stream.Collectors;

enum AdvancementLoader {
    INSTANCE;

    @SuppressWarnings("deprecation")
    private final UnsafeValues unsafe = Bukkit.getUnsafe();
    private final JSONComponentSerializer serializer = JSONComponentSerializer.json();
    private final Gson gson = new Gson();

    public Advancement load(final CustomAdvancement advancement) {

        final boolean root = advancement.isRoot();

        final CustomAdvancementDisplay display = advancement.display();
        final CustomAdvancementRewards rewards = advancement.rewards();

        try {
            final String criteria = gson.toJson(
                    advancement.criteria().stream()
                            .collect(Collectors.toMap(
                                    key -> key,
                                    key -> Map.of("trigger", "minecraft:impossible")
                            ))
            );
            final String requirements = gson.toJson(advancement.requirements());

            final String recipes = gson.toJson(
                    rewards.recipeKeys().stream().map(NamespacedKey::asString).toList()
            );

            final String loot = gson.toJson(
                    rewards.lootTableKeys().stream().map(NamespacedKey::asString).toList()
            );

            final String json = """
                    {
                        "parent": %s,
                        "display": {
                            "icon": %s,
                            "title": %s,
                            "description": %s,
                            "frame": "%s",
                            "background": "%s",
                            "show_toast": %b,
                            "announce_to_chat": %b,
                            "hidden": %b
                        },
                        "criteria": %s,
                        "requirements": %s,
                        "rewards": {
                            "experience": %d,
                            "recipes": %s,
                            "loot": %s
                        }
                    }
                    """
                    .formatted(
                            root ? null : '"' + advancement.parentKey().asString() + '"',
                            unsafe.serializeItemAsJson(display.icon()).toString(),
                            serializer.serialize(display.title()),
                            serializer.serialize(display.description()),
                            display.frame().id(),
                            root ? advancement.tab().background() : null,
                            display.showToast(),
                            display.announceToChat(),
                            display.hidden(),
                            criteria,
                            requirements,
                            rewards.experience(),
                            recipes,
                            loot
                    );

            return unsafe.loadAdvancement(
                    advancement.key(),
                    json
            );
        } catch (final Exception e) {
            throw new RuntimeException("Failed to load advancement: \"" + advancement.key() + "\"", e);
        }
    }
}
