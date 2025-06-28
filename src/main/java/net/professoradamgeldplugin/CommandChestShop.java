package net.professoradamgeldplugin;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class CommandChestShop implements CommandExecutor {

    private final HashMap<UUID, ChestShop> pendingShops = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§7Benutzung: /chestshop <preis> <item>");
            return true;
        }

        try {
            int preis = Integer.parseInt(args[0]);
            Material item = Material.matchMaterial(args[1].toUpperCase());
            if (item == null) {
                player.sendMessage("§cUngültiges Item: " + args[1]);
                return true;
            }

            // Add the shop to pending shops
            pendingShops.put(player.getUniqueId(), new ChestShop(player.getUniqueId(), null, preis, item));
            player.sendMessage("§aRechtsklicke innerhalb von 20 Sekunden auf eine Kiste, um den ChestShop zu erstellen!");

            // Remove the pending shop after 20 seconds
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (pendingShops.containsKey(player.getUniqueId())) {
                    pendingShops.remove(player.getUniqueId());
                    player.sendMessage("§cZeit abgelaufen! ChestShop wurde nicht erstellt.");
                }
            }, 20 * 20); // 20 seconds
        } catch (NumberFormatException e) {
            player.sendMessage("§cPreis muss eine ganze Zahl sein.");
        }

        return true;
    }

    public HashMap<UUID, ChestShop> getPendingShops() {
        return pendingShops;
    }
}