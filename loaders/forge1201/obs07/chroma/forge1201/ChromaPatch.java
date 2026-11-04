package obs07.chroma.forge1201;

import java.io.File;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import obs07.chroma.GameDir;
import obs07.chroma.Patcher;

@Mod("chromapatch")
public class ChromaPatch {

	public ChromaPatch() {
		File dir = GameDir.fmlPaths("net.minecraftforge.fml.loading.FMLPaths");
		Patcher.run(dir);
		if (FMLEnvironment.dist == Dist.CLIENT) Client.init(dir);
	}
}
