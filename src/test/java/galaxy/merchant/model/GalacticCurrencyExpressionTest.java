package galaxy.merchant.model;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import galaxy.merchant.exception.InvalidGalacticCurrencyExpressionException;


/**
 * @author kommineni
 *
 */

public class GalacticCurrencyExpressionTest {

	public static final int GLOB = 0;
	public static final int PROK = 1;
	public static final int PISH = 2;
	public static final int TEGJ = 3;

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
	public void repeatableCurrenciesRepeat_shouldPassValidation() throws Exception {
		List<GalacticCurrency> repeatCurrencyMoreThanLegalLimitExpression = Lists
				.newArrayList(galacticCurrencies.get(GLOB), galacticCurrencies.get(GLOB), galacticCurrencies.get(GLOB));
		GalacticCurrencyExpression.validateGCExpression(repeatCurrencyMoreThanLegalLimitExpression);
	}

	@Test
	public void nonConsecutiveRepetitionOfCurrencies_shouldPassValidation() {
		List<GalacticCurrency> repeatCurrencyMoreThanLegalLimitExpression = Lists.newArrayList(
				galacticCurrencies.get(GLOB), galacticCurrencies.get(GLOB), galacticCurrencies.get(GLOB),
				galacticCurrencies.get(PROK), galacticCurrencies.get(GLOB));
		GalacticCurrencyExpression.validateGCExpression(repeatCurrencyMoreThanLegalLimitExpression);
	}

	@Test
	public void smallerDenominationPrecedLargerDenomination_shouldPassValidation() {
		List<GalacticCurrency> smallerDenominationPrecedesLargerDenomination = Lists.newArrayList(
				galacticCurrencies.get(GLOB), galacticCurrencies.get(PISH), galacticCurrencies.get(PISH),
				galacticCurrencies.get(TEGJ), galacticCurrencies.get(GLOB));
		GalacticCurrencyExpression.validateGCExpression(smallerDenominationPrecedesLargerDenomination);
	}

	
	@Test
	public void validGCExpressionWithNoRepetitionOrSubtractions_calculateGCExpressionValue_shouldReturnValue() {
		List<GalacticCurrency> noRepetitionOrSubstractions = Lists.newArrayList(galacticCurrencies.get(TEGJ),
				galacticCurrencies.get(PISH), galacticCurrencies.get(PROK), galacticCurrencies.get(GLOB));
		final Integer expressionValue = GalacticCurrencyExpression
				.calculateGCExpressionValue(noRepetitionOrSubstractions);
		assertThat(expressionValue).isEqualTo(66);
	}

	@Test
	public void validGCExpressionWithRepetitionsButNoSubtractions_whenDecimalValueIsCalculated_shouldReturnValue() {
		List<GalacticCurrency> noRepetitionOrSubstractions = Lists.newArrayList(galacticCurrencies.get(TEGJ),
				galacticCurrencies.get(PISH), galacticCurrencies.get(PISH), galacticCurrencies.get(PISH),
				galacticCurrencies.get(PROK), galacticCurrencies.get(GLOB), galacticCurrencies.get(GLOB));
		final Integer expressionValue = GalacticCurrencyExpression
				.calculateGCExpressionValue(noRepetitionOrSubstractions);
		assertThat(expressionValue).isEqualTo(87);

	}

	@Test
	public void validGCExpressionWithNoRepetitionsButOneSubtraction_whenDecimalValueIsCalculated_shouldReturnValue() {
		List<GalacticCurrency> noRepetitionOrSubstractions = Lists.newArrayList(galacticCurrencies.get(TEGJ),
				galacticCurrencies.get(PISH), galacticCurrencies.get(TEGJ));
		final Integer expressionValue = GalacticCurrencyExpression
				.calculateGCExpressionValue(noRepetitionOrSubstractions);
		assertThat(expressionValue).isEqualTo(90);

	}

	@Test
	public void validGCExpressionWithNoRepetitionsButMultipleSubtractions_whenDecimalValueIsCalculated_shouldReturnValue() {
		List<GalacticCurrency> noRepetitionOrSubstractions = Lists.newArrayList(galacticCurrencies.get(TEGJ),
				galacticCurrencies.get(PISH), galacticCurrencies.get(TEGJ), galacticCurrencies.get(GLOB),
				galacticCurrencies.get(PROK), galacticCurrencies.get(GLOB));
		final Integer expressionValue = GalacticCurrencyExpression
				.calculateGCExpressionValue(noRepetitionOrSubstractions);
		assertThat(expressionValue).isEqualTo(95);

	}

	@Test
	public void validGCExpressionWithMultipleRepetitionsAndMultipleSubtractions_whenDecimalValueIsCalculated_shouldReturnValue() {
		List<GalacticCurrency> noRepetitionOrSubstractions = Lists.newArrayList(galacticCurrencies.get(TEGJ),
				galacticCurrencies.get(TEGJ), galacticCurrencies.get(PISH), galacticCurrencies.get(TEGJ),
				galacticCurrencies.get(GLOB), galacticCurrencies.get(PROK), galacticCurrencies.get(GLOB),
				galacticCurrencies.get(GLOB));
		final Integer expressionValue = GalacticCurrencyExpression
				.calculateGCExpressionValue(noRepetitionOrSubstractions);
		assertThat(expressionValue).isEqualTo(146);

	}
	
	@Test(expected = InvalidGalacticCurrencyExpressionException.class)
	public void nonRepeatableCurrenciesRepeat_shouldThrowInvalidExpressionException() throws Exception {
		List<GalacticCurrency> invalidGalacticCurrencyExpression = Lists.newArrayList(galacticCurrencies.get(PROK),
				galacticCurrencies.get(PROK));
		GalacticCurrencyExpression.validateGCExpression(invalidGalacticCurrencyExpression);
	}

	@Test(expected = InvalidGalacticCurrencyExpressionException.class)
	public void currenciesRepeatMoreThanAllowedLimit_shouldThrowInvalidExpressionException() throws Exception {
		List<GalacticCurrency> repeatCurrencyMoreThanLegalLimitExpression = Lists.newArrayList(
				galacticCurrencies.get(GLOB), galacticCurrencies.get(GLOB), galacticCurrencies.get(GLOB),
				galacticCurrencies.get(GLOB));
		GalacticCurrencyExpression.validateGCExpression(repeatCurrencyMoreThanLegalLimitExpression);
	}
	
	@Test(expected = InvalidGalacticCurrencyExpressionException.class)
	public void inValidSmallerDenominationPrecededLargerDenomination_shouldThrowInvalidExpressionException() {
		List<GalacticCurrency> invalidSmallerDenominationPrecedesLargerDenomination = Lists.newArrayList(
				galacticCurrencies.get(GLOB), galacticCurrencies.get(TEGJ), galacticCurrencies.get(PISH),
				galacticCurrencies.get(TEGJ), galacticCurrencies.get(GLOB));
		GalacticCurrencyExpression.validateGCExpression(invalidSmallerDenominationPrecedesLargerDenomination);
	}

}
