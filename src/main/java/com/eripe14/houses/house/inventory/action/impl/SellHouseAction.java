package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.history.HistorySell;
import com.eripe14.houses.history.HistoryUser;
import com.eripe14.houses.history.HistoryUserService;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ConfirmInventory;
import com.eripe14.houses.house.purchase.HouseSellService;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.position.PositionAdapter;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class SellHouseAction implements InventoryClickAction {

    private final HouseSellService houseSellService;
    private final HistoryUserService historyUserService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final ConfirmInventory confirmInventory;
    private final ProtectionService protectionService;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public SellHouseAction(
            HouseSellService houseSellService,
            HistoryUserService historyUserService,
            NotificationAnnouncer notificationAnnouncer,
            ConfirmInventory confirmInventory,
            ProtectionService protectionService,
            MessageConfiguration messageConfiguration,
            InventoryConfiguration inventoryConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.houseSellService = houseSellService;
        this.historyUserService = historyUserService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.confirmInventory = confirmInventory;
        this.protectionService = protectionService;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        return (uuid) -> {
            Formatter formatter = new Formatter();
            formatter.register("{SELL_PRICE}", this.houseSellService.getRefundMoney(player, house));
            formatter.register("{HOUSE_ID}", house.getHouseId());

            Consumer<UUID> confirmAction = (secondUuidVariable) -> {
                if (house.getRegion().getType() == HouseType.APARTMENT) {
                    player.teleport(this.protectionService.getCenterOfRegion(this.protectionService.getRegion(house.getBlockOfFlatsId())));
                } else {
                    player.teleport(PositionAdapter.convert(house.getRegion().getPurchaseFurniture().getPosition()));
                }

                this.houseSellService.sellHouse(player, house);
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.soldHouse, formatter);
                gui.close(player);

                Option<HistoryUser> userOption = this.historyUserService.getUser(player.getUniqueId());

                if (userOption.isEmpty()) {
                    return;
                }

                HistoryUser user = userOption.get();
                HistorySell historySell = new HistorySell(house.getHouseId(), this.houseSellService.getRefundMoney(player, house));
                user.addHistoryPurchase(historySell);

                this.historyUserService.addUser(user);
            };

            Consumer<Player> cancelAction = gui::close;

            this.confirmInventory.openInventory(
                    player,
                    this.inventoryConfiguration.confirm.confirmSellTitle,
                    Option.none(),
                    this.inventoryConfiguration.confirm.sellHouseAdditionalItem,
                    confirmAction,
                    cancelAction,
                    formatter
            );
        };
    }

}