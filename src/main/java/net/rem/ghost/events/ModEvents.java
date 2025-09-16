package net.rem.ghost.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.rem.ghost.Ghost;
import net.rem.ghost.entity.GhostEntity;
import net.rem.ghost.entity.ModEntities;
import com.mojang.authlib.properties.Property;


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


            ghost.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            ghost.setDeathPoint(player.blockPosition());
            ghost.setPersistenceRequired();
            level.addFreshEntity(ghost);
        }
    }
}
