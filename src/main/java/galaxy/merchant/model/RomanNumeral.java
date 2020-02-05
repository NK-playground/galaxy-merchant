package galaxy.merchant.model;

import java.util.Collections;
import java.util.List;

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
public class RomanNumeral {
	private final Character symbol;
	private final Boolean isRepeatable;
	private final Boolean isSubtractable;
	private final List<RomanNumeral> subtractableFrom;
	private final Integer value;

	public static RomanNumeral standAlone(Character symbol, Integer value) {
		return new RomanNumeral(symbol, Boolean.FALSE, Boolean.FALSE, Collections.emptyList(), value);
	}

	public static RomanNumeral repeatableAndSubtractable(Character symbol, List<RomanNumeral> subtractableFrom,
			Integer value) {
		return new RomanNumeral(symbol, Boolean.TRUE, Boolean.TRUE, subtractableFrom, value);
	}

	public boolean isSameSymbol(Character symbol) {
		return this.symbol.equals(symbol);
	}
}
