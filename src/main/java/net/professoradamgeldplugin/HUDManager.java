package net.professoradamgeldplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import net.professoradamgeldplugin.Main;

public class HUDManager {

    public static void startHUDUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String playerName = player.getName();
                    String worldName = player.getWorld().getName();
                    int balance = Main.getBalance(player.getUniqueId());

                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    if (manager == null) return;

                    Scoreboard scoreboard = manager.getNewScoreboard();
                    Objective objective = scoreboard.registerNewObjective("HUD", "dummy", "§aSpieler Info");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                    objective.getScore("§eName: §a" + playerName).setScore(3);
                    objective.getScore("§eWelt: §a" + worldName).setScore(2);
                    objective.getScore("§eCoins: §a" + balance).setScore(1);

                    player.setScoreboard(scoreboard);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L); // Update every second
    }
}