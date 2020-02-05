package galaxy.merchant.model;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author kommineni
 *
 */

public class GalacticCurrencyTest {
	List<GalacticCurrency> galacticCurrencies;

	@Before
	public void setup() {
		RomanNumeral romanSymbolThousand = RomanNumeral.standAlone('M', 1000);
		RomanNumeral romanSymbolFiveHundred = RomanNumeral.standAlone('D', 500);
		RomanNumeral romanSymbolHundred = RomanNumeral.repeatableAndSubtractable('C',
				Lists.newArrayList(romanSymbolFiveHundred, romanSymbolThousand), 100);
		RomanNumeral romanSymbolFifty = RomanNumeral.standAlone('L', 50);
		RomanNumeral romanSymbolTen = RomanNumeral.repeatableAndSubtractable('X',
				Lists.newArrayList(romanSymbolFifty, romanSymbolHundred), 10);
		RomanNumeral romanSymbolFive = RomanNumeral.standAlone('V', 5);
		RomanNumeral romanSymbolOne = RomanNumeral.repeatableAndSubtractable('I',
				Lists.newArrayList(romanSymbolFive, romanSymbolTen), 1);
		
		galacticCurrencies = ImmutableList.of(new GalacticCurrency("glob", romanSymbolOne),
				new GalacticCurrency("prok", romanSymbolFive), new GalacticCurrency("pish", romanSymbolTen),
				new GalacticCurrency("tegj", romanSymbolFifty));

	}

	@Test
	public void inputMetalValueDefinitions_getGalacticCurrencyFromComponents_shouldCreateGalacticCurrenciesFromTheDefinition()
			throws Exception {
		List<GalacticCurrency> galacticCurrencies = GalacticCurrency.getGalacticCurrencyFromComponents(
				Lists.newArrayList("glob", "glob", "Silver", "is", "34", "Credits"), this.galacticCurrencies);
		assertThat(galacticCurrencies).extracting("symbol").containsExactly("glob", "glob");

		galacticCurrencies = GalacticCurrency.getGalacticCurrencyFromComponents(
				Lists.newArrayList("glob", "prok", "Gold", "is", "57800", "Credits"), this.galacticCurrencies);
		assertThat(galacticCurrencies).extracting("symbol").containsExactly("glob", "prok");

		galacticCurrencies = GalacticCurrency.getGalacticCurrencyFromComponents(
				Lists.newArrayList("pish", "Iron", "is", "3910", "Credits"), this.galacticCurrencies);
		assertThat(galacticCurrencies).extracting("symbol").containsExactly("pish");

	}

	@Test
	public void inputGalacticCurrencyQuery_getGalacticCurrencyFromComponents_shouldCreateGalacticCurrenciesListedinTheQuery()
			throws Exception {
		List<GalacticCurrency> galacticCurrenciesFromQuery = GalacticCurrency.getGalacticCurrencyFromComponents(
				Lists.newArrayList("how", "much", "is", "pish", "tegj", "glob", "glob", "?"), this.galacticCurrencies);
		assertThat(galacticCurrenciesFromQuery).extracting("symbol").containsExactly("pish", "tegj", "glob", "glob");
	}

	@Test
	public void inputCreditValveQuery_getGalacticCurrencyFromComponents_shouldCreateGalacticCurrenciesListedinTheQuery()
			throws Exception {
		List<GalacticCurrency> galacticCurrencies = GalacticCurrency.getGalacticCurrencyFromComponents(
				Lists.newArrayList("how", "many", "Credits", "is", "glob", "prok", "Silver", "?"),
				this.galacticCurrencies);
		assertThat(galacticCurrencies).extracting("symbol").containsExactly("glob", "prok");
	}

	@Test
	public void inputWithoutGalacticCurrencies_getGalacticCurrencyFromComponents_shouldCreateEmptyListOfGalacticCurrencies()
			throws Exception {
		List<GalacticCurrency> galacticCurrenciesFromTransaction = GalacticCurrency.getGalacticCurrencyFromComponents(
				Lists.newArrayList("how", "many", "Credits", "is", "Silver", "?"), this.galacticCurrencies);
		assertThat(galacticCurrenciesFromTransaction).isEmpty();
	}

}
