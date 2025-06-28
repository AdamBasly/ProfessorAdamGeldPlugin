package net.professoradamgeldplugin;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Check if the player is joining for the first time
        if (!Main.kontostand.containsKey(playerUUID)) {
            // Add the player to the kontostand map (to track them)
            Main.kontostand.put(playerUUID, 0);

            // Create iron armor with Unbreaking III enchantment
            ItemStack helmet = new ItemStack(Material.IRON_HELMET);
            helmet.addEnchantment(Enchantment.DURABILITY, 3);

            ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
            chestplate.addEnchantment(Enchantment.DURABILITY, 3);

            ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
            leggings.addEnchantment(Enchantment.DURABILITY, 3);

            ItemStack boots = new ItemStack(Material.IRON_BOOTS);
            boots.addEnchantment(Enchantment.DURABILITY, 3);

            // Give the armor to the player
            event.getPlayer().getInventory().addItem(helmet, chestplate, leggings, boots);

            // Send a welcome message
            event.getPlayer().sendMessage("§aWillkommen! Du hast ein Set Eisenrüstung mit Haltbarkeit III erhalten.");
        }
    }
}