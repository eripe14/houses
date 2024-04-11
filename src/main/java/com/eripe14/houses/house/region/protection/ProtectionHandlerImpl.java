package com.eripe14.houses.house.region.protection;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.owner.Owner;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import panda.std.Option;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<ProtectionInteractResult> canInteract(PlayerInteractEvent event, org.bukkit.Location clickedBlockLocation, Player player, HouseMemberPermission permission) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = player.getUniqueId();

            ProtectionInteractResult notAllowed = new ProtectionInteractResult(false, false);
            ProtectionInteractResult notAllowedWithMessage = new ProtectionInteractResult(true, false);
            ProtectionInteractResult allowed = new ProtectionInteractResult(true, true);

            if (event.getHand() != EquipmentSlot.HAND) {
                return notAllowed;
            }

            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return notAllowed;
            }

            Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(clickedBlockLocation);

            if (houseRegionOption.isEmpty()) {
                return notAllowed;
            }

            ProtectedRegion houseRegion = houseRegionOption.get();
            Option<House> houseOption = this.houseService.getHouse(houseRegion);

            if (houseOption.isEmpty()) {
                return notAllowed;
            }

            House house = houseOption.get();
            Owner owner = house.getOwner().get();

            if (owner.getUuid().equals(uuid)) {
                return notAllowed;
            }

            Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuid);

            if (houseMemberOption.isEmpty()) {
                event.setCancelled(true);
                return notAllowedWithMessage;
            }

            HouseMember houseMember = houseMemberOption.get();

            if (this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.OPEN_CHESTS)) {
                return notAllowed;
            }

            return allowed;
        });
    }

}