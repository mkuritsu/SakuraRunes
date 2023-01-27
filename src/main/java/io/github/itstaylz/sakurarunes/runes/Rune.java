package io.github.itstaylz.sakurarunes.runes;

import io.github.itstaylz.hexlib.items.SkullBuilder;
import io.github.itstaylz.hexlib.utils.PDCUtils;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurarunes.RuneManager;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public record Rune(String id, String displayName, RuneRarity rarity, RuneType type, Particle particle, List<String> lore, String headURL) {

    public ItemStack itemStack() {
        String typeString = type.name().toLowerCase();
        char firstLetter = typeString.charAt(0);
        typeString = typeString.replaceFirst(firstLetter + "", Character.toUpperCase(firstLetter) + "");
        ItemStack item = new SkullBuilder()
                .setSkinFromURL(this.headURL)
                .setDisplayName(this.displayName)
                .addLore(StringUtils.colorize("&8" + typeString), "")
                .addLore(this.lore.toArray(new String[0]))
                .addLore("", StringUtils.colorize("&7Rarity: " + rarity.getDisplayName()))
                .build();
        PDCUtils.setPDCValue(item, RuneManager.RUNE_KEY, PersistentDataType.STRING, this.id);
        return item;
    }

}
