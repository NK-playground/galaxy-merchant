package galaxy.merchant;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import galaxy.merchant.model.GalacticCurrency;
import galaxy.merchant.model.GalacticCurrencyExpression;
import galaxy.merchant.model.CommonMetal;
import galaxy.merchant.model.RomanNumeral;

/**
 * @author kommineni
 *
 */

public class CurrencyConversionOutputProcessorTest {
	List<GalacticCurrency> galacticCurrencies;

	final List<String> inputData = Lists.newArrayList("glob is I", "prok is V", "pish is X", "tegj is L",
			"glob glob Silver is 34 Credits", "glob prok Gold is 57800 Credits", "pish pish Iron is 3910 Credits",
			"how much is pish tegj glob glob ?", "how many Credits is glob prok Silver ?",
			"how many Credits is glob prok Gold ?", "how many Credits is glob prok Iron ?",
			"how much wood could a woodchuck chuck if a woodchuck could chuck wood ?");

	@Before
	public void setup() {
		RomanNumeral romanSymbolThousand = RomanNumeral.standAlone('M', 1000);
		RomanNumeral romanSymbolFiveHundred = RomanNumeral.standAlone('D', 500);
		RomanNumeral romanSymbolFifty = RomanNumeral.standAlone('L', 50);
		RomanNumeral romanSymbolHundred = RomanNumeral.repeatableAndSubtractable('C',
				Lists.newArrayList(romanSymbolFiveHundred, romanSymbolThousand), 100);
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
	public void inputData_selectGalacticCurrencyQueries_shouldReturnAllGalacticCurrencyQueries() {
		final List<String> galacticCurrencyValueQueries = CurrencyConversionOutputProcessor
				.selectGalacticCurrencyQueries(inputData);
		assertThat(galacticCurrencyValueQueries).containsExactly("how much is pish tegj glob glob ?");
	}

	@Test
	public void inputData_selectCreditQueries_shouldReturnAllCreditQueries() {
		final List<String> creditQueries = CurrencyConversionOutputProcessor.selectCreditQueries(inputData);
		assertThat(creditQueries).containsExactly("how many Credits is glob prok Silver ?",
				"how many Credits is glob prok Gold ?", "how many Credits is glob prok Iron ?");
	}

	@Test
	public void creditQuery_formatCreditsQueryResult_shouldReturnResponseWithCreditsValue() {
		String expectedResult = "glob prok Gold is 57800 Credits";
		final String transactionOutputString = CurrencyConversionOutputProcessor.formatCreditsQueryResult(
				"how many Credits is glob prok Gold ?", new BigDecimal(57800), galacticCurrencies.subList(0, 2));
		assertThat(transactionOutputString).isEqualTo(expectedResult);
	}

	@Test
	public void galacticCurrencyQuery_formatGCQueryResult_shouldReturnResponseWithExpressionValue() {
		String expectedResult = "pish tegj glob glob is 42";

		// Index of pish is 2, Index of tegj is 3 and index of glob is 0
		List<GalacticCurrency> galacticCurrenciesInExpression = Lists.newArrayList(galacticCurrencies.get(2),
				galacticCurrencies.get(3), galacticCurrencies.get(0), galacticCurrencies.get(0));
		final String gcQueryResult = CurrencyConversionOutputProcessor.formatGCQueryResult(
				"how much is pish tegj glob glob ?", new GalacticCurrencyExpression(galacticCurrenciesInExpression));
		assertThat(gcQueryResult).isEqualTo(expectedResult);
	}

	@Test
	public void creditQuery_calculateValuesForCreditQueries_shouldReturnCreditsQueryResults() {
		CommonMetal silver = new CommonMetal("Silver", BigDecimal.valueOf(17));
		CommonMetal gold = new CommonMetal("Gold", BigDecimal.valueOf(14450));
		CommonMetal iron = new CommonMetal("Iron", BigDecimal.valueOf(195.5));
		final List<CommonMetal> metals = Lists.newArrayList(gold, silver, iron);
		CurrencyConversionOutputProcessor currencyConversionOutputProcessor = new CurrencyConversionOutputProcessor(
				galacticCurrencies);

		final List<String> creditQueries = CurrencyConversionOutputProcessor.selectCreditQueries(inputData);
		final List<String> output = new ArrayList<String>();

		for (String query : creditQueries) {
			output.add(currencyConversionOutputProcessor.calculateValuesForCreditQueries(query, metals));
		}

		assertThat(output).isEqualTo(Lists.newArrayList("glob prok Silver is 68 Credits",
				"glob prok Gold is 57800 Credits", "glob prok Iron is 782 Credits"));
	}

	@Test
	public void GalacticCurrencyQuery_calculateValuesForGalacticCurrencyQuery_shouldReturnGalacticCurrencyQueryResults() {
		CurrencyConversionOutputProcessor currencyConversionOutputProcessor = new CurrencyConversionOutputProcessor(
				galacticCurrencies);

		final List<String> galacticCurrencyQuery = CurrencyConversionOutputProcessor
				.selectGalacticCurrencyQueries(inputData);
		final List<String> output = new ArrayList<String>();

		for (String query : galacticCurrencyQuery) {
			output.add(currencyConversionOutputProcessor.calculateValuesForGalacticCurrencyQuery(query));
		}

		assertThat(output).isEqualTo(Lists.newArrayList("pish tegj glob glob is 42"));
	}

}
