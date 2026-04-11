package net.rem.ghost.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rem.ghost.Ghost;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Ghost.MOD_ID);

    public static final RegistryObject<EntityType<GhostEntity>> GHOST = ENTITY_TYPES.register("ghost",
            () -> EntityType.Builder.of(GhostEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .build(new ResourceLocation(Ghost.MOD_ID, "ghost").toString()));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(GHOST.get(), GhostEntity.createAttributes().build());
    }
}