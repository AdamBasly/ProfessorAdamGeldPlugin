package net.professoradamgeldplugin;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CommandMoney implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
            int coins = Main.kontostand.getOrDefault(p.getUniqueId(), 0);
            p.sendMessage("§aDu hast " + coins + " Coins.");
        }
        return true;
    }
}