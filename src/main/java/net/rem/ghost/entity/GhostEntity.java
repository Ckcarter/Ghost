package net.rem.ghost.entity;

import com.mojang.authlib.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;





import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GhostEntity extends PathfinderMob implements MenuProvider, IEntityAdditionalSpawnData {
    private static final int DEFAULT_GUARD_RADIUS = 16;
    private static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID =
            SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> PLAYER_NAME =
            SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<CompoundTag> PLAYER_SKIN =
            SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.COMPOUND_TAG);


    @Nullable
    private BlockPos deathPoint;
    private int guardRadius = DEFAULT_GUARD_RADIUS;



    public GhostEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PLAYER_UUID, Optional.empty());
        this.entityData.define(PLAYER_NAME, "");
        this.entityData.define(PLAYER_SKIN, new CompoundTag());
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
    public void setPlayerSkinProperty(@Nullable Property property) {
        CompoundTag tag = new CompoundTag();
        if (property != null) {
            String value = property.getValue();
            if (value != null && !value.isEmpty()) {
                tag.putString("Value", value);
            }

            String signature = property.getSignature();
            if (signature != null && !signature.isEmpty()) {
                tag.putString("Signature", signature);
            }
        }
        this.entityData.set(PLAYER_SKIN, tag);
    }

    public void setPlayerSkinTag(@Nullable CompoundTag tag) {
        this.entityData.set(PLAYER_SKIN, tag == null ? new CompoundTag() : tag);
    }

    public CompoundTag getPlayerSkinTag() {
        return this.entityData.get(PLAYER_SKIN);
    }

    public void setDeathPoint(@Nullable BlockPos deathPoint) {
        setDeathPoint(deathPoint, DEFAULT_GUARD_RADIUS);
    }

    public void setDeathPoint(@Nullable BlockPos deathPoint, int radius) {
        this.deathPoint = deathPoint;
        this.guardRadius = Math.max(1, radius);
        if (deathPoint != null) {
            this.restrictTo(deathPoint, this.guardRadius);
        } else {
            this.clearRestriction();
        }
    }

    @Nullable
    public BlockPos getDeathPoint() {
        return deathPoint;
    }

    public int getGuardRadius() {
        return guardRadius;
    }


    @Nullable
    public Property getPlayerSkinProperty() {
        CompoundTag tag = getPlayerSkinTag();
        if (tag.isEmpty() || !tag.contains("Value")) {
            return null;
        }
        String value = tag.getString("Value");
        String signature = tag.contains("Signature") ? tag.getString("Signature") : null;
        return new Property("textures", value, signature);
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
        CompoundTag skinTag = getPlayerSkinTag();
        if (!skinTag.isEmpty()) {
            tag.put("PlayerSkin", skinTag.copy());
        }
        if (deathPoint != null) {
            tag.putInt("DeathPointX", deathPoint.getX());
            tag.putInt("DeathPointY", deathPoint.getY());
            tag.putInt("DeathPointZ", deathPoint.getZ());
            tag.putInt("GuardRadius", guardRadius);
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
        if (tag.contains("PlayerSkin", 10)) {
            setPlayerSkinTag(tag.getCompound("PlayerSkin"));
        } else {
            setPlayerSkinTag(null);
        }
        if (tag.contains("DeathPointX", 99) && tag.contains("DeathPointY", 99) && tag.contains("DeathPointZ", 99)) {
            int radius = tag.contains("GuardRadius", 99) ? tag.getInt("GuardRadius") : DEFAULT_GUARD_RADIUS;
            setDeathPoint(new BlockPos(tag.getInt("DeathPointX"), tag.getInt("DeathPointY"), tag.getInt("DeathPointZ")), radius);
        } else {
            setDeathPoint(null);
        }


    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, 10, true, false,
                this::isEntityWithinGuardRange));
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        return super.doHurtTarget(entity);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack ghostItem = getMainHandItem();
        if (ghostItem.isEmpty() || !(ghostItem.getItem() instanceof SwordItem)) {
            return super.mobInteract(player, hand);
        }
        UUID ownerUuid = getPlayerUUID();
        if (ownerUuid != null && !ownerUuid.equals(player.getUUID())) {
            return InteractionResult.PASS;
        }

        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack sword = ghostItem.copy();
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);

        ItemStack handStack = player.getItemInHand(hand);
        if (handStack.isEmpty()) {
            player.setItemInHand(hand, sword);
        } else if (!player.addItem(sword)) {
            ItemEntity itemEntity = player.drop(sword, false);
            if (itemEntity != null) {
                itemEntity.setNoPickUpDelay();
                itemEntity.getOwner();
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public Component getDisplayName() {
        String name = getPlayerName();
        if (!name.isEmpty()) {
            return Component.translatable("entity.ghost.ghost.named", name);
        }
        return Component.translatable("entity.ghost.ghost");
    }



    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    private boolean isEntityWithinGuardRange(LivingEntity entity) {
        return entity != null && isWithinGuardRange(entity.blockPosition());
    }

    private boolean isWithinGuardRange(BlockPos position) {
        if (deathPoint == null) {
            return true;
        }
        int dx = position.getX() - deathPoint.getX();
        int dy = position.getY() - deathPoint.getY();
        int dz = position.getZ() - deathPoint.getZ();
        return dx * dx + dy * dy + dz * dz <= guardRadius * guardRadius;
    }


    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        UUID uuid = getPlayerUUID();
        buffer.writeBoolean(uuid != null);
        if (uuid != null) {
            buffer.writeUUID(uuid);

        }
        CompoundTag skinTag = getPlayerSkinTag();
        buffer.writeNbt(skinTag.isEmpty() ? null : skinTag.copy());
        buffer.writeUtf(getPlayerName());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        if (additionalData.readBoolean()) {
            setPlayerUUID(additionalData.readUUID());
        } else {
            setPlayerUUID(null);
        }

        setPlayerSkinTag(additionalData.readNbt());
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


}