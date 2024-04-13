package com.eripe14.houses.house.region.protection;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.owner.Owner;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
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

    private final HouseService houseService;
    private final HouseMemberService houseMemberService;
    private final ProtectionService protectionService;

    public ProtectionHandlerImpl(HouseService houseService, HouseMemberService houseMemberService, ProtectionService protectionService) {
        this.houseService = houseService;
        this.houseMemberService = houseMemberService;
        this.protectionService = protectionService;
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

        ProtectionInteractResult cancelEvent = new ProtectionInteractResult(true);
        ProtectionInteractResult notCancelEvent = new ProtectionInteractResult(false);

        if (event.getHand() != EquipmentSlot.HAND) {
            return resultCompletable.complete(notCancelEvent);
        }

        if (clickedBlock == null) {
            return resultCompletable.complete(notCancelEvent);
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return resultCompletable.complete(notCancelEvent);
        }

        Location blockLocation = clickedBlock.getLocation();
        Material blockMaterial = clickedBlock.getType();

        if (!interactableBlockMaterials.contains(blockMaterial)) {
            return resultCompletable.complete(notCancelEvent);
        }

        Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(blockLocation);

        if (houseRegionOption.isEmpty()) {
            return resultCompletable.complete(notCancelEvent);
        }

        ProtectedRegion houseRegion = houseRegionOption.get();
        Option<House> houseOption = this.houseService.getHouse(houseRegion);

        if (houseOption.isEmpty()) {
            return resultCompletable.complete(notCancelEvent);
        }

        House house = houseOption.get();
        Option<Owner> ownerOption = house.getOwner();
        Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuid);

        if (ownerOption.isEmpty()) {
            return resultCompletable.complete(cancelEvent);
        }

        Owner owner = ownerOption.get();

        if (owner.getUuid().equals(uuid)) {
            return resultCompletable.complete(notCancelEvent);
        }

        if (houseMemberOption.isEmpty()) {
            return resultCompletable.complete(cancelEvent);
        }

        HouseMember houseMember = houseMemberOption.get();

        if (this.houseMemberService.hasPermission(houseMember, permission)) {
            return resultCompletable.complete(notCancelEvent);
        }

        return resultCompletable.complete(cancelEvent);
    }

    @Override
    public Completable<ProtectionInteractResult> canBuild(BlockPlaceEvent event, Player player, HouseMemberPermission permission) {
        Completable<ProtectionInteractResult> resultCompletable = Completable.create();

        UUID uuid = player.getUniqueId();
        Block blockPlaced = event.getBlockPlaced();
        Location blockLocation = blockPlaced.getLocation();
        ItemStack itemInHand = event.getItemInHand();

        ProtectionInteractResult cancelEvent = new ProtectionInteractResult(true);
        ProtectionInteractResult notCancelEvent = new ProtectionInteractResult(false);
        /* NullPointer
        CustomStack customStack = CustomStack.byItemStack(itemInHand);

        if (!CustomStack.isInRegistry(customStack.getNamespacedID())) {
            return resultCompletable.complete(cancelEvent);
        }
         */

        Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(blockLocation);

        if (houseRegionOption.isEmpty()) {
            return resultCompletable.complete(notCancelEvent);
        }

        ProtectedRegion houseRegion = houseRegionOption.get();
        Option<House> houseOption = this.houseService.getHouse(houseRegion);

        if (houseOption.isEmpty()) {
            return resultCompletable.complete(notCancelEvent);
        }

        House house = houseOption.get();
        Option<Owner> ownerOption = house.getOwner();
        Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuid);

        if (ownerOption.isEmpty()) {
            return resultCompletable.complete(cancelEvent);
        }

        Owner owner = ownerOption.get();

        if (owner.getUuid().equals(uuid)) {
            return resultCompletable.complete(notCancelEvent);
        }

        if (houseMemberOption.isEmpty()) {
            return resultCompletable.complete(cancelEvent);
        }

        HouseMember houseMember = houseMemberOption.get();

        if (this.houseMemberService.hasPermission(houseMember, permission)) {
            return resultCompletable.complete(notCancelEvent);
        }

        return resultCompletable.complete(cancelEvent);
    }

}