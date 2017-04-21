package de.zabuza.beedlebot.service.routine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.chat.IChat;
import de.zabuza.sparkle.freewar.frames.EFrame;
import de.zabuza.sparkle.freewar.frames.IFrameManager;
import de.zabuza.sparkle.wait.EventQueueEmptyWait;
import de.zabuza.sparkle.wait.LinkTextPresenceWait;

public final class CentralTradersDepotNavigator {
	private static final String AMULET_CATEGORY_ANCHOR = "Kategorie: Halsschmuck";
	private static final String ATTACK_WEAPON_CATEGORY_ANCHOR = "Kategorie: Angriffswaffen";
	private static final String CONTINUE_ANCHOR = "Weiter";
	private static final String CSS_PURCHASE_ANCHOR_SELECTOR_PRE = "a[href=\"";
	private static final String CSS_PURCHASE_ANCHOR_SELECTOR_SUC = "\"]";
	private static final String DEFENSE_WEAPON_CATEGORY_ANCHOR = "Kategorie: Verteidigungswaffen";
	private static final String EXIT_MENU_ANCHOR = "Zur";
	private static final String MISCELLANEOUS_CATEGORY_ANCHOR = "Kategorie: Sonstiges";
	private static final String SPELL_CATEGORY_ANCHOR = "Kategorie: Anwendbare Items und Zauber";

	private final IChat mChat;
	private final WebDriver mDriver;
	private final IFrameManager mFrameManager;
	private final IFreewarInstance mInstance;
	private final Map<EItemCategory, String> mItemCategoryToAnchorNeedle;
	private final ILogger mLogger;

	public CentralTradersDepotNavigator(final IFreewarInstance instance, final WebDriver driver) {
		this.mInstance = instance;
		this.mChat = instance.getChat();
		this.mFrameManager = instance.getFrameManager();
		this.mDriver = driver;
		this.mLogger = LoggerFactory.getLogger();

		this.mItemCategoryToAnchorNeedle = new HashMap<>();
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.ATTACK_WEAPON, ATTACK_WEAPON_CATEGORY_ANCHOR);
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.DEFENSE_WEAPON, DEFENSE_WEAPON_CATEGORY_ANCHOR);
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.AMULET, AMULET_CATEGORY_ANCHOR);
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.SPELL, SPELL_CATEGORY_ANCHOR);
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.MISCELLANEOUS, MISCELLANEOUS_CATEGORY_ANCHOR);
	}

	public boolean exitMenu() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Exiting central traders depot menu");
		}

		final boolean wasClicked = this.mInstance.clickAnchorByContent(EFrame.MAIN, EXIT_MENU_ANCHOR);

		// Wait for click to get executed
		new EventQueueEmptyWait(this.mDriver).waitUntilCondition();

		// Focus chat input in case user was typing before
		this.mChat.focusChatInput();

		return wasClicked;
	}

	public boolean exitPurchasedDialog() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Exiting central traders depot purchased dialog");
		}

		// Wait for dialog to appear
		this.mFrameManager.switchToFrame(EFrame.MAIN);
		new LinkTextPresenceWait(this.mDriver, CONTINUE_ANCHOR).waitUntilCondition();
		final boolean wasClicked = this.mInstance.clickAnchorByContent(EFrame.MAIN, CONTINUE_ANCHOR);

		// Wait for click to get executed
		new EventQueueEmptyWait(this.mDriver).waitUntilCondition();

		// Focus chat input in case user was typing before
		this.mChat.focusChatInput();

		return wasClicked;
	}

	public boolean openItemCategory(final EItemCategory category) {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Opening central traders depot item category: " + category);
		}

		final String needle = this.mItemCategoryToAnchorNeedle.get(category);
		final boolean wasClicked = this.mInstance.clickAnchorByContent(EFrame.MAIN, needle);

		// Wait for click to get executed
		new EventQueueEmptyWait(this.mDriver).waitUntilCondition();

		// Focus chat input in case user was typing before
		this.mChat.focusChatInput();

		return wasClicked;
	}

	public boolean purchaseItem(final String purchaseAnchor) {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Purchasing central traders depot item: " + purchaseAnchor);
		}

		this.mFrameManager.switchToFrame(EFrame.MAIN);
		final String selector = CSS_PURCHASE_ANCHOR_SELECTOR_PRE + purchaseAnchor + CSS_PURCHASE_ANCHOR_SELECTOR_SUC;
		final List<WebElement> elements = this.mDriver.findElements(By.cssSelector(selector));
		if (elements.isEmpty()) {
			return false;
		}

		// Click the anchor
		elements.iterator().next().click();

		// Wait for click to get executed
		new EventQueueEmptyWait(this.mDriver).waitUntilCondition();

		// Focus chat input in case user was typing before
		this.mChat.focusChatInput();

		return true;
	}
}