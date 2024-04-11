package com.eripe14.houses;

import com.eripe14.houses.alert.AlertController;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.alert.AlertHandlerImpl;
import com.eripe14.houses.alert.AlertService;
import com.eripe14.houses.command.handler.InvalidUsageHandler;
import com.eripe14.houses.command.handler.MissingPermissionsHandler;
import com.eripe14.houses.configuration.ConfigurationManager;
import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.hook.HookService;
import com.eripe14.houses.hook.implementation.ItemsAdderHook;
import com.eripe14.houses.hook.implementation.VaultHook;
import com.eripe14.houses.house.HouseCommand;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.action.impl.AddCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.AddPlayerAction;
import com.eripe14.houses.house.inventory.action.impl.ChangePermissionsAction;
import com.eripe14.houses.house.inventory.action.impl.RemoveCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.RemovePlayerAction;
import com.eripe14.houses.house.inventory.impl.ChangePermissionsInventory;
import com.eripe14.houses.house.inventory.impl.ConfirmInventory;
import com.eripe14.houses.house.inventory.impl.HousePanelInventory;
import com.eripe14.houses.house.inventory.impl.ListOfCoOwnersInventory;
import com.eripe14.houses.house.inventory.impl.ListOfHouseMembersInventory;
import com.eripe14.houses.house.inventory.impl.RentInventory;
import com.eripe14.houses.house.invite.HouseInviteController;
import com.eripe14.houses.house.invite.HouseInviteService;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.purchase.PurchaseFurnitureInteractController;
import com.eripe14.houses.house.region.PolygonalRegionServiceImpl;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.house.region.protection.controller.OpenChestController;
import com.eripe14.houses.house.region.protection.controller.PlaceFurnitureController;
import com.eripe14.houses.house.rent.RentController;
import com.eripe14.houses.house.rent.RentService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.purchase.PurchaseService;
import com.eripe14.houses.scheduler.BukkitSchedulerImpl;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.schematic.SchematicService;
import com.eripe14.houses.util.adventure.LegacyColorProcessor;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.WorldGuard;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class HousesPlugin extends JavaPlugin {

    private Scheduler scheduler;

    private ConfigurationManager configurationManager;

    private PluginConfiguration pluginConfiguration;
    private MessageConfiguration messageConfiguration;
    private InventoryConfiguration inventoryConfiguration;

    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;
    private NotificationAnnouncer notificationAnnouncer;

    private HookService hookService;

    private Economy economy;

    private ItemsAdderHook itemsAdderHook;

    private WorldEdit worldEdit;
    private WorldGuard worldGuard;

    private PurchaseService purchaseService;

    private SchematicService schematicService;
    private PolygonalRegionServiceImpl polygonalRegionService;

    private EventCaller eventCaller;

    private HouseService houseService;
    private HouseInviteService houseInviteService;
    private HouseMemberService houseMemberService;

    private RentService rentService;

    private ProtectionService protectionService;

    private AlertService alertService;
    private AlertHandler alertHandler;

    private AddPlayerAction addPlayerAction;
    private AddCoOwnerAction addCoOwnerAction;
    private RemovePlayerAction removePlayerAction;
    private RemoveCoOwnerAction removeCoOwnerAction;
    private ChangePermissionsAction changePermissionsAction;

    private ConfirmInventory confirmInventory;
    private ListOfHouseMembersInventory listOfHouseMembersInventory;
    private ListOfCoOwnersInventory listOfCoOwnersInventory;
    private ChangePermissionsInventory changePermissionsInventory;
    private HousePanelInventory housePanelInventory;
    private RentInventory rentInventory;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Server server = this.getServer();
        Logger logger = this.getLogger();

        this.scheduler = new BukkitSchedulerImpl(this);

        this.configurationManager = new ConfigurationManager(this.getDataFolder());

        this.configurationManager.createFolderIfNotExists("schematics");
        this.pluginConfiguration = this.configurationManager.load(new PluginConfiguration());
        this.messageConfiguration = this.configurationManager.load(new MessageConfiguration());
        this.inventoryConfiguration = this.configurationManager.load(new InventoryConfiguration());

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
                .postProcessor(new LegacyColorProcessor())
                .build();
        this.notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, this.miniMessage);

        this.hookService = new HookService(server, logger);

        VaultHook vaultHook = new VaultHook(server);
        this.hookService.initialize(vaultHook);

        this.economy = vaultHook.getEconomy();

        this.itemsAdderHook = new ItemsAdderHook();

        this.worldEdit = WorldEdit.getInstance();
        this.worldGuard = WorldGuard.getInstance();

        this.purchaseService = new PurchaseService(this.economy);

        this.schematicService = new SchematicService(this, this.worldEdit);
        this.polygonalRegionService = new PolygonalRegionServiceImpl(server, this.scheduler, this.worldEdit, this.worldGuard, this.schematicService, this.pluginConfiguration);

        this.eventCaller = new EventCaller(server);

        this.houseService = new HouseService();
        this.houseInviteService = new HouseInviteService(this.eventCaller, this.pluginConfiguration);
        this.houseMemberService = new HouseMemberService(this.houseService, this.pluginConfiguration);

        this.rentService = new RentService();

        this.protectionService = new ProtectionService(this.worldGuard);

        this.alertService = new AlertService();
        this.alertHandler = new AlertHandlerImpl(server, this.alertService, this.scheduler, this.messageConfiguration, this.notificationAnnouncer);

        this.confirmInventory = new ConfirmInventory(
                this.scheduler,
                this.inventoryConfiguration
        );
        this.listOfHouseMembersInventory = new ListOfHouseMembersInventory(
                this.scheduler,
                server,
                this.confirmInventory, this.inventoryConfiguration
        );
        this.listOfCoOwnersInventory = new ListOfCoOwnersInventory(
                this.scheduler,
                server,
                this.confirmInventory, this.inventoryConfiguration
        );
        this.changePermissionsInventory = new ChangePermissionsInventory(
                this.scheduler,
                this.houseMemberService,
                this.alertHandler,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.inventoryConfiguration
        );

        this.addPlayerAction = new AddPlayerAction(
                this.houseMemberService,
                this.houseInviteService,
                this.confirmInventory,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.pluginConfiguration
        );
        this.addCoOwnerAction = new AddCoOwnerAction(
                this.houseMemberService,
                this.alertHandler,
                this.listOfHouseMembersInventory,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.inventoryConfiguration
        );
        this.removePlayerAction = new RemovePlayerAction(
                this.houseMemberService,
                this.alertHandler,
                this.listOfHouseMembersInventory,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.inventoryConfiguration
        );
        this.removeCoOwnerAction = new RemoveCoOwnerAction(
                this.houseMemberService,
                this.alertHandler,
                this.listOfCoOwnersInventory,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.inventoryConfiguration
        );
        this.changePermissionsAction = new ChangePermissionsAction(
                this.houseMemberService,
                this.changePermissionsInventory,
                this.listOfHouseMembersInventory,
                this.inventoryConfiguration
        );

        this.housePanelInventory = new HousePanelInventory(
                this.scheduler,
                this.addPlayerAction,
                this.addCoOwnerAction,
                this.removePlayerAction,
                this.removeCoOwnerAction,
                this.changePermissionsAction,
                this.confirmInventory, this.messageConfiguration,
                this.inventoryConfiguration,
                this.pluginConfiguration
        );

        this.rentInventory = new RentInventory(
                this.scheduler, this.houseService,
                this.rentService, this.messageConfiguration,
                this.inventoryConfiguration, this.pluginConfiguration,
                this.notificationAnnouncer);

        Stream.of(
                new PurchaseFurnitureInteractController(this.itemsAdderHook, this.houseService, this.pluginConfiguration),
                new HouseInviteController(server, this.houseInviteService, this.messageConfiguration, this.notificationAnnouncer),
                new RentController(this.rentService, this.pluginConfiguration, this.messageConfiguration, this.notificationAnnouncer),
                new AlertController(this.alertService, this.alertHandler, this.pluginConfiguration),
                new PlaceFurnitureController(this.protectionService, this.houseMemberService, this.houseService, this.notificationAnnouncer, this.messageConfiguration),
                new OpenChestController(
                        this.houseService,
                        this.protectionService,
                        this.houseMemberService,
                        this.notificationAnnouncer,
                        this.messageConfiguration,
                        this.pluginConfiguration
                )
        ).forEach(plugin -> this.getServer().getPluginManager().registerEvents(plugin, this));

        this.liteCommands = LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("houses")
                        .nativePermissions(false)
                )
                .message(LiteBukkitMessages.PLAYER_ONLY, input -> this.messageConfiguration.wrongUsage.onlyForPlayer)
                .missingPermission(new MissingPermissionsHandler(this.messageConfiguration, this.notificationAnnouncer))
                .invalidUsage(new InvalidUsageHandler(this.messageConfiguration, this.notificationAnnouncer))
                .commands(
                        new HouseCommand(server, this.itemsAdderHook, this.rentInventory, this.housePanelInventory, this.worldGuard, this.houseService, this.polygonalRegionService, this.messageConfiguration, this.pluginConfiguration, this.notificationAnnouncer)
                )
                .build();
    }

    @Override
    public void onDisable() {
        if (this.audienceProvider != null) {
            this.audienceProvider.close();
        }

        if (this.liteCommands != null) {
            this.liteCommands.getCommandManager().unregisterAll();
        }
    }

}