package net.rem.ghost.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.rem.ghost.Ghost;
import net.rem.ghost.entity.GhostEntity;

import java.util.UUID;

public class GhostRenderer extends HumanoidMobRenderer<GhostEntity, HumanoidModel<GhostEntity>> {
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Ghost.MOD_ID, "textures/entity/ghost.png");

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(GhostEntity entity) {
        UUID uuid = entity.getPlayerUUID();
        if (uuid != null) {
            GameProfile profile = new GameProfile(uuid, "");
            return Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(profile);
        }
        return DEFAULT_TEXTURE;
    }

    @Override
    protected RenderType getRenderType(GhostEntity entity, boolean showBody, boolean translucent, boolean outline) {
        ResourceLocation texture = getTextureLocation(entity);
        return outline ? RenderType.outline(texture) : RenderType.entityTranslucent(texture);
    }

    @Override
    public void render(GhostEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
        RenderSystem.disableBlend();
    }

}