package xyz.nifeather.pipfix.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.nifeather.pipfix.TextureTracker;
import xyz.nifeather.pipfix.PipFixValues;

@Mixin(PictureInPictureRenderer.class)
public abstract class PictureInPictureRendererMixin<T extends PictureInPictureRenderState>
{
    @Shadow private @Nullable GpuTexture texture;

    @Shadow private @Nullable GpuTextureView textureView;

    @Shadow private @Nullable GpuTexture depthTexture;

    @Shadow private @Nullable GpuTextureView depthTextureView;

    @Unique
    private void fmc$setTextureToNull()
    {
        this.texture = null;
        this.textureView = null;
        this.depthTexture = null;
        this.depthTextureView = null;
    }

    @WrapMethod(method = "prepareTexturesAndProjection")
    public void fmc$prepareTexture(boolean what, int width, int height, Operation<Void> original)
    {
        if (!PipFixValues.applyPictureInPictureWorkaround)
        {
            original.call(what, width, height);
        }
        else
        {
            fmc$setTextureToNull(); // So that we can make PIP render create new texture to render and for us to track

            original.call(true, width, height);

            assert textureView != null && depthTextureView != null;

            TextureTracker.addTrackingTexture(this.texture, this.textureView);
            TextureTracker.addTrackingTexture(this.depthTexture, this.depthTextureView);
        }
    }
}
