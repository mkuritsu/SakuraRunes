package io.github.itstaylz.sakurarunes.runes;

import io.github.itstaylz.hexlib.items.ItemBuilder;
import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.hexlib.utils.ItemUtils;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurarunes.SakuraRunesPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class RuneManager {

    private static final JavaPlugin PLUGIN;
    public static final NamespacedKey RUNE_KEY;
    private static final NamespacedKey APPLIED_RUNE_KEY;

    static {
        PLUGIN = JavaPlugin.getPlugin(SakuraRunesPlugin.class);
        RUNE_KEY = new NamespacedKey(PLUGIN, "rune");
        APPLIED_RUNE_KEY = new NamespacedKey(PLUGIN, "applied_rune");
    }

    private static final HashMap<String, Rune> RUNES_REGISTRY = new HashMap<>();

    public static void registerRune(Rune rune) {
        RUNES_REGISTRY.put(rune.id(), rune);
    }

    public static void loadRunes() {
        RUNES_REGISTRY.clear();
        YamlFile config = new YamlFile(new File(PLUGIN.getDataFolder(), "config.yml"));
        ConfigurationSection section = config.getSection("runes");
        if (section == null) {
            PLUGIN.getLogger().severe("No runes loaded!");
            return;
        }
        Set<String> keys = section.getKeys(false);
        for (String key : keys) {
            String id = key;
            key = "runes." + key;
            String name = StringUtils.fullColorize(config.get(key + ".display_name", String.class));
            RuneRarity rarity = RuneRarity.valueOf(config.get(key + ".rarity", String.class));
            RuneType type = RuneType.valueOf(config.get(key + ".type", String.class));
            Particle particle = Particle.valueOf(config.get(key + ".particle", String.class));
            List<String> uncoloredLore = config.getConfig().getStringList(key + ".lore");
            List<String> lore = new ArrayList<>();
            for (String line : uncoloredLore) {
                lore.add(StringUtils.fullColorize(line));
            }
            String headURL = config.get(key + ".head_skin", String.class);
            Rune rune = new Rune(id, name, rarity, type, particle, lore, headURL);
            registerRune(rune);
        }
    }

    public static Rune getRuneFromID(String runeID) {
        return RUNES_REGISTRY.get(runeID);
    }

    public static Rune getRune(ItemStack item) {
        String runeID = ItemUtils.getPDCValue(item, RUNE_KEY, PersistentDataType.STRING);
        return runeID == null ? null : RUNES_REGISTRY.get(runeID);
    }

    public static boolean isRune(ItemStack item) {
        return getRune(item) != null;
    }

    public static Rune getItemAppliedRune(ItemStack item) {
        String runeID = ItemUtils.getPDCValue(item, APPLIED_RUNE_KEY, PersistentDataType.STRING);
        return runeID == null ? null : RUNES_REGISTRY.get(runeID);
    }

    public static boolean hasAppliedRune(ItemStack item) {
        return getItemAppliedRune(item) != null;
    }

    public static boolean applyRuneToItem(ItemStack item, Rune rune) {
        if (!hasAppliedRune(item) && rune.type().canBePlacedOn(item.getType())) {
            ItemUtils.setPDCValue(item, APPLIED_RUNE_KEY, PersistentDataType.STRING, rune.id());
            new ItemBuilder(item)
                    .addLore("", rune.displayName(), "")
                    .build();
            return true;
        }
        return false;
    }

    public static Collection<Rune> getAllRunes() {
        return RUNES_REGISTRY.values();
    }
}
