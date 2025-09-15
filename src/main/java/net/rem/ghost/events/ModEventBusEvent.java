package net.rem.ghost.events;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.rem.ghost.Ghost;
import net.rem.ghost.entity.GhostEntity;
import net.rem.ghost.entity.ModEntities;

@Mod.EventBusSubscriber(modid = Ghost.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvent {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event){
//        event.registerLayerDefinition(ModModelLayers.TRIBBLE_LAYER, TribbleModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {

        //event.put(ModEntities.GHOST.get(), GhostEntity.createAttributes().build());
       // ModEntities.registerAttributes(event);
    }

}