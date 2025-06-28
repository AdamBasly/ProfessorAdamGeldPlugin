package net.professoradamgeldplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CommandGrundstückInfo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        Location loc = player.getLocation().getBlock().getLocation();
        for (Grundstück gs : Main.gsList) {
            if (gs.contains(loc)) {
                String besitzer = Bukkit.getOfflinePlayer(gs.owner).getName();
                int seite = gs.radius * 2 + 1;
                player.sendMessage("§eGrundstück gefunden!");
                player.sendMessage("§7Besitzer: §a" + besitzer);
                player.sendMessage("§7Mittelpunkt: §a" + format(gs.center));
                player.sendMessage("§7Größe: §a" + seite + "x" + seite);
                return true;
            }
        }

        player.sendMessage("§cDu stehst auf keinem Grundstück.");
        return true;
    }

    private String format(Location loc) {
        return "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ();
    }
}