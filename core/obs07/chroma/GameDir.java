package obs07.chroma;

import java.io.File;
import java.lang.reflect.Method;

// Game folder from the loader, falls back to the working directory
public final class GameDir {

	public static File fml1710() {
		try {
			Class<?> loader = Class.forName("cpw.mods.fml.common.Loader");
			Object instance = loader.getMethod("instance").invoke(null);
			File config = (File) loader.getMethod("getConfigDir").invoke(instance);
			return config.getParentFile();
		} catch (Throwable t) {
			return cwd();
		}
	}

	public static File fmlPaths(String className) {
		try {
			Class<?> paths = Class.forName(className);
			Object gameDir = paths.getField("GAMEDIR").get(null);
			Method get = gameDir.getClass().getMethod("get");
			return ((java.nio.file.Path) get.invoke(gameDir)).toFile();
		} catch (Throwable t) {
			return cwd();
		}
	}

	static File cwd() {
		return new File(System.getProperty("user.dir")).getAbsoluteFile();
	}
}
