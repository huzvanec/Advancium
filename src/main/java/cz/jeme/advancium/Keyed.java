package cz.jeme.advancium;

import org.bukkit.NamespacedKey;

/**
 * Something that has an associated {@link NamespacedKey}.
 * <p>
 * This is different from {@link org.bukkit.Keyed} by following the Java record naming convention.
 * </p>
 *
 * @see NamespacedKey
 * @see org.bukkit.Keyed
 * @see net.kyori.adventure.key.Keyed
 */
public interface Keyed extends net.kyori.adventure.key.Keyed {
    @Override
    NamespacedKey key();
}