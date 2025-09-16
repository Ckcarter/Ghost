package net.rem.ghost.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.authlib.properties.Property;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.rem.ghost.Ghost;
import net.rem.ghost.entity.GhostEntity;
import net.rem.ghost.entity.ModEntities;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mod.EventBusSubscriber(modid = Ghost.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
//
//        Level level = player.level();
//        if (level.isClientSide) {
//            return;
//        }
        ServerLevel level = player.serverLevel();

        GhostEntity ghost = ModEntities.GHOST.get().create(level);
        if (ghost != null) {
            ghost.setPlayerUUID(player.getUUID());
           // ghost.setPlayerName(player.getGameProfile().getName());


            String name = player.getGameProfile().getName();
            if (name == null || name.isEmpty()) {
                name = player.getName().getString();
            }

            ghost.setPlayerName(name);

            var textures = player.getGameProfile().getProperties().get("textures");
            if (!textures.isEmpty()) {
                Property textureProperty = textures.iterator().next();
                ghost.setPlayerSkinProperty(textureProperty);
            }

            ItemStack sword = takeBestSword(player.getInventory());
            if (!sword.isEmpty()) {
                ghost.setItemSlot(EquipmentSlot.MAINHAND, sword);
                ghost.setDropChance(EquipmentSlot.MAINHAND, 1.0F);
            }




            ghost.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            ghost.setDeathPoint(player.blockPosition());
            ghost.setPersistenceRequired();
            level.addFreshEntity(ghost);
        }
    }

    private static ItemStack takeBestSword(Inventory inventory) {
        ItemStack bestSword = ItemStack.EMPTY;
        float bestScore = -1.0F;
        int bestSlot = -1;
        boolean bestInOffhand = false;

        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack stack = inventory.items.get(i);
            float score = getSwordScore(stack);
            if (score > bestScore) {
                bestScore = score;
                bestSword = stack;
                bestSlot = i;
                bestInOffhand = false;
            }
        }

        for (int i = 0; i < inventory.offhand.size(); i++) {
            ItemStack stack = inventory.offhand.get(i);
            float score = getSwordScore(stack);
            if (score > bestScore) {
                bestScore = score;
                bestSword = stack;
                bestSlot = i;
                bestInOffhand = true;
            }
        }

        if (bestSword.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack sword = bestSword.split(1);
        if (bestSword.isEmpty()) {
            if (bestInOffhand) {
                inventory.offhand.set(bestSlot, ItemStack.EMPTY);
            } else {
                inventory.items.set(bestSlot, ItemStack.EMPTY);
            }
        }
        inventory.setChanged();
        return sword;
    }

    private static float getSwordScore(ItemStack stack) {
        if (!(stack.getItem() instanceof SwordItem swordItem)) {
            return -1.0F;
        }

        float baseDamage = swordItem.getDamage();
        float enchantmentBonus = 0.0F;

        for (MobType type : DAMAGE_BONUS_TYPES) {
            enchantmentBonus = Math.max(enchantmentBonus, EnchantmentHelper.getDamageBonus(stack, type));
        }

        return baseDamage + enchantmentBonus;
    }

    private static final MobType[] DAMAGE_BONUS_TYPES = new MobType[]{
            MobType.UNDEFINED,
            MobType.UNDEAD,
            MobType.ARTHROPOD
    };


}
