package com.eripe14.houses.schematic;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;


public class SchematicService {

    private final Plugin plugin;
    private final WorldEdit worldEdit;

    public SchematicService(Plugin plugin, WorldEdit worldEdit) {
        this.plugin = plugin;
        this.worldEdit = worldEdit;
    }

    public CompletableFuture<Clipboard> loadSchematic(String schematicFileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File schematicFile = new File(this.plugin.getDataFolder() + "/schematics/" + schematicFileName + ".schem");

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
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(true)
                    .build();

            Operations.complete(pasteOperation);
            editSession.close();

            return new SchematicResult(true, pasteOperation);
        });
    }

}