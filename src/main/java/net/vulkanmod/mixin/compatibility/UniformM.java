package net.vulkanmod.mixin.compatibility;

import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import net.vulkanmod.gl.GlProgram;
import net.vulkanmod.interfaces.ShaderMixed;
import net.vulkanmod.vulkan.Renderer;
import net.vulkanmod.vulkan.shader.Pipeline;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Uniform.class)
public class UniformM {

    @Shadow @Final private Shader parent;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static int glGetUniformLocation(int i, CharSequence charSequence) {
        //TODO
        return 1;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static int glGetAttribLocation(int i, CharSequence charSequence) {
        return 0;
    }

    @Inject(method = "upload", at = @At("HEAD"), cancellable = true)
    public void redirectUpload(CallbackInfo ci) {
        Renderer renderer = Renderer.getInstance();
        Pipeline boundPipeline = renderer.getBoundPipeline();

        ci.cancel();

        GlProgram program = GlProgram.getBoundProgram();

        if (program == null) {
            return;
        }

        // Update descriptors only if the pipeline has already been bound
        Pipeline pipeline = program.getPipeline();
        if (boundPipeline == pipeline) {
            renderer.uploadAndBindUBOs(boundPipeline);
        }
    }

    @Inject(method = "uploadInteger", at = @At("HEAD"), cancellable = true)
    private static void cancelUploadInteger(int i, int j, CallbackInfo ci) {
        ci.cancel();
    }
}
