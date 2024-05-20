package com.eripe14.houses.house.region.protection;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.hook.implementation.ItemsAdderHook;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.renovation.Renovation;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;
import panda.std.reactive.Completable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProtectionHandlerImpl implements ProtectionHandler {

    private final ProtectionInteractResult cancelEvent = new ProtectionInteractResult(ProtectionCause.CANCEL_EVENT_WITH_MESSAGE);
    private final ProtectionInteractResult cancelEventWithoutMessage = new ProtectionInteractResult(ProtectionCause.CANCEL_EVENT_WITHOUT_MESSAGE);
    private final ProtectionInteractResult notCancelEvent = new ProtectionInteractResult(ProtectionCause.NOT_CANCEL_EVENT_WITHOUT_MESSAGE);
    private final ProtectionInteractResult renovateComplete = new ProtectionInteractResult(ProtectionCause.RENOVATE_COMPLETE);
    private final ProtectionInteractResult renovateMajor = new ProtectionInteractResult(ProtectionCause.RENOVATE_MAJOR);
    private final ProtectionInteractResult renovateNonInterfering = new ProtectionInteractResult(ProtectionCause.RENOVATE_NON_INTERFERING);
    private final HouseService houseService;
    private final HouseMemberService houseMemberService;
    private final ProtectionService protectionService;
    private final ItemsAdderHook itemsAdderHook;
    private final PluginConfiguration pluginConfiguration;

    public ProtectionHandlerImpl(
            HouseService houseService,
            HouseMemberService houseMemberService,
            ProtectionService protectionService,
            ItemsAdderHook itemsAdderHook,
            PluginConfiguration pluginConfiguration
    ) {
        this.houseService = houseService;
        this.houseMemberService = houseMemberService;
        this.protectionService = protectionService;
        this.itemsAdderHook = itemsAdderHook;
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public Completable<ProtectionInteractResult> canInteractWithBlocks(
            PlayerInteractEvent event,
            List<Material> interactableBlockMaterials,
            Player player,
            HouseMemberPermission permission
    ) {
        Completable<ProtectionInteractResult> resultCompletable = Completable.create();

        UUID uuid = player.getUniqueId();
        Block clickedBlock = event.getClickedBlock();

        if (event.getHand() != EquipmentSlot.HAND) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        if (clickedBlock == null) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        if (player.hasPermission(this.pluginConfiguration.bypassPermission)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        Location blockLocation = clickedBlock.getLocation();
        Material blockMaterial = clickedBlock.getType();

        if (!interactableBlockMaterials.contains(blockMaterial)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(blockLocation);

        if (houseRegionOption.isEmpty()) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        ProtectedRegion houseRegion = houseRegionOption.get();
        Option<House> houseOption = this.houseService.getHouse(houseRegion);

        if (houseOption.isEmpty()) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        House house = houseOption.get();
        Option<Owner> ownerOption = house.getOwner();
        Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuid);

        if (ownerOption.isEmpty()) {
            return resultCompletable.complete(this.cancelEvent);
        }

        Owner owner = ownerOption.get();

        if (owner.getUuid().equals(uuid)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        if (houseMemberOption.isEmpty()) {
            return resultCompletable.complete(this.cancelEvent);
        }

        HouseMember houseMember = houseMemberOption.get();

        if (this.houseMemberService.hasPermission(houseMember, permission)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        return resultCompletable.complete(this.cancelEvent);
    }


    @Override
    public Completable<ProtectionInteractResult> canPlaceBlock(BlockPlaceEvent event, Player player) {
        Completable<ProtectionInteractResult> resultCompletable = Completable.create();

        UUID uuid = player.getUniqueId();
        ItemStack itemInUse = player.getInventory().getItemInMainHand();
        Block blockPlaced = event.getBlock();
        Location blockLocation = blockPlaced.getLocation();

        if (player.hasPermission(this.pluginConfiguration.bypassPermission)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(blockLocation);

        if (houseRegionOption.isEmpty()) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        ProtectedRegion houseRegion = houseRegionOption.get();
        Option<House> houseOption = this.houseService.getHouse(houseRegion);

        if (houseOption.isEmpty()) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        House house = houseOption.get();
        Option<Owner> ownerOption = house.getOwner();
        Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuid);
        Option<Renovation> currentRenovation = house.getCurrentRenovation();

        if (ownerOption.isEmpty()) {
            if (!this.itemsAdderHook.isItemsAdderCustomRecipe(itemInUse)) {
                return resultCompletable.complete(this.cancelEventWithoutMessage);
            }

            return resultCompletable.complete(this.cancelEvent);
        }

        Owner owner = ownerOption.get();

        if (owner.getUuid().equals(uuid)) {
            if (currentRenovation.isPresent()) {
                Renovation renovation = currentRenovation.get();

                switch (renovation.getRenovationType()) {
                    case COMPLETE -> {
                        return resultCompletable.complete(this.renovateComplete);
                    }
                    case MAJOR -> {
                        return resultCompletable.complete(this.renovateMajor);
                    }
                    case NON_INTERFERING -> {
                        return resultCompletable.complete(this.renovateNonInterfering);
                    }
                }
            }

            if (!this.itemsAdderHook.isItemsAdderCustomRecipe(itemInUse)) {
                return resultCompletable.complete(this.cancelEventWithoutMessage);
            }

            return resultCompletable.complete(this.notCancelEvent);
        }

        if (houseMemberOption.isEmpty()) {
            if (!this.itemsAdderHook.isItemsAdderCustomRecipe(itemInUse)) {
                return resultCompletable.complete(this.cancelEventWithoutMessage);
            }

            return resultCompletable.complete(this.cancelEvent);
        }

        HouseMember houseMember = houseMemberOption.get();

        if (currentRenovation.isPresent()) {
            Renovation renovation = currentRenovation.get();

            if (!this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.RENOVATE)) {
                if (this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.PLACE_FURNITURE)) {
                    if (!this.itemsAdderHook.isItemsAdderCustomRecipe(itemInUse)) {
                        return resultCompletable.complete(this.cancelEventWithoutMessage);
                    }

                    return resultCompletable.complete(this.notCancelEvent);
                }

                if (!this.itemsAdderHook.isItemsAdderCustomRecipe(itemInUse)) {
                    return resultCompletable.complete(this.cancelEventWithoutMessage);
                }

                return resultCompletable.complete(this.cancelEvent);
            }

            if (!this.itemsAdderHook.isItemsAdderCustomRecipe(itemInUse)) {
                switch (renovation.getRenovationType()) {
                    case COMPLETE -> {
                        return resultCompletable.complete(this.renovateComplete);
                    }
                    case MAJOR -> {
                        return resultCompletable.complete(this.renovateMajor);
                    }
                    case NON_INTERFERING -> {
                        return resultCompletable.complete(this.renovateNonInterfering);
                    }
                }
            }

            if (this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.PLACE_FURNITURE)) {
                return resultCompletable.complete(this.notCancelEvent);
            }

            return resultCompletable.complete(this.cancelEvent);
        }

        if (this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.PLACE_FURNITURE)) {
            if (!this.itemsAdderHook.isItemsAdderCustomRecipe(itemInUse)) {
                return resultCompletable.complete(this.cancelEventWithoutMessage);
            }

            return resultCompletable.complete(this.notCancelEvent);
        }

        if (!this.itemsAdderHook.isItemsAdderCustomRecipe(itemInUse)) {
            return resultCompletable.complete(this.cancelEventWithoutMessage);
        }

        return resultCompletable.complete(this.cancelEvent);
    }

    @Override
    public Completable<ProtectionInteractResult> canBreakBlock(BlockBreakEvent event, Player player) {
        Completable<ProtectionInteractResult> resultCompletable = Completable.create();

        UUID uuid = player.getUniqueId();
        Block blockPlaced = event.getBlock();
        Location blockLocation = blockPlaced.getLocation();

        if (player.hasPermission(this.pluginConfiguration.bypassPermission)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(blockLocation);

        if (houseRegionOption.isEmpty()) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        ProtectedRegion houseRegion = houseRegionOption.get();
        Option<House> houseOption = this.houseService.getHouse(houseRegion);

        if (houseOption.isEmpty()) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        House house = houseOption.get();
        Option<Owner> ownerOption = house.getOwner();
        Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuid);
        Option<Renovation> currentRenovation = house.getCurrentRenovation();

        if (ownerOption.isEmpty()) {
            return resultCompletable.complete(this.cancelEventWithoutMessage);
        }

        Owner owner = ownerOption.get();

        if (owner.getUuid().equals(uuid)) {
            if (currentRenovation.isPresent()) {
                Renovation renovation = currentRenovation.get();

                switch (renovation.getRenovationType()) {
                    case COMPLETE -> {
                        return resultCompletable.complete(this.renovateComplete);
                    }
                    case MAJOR -> {
                        return resultCompletable.complete(this.renovateMajor);
                    }
                    case NON_INTERFERING -> {
                        return resultCompletable.complete(this.renovateNonInterfering);
                    }
                }
            }

            return resultCompletable.complete(this.cancelEventWithoutMessage);
        }

        if (houseMemberOption.isEmpty()) {
            return resultCompletable.complete(this.cancelEventWithoutMessage);
        }

        HouseMember houseMember = houseMemberOption.get();

        if (currentRenovation.isPresent()) {
            Renovation renovation = currentRenovation.get();

            if (!this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.RENOVATE)) {
                return resultCompletable.complete(this.cancelEventWithoutMessage);
            }

            switch (renovation.getRenovationType()) {
                case COMPLETE -> {
                    return resultCompletable.complete(this.renovateComplete);
                }
                case MAJOR -> {
                    return resultCompletable.complete(this.renovateMajor);
                }
                case NON_INTERFERING -> {
                    return resultCompletable.complete(this.renovateNonInterfering);
                }
            }
        }

        return resultCompletable.complete(this.cancelEventWithoutMessage);
    }

    @Override
    public Completable<ProtectionInteractResult> canBreakFurniture(FurnitureBreakEvent event, Player player, HouseMemberPermission permission) {
        Completable<ProtectionInteractResult> resultCompletable = Completable.create();

        UUID uuid = player.getUniqueId();
        CustomFurniture furniture = event.getFurniture();

        if (furniture == null) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        Entity armorstand = furniture.getArmorstand();

        if (armorstand == null) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        if (player.hasPermission(this.pluginConfiguration.bypassPermission)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        Location blockLocation = armorstand.getLocation();

        Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(blockLocation);

        if (houseRegionOption.isEmpty()) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        ProtectedRegion houseRegion = houseRegionOption.get();
        Option<House> houseOption = this.houseService.getHouse(houseRegion);

        if (houseOption.isEmpty()) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        House house = houseOption.get();
        Option<Owner> ownerOption = house.getOwner();
        Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuid);

        if (ownerOption.isEmpty()) {
            return resultCompletable.complete(this.cancelEvent);
        }

        Owner owner = ownerOption.get();

        if (owner.getUuid().equals(uuid)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        if (houseMemberOption.isEmpty()) {
            return resultCompletable.complete(this.cancelEvent);
        }

        HouseMember houseMember = houseMemberOption.get();

        if (this.houseMemberService.hasPermission(houseMember, permission)) {
            return resultCompletable.complete(this.notCancelEvent);
        }

        return resultCompletable.complete(this.cancelEvent);
    }

}