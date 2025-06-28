package net.professoradamgeldplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class CommandGrundstückShow implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        Location loc = player.getLocation();

        for (Grundstück gs : Main.gsList) {
            if (gs.contains(loc)) {
                int r = gs.radius;
                Location center = gs.center.getBlock().getLocation();
                int x1 = center.getBlockX() - r;
                int x2 = center.getBlockX() + r;
                int z1 = center.getBlockZ() - r;
                int z2 = center.getBlockZ() + r;
                int y = center.getBlockY() + 1;

                // 4 fill-Befehle: oben, unten, links, rechts
                String[] fills = {
                    fillCmd(x1, y, z1, x2, y, z1), // Nordkante
                    fillCmd(x1, y, z2, x2, y, z2), // Südkante
                    fillCmd(x1, y, z1 + 1, x1, y, z2 - 1), // Westkante
                    fillCmd(x2, y, z1 + 1, x2, y, z2 - 1)  // Ostkante
                };

                for (String cmd : fills) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }

                player.sendMessage("§aGrundstücksgrenze wurde mit Steinziegeln markiert.");

                // Automatisch nach 10 Sekunden wieder entfernen
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (String cmd : fills) {
                            String undo = cmd.replace("minecraft:stone_bricks", "minecraft:air");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), undo);
                        }
                    }
                }.runTaskLater(Main.getInstance(), 20 * 10); // 10 Sekunden Delay

                return true;
            }
        }

        player.sendMessage("§cDu stehst auf keinem Grundstück.");
        return true;
    }

    private String fillCmd(int x1, int y1, int z1, int x2, int y2, int z2) {
        return String.format(
            Locale.US,
            "fill %d %d %d %d %d %d minecraft:stone_bricks replace",
            x1, y1, z1, x2, y2, z2
        );
    }
}