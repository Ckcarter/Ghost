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
import com.mojang.authlib.properties.Property;
import net.minecraft.client.resources.DefaultPlayerSkin;

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
        String name = entity.getPlayerName();
        Property skinProperty = entity.getPlayerSkinProperty();
        GameProfile profile = null;

        if (uuid != null && (skinProperty != null || !name.isEmpty())) {
            profile = new GameProfile(uuid, name.isEmpty() ? null : name);
        } else if (uuid == null && !name.isEmpty()) {
            profile = new GameProfile(null, name);
        }

        if (profile != null) {
            if (skinProperty != null) {
                profile.getProperties().removeAll("textures");
                profile.getProperties().put("textures", skinProperty);
            }

            boolean requireSecure = skinProperty == null;
            var skinManager = Minecraft.getInstance().getSkinManager();
            skinManager.registerSkins(profile, (type, location, texture) -> {
            }, requireSecure);
            ResourceLocation texture = skinManager.getInsecureSkinLocation(profile);
            if (texture != null) {
                return texture;
            }
        }

        if (uuid != null) {
            return DefaultPlayerSkin.getDefaultSkin(uuid);
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