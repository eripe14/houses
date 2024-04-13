package com.eripe14.houses.house;

import com.eripe14.houses.configuration.ConfigurationManager;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.hook.implementation.ItemsAdderHook;
import com.eripe14.houses.house.inventory.impl.RentInventory;
import com.eripe14.houses.house.inventory.impl.RentedPanelInventory;
import com.eripe14.houses.house.region.FinalRegionResult;
import com.eripe14.houses.house.region.PolygonalRegionServiceImpl;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Command(name = "house", aliases = {"dom"})
@Permission("rp.house.command")
public class HouseCommand {

    private final Server server;
    private final ItemsAdderHook itemsAdderHook;
    private final RentInventory rentInventory;
    private final RentedPanelInventory rentedPanelInventory;
    private final WorldGuard worldGuard;
    private final HouseService houseService;
    private final PolygonalRegionServiceImpl polygonalRegionService;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;
    private final ConfigurationManager configurationManager;

    public HouseCommand(Server server,
                        ItemsAdderHook itemsAdderHook,
                        RentInventory rentInventory,
                        RentedPanelInventory rentedPanelInventory,
                        WorldGuard worldGuard,
                        HouseService houseService,
                        PolygonalRegionServiceImpl polygonalRegionService,
                        MessageConfiguration messageConfiguration,
                        PluginConfiguration pluginConfiguration,
                        NotificationAnnouncer notificationAnnouncer,
                        ConfigurationManager configurationManager
    ) {
        this.server = server;
        this.itemsAdderHook = itemsAdderHook;
        this.rentInventory = rentInventory;
        this.rentedPanelInventory = rentedPanelInventory;
        this.worldGuard = worldGuard;
        this.houseService = houseService;
        this.polygonalRegionService = polygonalRegionService;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
        this.configurationManager = configurationManager;
    }

    @Execute(name = "usun")
    void delete(@Context Player player) {
        RegionContainer container = this.worldGuard.getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(player.getWorld()));

        if (manager == null) {
            return;
        }

        manager.getRegions().forEach((s, protectedRegion) -> manager.removeRegion(s));
    }

    @Execute(name = "create", aliases = {"stworz"})
    void create(@Context Player player,
                @Arg("house-id") String houseId,
                @Arg("house-district") HouseDistrict district,
                @Arg("house-type") HouseType type,
                @Arg("schematic-name") String schematicName,
                @Arg("rental-price") Integer rentalPrice,
                @Arg("buy-price") Optional<Integer> buyPrice) {
        CompletableFuture<FinalRegionResult> regions = this.polygonalRegionService.getRegions(player, houseId, district, type);

        Formatter formatter = new Formatter();
        formatter.register("{HOUSE_ID}", houseId);

        if (this.houseService.isHouseExists(houseId)) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseAlreadyExists, formatter);
            return;
        }

        regions.whenComplete((result, throwable) -> {
            if (throwable != null) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.cantCreateRegions);
                return;
            }

            if (!result.success()) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.cantCreateRegions);
                return;
            }

            CustomFurniture purchaseFurniture = this.itemsAdderHook.spawnCustomFurniture(player, this.pluginConfiguration.itemsAdderPurchaseNamespacedId);
            House house = this.houseService.createHouse(houseId, schematicName, district, type, player, result, purchaseFurniture, rentalPrice, buyPrice.orElse(0));

            this.houseService.addHouse(house);
            this.polygonalRegionService.saveRegions(player.getWorld(), result, schematicName);

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.createdHouse, formatter);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.createdBothRegions);
        });
    }

    @Execute(name = "panel")
    void panel(@Context Player player, @Arg("house-id") String houseId) {
        Option<House> optionHouse = this.houseService.getHouse(houseId);

        if (optionHouse.isEmpty()) {
            return;
        }

        this.rentedPanelInventory.openInventory(player, optionHouse.get());
    }

    @Execute(name = "info")
    void info(@Context Player player, @Arg("house-id") String houseId) {
        Option<House> optionHouse = this.houseService.getHouse(houseId);

        if (optionHouse.isEmpty()) {
            return;
        }

        House house = optionHouse.get();
        house.getMembers().forEach((uuid, houseMember) -> {
            player.sendMessage(Objects.requireNonNull(this.server.getPlayer(houseMember.getMemberUuid())).getName());
        });

        player.sendMessage("owner: " + this.server.getPlayer(house.getOwner().get().getUuid()).getName());
        player.sendMessage("houses: " + this.houseService.getAllHouses().size());
    }

    @Execute(name = "rent", aliases = {"wynajmij"})
    void rent(@Context Player player, @Arg("house-id") String houseId) {
        Option<House> optionHouse = this.houseService.getHouse(houseId);

        if (optionHouse.isEmpty()) {
            return;
        }

        this.rentInventory.openInventory(player, optionHouse.get());
    }

    @Execute(name = "reload", aliases = {"przeladuj"})
    void reload(@Context Player player) {
        this.configurationManager.reload();
    }

}