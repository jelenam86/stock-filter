
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class StockUtil {
	public static StockInfo getPrice(final String ticker) {
		return new StockInfo(ticker, prices.get(ticker));
	}

	public static Predicate<StockInfo> isPriceLessThan(final double price) {
		return stockInfo -> stockInfo.price < price;
	}

	public static StockInfo pickHigh(final StockInfo stockInfo1,
			final StockInfo stockInfo2) {
		return stockInfo1.price > stockInfo2.price ? stockInfo1 : stockInfo2;
	}

	static Map<String, Double> prices = new HashMap<String, Double>() {
		{

			put("AMD", 3.28);
			put("HPQ", 35.92);
			put("IBM", 189.36);
			put("TXN", 46.94);
			put("VMW", 92.98);
			put("XRX", 13.09);
			put("AAPL", 100.8);
			put("ADBE", 67.99);
			put("AMZN", 322.7);
			put("CRAY", 25.92);
			put("CSCO", 24.66);
			put("SNE", 17.79);
			put("GOOG", 572.5);
			put("INTC", 34.27);
			put("INTU", 83.8);
			put("MSFT", 46.78);
			put("ORCL", 39.02);
			put("TIBX", 23.44);
			put("VRSN", 56.13);
			put("YHOO", 41.08);
			put("BTC-USD", 9655.32);

		}
	};
}


// Copyright (c) jelenam86, 2019