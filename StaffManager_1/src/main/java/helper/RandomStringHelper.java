package helper;
import java.security.SecureRandom;

public class RandomStringHelper {

	public static String RandomString() {
		var random = new SecureRandom();
		return String.format("%06d", random.nextInt(1_000_000));
	}
}