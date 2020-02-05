package galaxy.merchant;

import static galaxy.merchant.model.CommonMetal.buildMetalList;
import static galaxy.merchant.model.CommonMetal.fetchMetalValueDefinitions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import galaxy.merchant.model.CommonMetal;
import galaxy.merchant.model.GalacticCurrency;
import galaxy.merchant.model.RomanNumeral;
import lombok.Getter;

/**
 * @author kommineni
 *
 */
public class GalaxyMerchant {

	@Getter
	private final List<RomanNumeral> romanSymbols;

	public GalaxyMerchant() {
		RomanNumeral romanSymbolThousand = RomanNumeral.standAlone('M', 1000);
		RomanNumeral romanSymbolFiveHundred = RomanNumeral.standAlone('D', 500);
		RomanNumeral romanSymbolFifty = RomanNumeral.standAlone('L', 50);
		RomanNumeral romanSymbolFive = RomanNumeral.standAlone('V', 5);
		RomanNumeral romanSymbolHundred = RomanNumeral.repeatableAndSubtractable('C',
				Lists.newArrayList(romanSymbolFiveHundred, romanSymbolThousand), 100);
		RomanNumeral romanSymbolTen = RomanNumeral.repeatableAndSubtractable('X',
				Lists.newArrayList(romanSymbolFifty, romanSymbolHundred), 10);
		RomanNumeral romanSymbolOne = RomanNumeral.repeatableAndSubtractable('I',
				Lists.newArrayList(romanSymbolFive, romanSymbolTen), 1);
		romanSymbols = ImmutableList.of(romanSymbolOne, romanSymbolFive, romanSymbolTen, romanSymbolFifty,
				romanSymbolHundred, romanSymbolFiveHundred, romanSymbolThousand);

	}

	/**
	 * @param inputData
	 * @return
	 */
	public List<String> galacticCurrencyConversion(final List<String> inputData) {

		// sanitize the input
		final List<String> sanitizedInput = santizeInput(inputData);

		// Filter the Galactic Currency definitions from the input.
		List<String> galacticCurrencyDefinitions = identifyGalacticCurrencyDefinitions(sanitizedInput, romanSymbols);

		// Build a list of Galactic Currencies along with their roman numeral values
		// from the input
		final List<GalacticCurrency> galacticCurrencies = ImmutableList
				.copyOf(createGalacticCurrencies(galacticCurrencyDefinitions, romanSymbols));

		// Identify the non currency definitions in the input for further processing
		// (Metal value determination)
		final ArrayList<String> nonCurrencyDefinitionInput = Lists.newArrayList(sanitizedInput);
		nonCurrencyDefinitionInput.removeAll(galacticCurrencyDefinitions);

		// Determine the unit Value for the metals that are traded
		final List<CommonMetal> metals = buildMetalList(nonCurrencyDefinitionInput, galacticCurrencies);

		// Filter the queryList
		final ArrayList<String> queryList = Lists.newArrayList(nonCurrencyDefinitionInput);
		final List<String> metalValueDefinitions = fetchMetalValueDefinitions(nonCurrencyDefinitionInput);
		queryList.removeAll(metalValueDefinitions);

		//Determine the values for the currency/credit queries
		List<String> output = calculateResultsForQueries(queryList, galacticCurrencies, metals);
		return output;
	}

	/**
	 * Function to Sanitize the input -- Trim and remove the extra spaces.
	 * @param inputList
	 * @return
	 */
	static List<String> santizeInput(final List<String> inputList) {
		return inputList.stream().map((input) -> input.trim().replaceAll(" +", " ")).collect(Collectors.toList());
	}

	/**
	 * Function to determine the GalacticCurrencyDefinitions from the list of inputData
	 * @param inputData
	 * @param romanSymbols
	 * @return
	 */
	static List<String> identifyGalacticCurrencyDefinitions(List<String> inputData, List<RomanNumeral> romanSymbols) {
		return inputData.stream().filter(inputRecord -> {
			if (inputRecord.split(" ").length == 3) {
				final Character symbol = inputRecord.split(" ")[2].toCharArray()[0];
				final Optional<RomanNumeral> matchedRomanSymbol = romanSymbols.stream()
						.filter(romanSymbol -> romanSymbol.isSameSymbol(symbol)).findAny();
				return matchedRomanSymbol.isPresent();
			}
			return false;
		}).collect(Collectors.toList());
	}

	/**
	 * Function to create the Galactic Currency from the Galactic Currency Definitions
	 * @param galacticCurrencyDefinitions
	 * @param romanSymbols
	 * @return
	 */
	static List<GalacticCurrency> createGalacticCurrencies(List<String> galacticCurrencyDefinitions,
			List<RomanNumeral> romanSymbols) {
		return galacticCurrencyDefinitions.stream().map(galacticCurrency -> {
			final String[] gcTokens = galacticCurrency.split(" ");
			final String galacticCurrencySymbol = gcTokens[0];
			final Character romanValueSymbol = gcTokens[2].toCharArray()[0];
			final RomanNumeral selectedRomanSymbol = romanSymbols.stream()
					.filter(romanSymbol -> romanSymbol.isSameSymbol(romanValueSymbol)).findAny().get();
			return new GalacticCurrency(galacticCurrencySymbol, selectedRomanSymbol);
		}).collect(Collectors.toList());
	}

	/**
	 * @param queryList
	 * @param galacticCurrencies
	 * @param metals
	 * @return
	 */
	private static List<String> calculateResultsForQueries(List<String> queryList,
			final List<GalacticCurrency> galacticCurrencies, List<CommonMetal> metals) {

		CurrencyConversionOutputProcessor currencyConversionOutputProcessor = new CurrencyConversionOutputProcessor(
				galacticCurrencies);

		List<String> results = new ArrayList<String>();
		for (String query : queryList) {
			if (!query.isEmpty()) {
				if (query.startsWith("how much is")) {
					results.add(calculateValuesForGalacticCurrencyQueries(query, currencyConversionOutputProcessor));
				} else if (query.startsWith("how many Credits")) {
					results.add(calculateValuesForCreditQueries(query, currencyConversionOutputProcessor, metals));
				} else {
					results.add(handleUncategorizedQuery(query, currencyConversionOutputProcessor));
				}
			}
		}

		return results;
	}

	/**
	 * @param query
	 * @param currencyConversionOutputProcessor
	 * @param rareMetals
	 * @return
	 */
	private static String calculateValuesForCreditQueries(String query,
			CurrencyConversionOutputProcessor currencyConversionOutputProcessor, List<CommonMetal> rareMetals) {
		return currencyConversionOutputProcessor.calculateValuesForCreditQueries(query, rareMetals);
	}

	
	/**
	 * @param qcQuery
	 * @param currencyConversionOutputProcessor
	 * @return
	 */
	private static String calculateValuesForGalacticCurrencyQueries(String qcQuery,
			CurrencyConversionOutputProcessor currencyConversionOutputProcessor) {
		return currencyConversionOutputProcessor.calculateValuesForGalacticCurrencyQuery(qcQuery);
	}

	/**
	 * @param qcQuery
	 * @param currencyConversionOutputProcessor
	 * @return
	 */
	private static String handleUncategorizedQuery(String qcQuery,
			CurrencyConversionOutputProcessor currencyConversionOutputProcessor) {
		return currencyConversionOutputProcessor.handleUncategorizedQuery(qcQuery);
	}

}
