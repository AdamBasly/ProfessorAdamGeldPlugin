package net.professoradamgeldplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.entity.Player;

public class GrundstückListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        for (Grundstück gs : Main.gsList) {
            if (gs.contains(loc) && !gs.owner.equals(player.getUniqueId())) {
                String name = Bukkit.getOfflinePlayer(gs.owner).getName();
                player.sendMessage("§cDu darfst hier nicht abbauen! Grundstück von §e" + name);
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        for (Grundstück gs : Main.gsList) {
            if (gs.contains(loc) && !gs.owner.equals(player.getUniqueId())) {
                String name = Bukkit.getOfflinePlayer(gs.owner).getName();
                player.sendMessage("§cDu darfst hier nichts bauen! Grundstück von §e" + name);
                event.setCancelled(true);
                return;
            }
        }
    }
}