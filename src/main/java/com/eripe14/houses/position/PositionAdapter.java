package com.eripe14.houses.position;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class PositionAdapter {

    private PositionAdapter() {
    }

    public static Position convert(Location location) {
        if (location.getWorld() == null) {
            throw new IllegalStateException("World is not defined");
        }

        return new Position(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getWorld().getName());
    }

    public static Location convert(Position position) {
        World world = Bukkit.getWorld(position.getWorld());

        if (world == null) {
            world = Bukkit.getWorld(PluginConfiguration.HOUSES_WORLD_NAME);
        }

        return new Location(world, position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch());
    }

    public static boolean compareLocations(Location firstLocation, Location secondLocation) {
        return firstLocation.getBlockX() == secondLocation.getBlockX() &&
                firstLocation.getBlockY() == secondLocation.getBlockY() &&
                firstLocation.getBlockZ() == secondLocation.getBlockZ();
    }

    public static Location convertInteger(Position position) {
        World world = Bukkit.getWorld(position.getWorld());

        if (world == null) {
            world = Bukkit.getWorld(PluginConfiguration.HOUSES_WORLD_NAME);
        }

        return new Location(
                world,
                (int) position.getX(),
                position.getY(),
                (int) position.getZ(),
                0,
                0
        );
    }

    public static Location convertFurniture(Position position) {
        World world = Bukkit.getWorld(position.getWorld());

        if (world == null) {
            world = Bukkit.getWorld(PluginConfiguration.HOUSES_WORLD_NAME);
        }

        return new Location(
                world,
                Math.round(position.getX() * 2) / 2.0,
                position.getY(),
                Math.round(position.getZ() * 2) / 2.0,
                0,
                0
        );
    }

}