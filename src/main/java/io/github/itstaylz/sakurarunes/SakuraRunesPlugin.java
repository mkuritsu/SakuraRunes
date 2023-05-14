package io.github.itstaylz.sakurarunes;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurarunes.runes.Rune;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class SakuraRunesPlugin extends JavaPlugin implements CommandExecutor {

    private final List<String> runesIDs = new ArrayList<>();
    private YamlFile messagesFile;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        this.messagesFile = new YamlFile(new File(getDataFolder(), "messages.yml"));
        RuneManager.loadRunes();
        for (Rune rune : RuneManager.getAllRunes()) {
            runesIDs.add(rune.id());
        }
        Bukkit.getPluginManager().registerEvents(new RuneListener(this), this);
        getCommand("runesmenu").setExecutor(this);
        getCommand("giverune").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("runesmenu")) {
            if (sender instanceof Player player) {
                new RunesMenu().open(player);
            } else {
                sender.sendMessage(StringUtils.colorize("&cThis command can only be used by players!"));
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("giverune")) {
            if (args.length != 2) {
                sender.sendMessage(StringUtils.colorize("&cUse /" + label + " <player> <rune id>"));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(StringUtils.colorize("&cPlayer not found!"));
                return true;
            }
            String runeID = args[1];
            Rune rune = RuneManager.getRuneFromID(runeID);
            if (rune == null) {
                sender.sendMessage(StringUtils.colorize("&cThat rune does not exist!"));
                return true;
            }
            target.getInventory().addItem(rune.itemStack());
            return true;
        } else if (command.getName().equalsIgnoreCase("runesreload")) {
            RuneManager.loadRunes();
            this.messagesFile.reload();
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("giverune") && args.length == 2)
            return runesIDs;
        return super.onTabComplete(sender, command, alias, args);
    }

    public YamlFile getMessagesFile() {
        return messagesFile;
    }
}
