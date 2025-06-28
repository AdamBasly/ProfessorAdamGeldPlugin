package net.professoradamgeldplugin;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class CommandPay implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p) || args.length != 2) return false;

        Player ziel = Bukkit.getPlayerExact(args[0]);
        int betrag;

        try { betrag = Integer.parseInt(args[1]); }
        catch (NumberFormatException e) {
            p.sendMessage("§cUngültiger Betrag.");
            return true;
        }

        if (ziel != null && !ziel.equals(p)) {
            if (Main.ziehCoins(p.getUniqueId(), betrag)) {
                Main.gibCoins(ziel.getUniqueId(), betrag);
                p.sendMessage("§aDu hast " + betrag + " Coins an " + ziel.getName() + " gesendet.");
                ziel.sendMessage("§aDu hast " + betrag + " Coins von " + p.getName() + " erhalten.");
            } else {
                p.sendMessage("§cNicht genug Coins.");
            }
        } else {
            p.sendMessage("§cSpieler nicht gefunden.");
        }
        return true;
    }
}