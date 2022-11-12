package io.github.itstaylz.sakurarunes.runes;

import org.bukkit.Material;

public enum RuneType {

    CHESTPLATE,
    HELMET,
    BOOTS,
    SWORD,
    BOW,
    PICKAXE,
    AXE,
    SHOVEL,
    ELYTRA;

    public boolean canBePlacedOn(Material material) {
        return material.name().contains(this.name());
    }

}
