package net.professoradamgeldplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestShopListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.CHEST) return;

        CommandChestShop commandChestShop = (CommandChestShop) Main.getInstance().getCommand("chestshop").getExecutor();
        ChestShop pendingShop = commandChestShop.getPendingShops().get(player.getUniqueId());

        // Handle chest shop creation
        if (pendingShop != null) {
            Location loc = block.getLocation();
            pendingShop.setLocation(loc);
            Main.shops.put(loc, pendingShop);

            // Summon text entity above the chest
            String summonCmd = String.format(
                "summon minecraft:text_display %.1f %.1f %.1f {text:'\"Preis: %d Coins\"',billboard:\"center\",see_through:0b}",
                loc.getX() + 0.5, loc.getY() + 1.5, loc.getZ() + 0.5, pendingShop.getPreis()
            );
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), summonCmd);

            player.sendMessage("§aChestShop erstellt! Preis: §6" + pendingShop.getPreis() + " Coins §7- Item: §e" + pendingShop.getItem().name());
            commandChestShop.getPendingShops().remove(player.getUniqueId());
            return;
        }

        // Handle chest shop interaction
        ChestShop shop = Main.shops.get(block.getLocation());
        if (shop == null) return;

        if (player.getUniqueId().equals(shop.getOwner())) {
            player.sendMessage("§aDu bist der Besitzer dieses ChestShops.");
            return;
        }

        if (!Main.hasEnoughCoins(player.getUniqueId(), shop.getPreis())) {
            player.sendMessage("§cNicht genug Coins. Preis: " + shop.getPreis());
            return;
        }

        // Process the transaction
        Main.ziehCoins(player.getUniqueId(), shop.getPreis());
        Main.gibCoins(shop.getOwner(), shop.getPreis());

        // Remove one item from the chest
        if (block.getState() instanceof org.bukkit.block.Chest chest) {
            if (chest.getInventory().contains(shop.getItem())) {
                chest.getInventory().removeItem(new org.bukkit.inventory.ItemStack(shop.getItem(), 1));
                player.getInventory().addItem(new org.bukkit.inventory.ItemStack(shop.getItem(), 1));
                player.sendMessage("§aGekauft für §e" + shop.getPreis() + " Coins.");
            } else {
                player.sendMessage("§cDieser ChestShop hat keine passenden Items.");
            }
        }
    }
}