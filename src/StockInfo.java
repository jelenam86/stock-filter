
public class StockInfo {
	public final String ticker;
	public final double price;

	public StockInfo(final String symbol, final double thePrice) {
		ticker = symbol;
		price = thePrice;
	}

	@Override
	public String toString() {
		return ticker + " " + price + ". ";
	}
}

// Copyright (c) jelenam86, 2019