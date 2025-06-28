package net.professoradamgeldplugin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;

public class CommandOpEnchant implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§7Benutzung: /openchant <enchantment> <level>");
            return true;
        }

        try {
            Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());
            if (enchantment == null) {
                player.sendMessage("§cUngültiges Verzauberung: " + args[0]);
                return true;
            }

            int level = Integer.parseInt(args[1]);
            int price = level * 2000; // Example: 2000 coins per level

            if (!Main.hasEnoughCoins(player.getUniqueId(), price)) {
                player.sendMessage("§cDu hast nicht genug Coins. Preis: " + price);
                return true;
            }

            Main.ziehCoins(player.getUniqueId(), price);

            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = book.getItemMeta();
            if (meta != null) {
                meta.addEnchant(enchantment, level, true);
                book.setItemMeta(meta);
            }

            player.getInventory().addItem(book);
            player.sendMessage("§aGekauft: " + enchantment.getKey().getKey() + " " + level + " für " + price + " Coins.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cLevel muss eine ganze Zahl sein.");
        }

        return true;
    }
}