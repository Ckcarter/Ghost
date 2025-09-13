package net.rem.ghost;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.rem.ghost.entity.ModEntities;
import net.rem.ghost.item.ModItems;
import net.minecraftforge.common.DeferredSpawnEggItem;
// The value here should match an entry in the META-INF/mods.toml file
@Mod(Ghost.MOD_ID)
public class Ghost {
    public static final String MOD_ID = "ghost";

    public Ghost() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(DeferredSpawnEggItem::initSpawnEggs);
}