package com.eripe14.houses.house;

import com.eripe14.database.Database;
import com.eripe14.database.data.DataService;
import com.eripe14.houses.command.argument.RegionArgument;
import com.eripe14.houses.configuration.ConfigurationManager;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.history.HistorySell;
import com.eripe14.houses.history.HistoryUser;
import com.eripe14.houses.history.HistoryUserService;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.FinalRegionResult;
import com.eripe14.houses.house.region.HouseDistrict;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.house.region.PolygonalRegionServiceImpl;
import com.eripe14.houses.house.renovation.RenovationService;
import com.eripe14.houses.house.renovation.request.RenovationRequestService;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceService;
import com.eripe14.houses.house.rent.Rent;
import com.eripe14.houses.house.rent.RentService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.schematic.SchematicResult;
import com.eripe14.houses.schematic.SchematicService;
import com.eripe14.houses.util.DurationUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import net.dzikoysk.cdn.entity.Description;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Command(name = "house", aliases = { "dom" })
@Permission("rp.house.command")
public class HouseCommand {

    private final HouseService houseService;
    private final HistoryUserService historyUserService;
    private final HouseMemberService houseMemberService;
    private final RentService rentService;
    private final RenovationService renovationService;
    private final RenovationRequestService renovationRequestService;
    private final RenovationAcceptanceService renovationAcceptanceService;
    private final SchematicService schematicService;
    private final PolygonalRegionServiceImpl polygonalRegionService;
    private final DataService dataService;
    private final Database database;
    private final WorldGuard worldGuard;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;
    private final RobberyConfiguration robberyConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;
    private final ConfigurationManager configurationManager;

    public HouseCommand(
            HouseService houseService,
            HistoryUserService historyUserService,
            HouseMemberService houseMemberService,
            RentService rentService,
            RenovationService renovationService,
            RenovationRequestService renovationRequestService,
            RenovationAcceptanceService renovationAcceptanceService,
            SchematicService schematicService,
            PolygonalRegionServiceImpl polygonalRegionService,
            DataService dataService,
            Database database,
            WorldGuard worldGuard,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration,
            RobberyConfiguration robberyConfiguration,
            NotificationAnnouncer notificationAnnouncer,
            ConfigurationManager configurationManager
    ) {
        this.houseService = houseService;
        this.historyUserService = historyUserService;
        this.houseMemberService = houseMemberService;
        this.rentService = rentService;
        this.renovationService = renovationService;
        this.renovationRequestService = renovationRequestService;
        this.renovationAcceptanceService = renovationAcceptanceService;
        this.schematicService = schematicService;
        this.polygonalRegionService = polygonalRegionService;
        this.dataService = dataService;
        this.database = database;
        this.worldGuard = worldGuard;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
        this.robberyConfiguration = robberyConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
        this.configurationManager = configurationManager;
    }

    @Execute(name = "reload", aliases = { "przeladuj" })
    @Async
    void reload(@Context Player player) {
        this.configurationManager.reload();
        player.sendMessage("Reloaded houses plugin configurations!");
    }

    @Execute(name = "123")
    void a123(@Context Player player) {
        for (int i = 0; i < 200; i++) {
            player.sendMessage(i + " ");
        }
    }

