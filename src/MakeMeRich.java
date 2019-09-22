
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class MakeMeRich {

	public static final List<String> symbols = Arrays.asList("AMD", "HPQ", "IBM", "TXN", "VMW", "XRX", "AAPL", "ADBE",
			"AMZN", "CRAY", "CSCO", "SNE", "GOOG", "INTC", "INTU", "MSFT", "ORCL", "TIBX", "VRSN", "YHOO");

	private static boolean checkCopyright(File file) throws IOException {
		return Arrays.stream(IOUtils.toString(file.toURI(), "UTF-8").split("\\r?\\n"))
				.filter(line -> line.startsWith("// Copyright")).findFirst().isPresent();
	}

	public static void setCopyright() {
		try {
			for (File f : FileUtils.listFiles(new File("src"), new String[] { "java" }, true))
				if (!checkCopyright(f))
					FileUtils.write(f, "\n\n// Copyright (c) jelenam86, " + LocalDate.now().getYear(), "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// 1. Print these symbols using a Java 8 for-each and lambdas
		symbols.stream().forEach(System.out::println);

		// 2. Use the StockUtil class to print the price of Bitcoin
		System.out.println("Price of Bitcoin: " + StockUtil.getPrice("BTC-USD"));

		// 3. Create a new List of StockInfo that includes the stock price
		List<StockInfo> stock = StockUtil.prices.entrySet().stream().map(x -> new StockInfo(x.getKey(), x.getValue()))
				.collect(Collectors.toList());
//		stock.forEach(System.out::println);

		// 4. Find the highest-priced stock under $500
		StockInfo highest = stock.stream().filter(StockUtil.isPriceLessThan(500)).reduce(StockUtil::pickHigh).get();
		System.out.println("The highest-priced stock under $500 is " + highest);

		setCopyright();
	}
}

// Copyright (c) jelenam86, 2019