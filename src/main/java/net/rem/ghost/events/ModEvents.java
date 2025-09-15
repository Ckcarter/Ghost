package net.rem.ghost.events;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.rem.ghost.Ghost;
import net.rem.ghost.entity.GhostEntity;
import net.rem.ghost.entity.ModEntities;


@Mod.EventBusSubscriber(modid = Ghost.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Level level = player.level();
        if (level.isClientSide) {
            return;
        }

        GhostEntity ghost = ModEntities.GHOST.get().create(level);
        if (ghost != null) {
            ghost.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            level.addFreshEntity(ghost);
        }
    }
}
