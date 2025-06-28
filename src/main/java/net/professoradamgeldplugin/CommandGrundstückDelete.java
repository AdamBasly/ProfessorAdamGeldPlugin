package net.professoradamgeldplugin;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class CommandGrundstückDelete implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        Iterator<Grundstück> iterator = Main.gsList.iterator();
        while (iterator.hasNext()) {
            Grundstück gs = iterator.next();
            if (gs.contains(player.getLocation()) && gs.owner.equals(player.getUniqueId())) {
                String removeCmd = "kill @e[type=minecraft:text_display,distance=..3,limit=1]";
                player.getServer().dispatchCommand(player, removeCmd);

                iterator.remove();
                Main.getInstance().speichereGrundstücke();
                player.sendMessage("§aDein Grundstück wurde entfernt.");
                return true;
            }
        }

        player.sendMessage("§cDu stehst auf keinem deiner Grundstücke.");
        return true;
    }
}