package obs07.chroma.forge1710;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import java.util.Collections;
import java.util.List;
import java.util.Set;

// Loaded by UniMixins when it is installed (GTNH), ignored otherwise
@LateMixin
public final class LateMixins implements ILateMixinLoader {

	@Override
	public String getMixinConfig() {
		return "mixins.chromapatch.late.json";
	}

	@Override
	public List<String> getMixins(Set<String> loadedMods) {
		return loadedMods.contains("gtnhlib") ? Collections.singletonList("ShaderProgramMixin") : Collections.<String>emptyList();
	}
}
