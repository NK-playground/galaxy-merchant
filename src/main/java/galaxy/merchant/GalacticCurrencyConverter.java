package galaxy.merchant;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.google.common.collect.ImmutableList;

/**
 * @author kommineni
 *
 */

public class GalacticCurrencyConverter {
	/**
	 * @param args
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws IOException, URISyntaxException {
		// Read Data from Input file
		final List<String> input = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("input.txt").toURI()));
		// Currency Conversion
		new GalaxyMerchant().galacticCurrencyConversion(ImmutableList.copyOf(input));
	}
}
