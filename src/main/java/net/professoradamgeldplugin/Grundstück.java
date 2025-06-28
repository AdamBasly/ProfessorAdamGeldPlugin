package net.professoradamgeldplugin;

import java.util.UUID;
import org.bukkit.Location;

public class Grundstück {
    public final UUID owner;
    public final Location center;
    public final int radius;

    public Grundstück(UUID owner, Location center, int radius) {
        this.owner = owner;
        this.center = center;
        this.radius = radius;
    }

    public boolean contains(Location loc) {
        int dx = Math.abs(loc.getBlockX() - center.getBlockX());
        int dz = Math.abs(loc.getBlockZ() - center.getBlockZ());
        return dx <= radius && dz <= radius;
    }
}