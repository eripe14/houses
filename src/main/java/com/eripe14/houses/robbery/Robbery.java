package com.eripe14.houses.robbery;

import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Robbery {

    private final UUID thief;
    private final String houseId;
    private final int maxWeight;
    private final List<ItemStack> stolenItems;
    private final Map<Location, CustomFurniture> stolenFurniture;
    private final Map<Block, Material> brokenGlass;
    private final List<BlockState> openedDoors;
    private int currentWeight;
    private boolean isPoliceNotified;

    public Robbery(UUID thief, String houseId, int maxWeight) {
        this.thief = thief;
        this.houseId = houseId;
        this.maxWeight = maxWeight;
        this.stolenItems = new ArrayList<>();
        this.stolenFurniture = new HashMap<>();
        this.brokenGlass = new HashMap<>();
        this.openedDoors = new ArrayList<>();
    }

    public UUID getThief() {
        return this.thief;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public int getMaxWeight() {
        return this.maxWeight;
    }

    public int getCurrentWeight() {
        return this.currentWeight;
    }

    public boolean isPoliceNotified() {
        return this.isPoliceNotified;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public void setPoliceNotified(boolean policeNotified) {
        this.isPoliceNotified = policeNotified;
    }

    public void addStolenItem(ItemStack itemStack) {
        this.stolenItems.add(itemStack);
    }

    public void addStolenFurniture(Location location, CustomFurniture furniture) {
        this.stolenFurniture.put(location, furniture);
    }

    public void addBrokenGlass(Block block, Material glassType) {
        this.brokenGlass.put(block, glassType);
    }

    public void addOpenedDoor(BlockState door) {
        this.openedDoors.add(door);
    }

    public List<ItemStack> getStolenItems() {
        return this.stolenItems;
    }

    public Map<Location, CustomFurniture> getStolenFurniture() {
        return this.stolenFurniture;
    }

    public Map<Block, Material> getBrokenGlass() {
        return this.brokenGlass;
    }

    public List<BlockState> getOpenedDoors() {
        return this.openedDoors;
    }

}