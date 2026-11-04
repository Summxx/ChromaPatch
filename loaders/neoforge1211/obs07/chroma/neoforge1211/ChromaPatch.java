package obs07.chroma.neoforge1211;

import java.io.File;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import obs07.chroma.GameDir;
import obs07.chroma.Patcher;

@Mod("chromapatch")
public class ChromaPatch {

	public ChromaPatch() {
		File dir = GameDir.fmlPaths("net.neoforged.fml.loading.FMLPaths");
		Patcher.run(dir);
		if (FMLEnvironment.dist == Dist.CLIENT) Client.init(dir);
	}
}
