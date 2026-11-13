package obs07.chroma.forge1710;

import java.lang.reflect.Method;
import java.nio.IntBuffer;
import java.util.Optional;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

// GregTech effects drawn with their own GL shader skip the shader pack. When the pack renders the world
// at a lower resolution (FSR2_SCALE), they must be drawn in the same corner of the screen as the pack.
public final class RenderScale {

	private static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer(16);
	private static int[] saved;
	private static boolean unavailable;
	private static Method currentPack, pipelines, pipeline, phase, shadows, options, values, value;
	private static Object lastPack;
	private static float lastScale;

	private RenderScale() {}

	public static void begin() {
		if (saved != null || unavailable) return;
		float scale = scale();
		if (scale <= 0f || scale >= 1f) return;
		VIEWPORT.clear();
		GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);
		int width = VIEWPORT.get(2), height = VIEWPORT.get(3);
		// only the world pass: full screen viewport (not the shadow map, not an offscreen buffer)
		if (VIEWPORT.get(0) != 0 || VIEWPORT.get(1) != 0 || width != Display.getWidth() || height != Display.getHeight()) return;
		saved = new int[] {width, height};
		GL11.glViewport(0, 0, (int) Math.floor(width * scale), (int) Math.floor(height * scale));
	}

	public static void end() {
		if (saved == null) return;
		GL11.glViewport(0, 0, saved[0], saved[1]);
		saved = null;
	}

	// render scale of the world pass, 0 when there is none
	private static float scale() {
		try {
			if (packScale() <= 0f || (Boolean) shadows.invoke(null)) return 0f;
			Object manager = pipelines.invoke(null);
			Object current = manager == null ? null : pipeline.invoke(manager);
			if (current == null || "NONE".equals(String.valueOf(phase.invoke(current)))) return 0f;
			return lastScale;
		} catch (Throwable t) {
			return 0f;
		}
	}

	// render scale the current shader pack asks for, 0 when it renders at full size or there is no pack
	static float packScale() {
		if (unavailable) return 0f;
		try {
			if (currentPack == null) {
				Class<?> iris = Class.forName("net.coderbot.iris.Iris");
				currentPack = iris.getMethod("getCurrentPack");
				pipelines = iris.getMethod("getPipelineManagerNullable");
				pipeline = Class.forName("net.coderbot.iris.pipeline.PipelineManager").getMethod("getPipelineNullable");
				phase = Class.forName("net.coderbot.iris.pipeline.WorldRenderingPipeline").getMethod("getPhase");
				shadows = Class.forName("net.coderbot.iris.shadows.ShadowRenderingState").getMethod("areShadowsCurrentlyBeingRendered");
				options = Class.forName("net.coderbot.iris.shaderpack.ShaderPack").getMethod("getShaderPackOptions");
				values = Class.forName("net.coderbot.iris.shaderpack.option.ShaderPackOptions").getMethod("getOptionValues");
				value = Class.forName("net.coderbot.iris.shaderpack.option.values.OptionValues").getMethod("getStringValue", String.class);
			}
		} catch (Throwable t) {
			// no Angelica
			unavailable = true;
			return 0f;
		}
		try {
			Optional<?> pack = (Optional<?>) currentPack.invoke(null);
			if (!pack.isPresent()) return 0f;
			if (pack.get() != lastPack) {
				lastPack = pack.get();
				Optional<?> fsr = (Optional<?>) value.invoke(values.invoke(options.invoke(lastPack)), "FSR2_SCALE");
				lastScale = fsr.isPresent() ? fsrScale(String.valueOf(fsr.get())) : 0f;
			}
			return lastScale;
		} catch (Throwable t) {
			return 0f;
		}
	}

	// same factors as the pack
	private static float fsrScale(String option) {
		switch (option) {
			case "1": return 0.6667f;
			case "2": return 0.5882f;
			case "3": return 0.5f;
			case "4": return 0.3333f;
			default: return 0f;
		}
	}
}
