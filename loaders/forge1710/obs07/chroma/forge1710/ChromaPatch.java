package obs07.chroma.forge1710;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import java.io.File;
import obs07.chroma.GameDir;
import obs07.chroma.Patcher;

@Mod(modid = "chromapatch", name = "Chroma Patch", version = "@VERSION@", acceptableRemoteVersions = "*")
public class ChromaPatch {

	public ChromaPatch() {
		File dir = GameDir.fml1710();
		Patcher.run(dir);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			Client.init(dir);
			TextAA.init();
		}
	}
}
