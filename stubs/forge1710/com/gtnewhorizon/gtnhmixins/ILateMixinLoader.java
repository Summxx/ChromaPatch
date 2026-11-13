package com.gtnewhorizon.gtnhmixins;
import java.util.List;
import java.util.Set;
public interface ILateMixinLoader {
	String getMixinConfig();
	List<String> getMixins(Set<String> loadedMods);
}
