package io.github.itstaylz.sakurarunes;

import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurarunes.runes.Rune;
import io.github.itstaylz.sakurarunes.runes.RuneManager;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Locale;

public class RuneListener implements Listener {

    private final JavaPlugin plugin;

    public RuneListener(JavaPlugin plugin) {
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
                    if (helmet != null && RuneManager.hasAppliedRune(helmet) && count == 5) {
                        Rune rune = RuneManager.getItemAppliedRune(helmet);
                        if (rune != null) {
                            Location loc = player.getLocation().add(0, 2.2, 0);
                            ParticleUtils.drawCircleParticles(loc, rune.particle(), 0.5, 20);
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
            final Location location = block.getLocation().add(0, 1, 0);
            new BukkitRunnable() {

                int count = 0;
                @Override
                public void run() {
                    if (count == 10) {
                        cancel();
                        return;
                    }
                    ParticleUtils.drawSquareParticles(location, rune.particle());
                    location.subtract(0, 0.1, 0);
                    count++;
                }
            }.runTaskTimer(this.plugin, 0L, 4L);
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
            if (clicked != null && cursor != null) {
                Rune rune = RuneManager.getRune(cursor);
                if (rune != null && rune.type().canBePlacedOn(clicked.getType())) {
                    event.setCancelled(true);
                    if (RuneManager.applyRuneToItem(clicked, rune)) {
                        player.setItemOnCursor(null);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                        player.sendMessage(StringUtils.colorize("&aRune applied successfully!"));
                    } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        player.sendMessage(StringUtils.colorize("&cThis rune cannot be applied to this item!"));
                    }
                }
            }
        }
    }
}
