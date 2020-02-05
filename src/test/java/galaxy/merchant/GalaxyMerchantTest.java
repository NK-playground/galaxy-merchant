package galaxy.merchant;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.Lists;
import galaxy.merchant.model.GalacticCurrency;

/**
 * @author kommineni
 *
 */

public class GalaxyMerchantTest {

	final List<String> inputData = Lists.newArrayList("glob is I", "prok is V", "pish is X", "tegj is L", 
			"glob glob Silver is 34 Credits", "glob prok Gold is 57800 Credits", "pish pish Iron is 3910 Credits",
			"how much is pish tegj glob glob ?","how many Credits is glob prok Silver ?", "how many Credits is glob prok Gold ?", 
			"how many Credits is glob prok Iron ?" , "how much wood could a woodchuck chuck if a woodchuck could chuck wood ?"); 
						
	@Before
	public void setup() {

	}

	@Test
	public void inputWithSpaces_santizeInput_shouldRemoveTheSpaces()
			throws Exception {
		final List<String> actualResult = GalaxyMerchant
				.santizeInput(Lists.newArrayList("    glob is I", "prok is V  ", "    pish is X    ","tegj    is    L"));
		assertThat(actualResult).containsExactly("glob is I", "prok is V", "pish is X", "tegj is L");
	}


	@Test
	public void inputData_identifyGalacticCurrencyDefinitions_shouldReturnValidGalacticCurrencyDefnitionsOnly() {
		GalaxyMerchant galaxyMerchant = new GalaxyMerchant();
		final String invalidGalacticCurrencyDefinition= "testCurrency is T";
		inputData.add(invalidGalacticCurrencyDefinition);
		final List<String> actualResult = GalaxyMerchant.identifyGalacticCurrencyDefinitions(
				inputData, galaxyMerchant.getRomanSymbols());
		assertThat(actualResult).containsExactly("glob is I", "prok is V", "pish is X", "tegj is L");
	}

	@Test
	public void inputData_identifyGalacticCurrencyDefinitions_shouldReturnAllGalacticCurrencyDefnitions() {
		GalaxyMerchant galaxyMerchant = new GalaxyMerchant();
		final String validGalacticCurrencyDefinition= "testCurrency is M"; // M is a valid Roman symbol
		inputData.add(validGalacticCurrencyDefinition);
		final List<String> actualResult = GalaxyMerchant.identifyGalacticCurrencyDefinitions(
				inputData, galaxyMerchant.getRomanSymbols());
		assertThat(actualResult).containsExactly("glob is I", "prok is V", "pish is X", "tegj is L","testCurrency is M");
	}
	

	@Test
	public void inputData_createGalacticCurrencies_shouldReturnAllGalacticCurrencies() {

		GalaxyMerchant galaxyMerchant = new GalaxyMerchant();
		final List<String> galacticCurrencyDefinitions = GalaxyMerchant.identifyGalacticCurrencyDefinitions(
				inputData, galaxyMerchant.getRomanSymbols());
		final List<GalacticCurrency> galacticCurrencies = GalaxyMerchant
				.createGalacticCurrencies(galacticCurrencyDefinitions, galaxyMerchant.getRomanSymbols());
		assertThat(galacticCurrencies).extracting("symbol").containsExactly("glob", "prok", "pish", "tegj");
		assertThat(galacticCurrencies).extracting("romanNumeral.symbol").containsExactly('I','V', 'X', 'L');
	}

	@Test
	public void inputData_galacticCurrencyConversion_shouldProcessAllLinesInInputFile() {

		List<String> expectedOutput = Lists.newArrayList("pish tegj glob glob is 42", "glob prok Silver is 68 Credits",
				"glob prok Gold is 57800 Credits", "glob prok Iron is 782 Credits","I have no idea what you are talking about");

		GalaxyMerchant galaxyMerchant = new GalaxyMerchant();
		final List<String> actualOutput = galaxyMerchant.galacticCurrencyConversion(inputData);
		assertThat(actualOutput).isEqualTo(expectedOutput);
	}

}