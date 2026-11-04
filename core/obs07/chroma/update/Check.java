package obs07.chroma.update;

import java.io.File;
import java.util.Properties;

// Checks the signed version notice the same way the mod does: java -cp chromapatch.jar obs07.chroma.update.Check updates
public final class Check {

	public static void main(String[] args) throws Exception {
		File dir = new File(args.length > 0 ? args[0] : "updates");
		if (!Trust.enabled()) fail("this jar has no release key");
		byte[] m = Updates.read(new File(dir, "manifest.properties"), Updates.MAX_MANIFEST);
		byte[] s = Updates.read(new File(dir, "manifest.sig"), Updates.MAX_SIG);
		Properties props = Updates.verifyManifest(m, s);
		if (props == null) fail("manifest signature is not valid");
		String mod = props.getProperty("mod.version");
		if (mod == null || !mod.matches(Updates.VERSION)) fail("bad mod.version");
		System.out.println("ok mod " + mod);
		System.out.println("manifest serial " + props.getProperty("serial") + " ok");
	}

	static void fail(String message) {
		System.err.println("FAILED: " + message);
		System.exit(1);
	}
}
