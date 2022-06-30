package io.github.itstaylz.sakurarunes;

import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurarunes.runes.Rune;
import io.github.itstaylz.sakurarunes.runes.RuneManager;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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

public class RuneListener implements Listener {

    private final JavaPlugin plugin;

    public RuneListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    // ARMOR RUNE
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onMove(PlayerMoveEvent event) {

    }

    // SWORD RUNE
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onKill(EntityDeathEvent event) {
        Entity victim = event.getEntity();
        EntityDamageEvent damageEvent = victim.getLastDamageCause();
        if (damageEvent instanceof EntityDamageByEntityEvent entityDamageEvent && entityDamageEvent.getDamager() instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().name().contains("SWORD")) {
                Rune rune = RuneManager.getItemAppliedRune(item);
                if (rune != null ) {
                    // TODO PARTICLE CODE
                    victim.getWorld().spawnParticle(rune.particle(), victim.getLocation(), 100);
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
            // TODO PARTICLE CODE
            block.getWorld().spawnParticle(rune.particle(), block.getLocation(), 100);
        }
    }

    // BOW RUNES
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack item = event.getBow();
            Rune rune = RuneManager.getItemAppliedRune(item);
            if (rune != null) {
                Entity arrow = event.getProjectile();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // TODO PARTICLE CODE
                        arrow.getWorld().spawnParticle(rune.particle(), arrow.getLocation(), 10);
                        if (arrow.isOnGround() || arrow.isDead())
                            cancel();
                    }
                }.runTaskTimer(this.plugin, 0L, 2L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && player.getInventory().getViewers().contains(player)) {
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
