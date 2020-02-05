package galaxy.merchant.model;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author kommineni
 *
 */
public class CommonMetalTest {

	List<GalacticCurrency> galacticCurrencies;

	final static List<String> inputData = Lists.newArrayList("glob is I", "prok is V", "pish is X", "tegj is L",
			"glob glob Silver is 34 Credits", "glob prok Gold is 57800 Credits", "pish pish Iron is 3910 Credits",
			"how much is pish tegj glob glob ?", "how many Credits is glob prok Silver ?",
			"how many Credits is glob prok Gold ?", "how many Credits is glob prok Iron ?",
			"how much wood could a woodchuck chuck if a woodchuck could chuck wood ?");
	
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
	public void commonMetal_extractMetalValue_shouldExtractMetalValue() {
		final Integer expectedCreditValue = CommonMetal.extractMetalValue("glob glob Silver is 34 Credits");
		assertThat(expectedCreditValue).isEqualTo(34);
	}

	@Test
	public void commonMetal_extractCommonMetalName_shouldExtractMetalName() {
		final String expectedRareMetalSymbol = CommonMetal.extractCommonMetalName("glob prok Gold is 57800 Credits");
		assertThat(expectedRareMetalSymbol).isEqualTo("Gold");
	}

	@Test
	public void commonMetal_createFromMetalValueDefinition_shouldReturnUnitValueForTheMetal() throws Exception {
		List<GalacticCurrency> galacticCurrencyDefinition = galacticCurrencies.subList(0, 2);
		GalacticCurrencyExpression galacticCurrencyExpression = new GalacticCurrencyExpression(
				galacticCurrencyDefinition);
		final CommonMetal goldRareMetal = CommonMetal.createFromMetalValueDefinition("glob prok Gold is 57800 Credits",
				galacticCurrencyExpression);
		assertThat(goldRareMetal.getPerUnitValue()).isEqualTo(BigDecimal.valueOf(14450));
		galacticCurrencyDefinition = Lists.newArrayList(galacticCurrencies.get(2), galacticCurrencies.get(2));
		galacticCurrencyExpression = new GalacticCurrencyExpression(galacticCurrencyDefinition);
		final CommonMetal ironRareMetal = CommonMetal.createFromMetalValueDefinition("pish pish Iron is 3910 Credits",
				galacticCurrencyExpression);
		assertThat(ironRareMetal.getPerUnitValue()).isEqualTo(BigDecimal.valueOf(195.5));
	}

	@Test
	public void inputData_fetchMetalValueDefinitions_shouldReturnAllMetalPerUnitTransactions() {
		final List<String> metalValueDefinitions = CommonMetal
				.fetchMetalValueDefinitions(inputData);
		assertThat(metalValueDefinitions).containsExactly("glob glob Silver is 34 Credits",
				"glob prok Gold is 57800 Credits","pish pish Iron is 3910 Credits");
	}

	@Test
	public void inputData_buildMetalList_shouldBuildMetalListWithValues() {
		
		final List<CommonMetal> rareMetals = CommonMetal.buildMetalList(inputData, galacticCurrencies);
        assertThat(rareMetals).extracting("metalName").containsExactly("Silver","Gold","Iron");
        assertThat(rareMetals).extracting("perUnitValue").contains(BigDecimal.valueOf(17),BigDecimal.valueOf(14450), BigDecimal.valueOf(195.5));
	}

}