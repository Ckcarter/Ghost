package net.rem.ghost.item;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rem.ghost.Ghost;
import net.rem.ghost.entity.ModEntities;
import net.minecraftforge.common.DeferredSpawnEggItem;
import net.minecraftforge.event.CreativeModeTabEvent;



@Mod.EventBusSubscriber(modid = Ghost.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Ghost.MOD_ID);

    public static final RegistryObject<Item> GHOST_SPAWN_EGG = ITEMS.register("ghost_spawn_egg",
            () -> new SpawnEggItem(ModEntities.GHOST.get(), 0xFFFFFF, 0xAAAAFF,
                    new Item.Properties()));

    @SubscribeEvent
    public static void addToCreativeTab(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(GHOST_SPAWN_EGG.get());
        }
    }
}
