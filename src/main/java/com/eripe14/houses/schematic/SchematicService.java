package com.eripe14.houses.schematic;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.util.DurationUtil;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class SchematicService {

    private final Plugin plugin;
    private final WorldEdit worldEdit;

    public SchematicService(Plugin plugin, WorldEdit worldEdit) {
        this.plugin = plugin;
        this.worldEdit = worldEdit;
    }

    public List<String> getSchematicNames() {
        String path = this.plugin.getDataFolder().getAbsolutePath().replace(this.plugin.getName(), "")
                + "FastAsyncWorldEdit/schematics/";
        File schematicFolder = new File(path);

        if (!schematicFolder.exists()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();

        for (String schematicName : schematicFolder.list()) {
            result.add(schematicName.replace(".schem", ""));
        }

        return result;
    }

    public List<String> getHousesSchematicNames(House house) {
        List<String> schematicNames = this.getSchematicNames();

        return schematicNames.stream().filter(schematicName -> schematicName.contains(house.getHouseId())).toList();
    }

    public void saveSchematic(HouseRegion houseRegion, String suffix) {
        String path = this.plugin.getDataFolder().getAbsolutePath().replace(this.plugin.getName(), "")
                + "FastAsyncWorldEdit/schematics/" + houseRegion.getHouseId() + suffix + "_" + DurationUtil.formatSchematic(Instant.now()) + ".schem";
        File schematicFile = new File(path);

        ProtectedPolygonalRegion plot = houseRegion.getPlot();
        CuboidRegion cuboidRegion = new CuboidRegion(
                BukkitAdapter.adapt(houseRegion.getWorld()),
                plot.getMinimumPoint(),
                plot.getMaximumPoint()
        );

        BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);

        if (cuboidRegion.getWorld() == null) {
            return;
        }

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                cuboidRegion.getWorld(), cuboidRegion, clipboard, cuboidRegion.getMinimumPoint()
        );
        forwardExtentCopy.setCopyingEntities(true);

        Operations.complete(forwardExtentCopy);

        try (ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(new FileOutputStream(schematicFile))) {
            writer.write(clipboard);
            System.out.println("Schematic saved!");
        } catch (IOException ignored) {
            System.out.println("Failed to save schematic! Contact the developer.");
        }
    }

    public void saveSchematicWithName(HouseRegion houseRegion, String name) {
        String path = this.plugin.getDataFolder().getAbsolutePath().replace(this.plugin.getName(), "")
                + "FastAsyncWorldEdit/schematics/" + name + ".schem";
        File schematicFile = new File(path);

        ProtectedPolygonalRegion plot = houseRegion.getPlot();
        CuboidRegion cuboidRegion = new CuboidRegion(
                BukkitAdapter.adapt(houseRegion.getWorld()),
                plot.getMinimumPoint(),
                plot.getMaximumPoint()
        );

        BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);

        if (cuboidRegion.getWorld() == null) {
            return;
        }

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                cuboidRegion.getWorld(), cuboidRegion, clipboard, cuboidRegion.getMinimumPoint()
        );
        forwardExtentCopy.setCopyingEntities(true);

        Operations.complete(forwardExtentCopy);

        try (ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(new FileOutputStream(schematicFile))) {
            writer.write(clipboard);
            System.out.println("Schematic saved!");
        } catch (IOException ignored) {
            System.out.println("Failed to save schematic! Contact the developer.");
        }
    }

    public CompletableFuture<Clipboard> loadSchematic(String schematicFileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String path = this.plugin.getDataFolder().getAbsolutePath().replace(this.plugin.getName(), "") + "FastAsyncWorldEdit/schematics/" + schematicFileName + ".schem";
                File schematicFile = new File(path);

                ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);

                if (format == null) {
                    throw new IllegalArgumentException("Unknown schematic format!");
                }

                ClipboardReader reader = format.getReader(new FileInputStream(schematicFile));

                return reader.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<SchematicResult> pasteSchematic(org.bukkit.World world, BlockVector3 vector3, String schematicFileName) {
        return this.loadSchematic(schematicFileName).thenApply(clipboard -> {
            Location location = new Location(world, vector3.getBlockX(), vector3.getBlockY() + 1, vector3.getBlockZ());
            World adaptedWorld = BukkitAdapter.adapt(location.getWorld());

            EditSession editSession = this.worldEdit.newEditSessionBuilder()
                    .world(adaptedWorld)
                    .maxBlocks(-1)
                    .build();

            Operation pasteOperation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .ignoreAirBlocks(false)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .build();

            Operations.complete(pasteOperation);
            editSession.close();

            return new SchematicResult(true, pasteOperation);
        });
    }

    public CompletableFuture<SchematicResult> pasteSchematicNormalHeight(org.bukkit.World world, BlockVector3 vector3, String schematicFileName) {
        return this.loadSchematic(schematicFileName).thenApply(clipboard -> {
            Location location = new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
            World adaptedWorld = BukkitAdapter.adapt(location.getWorld());

            EditSession editSession = this.worldEdit.newEditSessionBuilder()
                    .world(adaptedWorld)
                    .maxBlocks(-1)
                    .build();

            Operation pasteOperation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .ignoreAirBlocks(false)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .build();

            Operations.complete(pasteOperation);
            editSession.close();

            return new SchematicResult(true, pasteOperation);
        });
    }

}