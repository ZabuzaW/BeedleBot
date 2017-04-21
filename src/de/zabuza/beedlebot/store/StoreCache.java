package de.zabuza.beedlebot.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import de.zabuza.beedlebot.exceptions.StoreCacheDeserializationUnsuccessfulException;
import de.zabuza.beedlebot.exceptions.StoreCacheSerializationUnsuccessfulException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.EWorld;

public final class StoreCache implements Serializable {
	private static final String FILEPATH_SERIALIZATION_PRE = "storeCache_";

	private static final String FILEPATH_SERIALIZATION_SUC = ".ser";
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public static StoreCache deserialize(final EWorld world) throws StoreCacheDeserializationUnsuccessfulException {
		LoggerFactory.getLogger().logInfo("Deserializing StoreCache");

		StoreCache cache = null;
		try (final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(buildSerializationPath(world)))) {
			cache = (StoreCache) ois.readObject();
			cache.refreshLogger();
		} catch (final IOException | ClassNotFoundException e) {
			throw new StoreCacheDeserializationUnsuccessfulException(e);
		}
		return cache;
	}

	public static boolean hasSerializedCache(final EWorld world) {
		final File cacheFile = new File(buildSerializationPath(world));
		return cacheFile.exists() && !cacheFile.isDirectory();
	}

	private static String buildSerializationPath(final EWorld world) {
		return FILEPATH_SERIALIZATION_PRE + world + FILEPATH_SERIALIZATION_SUC;
	}

	private transient ILogger mLogger;

	private final HashMap<String, ItemPrice> mNameToPriceData;
	private final EWorld mWorld;

	public StoreCache(final EWorld world) {
		this.mNameToPriceData = new HashMap<>();
		this.mWorld = world;
		this.mLogger = LoggerFactory.getLogger();
	}

	public void clear() {
		this.mNameToPriceData.clear();
	}

	public Collection<ItemPrice> getAllItemPrices() {
		return Collections.unmodifiableCollection(this.mNameToPriceData.values());
	}

	public ItemPrice getItemPrice(final String itemName) {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Get item price from cache: " + itemName);
		}
		return this.mNameToPriceData.get(itemName);
	}

	public boolean hasItemPrice(final String itemName) {
		return this.mNameToPriceData.containsKey(itemName);
	}

	public void putItemPrice(final ItemPrice itemPrice) {
		this.mNameToPriceData.put(itemPrice.getName(), itemPrice);
	}

	public void serialize() throws StoreCacheSerializationUnsuccessfulException {
		this.mLogger.logInfo("Serializing StoreCache");

		try (final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(buildSerializationPath(this.mWorld)))) {
			oos.writeObject(this);
		} catch (final IOException e) {
			throw new StoreCacheSerializationUnsuccessfulException(e);
		}
	}

	public int size() {
		return this.mNameToPriceData.size();
	}

	private void refreshLogger() {
		this.mLogger = LoggerFactory.getLogger();
	}
}
