package net.rem.ghost.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rem.ghost.Ghost;
import net.rem.ghost.entity.ModEntities;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Ghost.MOD_ID);

    public static final RegistryObject<Item> GHOST_SPAWN_EGG = ITEMS.register("ghost_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GHOST, 0xFFFFFF, 0xAAAAFF,
                    new Item.Properties()));
}