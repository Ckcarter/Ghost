package net.rem.ghost.events;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.rem.ghost.Ghost;
import net.rem.ghost.entity.GhostEntity;
import net.rem.ghost.entity.ModEntities;

@Mod.EventBusSubscriber(modid = Ghost.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerDrops(PlayerDropsEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }

        GhostEntity ghost = ModEntities.GHOST.get().create(level);
        if (ghost == null) {
            return;
        }

        ghost.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        SimpleContainer inventory = ghost.getInventory();

        int slot = 0;
        var iterator = event.getDrops().iterator();
        while (iterator.hasNext() && slot < inventory.getContainerSize()) {
            ItemEntity itemEntity = iterator.next();
            inventory.setItem(slot++, itemEntity.getItem().copy());
            iterator.remove();
        }

        level.addFreshEntity(ghost);
    }
}
