package net.rem.ghost.client;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.rem.ghost.Ghost;
import net.rem.ghost.entity.GhostEntity;

public class GhostRenderer extends HumanoidMobRenderer<GhostEntity, ZombieModel<GhostEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Ghost.MOD_ID, "textures/entity/ghost.png");

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(GhostEntity entity) {
        return TEXTURE;
    }

    @Override
    protected RenderType getRenderType(GhostEntity entity, boolean showBody, boolean translucent, boolean outline) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }
}