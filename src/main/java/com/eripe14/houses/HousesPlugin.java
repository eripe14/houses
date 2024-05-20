package com.eripe14.houses;

import com.eripe14.houses.alert.AlertController;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.alert.AlertHandlerImpl;
import com.eripe14.houses.alert.AlertService;
import com.eripe14.houses.command.argument.BlockId;
import com.eripe14.houses.command.argument.BlockIdsResolver;
import com.eripe14.houses.command.argument.HouseResolver;
import com.eripe14.houses.command.handler.InvalidUsageHandler;
import com.eripe14.houses.command.handler.MissingPermissionsHandler;
import com.eripe14.houses.configuration.ConfigurationManager;
import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.history.HistoryUserController;
import com.eripe14.houses.history.HistoryUserService;
import com.eripe14.houses.hook.HookService;
import com.eripe14.houses.hook.implementation.ItemsAdderHook;
import com.eripe14.houses.hook.implementation.VaultHook;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseCommand;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.alarm.AlarmController;
import com.eripe14.houses.house.inventory.action.impl.AddCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.AddPlayerAction;
import com.eripe14.houses.house.inventory.action.impl.BuyHouseAction;
import com.eripe14.houses.house.inventory.action.impl.ChangeOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.ChangePermissionsAction;
import com.eripe14.houses.house.inventory.action.impl.ExtendRentAction;
import com.eripe14.houses.house.inventory.action.impl.RemoveCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.RemovePlayerAction;
import com.eripe14.houses.house.inventory.action.impl.RenovateAction;
import com.eripe14.houses.house.inventory.action.impl.SellHouseAction;
import com.eripe14.houses.house.inventory.impl.ApartamentRenovationInventory;
import com.eripe14.houses.house.inventory.impl.ApartmentListInventory;
import com.eripe14.houses.house.inventory.impl.ChangePermissionsInventory;
import com.eripe14.houses.house.inventory.impl.ConfirmInventory;
import com.eripe14.houses.house.inventory.impl.ExtendRentInventory;
import com.eripe14.houses.house.inventory.impl.ListOfCoOwnersInventory;
import com.eripe14.houses.house.inventory.impl.ListOfHouseMembersInventory;
import com.eripe14.houses.house.inventory.impl.ListOfHousesInventory;
import com.eripe14.houses.house.inventory.impl.MenageRenovationInventory;
import com.eripe14.houses.house.inventory.impl.PurchasedPanelInventory;
import com.eripe14.houses.house.inventory.impl.RenovationAcceptanceInventory;
import com.eripe14.houses.house.inventory.impl.RenovationApplicationsInventory;
import com.eripe14.houses.house.inventory.impl.RenovationInventory;
import com.eripe14.houses.house.inventory.impl.RentInventory;
import com.eripe14.houses.house.inventory.impl.RentedPanelInventory;
import com.eripe14.houses.house.inventory.impl.SelectPurchaseInventory;
import com.eripe14.houses.house.invite.HouseInviteController;
import com.eripe14.houses.house.invite.HouseInviteService;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.panel.HousePanelController;
import com.eripe14.houses.house.purchase.HouseApartmentPurchaseController;
import com.eripe14.houses.house.purchase.HousePurchaseService;
import com.eripe14.houses.house.purchase.HouseSellService;
import com.eripe14.houses.house.purchase.PurchaseFurnitureController;
import com.eripe14.houses.house.region.PolygonalRegionServiceImpl;
import com.eripe14.houses.house.region.protection.ProtectionHandler;
import com.eripe14.houses.house.region.protection.ProtectionHandlerImpl;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.house.region.protection.controller.BreakFurnitureController;
import com.eripe14.houses.house.region.protection.controller.OpenChestController;
import com.eripe14.houses.house.region.protection.controller.OpenDoorController;
import com.eripe14.houses.house.region.protection.controller.PlaceFurnitureController;
import com.eripe14.houses.house.region.protection.controller.RenovationController;
import com.eripe14.houses.house.region.selection.HouseSelectionController;
import com.eripe14.houses.house.region.selection.HouseSelectionService;
import com.eripe14.houses.house.renovation.RenovationApplicationsController;
import com.eripe14.houses.house.renovation.RenovationExpireTask;
import com.eripe14.houses.house.renovation.RenovationInventoryController;
import com.eripe14.houses.house.renovation.RenovationService;
import com.eripe14.houses.house.renovation.request.RenovationRequestService;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceController;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceService;
import com.eripe14.houses.house.rent.RentController;
import com.eripe14.houses.house.rent.RentEndTask;
import com.eripe14.houses.house.rent.RentService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.purchase.PurchaseService;
import com.eripe14.houses.robbery.RobberyService;
import com.eripe14.houses.robbery.RobberyStartHandler;
import com.eripe14.houses.robbery.controller.BreakWindowController;
import com.eripe14.houses.robbery.controller.KickingDoorController;
import com.eripe14.houses.robbery.controller.RobberyController;
import com.eripe14.houses.robbery.controller.RobberyEndController;
import com.eripe14.houses.robbery.controller.RobberyStartController;
import com.eripe14.houses.robbery.lockpicking.LockPickingController;
import com.eripe14.houses.robbery.lockpicking.LockPickingService;
import com.eripe14.houses.robbery.task.RobberySpawnPrincipalNpcTask;
import com.eripe14.houses.robbery.task.RobberySpawnThiefNpcTask;
import com.eripe14.houses.scheduler.BukkitSchedulerImpl;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.schematic.SchematicService;
import com.eripe14.houses.text.ChatTextProvider;
import com.eripe14.houses.util.adventure.LegacyColorProcessor;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.WorldGuard;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.craftcityrp.developerapi.data.DataManager;

