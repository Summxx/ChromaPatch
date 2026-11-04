package obs07.chroma.update;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

// Release keys (ECDSA P-256) and signature checks
public final class Trust {

	// id=base64 X.509 key, filled in by build.sh from keys/*.pub
	static final String KEYS = "@KEYS@";

	// order of the P-256 group
	static final BigInteger N = new BigInteger("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 16);

	static Map<String, PublicKey> keys;

	static synchronized Map<String, PublicKey> keys() {
		if (keys != null) return keys;
		Map<String, PublicKey> out = new LinkedHashMap<String, PublicKey>();
		if (!KEYS.startsWith("@")) {
			for (String entry : KEYS.split(";")) {
				int eq = entry.indexOf('=');
				if (eq <= 0) continue;
				try {
					PublicKey key = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(entry.substring(eq + 1))));
					out.put(entry.substring(0, eq), key);
				} catch (Exception ignored) {
				}
			}
		}
		keys = out;
		return out;
	}

	public static boolean enabled() {
		return !keys().isEmpty();
	}

	// sig file: "<key id> <base64 DER signature>"
	static boolean verify(byte[] data, byte[] sigFile) {
		try {
			String text = new String(sigFile, "US-ASCII").trim();
			int space = text.indexOf(' ');
			if (space <= 0) return false;
			PublicKey key = keys().get(text.substring(0, space));
			if (key == null) return false;
			byte[] der = Base64.getDecoder().decode(text.substring(space + 1).trim());
			if (!strictDer(der)) return false;
			Signature s = Signature.getInstance("SHA256withECDSA");
			s.initVerify(key);
			s.update(data);
			return s.verify(der);
		} catch (Exception e) {
			return false;
		}
	}

	// some Java 15-18 builds accept r = s = 0 (CVE-2022-21449), so we check the values ourselves
	static boolean strictDer(byte[] der) {
		if (der.length < 8 || der.length > 72 || der[0] != 0x30 || (der[1] & 0xff) != der.length - 2) return false;
		int p = 2;
		for (int i = 0; i < 2; i++) {
			if (p + 2 > der.length || der[p] != 0x02) return false;
			int len = der[p + 1] & 0xff;
			p += 2;
			if (len < 1 || len > 33 || p + len > der.length) return false;
			// no negative numbers, no extra leading zeros
			if ((der[p] & 0x80) != 0) return false;
			if (len > 1 && der[p] == 0 && (der[p + 1] & 0x80) == 0) return false;
			byte[] v = new byte[len];
			System.arraycopy(der, p, v, 0, len);
			BigInteger n = new BigInteger(1, v);
			if (n.signum() <= 0 || n.compareTo(N) >= 0) return false;
			p += len;
		}
		return p == der.length;
	}
}
