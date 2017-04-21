package de.zabuza.beedlebot.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import de.zabuza.beedlebot.exceptions.StandardShopPriceServiceUnavailableException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;

public final class StandardShopPriceFinder {

	public static final int NO_PRICE = -1;
	private static final String CONTENT_END_PATTERN_FIRST = "|";
	private static final String CONTENT_END_PATTERN_SECOND = "}";
	private static final String CONTENT_IGNORE_PET_PATTERN = "{{NPC/Layout|";
	private static final String CONTENT_START_PATTERN = "|VerkPreis=";
	private static final String SERVER_QUERY_POST = "&action=edit";
	private static final String SERVER_QUERY_PRE = "?title=";
	private static final String SERVER_URL = "http://www.fwwiki.de/index.php";
	private static final String STRIP_INTEGER_PATTERN = "[\\s\\.,]";

	private final ItemDictionary mItemDictionary;
	private final ILogger mLogger;

	public StandardShopPriceFinder(final ItemDictionary itemDictionary) {
		this.mItemDictionary = itemDictionary;
		this.mLogger = LoggerFactory.getLogger();
	}

	public Optional<Integer> findStandardShopPrice(final String itemName)
			throws StandardShopPriceServiceUnavailableException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Finding standard shop price: " + itemName);
		}

		Integer shopPrice = null;

		final String parsedItemName = this.mItemDictionary.applyItemNamePatterns(itemName);

		// Process exceptional items
		if (this.mItemDictionary.containsStandardShopPrice(parsedItemName)) {
			return this.mItemDictionary.getStandardShopPrice(parsedItemName);
		}

		final String itemToUrl = parsedItemName.replaceAll("\\s", "_");
		URL url;
		try {
			url = new URL(SERVER_URL + SERVER_QUERY_PRE + itemToUrl + SERVER_QUERY_POST);
		} catch (final MalformedURLException e) {
			throw new StandardShopPriceServiceUnavailableException(e);
		}

		try (final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
			// Find shop price parameter
			final StringBuilder shopPriceContent = new StringBuilder();
			int startIndex = -1;
			while (br.ready()) {
				final String line = br.readLine();
				if (line == null) {
					break;
				}

				// The item is a pet
				if (line.indexOf(CONTENT_IGNORE_PET_PATTERN) != -1) {
					return Optional.of(Integer.valueOf(0));
				}

				startIndex = line.indexOf(CONTENT_START_PATTERN);

				if (startIndex != -1) {
					// Add rest of line
					shopPriceContent.append(line.substring(startIndex + CONTENT_START_PATTERN.length()));
					break;
				}
			}
			if (startIndex == -1) {
				return Optional.empty();
			}

			// Find shop price parameter ending
			int endIndex = -1;
			while (br.ready()) {
				final String line = br.readLine();
				if (line == null) {
					break;
				}
				endIndex = line.indexOf(CONTENT_END_PATTERN_FIRST);
				if (endIndex == -1) {
					// Try next pattern
					endIndex = line.indexOf(CONTENT_END_PATTERN_SECOND);
				}

				if (endIndex != -1) {
					// Add beginning of line
					shopPriceContent.append(line.substring(0, endIndex));
					break;
				}
				// Add whole line
				shopPriceContent.append(line);
			}
			if (endIndex == -1) {
				return Optional.empty();
			}

			// Price extracted, format it
			final String shopPriceText = shopPriceContent.toString();
			shopPrice = new Integer(shopPriceText.replaceAll(STRIP_INTEGER_PATTERN, ""));
		} catch (final IOException e) {
			throw new StandardShopPriceServiceUnavailableException(e);
		}

		return Optional.of(shopPrice);
	}
}
