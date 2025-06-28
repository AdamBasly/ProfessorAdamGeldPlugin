package net.professoradamgeldplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ChestShop {
    private final UUID owner;
    private Location location;
    private final int preis;
    private final Material item;

    public ChestShop(UUID owner, Location location, int preis, Material item) {
        this.owner = owner;
        this.location = location;
        this.preis = preis;
        this.item = item;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getPreis() {
        return preis;
    }

    public Material getItem() {
        return item;
    }

    public static boolean exchangeGold(Player player, boolean toGold) {
        UUID playerUUID = player.getUniqueId();

        if (toGold) {
            int goldPrice = 60; // Example price for 1 gold ingot
            if (!Main.hasEnoughCoins(playerUUID, goldPrice)) {
                player.sendMessage("§cDu hast nicht genug Coins, um Gold zu kaufen.");
                return false;
            }
            Main.ziehCoins(playerUUID, goldPrice);
            player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
            player.sendMessage("§a1x Gold gekauft für " + goldPrice + " Coins.");
            return true;
        } else {
            if (!player.getInventory().contains(Material.GOLD_INGOT)) {
                player.sendMessage("§cDu hast kein Gold, um es zu verkaufen.");
                return false;
            }
            int goldSellPrice = 50; // Example sell price for 1 gold ingot
            player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 1));
            Main.gibCoins(playerUUID, goldSellPrice);
            player.sendMessage("§a1x Gold verkauft für " + goldSellPrice + " Coins.");
            return true;
        }
    }

    public static void updateGoldValue() {
        int newGoldPrice = 50 + (int) (Math.random() * 21); // Random price between 50 and 70
        Main.setGoldPrice(newGoldPrice);
        System.out.println("Goldpreis aktualisiert: " + newGoldPrice + " Coins");
    }
}