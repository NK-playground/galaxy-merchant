package galaxy.merchant.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import galaxy.merchant.exception.InvalidGalacticCurrencyExpressionException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author kommineni
 *
 */
@EqualsAndHashCode
public class GalacticCurrencyExpression {

	public static final int LEGAL_REPETITION_LIMIT = 3;
	private final List<GalacticCurrency> galacticCurrencyExpression;

	@Getter
	private final Integer galacticCurrencyExpressionValue;

	public GalacticCurrencyExpression(List<GalacticCurrency> galacticCurrencies) {
		this.galacticCurrencyExpression = ImmutableList.copyOf(galacticCurrencies);
		validateGCExpression(galacticCurrencies);
		galacticCurrencyExpressionValue = calculateGCExpressionValue(galacticCurrencies);

	}

	
	static Integer calculateGCExpressionValue(List<GalacticCurrency> galacticCurrencyExpression) {
		final List<Integer> decimalValues = galacticCurrencyExpression.stream()
				.mapToInt(GalacticCurrency::getRomanValue).boxed().collect(Collectors.toList());
		final List<Integer> additionsList = Lists.newArrayList();
		for (int currentIndex = 0; currentIndex < decimalValues.size();) {
			Integer currentValue = decimalValues.get(currentIndex);
			Integer nextValue = hasReachedEndOfExpression(decimalValues, currentIndex) ? 0
					: decimalValues.get(currentIndex + 1);
			if (currentValue >= nextValue) {
				additionsList.add(currentValue);
				currentIndex++;
			} else {
				additionsList.add(nextValue - currentValue);
				currentIndex = currentIndex + 2;
			}
		}
		return additionsList.stream().mapToInt(value -> value.intValue()).sum();
	}

	private static boolean hasReachedEndOfExpression(List<Integer> decimalValues, int currentIndex) {
		return currentIndex + 1 >= decimalValues.size();
	}

	/**
	 * @param galacticCurrencyExpression
	 * @throws InvalidGalacticCurrencyExpressionException
	 */
	static void validateGCExpression(List<GalacticCurrency> galacticCurrencyExpression) {
		validateRepetition(galacticCurrencyExpression);
		subtractionRule(galacticCurrencyExpression);
	}

	static void validateRepetition(List<GalacticCurrency> galacticCurrencyExpression) {
		validateOnlyRepeatableCurrenciesAreRepeated(galacticCurrencyExpression);
//        validateCurrenciesRepeatOnlyValidNumberOfTimesConsecutively(LEGAL_REPETITION_LIMIT,galacticCurrencyExpression);
		validateCurrenciesConsecutiveRepetition(galacticCurrencyExpression);

	}

	private static void validateCurrenciesConsecutiveRepetition(List<GalacticCurrency> galacticCurrencyExpression) {
		for (int currentIndex = 0; currentIndex < galacticCurrencyExpression.size(); currentIndex++) {
			GalacticCurrency candidateGalacticCurrency = galacticCurrencyExpression.get(currentIndex);
			int endIndexToCheckRepeatLimit = currentIndex + LEGAL_REPETITION_LIMIT + 1;
			endIndexToCheckRepeatLimit = endIndexToCheckRepeatLimit > galacticCurrencyExpression.size()
					? galacticCurrencyExpression.size()
					: endIndexToCheckRepeatLimit;
			final List<GalacticCurrency> currenciesImmediatelyFollowingCurrentCurrency = galacticCurrencyExpression
					.subList(currentIndex + 1, endIndexToCheckRepeatLimit);
			if (LEGAL_REPETITION_LIMIT > currenciesImmediatelyFollowingCurrentCurrency.size()) {
				break;
			}

			final boolean isRepeatLimitExceeded = currenciesImmediatelyFollowingCurrentCurrency.stream()
					.allMatch(galacticCurrency -> galacticCurrency.equals(candidateGalacticCurrency));
			if (isRepeatLimitExceeded)
				throw new InvalidGalacticCurrencyExpressionException(
						"Invalid Currency Format - Repetition beyond permitted");
		}
	}

	private static void validateOnlyRepeatableCurrenciesAreRepeated(List<GalacticCurrency> galacticCurrencyExpression) {
		for (GalacticCurrency galacticCurrency : galacticCurrencyExpression) {
			final int frequency = Collections.frequency(galacticCurrencyExpression, galacticCurrency);
			if (!galacticCurrency.isRepeatable()) {
				if (frequency > 1)
					throw new InvalidGalacticCurrencyExpressionException(
							"Invalid Currency Format - Repetition not permitted");
			}
		}
	}

	static void subtractionRule(List<GalacticCurrency> galacticCurrencyExpression) {
		for (int i = 0; i < galacticCurrencyExpression.size() - 1; i++) {
			GalacticCurrency currentGalacticCurrencyInExpression = galacticCurrencyExpression.get(i);
			GalacticCurrency nextGalacticCurrencyInExpression = galacticCurrencyExpression.get(i + 1);
			if (currentGalacticCurrencyInExpression.getRomanValue() < nextGalacticCurrencyInExpression
					.getRomanValue()) {
				final Boolean validSubtraction = currentGalacticCurrencyInExpression
						.isValidSubtraction(nextGalacticCurrencyInExpression);
				if (!validSubtraction)
					throw new InvalidGalacticCurrencyExpressionException(
							"Invalid Currency Format - character Sequence invalid for substraction");
			}
		}
	}

	public List<GalacticCurrency> getGalacticCurrencyExpression() {
		return ImmutableList.copyOf(galacticCurrencyExpression);
	}
}
