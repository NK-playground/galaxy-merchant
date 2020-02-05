package galaxy.merchant.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import galaxy.merchant.exception.InvalidGalacticCurrencyConvesionQueryException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author kommineni
 *
 */

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class CommonMetal {

	private final String metalName;
	private final BigDecimal perUnitValue;

	public static CommonMetal createFromMetalValueDefinition(String metalValueDefinition,
			final GalacticCurrencyExpression galacticCurrencyExpression) {
		final Integer galacticCurrencyExpressionValue = galacticCurrencyExpression.getGalacticCurrencyExpressionValue();
		final String metalSymbol = extractCommonMetalName(metalValueDefinition);
		final Integer metalTotalValue = extractMetalValue(metalValueDefinition);
		BigDecimal perUnitValue = BigDecimal.valueOf(metalTotalValue)
				.divide(BigDecimal.valueOf(galacticCurrencyExpressionValue));
		return new CommonMetal(metalSymbol, perUnitValue);
	}

	static String extractCommonMetalName(String metalValueDefinition) {
		final String[] components = metalValueDefinition.split(" ");
		for (int i = 0; i < components.length; i++) {
			if (components[i].equals("is")) {
				return components[i - 1];
			}
		}
		throw new InvalidGalacticCurrencyConvesionQueryException("No metal name in input String");
	}

	/**
	 * @param assignmentTransaction
	 * @return
	 */
	static Integer extractMetalValue(String assignmentTransaction) {
		final String[] components = assignmentTransaction.split(" ");
		for (int i = 0; i < components.length; i++) {
			if (components[i].equals("Credits")) {
				return Integer.valueOf(components[i - 1]);
			}
		}
		throw new InvalidGalacticCurrencyConvesionQueryException("No credits found in transaction");
	}

	public static Optional<CommonMetal> selectBySymbol(final String metal, final Collection<CommonMetal> metalList) {
		return metalList.stream().filter(cMetal -> cMetal.metalName.equals(metal)).findFirst();
	}

	/**
	 * @param nonCurrencyDefinitionInput
	 * @param galacticCurrencyList
	 * @return
	 */
	public static List<CommonMetal> buildMetalList(List<String> nonCurrencyDefinitionInput,
			List<GalacticCurrency> galacticCurrencyList) {

		// Determine the metal value definition input records
		final List<String> metalValueDefinitions = fetchMetalValueDefinitions(nonCurrencyDefinitionInput);

		final List<CommonMetal> commonMetals = Lists.newArrayList();
		for (String metalValueDefinition : metalValueDefinitions) {
			final List<GalacticCurrency> galacticCurrenciesInMetalValueDefinition = GalacticCurrency
					.getGalacticCurrencyFromComponents(Arrays.asList(metalValueDefinition.split(" ")),
							galacticCurrencyList);
			GalacticCurrencyExpression galacticCurrencyExpression = new GalacticCurrencyExpression(
					galacticCurrenciesInMetalValueDefinition);
			final CommonMetal rareMetal = CommonMetal.createFromMetalValueDefinition(metalValueDefinition,
					galacticCurrencyExpression);
			commonMetals.add(rareMetal);
		}
		return commonMetals;
	}

	/**
	 * @param nonCurrencyDefinitionInput
	 * @return
	 */
	public static List<String> fetchMetalValueDefinitions(List<String> nonCurrencyDefinitionInput) {
		return nonCurrencyDefinitionInput.stream().filter(ncdInput -> ncdInput.endsWith("Credits"))
				.collect(Collectors.toList());
	}
}
