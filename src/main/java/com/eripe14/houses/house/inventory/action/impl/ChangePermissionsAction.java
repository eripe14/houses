package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ChangePermissionsInventory;
import com.eripe14.houses.house.inventory.impl.ListOfHouseMembersInventory;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberService;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.UUID;
import java.util.function.Consumer;

public class ChangePermissionsAction implements InventoryClickAction {

    private final HouseMemberService houseMemberService;
    private final ChangePermissionsInventory changePermissionsInventory;
    private final ListOfHouseMembersInventory listOfHouseMembersInventory;
    private final InventoryConfiguration inventoryConfiguration;

    public ChangePermissionsAction(
            HouseMemberService houseMemberService,
            ChangePermissionsInventory changePermissionsInventory,
            ListOfHouseMembersInventory listOfHouseMembersInventory,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.houseMemberService = houseMemberService;
        this.changePermissionsInventory = changePermissionsInventory;
        this.listOfHouseMembersInventory = listOfHouseMembersInventory;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        Consumer<UUID> changeMemberPermissions = (houseMemberUuid) -> {
            Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, houseMemberUuid);

            if (houseMemberOption.isEmpty()) {
                return;
            }

            HouseMember houseMember = houseMemberOption.get();
            this.changePermissionsInventory.openInventory(player, house, houseMember);
        };

        return (actionPlayer) -> this.listOfHouseMembersInventory.openInventory(
                player,
                house,
                this.inventoryConfiguration.changePermission.listOfPlayersTitle,
                changeMemberPermissions
        );
    }

}