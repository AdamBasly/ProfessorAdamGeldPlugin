package net.professoradamgeldplugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class ATMListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.EMERALD_BLOCK) return; // Define ATM block type

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Withdraw money as cash
            int balance = Main.getBalance(player.getUniqueId());
            if (balance <= 0) {
                player.sendMessage("§cDu hast kein Geld auf deinem Konto.");
                return;
            }

            int amountToWithdraw = Math.min(balance, 100); // Example: Withdraw up to 100 coins at a time
            Main.withdraw(player.getUniqueId(), amountToWithdraw);
            player.getInventory().addItem(Main.createCashItem(amountToWithdraw));
            player.sendMessage("§aDu hast " + amountToWithdraw + " Coins als Bargeld abgehoben.");
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Deposit cash into the bank
            int cashAmount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (Main.isValidCashItem(item)) {
                    cashAmount += item.getAmount();
                    player.getInventory().remove(item);
                } else if (item != null && item.getType() == Main.CASH_ITEM) {
                    player.sendMessage("§cUngültiges Bargeld gefunden und entfernt.");
                    player.getInventory().remove(item);
                }
            }

            if (cashAmount <= 0) {
                player.sendMessage("§cDu hast kein gültiges Bargeld.");
                return;
            }

            Main.deposit(player.getUniqueId(), cashAmount);
            player.sendMessage("§aDu hast " + cashAmount + " Coins auf dein Konto eingezahlt.");
        }
    }
}