
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlphaVantage {

	private static final String API_KEY = "3D5IQHGREP8EXDTP";
	private static String time;

	// get tickers with price, no api key
	private static Map<String, Double> getData() throws IOException {
		Map<String, Double> data = new HashMap<String, Double>();
		String root = "https://financialmodelingprep.com/api/stock/list/all?datatype=json";
		URL request = new URL(root);
		InputStream openStream = request.openStream();
		String response = IOUtils.toString(openStream, "UTF-8");
		JSONArray json = new JSONArray(response);
		json.forEach(item -> data.put(((JSONObject) item).getString("Ticker"), ((JSONObject) item).getDouble("Price")));
		return data;
	}

	// get tickers, no price, no api key
	private static List<String> symbols() throws IOException {
		List<String> symbols = new ArrayList<String>();
		String root = "https://api.iextrading.com/1.0/ref-data/symbols";
		URL request = new URL(root);
		InputStream openStream = request.openStream();
		String response = IOUtils.toString(openStream, "UTF-8");
		JSONArray json = new JSONArray(response);
		json.forEach(item -> symbols.add(((JSONObject) item).getString("symbol")));
		return symbols;
	}

	private static List<String> randomElements(List<String> list, int size) {
		List<String> rList = new ArrayList<String>();
		List<String> helper = list;
		Random rand = new Random();
		for (int i = 0; i < size; i++) {
			int index = rand.nextInt(helper.size());
			rList.add(helper.get(index));
			helper.remove(index);
		}
		return rList;
	}

	// BATCH_STOCK_QUOTES -> up to 100 quotes
	private static Map<StockInfo, String> alphaData() throws IOException {
		Map<StockInfo, String> map = new HashMap<StockInfo, String>();
		List<String> symbols = randomElements(symbols(), 100);
		String root = "https://www.alphavantage.co/query?";
		root += "function=BATCH_STOCK_QUOTES";
		root += "&apikey=" + API_KEY + "&symbols=";
		for (String name : symbols)
			root += URLEncoder.encode(name, "UTF-8") + ",";
		URL request = new URL(root.substring(0, root.length() - 1));
		InputStream openStream = request.openStream();
		String response = IOUtils.toString(openStream, "UTF-8");
		JSONObject body = new JSONObject(response);
		JSONArray quotes;
		try {
			quotes = body.getJSONArray("Stock Quotes");
			quotes.forEach(x -> map.put(
					new StockInfo(((JSONObject) x).getString("1. symbol"), ((JSONObject) x).getDouble("2. price")),
					" at " + ((JSONObject) x).getString("4. timestamp")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return map;
	}

	// to get price for given ticker
	// max 5 requests per minute to Alpha Vantage, 500 daily
	private static double getPrice(String name) throws IOException {
		String root = "https://www.alphavantage.co/query?";
		root += "function=GLOBAL_QUOTE";
		root += "&symbol=" + URLEncoder.encode(name, "UTF-8");
		root += "&apikey=" + API_KEY;
		URL request = new URL(root);
		InputStream openStream = request.openStream();
		String response = IOUtils.toString(openStream, "UTF-8");
		JSONObject body = new JSONObject(response);
		JSONObject quote;
		try {
			quote = body.getJSONObject("Global Quote");
			time = quote.getString("07. latest trading day");
			return quote.getDouble("05. price");
		} catch (JSONException e) {
			System.out.println("No info for " + name);
			return -1;
		}
	}

	private static double exchangeRate(String fromCurrencyCode, String toCurrencyCode) throws IOException {
		String root = "https://www.alphavantage.co/query?";
		root += "function=CURRENCY_EXCHANGE_RATE";
		root += "&from_currency=" + URLEncoder.encode(fromCurrencyCode, "UTF-8");
		root += "&to_currency=" + URLEncoder.encode(toCurrencyCode, "UTF-8");
		root += "&apikey=" + API_KEY;
		URL request = new URL(root);
		InputStream openStream = request.openStream();
		String response = IOUtils.toString(openStream, "UTF-8");
		JSONObject meta = new JSONObject(response);
		JSONObject rate;
		try {
			rate = meta.getJSONObject("Realtime Currency Exchange Rate");
			time = rate.getString("6. Last Refreshed");
			return rate.getDouble("5. Exchange Rate");
		} catch (JSONException e) {
			System.out.println("Invalid currency code.");
			return -1;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		// 1. Print these symbols using a Java 8 for-each and lambdas
		symbols().forEach(System.out::println);

		// 2. print the price of Bitcoin
		System.out.println("Price of Bitcoin: " + getPrice("BTC-USD") + " at " + time);
		System.out.println("Exchange rate of Bitcoin to USD: " + exchangeRate("BTC", "USD") + " at " + time);

		// 3. Create a new List of StockInfo that includes the stock price
		List<StockInfo> stock = getData().entrySet().stream().map(x -> new StockInfo(x.getKey(), x.getValue()))
				.collect(Collectors.toList());
		List<StockInfo> alphaStock = alphaData().entrySet().stream().map(x -> x.getKey()).collect(Collectors.toList());

		// 4. Find the highest-priced stock under $500
		StockInfo highest = stock.stream().filter(StockUtil.isPriceLessThan(500)).reduce(StockUtil::pickHigh).get();
		System.out.println("financialmodelingprep.com: The highest-priced stock under $500 is " + highest);
		System.out.println("Price of " + highest.ticker + " on alphavantage.co: " + getPrice(highest.ticker));

		StockInfo alphaStockHighest = alphaStock.stream().filter(StockUtil.isPriceLessThan(500))
				.reduce(StockUtil::pickHigh).get();
		System.out.println("Alpha Vantage random list: The highest-priced stock under $500 is " + alphaStockHighest);
	}
}

// Copyright (c) jelenam86, 2019