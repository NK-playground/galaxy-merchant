package galaxy.merchant;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import galaxy.merchant.exception.InvalidGalacticCurrencyConvesionQueryException;
import galaxy.merchant.model.CommonMetal;
import galaxy.merchant.model.GalacticCurrency;
import galaxy.merchant.model.GalacticCurrencyExpression;

/**
 * @author kommineni
 *
 */
public class CurrencyConversionOutputProcessor {

	static final String UNCATEGORIZED_QUERY = "I have no idea what you are talking about";

	final List<GalacticCurrency> galacticCurrencies;

	/**
	 * @param galacticCurrenciesList
	 */
	public CurrencyConversionOutputProcessor(List<GalacticCurrency> galacticCurrenciesList) {
		this.galacticCurrencies = galacticCurrenciesList;
	}

	
	/**
	 * Function to calculate the value for Galactic Currency queries
	 * @param query
	 * @return
	 */
	public String calculateValuesForGalacticCurrencyQuery(String query) {
		final GalacticCurrencyExpression galacticCurrencyExpression = getGalacticCurrencyExpression(galacticCurrencies,
				query);
		String gcValueResult = generateGalacticCurrencyQueryResult(query, galacticCurrencyExpression);
		printResult(gcValueResult);
		return gcValueResult;
	}

	/**
	 * Function to calculate the value for Metal Credit value queries
	 * @param query
	 * @param metalsInTrade
	 * @return
	 */
	public String calculateValuesForCreditQueries(String query, List<CommonMetal> metalsInTrade) {
		final GalacticCurrencyExpression galacticCurrencyExpressions = getGalacticCurrencyExpression(galacticCurrencies,
				query);
		final String creditQueryResult = generateCreditQueryResult(query, metalsInTrade, galacticCurrencyExpressions);
		printResult(creditQueryResult);
		return creditQueryResult;
	}

	
	/**
	 * Function to handle the responses for uncategorized queries
	 * @param query
	 * @return
	 */
	public String handleUncategorizedQuery(String query) {
		printResult(UNCATEGORIZED_QUERY);
		return UNCATEGORIZED_QUERY;
	}

	/**
	 * Function to build the GalacticCurrencyExpression from the GalacticCurrencyValueQuery
	 * @param galacticCurrenciesList
	 * @param gcQuery
	 * @return
	 */
	static GalacticCurrencyExpression getGalacticCurrencyExpression(List<GalacticCurrency> galacticCurrenciesList,
			String gcQuery) {
		final List<String> galacticCurrencyQueryComponents = Arrays.asList(gcQuery.split(" "));
		final List<GalacticCurrency> galacticCurrencies = GalacticCurrency
				.getGalacticCurrencyFromComponents(galacticCurrencyQueryComponents, galacticCurrenciesList);
		GalacticCurrencyExpression galacticCurrencyExpression = new GalacticCurrencyExpression(galacticCurrencies);

		return galacticCurrencyExpression;
	}

	/**
	 * Function to filter the GalacticCurrencyQueries
	 * @param inputWithoutCurrencyAssignments
	 * @return
	 */
	static List<String> selectGalacticCurrencyQueries(List<String> inputWithoutCurrencyAssignments) {
		return inputWithoutCurrencyAssignments.stream().filter(s -> s.startsWith("how much is"))
				.collect(Collectors.toList());
	}

	/**
	 * @param galacticCurrencyValueQuery
	 * @param galacticCurrencyExpression
	 * @return
	 */
	private String generateGalacticCurrencyQueryResult(String galacticCurrencyValueQuery,
			GalacticCurrencyExpression galacticCurrencyExpression) {
		Preconditions.checkArgument(galacticCurrencyValueQuery != null && galacticCurrencyExpression != null);
		return formatGCQueryResult(galacticCurrencyValueQuery, galacticCurrencyExpression);
	}

	/**
	 * @param gcValueQuery
	 * @param gcExpression
	 * @return
	 */
	static String formatGCQueryResult(final String gcValueQuery, final GalacticCurrencyExpression gcExpression) {
		final String[] splits = gcValueQuery.split(" ");
		final List<String> gcQueryResult = Arrays.asList(splits).stream().filter(s -> gcExpression
				.getGalacticCurrencyExpression().stream().anyMatch(galacticCurrency -> galacticCurrency.isSame(s)))
				.collect(Collectors.toList());
		gcQueryResult.add("is");
		gcQueryResult.add(gcExpression.getGalacticCurrencyExpressionValue().toString());
		return Joiner.on(" ").join(gcQueryResult);
	}

	/**
	 * @param creditsQuery
	 * @param metals
	 * @param galacticCurrencyExpressions
	 * @return
	 */
	private String generateCreditQueryResult(String creditsQuery, List<CommonMetal> metals,
			GalacticCurrencyExpression galacticCurrencyExpression) {
		final String creditQueryResult = formatResultsForCreditQueries(creditsQuery, metals, galacticCurrencyExpression);
		return creditQueryResult;
	}

	
	/**
	 * @param creditsTransaction
	 * @param metalList
	 * @param galacticCurrencyExpression
	 * @return
	 */
	private static String formatResultsForCreditQueries(String creditsTransaction, List<CommonMetal> metalList,
			GalacticCurrencyExpression galacticCurrencyExpression) {
		String[] creditTransactionComponents = creditsTransaction.split(" ");
		final String rareMetalSymbol = creditTransactionComponents[creditTransactionComponents.length - 2];
		Optional<CommonMetal> rareMetal = CommonMetal.selectBySymbol(rareMetalSymbol, metalList);
		if (rareMetal.isPresent()) {
			final BigDecimal creditsValue = rareMetal.get().getPerUnitValue()
					.multiply(BigDecimal.valueOf(galacticCurrencyExpression.getGalacticCurrencyExpressionValue()));
			final String creditTransactionOutput = formatCreditsQueryResult(creditsTransaction,
					creditsValue, galacticCurrencyExpression.getGalacticCurrencyExpression());
			return creditTransactionOutput;
		} else {
			throw new InvalidGalacticCurrencyConvesionQueryException("Common metal not found in credit transaction");
		}
	}

	/**
	 * @param creditsTransaction
	 * @param creditsValue
	 * @param galacticCurrenciesInQuery
	 * @return
	 */
	static String formatCreditsQueryResult(String creditsTransaction, BigDecimal creditsValue,
			List<GalacticCurrency> galacticCurrenciesInQuery) {
		final String[] splits = creditsTransaction.split(" ");
		final List<String> galacticCurrenciesInTransactionOutput = Arrays.asList(splits).stream().filter(
				s -> galacticCurrenciesInQuery.stream().anyMatch(galacticCurrency -> galacticCurrency.isSame(s)))
				.collect(Collectors.toList());
		galacticCurrenciesInTransactionOutput.add(splits[splits.length - 2]);
		galacticCurrenciesInTransactionOutput.add("is");
		galacticCurrenciesInTransactionOutput.add(creditsValue.stripTrailingZeros().toPlainString());
		galacticCurrenciesInTransactionOutput.add("Credits");
		return Joiner.on(" ").join(galacticCurrenciesInTransactionOutput);
	}

	/**
	 * Function to filter the CreditQueries
	 * @param inputWithoutCurrencyAssignments
	 * @return
	 */
	static List<String> selectCreditQueries(List<String> inputWithoutCurrencyAssignments) {
		return inputWithoutCurrencyAssignments.stream().filter(s -> s.startsWith("how many Credits"))
				.collect(Collectors.toList());
	}

	/**
	 * @param queryResult
	 */
	private static void printResult(String queryResult) {
		System.out.println(queryResult);
	}

}
