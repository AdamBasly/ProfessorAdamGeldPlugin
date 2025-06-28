package net.professoradamgeldplugin;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CommandGrundstück implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length != 2 || !args[0].equalsIgnoreCase("radius")) {
            player.sendMessage("§7Benutzung: /gs radius <Zahl>");
            return true;
        }

        int radius;
        try {
            radius = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cRadius muss eine ganze Zahl sein.");
            return true;
        }

        int seite = radius * 2 + 1;
        int fläche = seite * seite;
        int kosten = fläche * 10;

        if (!Main.hasEnoughCoins(player.getUniqueId(), kosten)) {
            player.sendMessage("§cNicht genug Coins. Preis: " + kosten);
            return true;
        }

        Main.ziehCoins(player.getUniqueId(), kosten);

        Location center = player.getLocation().getBlock().getLocation();
        Grundstück gs = new Grundstück(player.getUniqueId(), center, radius);
        Main.gsList.add(gs);

        // Apostroph im Namen escapen
        String escapedName = player.getName().replace("'", "\\\\'");
        String summonCmd = String.format(
            Locale.US,
            "summon minecraft:text_display %.1f %.1f %.1f {text:'\"%s\\'s GS\"',billboard:\"center\",see_through:0b}",
            center.getX() + 0.5, center.getY() + 2.2, center.getZ() + 0.5,
            escapedName
        );
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), summonCmd);

        player.sendMessage("§aGrundstück gesetzt! §7(" + seite + "x" + seite + ") §8- §6" + kosten + " Coins");
        return true;
    }
}