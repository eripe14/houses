package com.eripe14.houses.house.region;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.schematic.SchematicService;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PolygonalRegionServiceImpl implements RegionService {

    private final Server server;
    private final Scheduler scheduler;
    private final WorldEdit worldEdit;
    private final WorldGuard worldGuard;
    private final SchematicService schematicService;
    private final PluginConfiguration pluginConfiguration;

    public PolygonalRegionServiceImpl(Server server, Scheduler scheduler, WorldEdit worldEdit, WorldGuard worldGuard, SchematicService schematicService, PluginConfiguration pluginConfiguration) {
        this.server = server;
        this.scheduler = scheduler;
        this.worldEdit = worldEdit;
        this.worldGuard = worldGuard;
        this.schematicService = schematicService;
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

        this.schematicService.pasteSchematic(world, houseRegion.getMinimumPoint(), schematicFileName);
    }

    @Override
    public CompletableFuture<FinalRegionResult> getRegions(Player player, String houseId, HouseDistrict houseDistrict, HouseType houseType) {
        CompletableFuture<FinalRegionResult> finalRegionResultCompletableFuture = new CompletableFuture<>();

        String plotRegionName = this.getRegionName("plot", houseId, houseDistrict, houseType);
        String houseRegionName = this.getRegionName("house", houseId, houseDistrict, houseType);

        RegionResult plotRegionResult = this.getRegion(player, plotRegionName);
        FinalRegionResult failure = new FinalRegionResult(false, Option.none(), Option.none());

        this.scheduler.sync(() -> this.server.dispatchCommand(player, "/sel"));

        this.scheduler.laterSync(() -> {
            try {
                RegionResult houseRegionResult = this.getRegion(player, houseRegionName);

                if (!plotRegionResult.success()) {
                    finalRegionResultCompletableFuture.complete(failure);
                    return;
                }

                if (!houseRegionResult.success()) {
                    finalRegionResultCompletableFuture.complete(failure);
                    return;
                }

                ProtectedRegion plotRegion = plotRegionResult.optionalRegion().get();
                ProtectedRegion houseRegion = houseRegionResult.optionalRegion().get();

                plotRegion.setPriority(10);
                houseRegion.setPriority(10);
                houseRegion.setParent(plotRegion);

                plotRegion.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
                plotRegion.setFlag(Flags.USE, StateFlag.State.ALLOW);
                houseRegion.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
                houseRegion.setFlag(Flags.USE, StateFlag.State.ALLOW);

                finalRegionResultCompletableFuture.complete(new FinalRegionResult(true, Option.of(plotRegion), Option.of(houseRegion)));
                this.scheduler.sync(() -> this.server.dispatchCommand(player, "/sel"));
            } catch (ProtectedRegion.CircularInheritanceException e) {
                finalRegionResultCompletableFuture.complete(failure);

            }
        }, this.pluginConfiguration.timeToSetHomeRegion);

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