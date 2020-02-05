package galaxy.merchant.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class GalacticCurrency {

	final String symbol;
	final private RomanNumeral romanNumeral;

	public static GalacticCurrency createFromQueryComponent(String galacticCurrencySymbol,
			List<GalacticCurrency> galacticCurrenciesMasterList) {
		return galacticCurrenciesMasterList.stream()
				.filter(galacticCurrency -> galacticCurrency.isSame(galacticCurrencySymbol)).findFirst().get();
	}

	public static List<GalacticCurrency> getGalacticCurrencyFromComponents(List<String> galacticCurrencyQueryComponents,
			List<GalacticCurrency> galacticCurrenciesMasterList) {
		final Stream<String> galacticCurrencyComponents = galacticCurrencyQueryComponents.stream()
				.filter(gcComponent -> {
					return galacticCurrenciesMasterList.stream()
							.anyMatch(galacticCurrency -> galacticCurrency.isSame(gcComponent));
				});
		final List<GalacticCurrency> galacticCurrenciesInQuery = galacticCurrencyComponents
				.map(galacticCurrencySymbol -> createFromQueryComponent(galacticCurrencySymbol,
						galacticCurrenciesMasterList))
				.collect(Collectors.toList());
		return galacticCurrenciesInQuery;
	}

	public Boolean isValidSubtraction(GalacticCurrency galacticCurrency) {
		return this.isSubstractable()
				&& this.romanNumeral.getSubtractableFrom().contains(galacticCurrency.getRomanNumeral());
	}

	public boolean isSame(String symbol) {
		return this.symbol.equals(symbol);
	}

	public Boolean isSubstractable() {
		return romanNumeral.getIsSubtractable();
	}

	public Boolean isRepeatable() {
		return romanNumeral.getIsRepeatable();
	}

	public Integer getRomanValue() {
		return romanNumeral.getValue();
	}

}
