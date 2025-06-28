package net.professoradamgeldplugin;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CommandSell implements CommandExecutor {

    private final Map<Material, Integer> preise;

    public CommandSell(Map<Material, Integer> preise) {
        this.preise = preise;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (args.length != 1) {
            p.sendMessage("§cVerwendung: /sell <item>");
            return true;
        }

        Material mat = Material.matchMaterial(args[0].toUpperCase());
        if (mat == null) {
            p.sendMessage("§cUngültiges Item.");
            return true;
        }

        if (!p.getInventory().containsAtLeast(new ItemStack(mat), 1)) {
            p.sendMessage("§cDu hast dieses Item nicht im Inventar.");
            return true;
        }

        int verkaufswert = preise.getOrDefault(mat, 1);
        p.getInventory().removeItem(new ItemStack(mat, 1));
        Main.gibCoins(p.getUniqueId(), verkaufswert);
        p.sendMessage("§aVerkauft: 1x " + mat.name() + " für " + verkaufswert + " Coins.");
        return true;
    }
}