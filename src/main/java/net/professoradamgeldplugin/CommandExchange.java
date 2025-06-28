package net.professoradamgeldplugin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandExchange implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen.");
            return true;
        }

        if (args.length != 1 || (!args[0].equalsIgnoreCase("gold") && !args[0].equalsIgnoreCase("money"))) {
            player.sendMessage("§7Benutzung: /exchange <gold|money>");
            return true;
        }

        boolean toGold = args[0].equalsIgnoreCase("gold");
        int goldPrice = Main.getGoldPrice();

        if (toGold) {
            if (!Main.hasEnoughCoins(player.getUniqueId(), goldPrice)) {
                player.sendMessage("§cDu hast nicht genug Coins, um Gold zu kaufen. Preis: " + goldPrice);
                return true;
            }
            Main.ziehCoins(player.getUniqueId(), goldPrice);
            player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
            player.sendMessage("§a1x Gold gekauft für " + goldPrice + " Coins.");
        } else {
            if (!player.getInventory().contains(Material.GOLD_INGOT)) {
                player.sendMessage("§cDu hast kein Gold, um es zu verkaufen.");
                return true;
            }
            player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 1));
            Main.gibCoins(player.getUniqueId(), goldPrice);
            player.sendMessage("§a1x Gold verkauft für " + goldPrice + " Coins.");
        }

        return true;
    }
}