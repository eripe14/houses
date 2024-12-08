package com.eripe14.houses.house.region;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.house.region.selection.HouseSelectionService;
import com.eripe14.houses.position.Position;
import com.eripe14.houses.position.PositionAdapter;
import com.eripe14.houses.scheduler.Scheduler;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PolygonalRegionServiceImpl implements RegionService {

    private final Server server;
    private final Scheduler scheduler;
    private final WorldEdit worldEdit;
    private final WorldGuard worldGuard;
    private final HouseSelectionService houseSelectionService;
    private final ProtectionService protectionService;
    private final PluginConfiguration pluginConfiguration;

    public PolygonalRegionServiceImpl(
            Server server,
            Scheduler scheduler,
            WorldEdit worldEdit,
            WorldGuard worldGuard,
            HouseSelectionService houseSelectionService,
            ProtectionService protectionService,
            PluginConfiguration pluginConfiguration
    ) {
        this.server = server;
        this.scheduler = scheduler;
        this.worldEdit = worldEdit;
        this.worldGuard = worldGuard;
        this.houseSelectionService = houseSelectionService;
        this.protectionService = protectionService;
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public void saveRegions(World world, FinalRegionResult result, String schematicFileName) {
        RegionContainer container = this.worldGuard.getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(world));

        if (manager == null) {
            return;
        }

        ProtectedRegion houseRegion = result.house().get();

        manager.addRegion(result.plot().get());
        manager.addRegion(houseRegion);

    }

    @Override
    public void resetRegion(HouseRegion houseRegion) {
        for (ArmorStand armorStand : houseRegion.getWorld().getEntitiesByClass(ArmorStand.class)) {
            for (
                    Iterator<Position> iterator = houseRegion.getPlacedFurnitureLocations().iterator();
                    iterator.hasNext();
                ) {
                Location location = armorStand.getLocation().clone();
                Location placedFurnitureLocation = PositionAdapter.convert(iterator.next());

                if (!location.equals(placedFurnitureLocation)) {
                    continue;
                }

                CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(armorStand);
                customFurniture.remove(false);

                iterator.remove();
            }
        }
    }

    @Override
    public void killAllFurniture(HouseRegion houseRegion) {
        for (ArmorStand armorStand : houseRegion.getWorld().getEntitiesByClass(ArmorStand.class)) {
            ApplicableRegionSet locationRegions = this.protectionService.getLocationRegions(armorStand.getLocation());

            if (locationRegions.getRegions().isEmpty()) {
                continue;
            }

            for (ProtectedRegion region : locationRegions.getRegions()) {
                if (houseRegion.getPlot().getId().equalsIgnoreCase(region.getId())
                        || houseRegion.getHouse().getId().equalsIgnoreCase(region.getId())) {
                    armorStand.remove();
                }
            }
        }
    }

    @Override
    public CompletableFuture<FinalRegionResult> getRegions(Player player, String houseId, HouseDistrict houseDistrict, HouseType houseType) {
        CompletableFuture<FinalRegionResult> finalRegionResultCompletableFuture = new CompletableFuture<>();

        String plotRegionName = this.getRegionName("plot", houseId, houseDistrict, houseType);
        String houseRegionName = this.getRegionName("house", houseId, houseDistrict, houseType);

        RegionResult plotRegionResult = this.getRegion(player, plotRegionName);
        FinalRegionResult failure = new FinalRegionResult(false, Option.none(), Option.none());

        if (!plotRegionResult.success()) {
            finalRegionResultCompletableFuture.complete(failure);
            return finalRegionResultCompletableFuture;
        }

        this.scheduler.sync(() -> this.server.dispatchCommand(player, "/sel"));

        CompletableFuture<Player> playerSelectionFuture = new CompletableFuture<>();
        this.houseSelectionService.putSelection(player, playerSelectionFuture);

        playerSelectionFuture.whenComplete((playerFuture, throwable) -> {
            try {
                RegionResult houseRegionResult = this.getRegion(player, houseRegionName);

                if (!houseRegionResult.success()) {
                    finalRegionResultCompletableFuture.complete(failure);
                    return;
                }

                ProtectedRegion plotRegion = plotRegionResult.optionalRegion().get();
                ProtectedRegion houseRegion = houseRegionResult.optionalRegion().get();

                plotRegion.setPriority(10);
                houseRegion.setPriority(10);
                houseRegion.setParent(plotRegion);

                StateFlag[] flags = { Flags.CHEST_ACCESS, Flags.USE, Flags.BLOCK_PLACE, Flags.BLOCK_BREAK, Flags.PASSTHROUGH };

                for (StateFlag flag : flags) {
                    plotRegion.setFlag(flag, StateFlag.State.ALLOW);
                    houseRegion.setFlag(flag, StateFlag.State.ALLOW);
                }

                finalRegionResultCompletableFuture.complete(
                        new FinalRegionResult(true, Option.of(plotRegion), Option.of(houseRegion))
                );
                this.scheduler.sync(() -> this.server.dispatchCommand(player, "/sel"));
            } catch (ProtectedRegion.CircularInheritanceException e) {
                finalRegionResultCompletableFuture.complete(failure);
            }
        });

        return finalRegionResultCompletableFuture;
    }

    @Override
    public CompletableFuture<FinalRegionResult> getApartmentRegion(Player player, String houseId, ProtectedRegion blockOfFlatsRegion, HouseDistrict houseDistrict) {
        CompletableFuture<FinalRegionResult> finalRegionResultCompletableFuture = new CompletableFuture<>();

        String houseRegionName = this.getRegionName("house_apartment", houseId, houseDistrict, HouseType.APARTMENT);

        RegionResult apartmentRegionResult = this.getRegion(player, houseRegionName);
        FinalRegionResult failure = new FinalRegionResult(false, Option.none(), Option.none());

        if (!apartmentRegionResult.success()) {
            finalRegionResultCompletableFuture.complete(failure);
            return finalRegionResultCompletableFuture;
        }

        try {
            ProtectedRegion apartmentRegion = apartmentRegionResult.optionalRegion().get();
            apartmentRegion.setPriority(10);
            apartmentRegion.setParent(blockOfFlatsRegion);

            StateFlag[] flags = { Flags.CHEST_ACCESS, Flags.USE, Flags.BLOCK_PLACE, Flags.BLOCK_BREAK, Flags.PASSTHROUGH };

            for (StateFlag flag : flags) {
                apartmentRegion.setFlag(flag, StateFlag.State.ALLOW);
            }

            finalRegionResultCompletableFuture.complete(
                    new FinalRegionResult(true, Option.of(apartmentRegion), Option.of(apartmentRegion))
            );

            this.scheduler.sync(() -> this.server.dispatchCommand(player, "/sel"));
        } catch (ProtectedRegion.CircularInheritanceException e) {
            finalRegionResultCompletableFuture.complete(failure);
        }

        return finalRegionResultCompletableFuture;
    }

    @Override
    public RegionResult getRegion(Player player, String regionName) {
        com.sk89q.worldedit.entity.Player adaptedPlayer = BukkitAdapter.adapt(player);
        SessionManager sessionManager = this.worldEdit.getSessionManager();
        LocalSession localSession = sessionManager.get(adaptedPlayer);
        com.sk89q.worldedit.world.World sessionWorld = localSession.getSelectionWorld();

        try {
            Region region = localSession.getSelection(sessionWorld);

            BlockVector3 minimumPoint = region.getMinimumPoint();
            BlockVector3 maximumPoint = region.getMaximumPoint();
            List<BlockVector2> listOfPoints = region.polygonize(-1);

            ProtectedPolygonalRegion polygonalRegion = new ProtectedPolygonalRegion(regionName, listOfPoints, minimumPoint.getBlockY(), maximumPoint.getBlockY());

            return new RegionResult(true, Option.of(polygonalRegion));
        } catch (IncompleteRegionException ex) {
            return new RegionResult(false, Option.none());
        }
    }

    @Override
    public String getRegionName(String prefix, String houseId, HouseDistrict houseDistrict, HouseType houseType) {
        return prefix + "_" + houseDistrict.name().toLowerCase() + "_" + houseType.name().toLowerCase() + "_" + houseId;
    }

}