import java.time.Duration;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class HousesPlugin extends JavaPlugin {

    private Scheduler scheduler;

    private ConfigurationManager configurationManager;

    private PluginConfiguration pluginConfiguration;
    private MessageConfiguration messageConfiguration;
    private RobberyConfiguration robberyConfiguration;
    private InventoryConfiguration inventoryConfiguration;

    private DataManager dataManager;

    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;
    private NotificationAnnouncer notificationAnnouncer;

    private HookService hookService;

    private Economy economy;

    private ItemsAdderHook itemsAdderHook;

    private WorldEdit worldEdit;
    private WorldGuard worldGuard;

    private PurchaseService purchaseService;

    private HouseSelectionService houseSelectionService;

    private SchematicService schematicService;
    private PolygonalRegionServiceImpl polygonalRegionService;

    private EventCaller eventCaller;

    private HistoryUserService historyUserService;

    private HouseService houseService;
    private HouseInviteService houseInviteService;
    private HouseMemberService houseMemberService;
    private HousePurchaseService housePurchaseService;
    private HouseSellService houseSellService;

    private RentService rentService;

    private ProtectionService protectionService;
    private ProtectionHandler protectionHandler;

    private AlertService alertService;
    private AlertHandler alertHandler;

    private ChatTextProvider textProvider;

    private RenovationRequestService renovationRequestService;
    private RenovationService renovationService;
    private RenovationAcceptanceService renovationAcceptanceService;

    private RobberyService robberyService;
    private RobberyStartHandler robberyStartHandler;

    private LockPickingService lockPickingService;

    private AddPlayerAction addPlayerAction;
    private AddCoOwnerAction addCoOwnerAction;
    private RemovePlayerAction removePlayerAction;
    private RemoveCoOwnerAction removeCoOwnerAction;
    private ChangePermissionsAction changePermissionsAction;
    private ExtendRentAction extendRentAction;
    private ChangeOwnerAction changeOwnerAction;
    private SellHouseAction sellHouseAction;
    private RenovateAction renovateAction;
    private BuyHouseAction buyHouseAction;

    private ConfirmInventory confirmInventory;
    private SelectPurchaseInventory selectPurchaseInventory;
    private ListOfHouseMembersInventory listOfHouseMembersInventory;
    private ListOfCoOwnersInventory listOfCoOwnersInventory;
    private ChangePermissionsInventory changePermissionsInventory;
    private ExtendRentInventory extendRentInventory;
    private RenovationInventory renovationInventory;
    private ApartamentRenovationInventory apartamentRenovationInventory;
    private RenovationApplicationsInventory renovationApplicationsInventory;
    private MenageRenovationInventory menageRenovationInventory;
    private RenovationAcceptanceInventory renovationAcceptanceInventory;
    private PurchasedPanelInventory purchasedPanelInventory;
    private RentedPanelInventory rentedPanelInventory;
    private RentInventory rentInventory;
    private ApartmentListInventory apartmentListInventory;
    private ListOfHousesInventory listOfHousesInventory;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Server server = this.getServer();
        Logger logger = this.getLogger();

        this.scheduler = new BukkitSchedulerImpl(this);

        this.configurationManager = new ConfigurationManager(this.getDataFolder());

        this.pluginConfiguration = this.configurationManager.load(new PluginConfiguration());
        this.messageConfiguration = this.configurationManager.load(new MessageConfiguration());
        this.robberyConfiguration = this.configurationManager.load(new RobberyConfiguration());
        this.inventoryConfiguration = this.configurationManager.load(new InventoryConfiguration());

        this.dataManager = new DataManager(this.pluginConfiguration.serviceId, this.pluginConfiguration.serviceToken);

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

        this.houseSelectionService = new HouseSelectionService();

        this.protectionService = new ProtectionService(this.worldGuard);

        this.schematicService = new SchematicService(this, this.worldEdit);
        this.polygonalRegionService = new PolygonalRegionServiceImpl(
                server,
                this.scheduler,
                this.worldEdit,
                this.worldGuard,
                this.houseSelectionService,
                this.protectionService,
                this.pluginConfiguration
        );

        this.eventCaller = new EventCaller(server);

        this.historyUserService = new HistoryUserService(this.dataManager);

        this.renovationRequestService = new RenovationRequestService(this.dataManager);
        this.renovationService = new RenovationService(this.dataManager);
        this.renovationAcceptanceService = new RenovationAcceptanceService(this.dataManager);

        this.houseService = new HouseService(
                this.protectionService,
                this.dataManager,
                server
        );
        this.houseInviteService = new HouseInviteService(this.eventCaller, this.pluginConfiguration);
        this.houseMemberService = new HouseMemberService(this.houseService, this.pluginConfiguration);
        this.housePurchaseService = new HousePurchaseService(
                this.houseService,
                this.polygonalRegionService,
                this.purchaseService, this.pluginConfiguration
        );
        this.houseSellService = new HouseSellService(
                this.houseService,
                this.renovationService,
                this.renovationRequestService,
                this.renovationAcceptanceService,
                this.itemsAdderHook,
                this.schematicService,
                this.purchaseService,
                this.polygonalRegionService,
                this.pluginConfiguration
        );

        this.rentService = new RentService(this.dataManager);

        this.protectionHandler = new ProtectionHandlerImpl(
                this.houseService,
                this.houseMemberService,
                this.protectionService,
                this.itemsAdderHook,
                this.pluginConfiguration
        );

        this.alertService = new AlertService(this.dataManager);
        this.alertHandler = new AlertHandlerImpl(
                server,
                this.alertService,
                this.scheduler,
                this.messageConfiguration,
                this.notificationAnnouncer
        );

        this.textProvider = new ChatTextProvider(this.pluginConfiguration);

        this.lockPickingService = new LockPickingService();

        this.robberyService = new RobberyService();
        this.robberyStartHandler = new RobberyStartHandler(
                server,
                this.houseService,
                this.robberyService,
                this.alertHandler,
                this.messageConfiguration,
                this.robberyConfiguration
        );

        this.scheduler.timerAsync(
                new RenovationExpireTask(
                        server,
                        this.renovationService,
                        this.renovationAcceptanceService,
                        this.schematicService,
                        this.houseService,
                        this.alertHandler,
                        this.messageConfiguration,
                        this.pluginConfiguration
                ), Duration.ZERO, this.pluginConfiguration.renovationExpireTaskFrequency
        );

        this.scheduler.timerSync(
                new RentEndTask(
                        this.rentService,
                        this.houseService,
                        this.houseSellService,
                        this.alertHandler,
                        this.messageConfiguration
                ), Duration.ZERO, this.pluginConfiguration.rentExpireTaskFrequency
        );

        this.scheduler.timerSync(
                new RobberySpawnPrincipalNpcTask(this.robberyConfiguration),
                Duration.ofSeconds(2),
                this.robberyConfiguration.npcRefreshRate
        );

        this.scheduler.timerSync(
                new RobberySpawnThiefNpcTask(this.robberyConfiguration),
                Duration.ofSeconds(2),
                this.robberyConfiguration.npcRefreshRate
        );

        this.confirmInventory = new ConfirmInventory(
                this.scheduler,
                this.inventoryConfiguration
        );
        this.extendRentInventory = new ExtendRentInventory(
                this.scheduler,
                this.purchaseService,
                this.rentService,
                this.houseService,
                this.confirmInventory,
                this.notificationAnnouncer,
                this.inventoryConfiguration,
                this.messageConfiguration
        );
        this.rentInventory = new RentInventory(
                this.scheduler,
                this.purchaseService,
                this.housePurchaseService,
                this.houseService,
                this.rentService,
                this.polygonalRegionService,
                this.confirmInventory,
                this.schematicService,
                this.messageConfiguration,
                this.inventoryConfiguration,
                this.pluginConfiguration,
                this.notificationAnnouncer
        );
        this.selectPurchaseInventory = new SelectPurchaseInventory(
                this.scheduler,
                this.housePurchaseService,
                this.confirmInventory,
                this.rentInventory,
                this.notificationAnnouncer,
                this.inventoryConfiguration,
                this.messageConfiguration
        );
        this.listOfHouseMembersInventory = new ListOfHouseMembersInventory(
                this.scheduler,
                server,
                this.confirmInventory,
                this.inventoryConfiguration,
                this.notificationAnnouncer,
                this.messageConfiguration
        );
        this.listOfCoOwnersInventory = new ListOfCoOwnersInventory(
                this.scheduler,
                server,
                this.confirmInventory,
                this.inventoryConfiguration,
                this.notificationAnnouncer,
                this.messageConfiguration
        );
        this.changePermissionsInventory = new ChangePermissionsInventory(
                this.scheduler,
                this.houseMemberService,
                this.alertHandler,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.inventoryConfiguration
        );
        this.renovationInventory = new RenovationInventory(
                this.scheduler,
                server,
                this.renovationRequestService,
                this.houseService,
                this.textProvider,
                this.alertHandler,
                this.purchaseService,
                this.confirmInventory,
                this.notificationAnnouncer,
                this.inventoryConfiguration,
                this.messageConfiguration,
                this.pluginConfiguration
        );
        this.apartamentRenovationInventory = new ApartamentRenovationInventory(
                server,
                this.scheduler,
                this.renovationRequestService,
                this.houseService,
                this.textProvider,
                this.purchaseService,
                this.confirmInventory,
                this.alertHandler,
                this.notificationAnnouncer,
                this.inventoryConfiguration,
                this.messageConfiguration,
                this.pluginConfiguration
        );
        this.renovationApplicationsInventory = new RenovationApplicationsInventory(
                this.scheduler,
                this.renovationRequestService,
                this.renovationService,
                this.houseService,
                this.alertHandler,
                this.schematicService,
                this.confirmInventory,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.inventoryConfiguration
        );
        this.menageRenovationInventory = new MenageRenovationInventory(
                server,
                this.scheduler,
                this.renovationService,
                this.renovationAcceptanceService,
                this.houseService,
                this.alertHandler,
                this.schematicService,
                this.messageConfiguration,
                this.inventoryConfiguration,
                this.pluginConfiguration
        );
        this.renovationAcceptanceInventory = new RenovationAcceptanceInventory(
                this.scheduler,
                this.alertHandler,
                this.renovationAcceptanceService,
                this.houseService,
                this.schematicService,
                this.confirmInventory,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.inventoryConfiguration
        );
        this.apartmentListInventory = new ApartmentListInventory(
                this.scheduler,
                this.houseService,
                this.selectPurchaseInventory,
                this.inventoryConfiguration
        );
        this.listOfHousesInventory = new ListOfHousesInventory(
                this.scheduler,
                this.houseService,
                this.apartamentRenovationInventory,
                this.renovationInventory,
                this.menageRenovationInventory,
                this.inventoryConfiguration
        );

        this.addPlayerAction = new AddPlayerAction(
                this.houseMemberService,
                this.houseInviteService,
                this.confirmInventory,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.pluginConfiguration,
                this.inventoryConfiguration
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
                this.historyUserService,
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
        this.extendRentAction = new ExtendRentAction(
                this.extendRentInventory
        );
        this.changeOwnerAction = new ChangeOwnerAction(
                this.houseMemberService,
                this.houseInviteService,
                this.rentService,
                this.confirmInventory,
                this.notificationAnnouncer,
                this.pluginConfiguration,
                this.messageConfiguration,
                this.inventoryConfiguration
        );
        this.sellHouseAction = new SellHouseAction(
                this.houseSellService,
                this.historyUserService,
                this.notificationAnnouncer,
                this.confirmInventory,
                this.protectionService,
                this.messageConfiguration,
                this.inventoryConfiguration,
                this.pluginConfiguration
        );
        this.renovateAction = new RenovateAction(
                this.renovationInventory,
                this.renovationAcceptanceService,
                this.apartamentRenovationInventory,
                this.menageRenovationInventory,
                this.notificationAnnouncer,
                this.messageConfiguration
        );
        this.buyHouseAction = new BuyHouseAction(
                this.housePurchaseService,
                this.houseService,
                this.rentService,
                this.confirmInventory,
                this.notificationAnnouncer,
                this.messageConfiguration,
                this.inventoryConfiguration
        );

        this.rentedPanelInventory = new RentedPanelInventory(
                this.scheduler,
                this.addPlayerAction,
                this.addCoOwnerAction,
                this.removePlayerAction,
                this.removeCoOwnerAction,
                this.changePermissionsAction,
                this.extendRentAction,
                this.changeOwnerAction,
                this.buyHouseAction,
                this.houseMemberService,
                this.menageRenovationInventory,
                this.inventoryConfiguration,
                this.notificationAnnouncer,
                this.pluginConfiguration,
                this.messageConfiguration
        );
        this.purchasedPanelInventory = new PurchasedPanelInventory(
                this.scheduler,
                this.houseMemberService,
                this.addPlayerAction,
                this.addCoOwnerAction,
                this.removePlayerAction,
                this.removeCoOwnerAction,
                this.changePermissionsAction,
                this.changeOwnerAction,
                this.sellHouseAction,
                this.menageRenovationInventory,
                this.notificationAnnouncer,
                this.inventoryConfiguration,
                this.messageConfiguration,
                this.pluginConfiguration
        );

        Stream.of(
                new PurchaseFurnitureController(
                        this.robberyService,
                        this.selectPurchaseInventory,
                        this.rentInventory,
                        this.houseService,
                        this.protectionService,
                        this.notificationAnnouncer,
                        this.pluginConfiguration,
                        this.messageConfiguration
                ),
                new HouseInviteController(
                        server,
                        this.houseInviteService,
                        this.confirmInventory,
                        this.messageConfiguration,
                        this.notificationAnnouncer,
                        this.pluginConfiguration,
                        this.inventoryConfiguration
                ),
                new RentController(this.rentService, this.pluginConfiguration, this.alertHandler, this.messageConfiguration),
                new AlertController(this.alertService, this.alertHandler, this.pluginConfiguration),
                new RenovationController(
                        this.protectionHandler,
                        this.protectionService,
                        this.houseService,
                        this.pluginConfiguration
                ),
                new PlaceFurnitureController(
                        this.protectionHandler,
                        this.protectionService,
                        this.houseService,
                        this.notificationAnnouncer,
                        this.messageConfiguration,
                        this.pluginConfiguration
                ),
                new BreakFurnitureController(
                        this.protectionHandler,
                        this.notificationAnnouncer,
                        this.messageConfiguration
                ),
                new OpenChestController(
                        this.protectionHandler,
                        this.notificationAnnouncer,
                        this.messageConfiguration,
                        this.pluginConfiguration
                ),
                new OpenDoorController(
                        this.protectionHandler,
                        this.notificationAnnouncer,
                        this.messageConfiguration,
                        this.pluginConfiguration
                ),
                new HousePanelController(
                        this.houseService,
                        this.houseMemberService,
                        this.protectionService,
                        this.rentedPanelInventory,
                        this.purchasedPanelInventory,
                        this.pluginConfiguration
                ),
                this.textProvider,
                new RenovationApplicationsController(
                        this.renovationApplicationsInventory,
                        this.pluginConfiguration
                ),
                new RenovationAcceptanceController(
                        this,
                        this.alertHandler,
                        this.renovationAcceptanceService,
                        this.renovationAcceptanceInventory,
                        this.messageConfiguration,
                        this.pluginConfiguration
                ),
                new KickingDoorController(
                        this,
                        server,
                        this.notificationAnnouncer,
                        this.robberyConfiguration,
                        this.pluginConfiguration,
                        this.robberyService,
                        this.robberyStartHandler,
                        this.houseService,
                        this.protectionService,
                        this.messageConfiguration
                ),
                new BreakWindowController(
                        this,
                        this.robberyService,
                        this.robberyStartHandler,
                        this.houseService,
                        this.protectionService,
                        this.notificationAnnouncer,
                        this.robberyConfiguration,
                        this.messageConfiguration
                ),
                new LockPickingController(
                        this,
                        this.lockPickingService,
                        this.robberyService,
                        this.robberyStartHandler,
                        this.houseService,
                        this.protectionService,
                        this.notificationAnnouncer,
                        this.robberyConfiguration,
                        this.pluginConfiguration,
                        this.messageConfiguration
                ),
                new RobberyStartController(
                        this.robberyStartHandler,
                        this.robberyService,
                        this.notificationAnnouncer,
                        this.messageConfiguration,
                        this.robberyConfiguration
                ),
                new RobberyController(
                        this,
                        this.houseService,
                        this.protectionService,
                        this.robberyService,
                        this.notificationAnnouncer,
                        this.messageConfiguration,
                        this.robberyConfiguration
                ),
                new RobberyEndController(
                        this.robberyService,
                        this.purchaseService,
                        this.houseService,
                        this.alertHandler,
                        this.notificationAnnouncer,
                        this.robberyConfiguration,
                        this.messageConfiguration
                ),
                new AlarmController(
                        this.houseService,
                        this.protectionService,
                        this.pluginConfiguration
                ),
                new DataSaveController(this.dataManager),
                new HouseSelectionController(this.houseSelectionService),
                new HouseApartmentPurchaseController(
                        this.apartmentListInventory,
                        this.protectionService,
                        this.houseService,
                        this.pluginConfiguration
                ),
                new RenovationInventoryController(
                        this.listOfHousesInventory,
                        this.pluginConfiguration
                ),
                new HistoryUserController(this.historyUserService)
        ).forEach(plugin -> this.getServer().getPluginManager().registerEvents(plugin, this));

        this.liteCommands = LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("houses")
                        .nativePermissions(false)
                )
                .message(LiteBukkitMessages.PLAYER_ONLY, input -> this.messageConfiguration.wrongUsage.onlyForPlayer)
                .missingPermission(new MissingPermissionsHandler(this.messageConfiguration, this.notificationAnnouncer))
                .invalidUsage(new InvalidUsageHandler(this.messageConfiguration, this.notificationAnnouncer))
                .argument(House.class, new HouseResolver(this.houseService))
                .argument(BlockId.class, new BlockIdsResolver(this.houseService))
                .argumentSuggestion(
                        String.class,
                        ArgumentKey.of("schematic-name"),
                        SuggestionResult.of(this.schematicService.getSchematicNames())
                )
                .argumentSuggestion(
                        Integer.class,
                        ArgumentKey.of("rental-price"),
                        SuggestionResult.of("cena-wynajmu")
                )
                .argumentSuggestion(
                        Integer.class,
                        ArgumentKey.of("buy-price"),
                        SuggestionResult.of("cena-kupna-opcjonalnie")
                )
                .argumentSuggestion(
                        String.class,
                        ArgumentKey.of("house-id"),
                        SuggestionResult.of(
                                this.houseService.getAllHouses().stream()
                                        .map(House::getHouseId)
                                        .toList()
                        )
                )
                .commands(
                        new HouseCommand(
                                this.houseService,
                                this.historyUserService,
                                this.houseMemberService,
                                this.rentService,
                                this.renovationService,
                                this.renovationRequestService,
                                this.renovationAcceptanceService,
                                this.schematicService,
                                this.polygonalRegionService,
                                this.dataManager,
                                this.worldGuard,
                                this.messageConfiguration,
                                this.pluginConfiguration,
                                this.robberyConfiguration,
                                this.notificationAnnouncer,
                                this.configurationManager
                        )
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