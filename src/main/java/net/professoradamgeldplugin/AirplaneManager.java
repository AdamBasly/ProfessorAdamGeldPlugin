import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class AirplaneManager implements Listener {

    public void spawnAirplane(Location location, Player player) {
        ArmorStand airplane = location.getWorld().spawn(location, ArmorStand.class);
        airplane.setCustomName("§aAirplane");
        airplane.setCustomNameVisible(true);
        airplane.setGravity(false);
        airplane.setVisible(false);

        // Add wooden texture (using blocks as visual representation)
        location.getWorld().getBlockAt(location).setType(Material.OAK_PLANKS);
        location.getWorld().getBlockAt(location.clone().add(0, 1, 0)).setType(Material.OAK_PLANKS);

        player.sendMessage("§aAirplane spawned! Use WASD to control it.");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getVehicle() instanceof ArmorStand && player.getVehicle().getCustomName().equals("§aAirplane")) {
            ArmorStand airplane = (ArmorStand) player.getVehicle();

            // Basic flight physics
            Vector direction = player.getLocation().getDirection();
            airplane.setVelocity(direction.multiply(0.5));
        }
    }
}