package net.rem.ghost.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GhostEntity extends PathfinderMob implements MenuProvider, IEntityAdditionalSpawnData {
    private static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID =
            SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> PLAYER_NAME =
            SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.STRING);



    public GhostEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PLAYER_UUID, Optional.empty());
        this.entityData.define(PLAYER_NAME, "");
    }

    public void setPlayerUUID(UUID uuid) {
        this.entityData.set(PLAYER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getPlayerUUID() {
        return this.entityData.get(PLAYER_UUID).orElse(null);
    }

    public void setPlayerName(String name) {
        this.entityData.set(PLAYER_NAME, name == null ? "" : name);
    }

    public String getPlayerName() {
        return this.entityData.get(PLAYER_NAME);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID uuid = getPlayerUUID();
        if (uuid != null) {
            tag.putUUID("PlayerUUID", uuid);
        }
        String name = getPlayerName();
        if (!name.isEmpty()) {
            tag.putString("PlayerName", name);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("PlayerUUID")) {
            setPlayerUUID(tag.getUUID("PlayerUUID"));
        }
        if (tag.contains("PlayerName")) {
            setPlayerName(tag.getString("PlayerName"));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        return false; // no damage
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(this);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.ghost.ghost");
    }



    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D);
    }


    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        UUID uuid = getPlayerUUID();
        buffer.writeBoolean(uuid != null);
        if (uuid != null) {
            buffer.writeUUID(uuid);
        }
        buffer.writeUtf(getPlayerName());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        if (additionalData.readBoolean()) {
            setPlayerUUID(additionalData.readUUID());
        }
        setPlayerName(additionalData.readUtf(32767));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }




    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

//    @Override
//    public void writeSpawnData(FriendlyByteBuf buffer) {
//
//    }
}