    @Execute(name = "create_plot", aliases = { "stworz", "create" })
    @Description("Komenda do tworzenia domu")
    void create(@Context Player player,
                @Arg("house-id") String houseId,
                @Arg("house-district") HouseDistrict district,
                @Arg("house-type") HouseType type,
                @Arg("block-of-flats-id") RegionArgument blockOfFlatsId,
                @Arg("rental-price") Integer rentalPrice,
                @Arg("buy-price") Optional<Integer> buyPrice) {
        Formatter formatter = new Formatter();
        formatter.register("{HOUSE_ID}", houseId);
        formatter.register("{BLOCK}", blockOfFlatsId.regionName());
        formatter.register("{TIME}", DurationUtil.format(this.pluginConfiguration.timeToSetHomeRegion));

        if (this.houseService.isHouseExists(houseId)) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseAlreadyExists, formatter);
            return;
        }

        if (type == HouseType.APARTMENT) {
            ProtectedRegion blockOfFlatsRegion = this.worldGuard.getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(Bukkit.getWorld(PluginConfiguration.HOUSES_WORLD_NAME)))
                    .getRegion(blockOfFlatsId.regionName());

            this.polygonalRegionService.getApartmentRegion(player, houseId, blockOfFlatsRegion, district).whenComplete(((result, throwable) -> {
                if (throwable != null) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.cantCreateApartment);
                    return;
                }

                House house = this.houseService.createHouse(
                        houseId.toLowerCase(),
                        houseId.toLowerCase(),
                        district,
                        type,
                        result,
                        player.getLocation(),
                        blockOfFlatsId.regionName(),
                        rentalPrice,
                        buyPrice.orElse(0)
                );

                this.houseService.addHouse(house);
                this.polygonalRegionService.saveRegions(player.getWorld(), result, houseId);

                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.createdApartmentHouse, formatter);
            }));

            return;
        }

        CompletableFuture<FinalRegionResult> regions = this.polygonalRegionService.getRegions(player, houseId, district, type);

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.preCreateRegions, formatter);

        regions.whenComplete((result, throwable) -> {
            if (throwable != null) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.cantCreateRegions);
                return;
            }

            if (!result.success()) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.cantCreateRegions);
                return;
            }

            House house = this.houseService.createHouse(
                    houseId.toLowerCase(),
                    houseId.toLowerCase(),
                    district,
                    type,
                    result,
                    player.getLocation(),
                    blockOfFlatsId.regionName(),
                    rentalPrice,
                    buyPrice.orElse(0)
            );

            this.houseService.addHouse(house);
            this.polygonalRegionService.saveRegions(player.getWorld(), result, houseId);

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.createdHouse, formatter);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.createdBothRegions);
        });
    }

    @Execute(name = "clearRegions")
    @Description("Komenda do usuwania wszystkich regionów z WG")
    void clearRegions(@Context Player player) {
        RegionContainer regionContainer = this.worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(player.getWorld()));

        regionManager.getRegions().values().forEach(region -> regionManager.removeRegion(region.getId()));
        player.sendMessage("Cleared all regions!");
    }

    @Execute(name = "updateDatabase")
    void updateDatabase(@Context CommandSender player) {
        this.dataService.updateDatabase(this.database, "/");
        player.sendMessage("Updated database!");
    }

    @Execute(name = "schem", aliases = { "schematic", "schematic-backup" })
    void schem(@Context Player player, @Arg House house) {
        for (String schematicName : this.schematicService.getHousesSchematicNames(house)) {
            ClickEvent callback = ClickEvent.runCommand("/dom schemLoad " + house.getHouseId() + " " + schematicName);

            Component clickComponent = Component.text()
                    .content("[+]")
                    .color(TextColor.fromHexString("#4CBB17"))
                    .clickEvent(callback)
                    .build();

            Component textComponent = Component.text()
                    .content(schematicName)
                    .color(TextColor.fromHexString("#AAAAAA"))
                    .append(Component.text(" "))
                    .append(clickComponent)
                    .build();

            this.notificationAnnouncer.sendMessage(player, textComponent);
        }
    }

    @Execute(name = "schemLoad", aliases = { "schem-load", "schematic-load" })
    void schemLoad(@Context Player player, @Arg String houseId, @Arg("schematic-name") String schematicName) {
        Option<House> houseOption = this.houseService.getHouse(houseId);

        if (houseOption.isEmpty()) {
            player.sendMessage("House not found!");
            return;
        }

        House house = houseOption.get();

        if (schematicName.equalsIgnoreCase(houseId)) {
            this.polygonalRegionService.killAllFurniture(house.getRegion());

            CompletableFuture<SchematicResult> domekBackup = this.schematicService.pasteSchematic(
                    player.getWorld(),
                    house.getRegion().getHouse().getMinimumPoint(),
                    schematicName
            );

            domekBackup.whenComplete((schematicResult, throwable) -> {
                if (throwable != null) {
                    player.sendMessage("cant paste schematic!");
                    player.sendMessage(throwable.getMessage());
                    return;
                }
                player.sendMessage("pasted schematic!");
            });
            return;
        }

        this.polygonalRegionService.killAllFurniture(house.getRegion());

        CompletableFuture<SchematicResult> domekBackup = this.schematicService.pasteSchematicNormalHeight(
                player.getWorld(),
                house.getRegion().getPlot().getMinimumPoint(),
                schematicName
        );

        domekBackup.whenComplete((schematicResult, throwable) -> {
            if (throwable != null) {
                player.sendMessage("cant paste schematic!");
                player.sendMessage(throwable.getMessage());
                return;
            }
            player.sendMessage("pasted schematic!");
        });
    }

    @Execute(name = "list", aliases = { "lista" })
    void houseList(@Context Player player, @Arg Optional<Integer> pageOpt) {
        Formatter formatter = new Formatter();

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseListCommandHeader);

        List<House> houses = this.houseService.getAllHousesAsList();

        int linesPerPage = 10;
        int page = pageOpt.orElse(1);
        int totalPages = (int) Math.ceil((double) houses.size() / linesPerPage);

        if (page > totalPages) {
            page = totalPages;
        } else if (page < 1) {
            page = 1;
        }

        int start = (page - 1) * linesPerPage;
        int end = Math.min(start + linesPerPage, houses.size());

        for (int i = start; i < end; i++) {
            House house = houses.get(i);

            formatter.register("{HOUSE_ID}", house.getHouseId());
            formatter.register("{HOUSE_TYPE}", house.getRegion().getHouseType().name());
            formatter.register("{HOUSE_DISTRICT}", house.getRegion().getDistrict().name());
            formatter.register("{HOUSE_OWNER}", house.getOwner().isPresent() ? house.getOwner().get().getName() : "Brak");

            String houseInfo = formatter.format(this.messageConfiguration.house.houseListCommandEntry);
            TextComponent houseComponent = Component.text(houseInfo);

            TextComponent teleportButton = createClickableTextComponent(" [Teleportuj]", "#00AAFF", "/dom teleport " + house.getHouseId());
            houseComponent = houseComponent.append(teleportButton);

            this.notificationAnnouncer.sendMessage(player, houseComponent);
        }

        if (totalPages > 1) {
            TextComponent paginationMessage = Component.empty();

            TextComponent pageInfo = Component.text("Strona " + page)
                    .color(TextColor.fromHexString("#FFFF00"))
                    .append(Component.text("/").color(TextColor.fromHexString("#808080")))
                    .append(Component.text(totalPages).color(TextColor.fromHexString("#FFFF00")));
            paginationMessage = paginationMessage.append(pageInfo);

            if (page > 1) {
                TextComponent previousPage = createClickableTextComponent(" « Strona wstecz", "#FF0000", page - 1);
                paginationMessage = paginationMessage.append(previousPage);
            }

            if (page < totalPages) {
                if (page > 1) {
                    paginationMessage = paginationMessage.append(Component.text(" ".repeat(10)));
                }
                TextComponent nextPage = createClickableTextComponent(" Strona dalej »", "#00b000", page + 1);
                paginationMessage = paginationMessage.append(nextPage);
            }

            this.notificationAnnouncer.sendMessage(player, paginationMessage);
        }
    }

    private TextComponent createClickableTextComponent(String displayText, String hex, int targetPage) {
        return Component.text(displayText)
                .style(Style.style(TextDecoration.BOLD))
                .color(TextColor.fromHexString(hex))
                .clickEvent(ClickEvent.runCommand("/dom list " + targetPage));
    }

    private TextComponent createClickableTextComponent(String displayText, String hex, String command) {
        return Component.text(displayText)
                .style(Style.style(TextDecoration.BOLD))
                .color(TextColor.fromHexString(hex))
                .clickEvent(ClickEvent.runCommand(command));
    }

    @Execute(name = "edit", aliases = { "edytuj" })
    void houseEdit(
            @Context Player player,
            @Arg("house") House house,
            @Arg("house-district") HouseDistrict district,
            @Arg("house-type") HouseType type,
            @Arg("block-of-flats-id") RegionArgument blockOfFlatsId,
            @Arg("rental-price") Integer rentalPrice,
            @Arg("buy-price") Optional<Integer> buyPrice
    ) {
        House updatedHouse = this.houseService.editHouse(
                house,
                house.getHouseId(),
                district,
                type,
                blockOfFlatsId.regionName(),
                rentalPrice,
                buyPrice.orElse(0)
        );

        this.houseService.addHouse(updatedHouse);
        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseUpdated);
    }

    @Execute(name = "info")
    void houseInfo(@Context Player player, @Arg House house) {
        Formatter formatter = new Formatter();
        formatter.register("{HOUSE_ID}", house.getHouseId());
        formatter.register("{HOUSE_TYPE}", house.getRegion().getHouseType().name());
        formatter.register("{HOUSE_DISTRICT}", house.getRegion().getDistrict().name());
        formatter.register("{PURCHASE_METHOD}",
                house.getRent().isPresent() ?
                        this.messageConfiguration.house.tenantInfo :
                        this.messageConfiguration.house.ownerInfo
        );
        formatter.register("{HOUSE_OWNER}",
                house.getOwner().isPresent() ?
                        house.getOwner().get().getName() :
                        "Brak"
        );
        formatter.register("{HOUSE_RENTAL_PRICE}", house.getDailyRentalPrice());
        formatter.register("{HOUSE_BUY_PRICE}", house.getBuyPrice());
        formatter.register("{OWNER_SINCE}",
                house.getOwner().isPresent() ?
                        DurationUtil.format(house.getOwner().get().getOwnerSince()) :
                        "Brak"
        );

        ClickEvent callback = ClickEvent.runCommand("/dom teleport " + house.getHouseId());
        Component clickComponent = Component.text()
                .content("[TELEPORT]")
                .color(TextColor.fromHexString("#4CBB17"))
                .clickEvent(callback)
                .build();

        this.notificationAnnouncer.sendMessage(player, clickComponent);
        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseInfo, formatter);
        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseInfoMembersHeader, formatter);

        if (house.getMembers().values().isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseInfoMembersEmpty);
            return;
        }

        for (HouseMember member : house.getMembers().values()) {
            formatter.register("{MEMBER_NAME}", member.getMemberName());
            formatter.register("{MEMBER_JOINED_AT}", DurationUtil.format(member.getJoinedAt()));
            formatter.register("{MEMBER_IS_CO_OWNER}", member.isCoOwner() ? "tak" : "nie");

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseInfoMembersEntry, formatter);
        }

        List<HouseMember> coOwners = this.houseMemberService.getCoOwners(house);

        if (coOwners.isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseInfoCoOwnersEmpty);
            return;
        }

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseInfoCoOwnersHeader, formatter);

        for (HouseMember coOwner : coOwners) {
            formatter.register("{CO_OWNER_NAME}", coOwner.getMemberName());
            formatter.register("{CO_OWNER_JOINED_AT}", DurationUtil.format(coOwner.getJoinedAt()));

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseInfoCoOwnersEntry, formatter);
        }
    }

    @Execute(name = "userInfo")
    void houseUserInfo(@Context Player player, @Arg("user-target") Player target) {
        Collection<House> housesWithUser = this.houseService.getHousesWithUser(target.getName());

        MessageConfiguration.House houseMessage = this.messageConfiguration.house;
        Formatter formatter = new Formatter();
        formatter.register("{TARGET}", target.getName());

        Option<HistoryUser> userOption = this.historyUserService.getUser(target.getUniqueId());

        if (userOption.isEmpty()) {
            return;
        }

        HistoryUser user = userOption.get();

        this.notificationAnnouncer.sendMessage(player, houseMessage.soldHousesInfoUserHeader, formatter);

        if (user.getHistoryPurchase().isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, houseMessage.soldHousesInfoUserEmpty);
        } else {
            for (HistorySell historySell : user.getHistoryPurchase().values()) {
                formatter.register("{HOUSE_ID}", historySell.getHouseId());
                formatter.register("{PRICE}", historySell.getGainedMoney());
                formatter.register("{DATE}", DurationUtil.format(historySell.getSoldTime()));

                this.notificationAnnouncer.sendMessage(player, houseMessage.soldHousesInfoUserEntry, formatter);
            }
        }

        this.notificationAnnouncer.sendMessage(player, houseMessage.leftHousesInfoUserHeader, formatter);

        if (user.getLeftHouses().isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, houseMessage.leftHousesInfoUserEmpty);
        } else {
            for (String houseId : user.getLeftHouses()) {
                formatter.register("{HOUSE_ID}", houseId);
                this.notificationAnnouncer.sendMessage(player, houseMessage.leftHousesInfoUserEntry, formatter);
            }
        }

        this.notificationAnnouncer.sendMessage(player, houseMessage.houseInfoUserHeader, formatter);

        for (House house : housesWithUser) {
            System.out.println(house.getHouseId());
        }

        for (House house : housesWithUser) {
            Owner owner = house.getOwner().get();

            formatter.register("{HOUSE_ID}", house.getHouseId());

            if (house.getRent().isPresent()) {
                Rent rent = house.getRent().get();
                Formatter rentFormatter = new Formatter();
                rentFormatter.register("{RENTER}", owner.getName());
                rentFormatter.register("{DAYS}", DurationUtil.format(rent.getRentDuration()));

                formatter.register("{PURCHASE_INFO}", rentFormatter.format(houseMessage.houseRentInfo));
            } else {
                Formatter buyFormatter = new Formatter();
                buyFormatter.register("{BUYER}", owner.getName());
                buyFormatter.register("{PRICE}", house.getBuyPrice());
                formatter.register("{PURCHASE_INFO}", buyFormatter.format(houseMessage.houseBuyInfo));
            }

            if (owner.getName().equalsIgnoreCase(target.getName())) {
                formatter.register("{RANK}", houseMessage.houseInfoUserRankOwner);
                formatter.register("{RANK_SINCE}", DurationUtil.format(owner.getOwnerSince()));
            } else if (this.houseMemberService.getHouseMember(house, target.getName()).isPresent()) {
                HouseMember member = this.houseMemberService.getHouseMember(house, target.getName()).get();

                if (member.isCoOwner()) {
                    formatter.register("{RANK}", houseMessage.houseInfoUserRankCoOwner);
                    formatter.register("{RANK_SINCE}", DurationUtil.format(member.getCoOwnerAt().get()));
                } else {
                    formatter.register("{RANK}", houseMessage.houseInfoUserRankMember);
                    formatter.register("{RANK_SINCE}", DurationUtil.format(member.getJoinedAt()));

                    formatter.register(
                            "{CAN_OPEN_DOORS}",
                            member.getPermissions().get(HouseMemberPermission.OPEN_DOORS) ? "tak" : "nie"
                    );
                    formatter.register(
                            "{CAN_OPEN_CHESTS}",
                            member.getPermissions().get(HouseMemberPermission.OPEN_CHESTS) ? "tak" : "nie"
                    );
                    formatter.register(
                            "{CAN_PLACE_FURNITURE}",
                            member.getPermissions().get(HouseMemberPermission.PLACE_FURNITURE) ? "tak" : "nie"
                    );
                    formatter.register(
                            "{CAN_RENOVATE}",
                            member.getPermissions().get(HouseMemberPermission.RENOVATE) ? "tak" : "nie"
                    );
                }
            }

            this.notificationAnnouncer.sendMessage(player, houseMessage.houseInfoUserEntry, formatter);

            Optional<HouseMember> houseMember = this.houseMemberService.getHouseMember(house, target.getName());

            if (houseMember.isPresent()) {
                if (!houseMember.get().isCoOwner()) {
                    this.notificationAnnouncer.sendMessage(player, houseMessage.houseInfoUserPermissionsHeader, formatter);
                    this.notificationAnnouncer.sendMessage(player, houseMessage.canOpenDoorsPermission, formatter);
                    this.notificationAnnouncer.sendMessage(player, houseMessage.canOpenChestsPermission, formatter);
                    this.notificationAnnouncer.sendMessage(player, houseMessage.canPlaceFurniturePermission, formatter);
                    this.notificationAnnouncer.sendMessage(player, houseMessage.canRenovationPermission, formatter);
                }
            }
        }
    }

    @Execute(name = "delete", aliases = { "usun" })
    void houseRemove(@Context Player player, @Arg House house) {
        String houseId = house.getHouseId();

        Formatter formatter = new Formatter();
        formatter.register("{HOUSE_ID}", houseId);

        if (this.rentService.getRent(houseId).isPresent()) {
            this.rentService.removeRent(houseId);
        }

        if (this.renovationService.getRenovation(houseId).isPresent()) {
            this.renovationService.removeRenovation(houseId);
        }

        if (this.renovationRequestService.getRequest(houseId).isPresent()) {
            this.renovationRequestService.removeRequest(houseId);
        }

        if (this.renovationAcceptanceService.getRenovationAcceptanceRequest(houseId).isPresent()) {
            this.renovationAcceptanceService.removeRenovationAcceptanceRequest(houseId);
        }

        RegionManager regionManager = this.worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld(PluginConfiguration.HOUSES_WORLD_NAME)));
        regionManager.removeRegion(house.getRegion().getPlot().getId());
        regionManager.removeRegion(house.getRegion().getHouse().getId());

        this.houseService.removeHouse(house.getHouseId());
        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.houseDeleted, formatter);
    }

    @Execute(name = "items")
    void items(@Context Player player) {
        ItemStack snowball = this.robberyConfiguration.robberySnowball.asGuiItem().getItemStack();
        ItemStack lockpick = this.robberyConfiguration.lockpickItem.asGuiItem().getItemStack();
        ItemStack kickDoor = this.robberyConfiguration.kickDoorItem.asGuiItem().getItemStack();
        ItemStack renovationAcceptance = this.pluginConfiguration.renovationAcceptanceItem.asGuiItem().getItemStack();

        List.of(snowball, lockpick, kickDoor, renovationAcceptance).forEach(item -> player.getInventory().addItem(item));
    }

    @Execute(name = "teleport")
    void teleport(@Context Player player, @Arg("house-id") String houseId) {
        this.houseService.getHouse(houseId).peek((house) -> {
            player.teleport(house.getRegion().getHouseCenter());
            player.setGameMode(GameMode.SPECTATOR);
        });
    }

}