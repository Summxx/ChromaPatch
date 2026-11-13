package obs07.chroma.forge1710.mixin;

import obs07.chroma.forge1710.RenderScale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.gtnewhorizon.gtnhlib.client.renderer.shader.ShaderProgram", remap = false)
public abstract class ShaderProgramMixin {

	@Unique
	private boolean chromapatch$worldEffect;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void chromapatch$init(String domain, String vertex, String fragment, CallbackInfo ci) {
		// gtnhlib's own programs (bloom, tonemap) draw into their own buffers
		chromapatch$worldEffect = !"gtnhlib".equals(domain);
	}

	@Inject(method = "use", at = @At("RETURN"))
	private void chromapatch$use(CallbackInfo ci) {
		if (chromapatch$worldEffect) RenderScale.begin();
	}

	@Inject(method = "unbind", at = @At("HEAD"))
	private static void chromapatch$unbind(CallbackInfo ci) {
		RenderScale.end();
	}
}
