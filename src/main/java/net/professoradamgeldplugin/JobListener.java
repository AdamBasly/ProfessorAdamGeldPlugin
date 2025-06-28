package net.professoradamgeldplugin;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Player;

public class JobListener implements Listener {

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player p) {
            Main.gibCoins(p.getUniqueId(), 10);
            p.sendMessage("§a+10 Coins für den Kill!");
        }
    }
}