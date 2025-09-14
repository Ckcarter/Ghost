package net.rem.ghost;


import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.rem.ghost.entity.ModEntities;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Ghost.MOD_ID)
public class Ghost extends Zombie {
    public static final String MOD_ID = "ghost";
    private static final Level Ghost = ;

    public Ghost() {
        super(Ghost);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITY_TYPES.register(modEventBus);


        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }
}