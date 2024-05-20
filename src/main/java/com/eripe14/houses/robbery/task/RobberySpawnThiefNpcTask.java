package com.eripe14.houses.robbery.task;

import com.eripe14.houses.configuration.contextual.NpcData;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.position.Position;
import com.eripe14.houses.position.PositionAdapter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RobberySpawnThiefNpcTask extends BukkitRunnable {

    private final Random random;
    private final RobberyConfiguration robberyConfiguration;
    private Position lastPosition;

    public RobberySpawnThiefNpcTask(RobberyConfiguration robberyConfiguration) {
        this.random = new Random();
        this.robberyConfiguration = robberyConfiguration;
        this.loadNpc();
    }

    @Override
    public void run() {
        List<Position> robberyNpcLocation = this.robberyConfiguration.thiefNpcLocation;
        Position position = robberyNpcLocation.get(this.random.nextInt(robberyNpcLocation.size()));
        NPC npcById = CitizensAPI.getNPCRegistry().getById(998);

        if (this.lastPosition != null && this.lastPosition.equals(position)) {
            position = robberyNpcLocation.get(this.random.nextInt(robberyNpcLocation.size()));
        }

        if (npcById == null) {
            npcById = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, UUID.randomUUID(), 998, "a");
            npcById.spawn(PositionAdapter.convert(position));
            this.setUpNpc(npcById);

            return;
        }

        this.setUpNpc(npcById);
        npcById.despawn();
        npcById.spawn(PositionAdapter.convert(position));

        this.lastPosition = position;
    }

    private void loadNpc() {
        List<Position> robberyNpcLocation = this.robberyConfiguration.thiefNpcLocation;
        Position position = robberyNpcLocation.get(this.random.nextInt(robberyNpcLocation.size()));

        NPC npc = CitizensAPI.getNPCRegistry().getById(998);
        if (npc == null) {
            npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, UUID.randomUUID(), 998, "-");
        }

        this.setUpNpc(npc);

        npc.spawn(PositionAdapter.convert(position));
    }

    private void setUpNpc(NPC npc) {
        NpcData thiefNpcData = this.robberyConfiguration.thiefNpcData;

        npc.setName(thiefNpcData.getName());
        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(
                thiefNpcData.getName(),
                thiefNpcData.getSkinSignatureValue(),
                thiefNpcData.getSkinTextureValue()
        );
    }

}