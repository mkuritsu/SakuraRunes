package io.github.itstaylz.sakurarunes;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurarunes.runes.Rune;
import io.github.itstaylz.sakurarunes.utils.ParticleUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class RuneListener implements Listener {

    private final SakuraRunesPlugin plugin;

    public RuneListener(SakuraRunesPlugin plugin) {
        this.plugin = plugin;
        startRunnable();
    }

    private void startRunnable() {
        new BukkitRunnable() {

            int count = 0;
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack helmet = player.getInventory().getHelmet();
                    ItemStack chestplate = player.getInventory().getChestplate();
                    ItemStack boots = player.getInventory().getBoots();
                    if (helmet != null && RuneManager.hasAppliedRune(helmet)) {
                        Rune rune = RuneManager.getItemAppliedRune(helmet);
                        if (rune != null) {
                            Location loc = player.getLocation().add(0, 2.2, 0);
                            player.getWorld().spawnParticle(rune.particle(), loc, 2, 0, 0, 0, 0.025);
                        }
                    }
                    if (chestplate != null && RuneManager.hasAppliedRune(chestplate)) {
                        Rune rune = RuneManager.getItemAppliedRune(chestplate);
                        if (rune != null) {
                            Location loc = player.getLocation().add(0, 1.2, 0);
                            player.getWorld().spawnParticle(rune.particle(), loc, 3, 0, 0, 0, 0.025);
                        }
                    }
                    if (boots != null && RuneManager.hasAppliedRune(boots)) {
                        Rune rune = RuneManager.getItemAppliedRune(boots);
                        if (rune != null) {
                            Location loc = player.getLocation();
                            player.getWorld().spawnParticle(rune.particle(), loc, 3, 0, 0, 0, 0.01);
                        }
                    }
                }
                if (count == 5)
                    count = -1;
                count++;
            }
        }.runTaskTimer(this.plugin, 0L, 2L);
    }

    // SWORD RUNE
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onKill(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        EntityDamageEvent damageEvent = victim.getLastDamageCause();
        if (damageEvent instanceof EntityDamageByEntityEvent entityDamageEvent
                && entityDamageEvent.getDamager() instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().name().contains("SWORD")) {
                Rune rune = RuneManager.getItemAppliedRune(item);
                if (rune != null ) {
                    double yHalf = (victim.getEyeLocation().getY() - victim.getLocation().getY()) / 2;
                    Location location = victim.getLocation().add(0, yHalf, 0);
                    victim.getWorld().spawnParticle(rune.particle(), location, 25, 0, 0, 0, 0.3);
                }
            }
        }
    }

    // TOOLS RUNE
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        Rune rune = RuneManager.getItemAppliedRune(item);
        if (rune != null) {
            Location location = block.getLocation().add(0.5, 0.5, 0.5); // MIDDLE
            ParticleUtils.drawBlockBreakParticles(location, rune.particle());
        }
    }

    // BOW RUNES
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack item = event.getBow();
            Rune rune = RuneManager.getItemAppliedRune(item);
            if (rune != null) {
                Entity arrow = event.getProjectile();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        arrow.getWorld().spawnParticle(rune.particle(), arrow.getLocation(), 3, 0, 0, 0, 0.03);
                        if (arrow.isOnGround() || arrow.isDead())
                            cancel();
                    }
                }.runTaskTimer(this.plugin, 0L, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && player.getInventory().getViewers().contains(player) && player.getGameMode() != GameMode.CREATIVE) {
            ItemStack cursor = event.getCursor();
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && cursor != null && clicked.getType() != Material.AIR) {
                Rune rune = RuneManager.getRune(cursor);
                if (rune != null) {
                    event.setCancelled(true);
                    if (rune.type().canBePlacedOn(clicked.getType())) {
                        if (!RuneManager.hasAppliedRune(clicked)) {
                            player.setItemOnCursor(null);
                            RuneManager.applyRuneToItem(clicked, rune);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                            player.sendMessage(StringUtils.colorize(this.plugin.getMessagesFile().getConfig().getString("blessing_applied_message")));
                        } else {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                            player.sendMessage(StringUtils.colorize(this.plugin.getMessagesFile().getConfig().getString("blessing_already_applied_message")));
                        }
                    } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        player.sendMessage(StringUtils.colorize(this.plugin.getMessagesFile().getConfig().getString("blessing_invalid_item_message")));
                    }
                }
            }
        }
    }
}
