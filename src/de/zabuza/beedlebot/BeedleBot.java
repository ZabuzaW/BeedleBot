package de.zabuza.beedlebot;

import java.awt.AWTException;
import java.io.IOException;

import de.zabuza.beedlebot.logindialog.LoginDialog;
import de.zabuza.beedlebot.logindialog.controller.settings.IBrowserSettingsProvider;
import de.zabuza.beedlebot.logindialog.controller.settings.IUserSettingsProvider;
import de.zabuza.beedlebot.tray.TrayManager;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class BeedleBot {
	private TrayManager mTrayManager;
	private LoginDialog mLoginDialog;

	public BeedleBot() {
		mTrayManager = null;
		mLoginDialog = null;
	}

	public void initialize() throws IOException, AWTException {
		// TODO Correct error handling, don't use throw
		mTrayManager = new TrayManager(this);
		mTrayManager.addTrayIcon();
	}

	public void start() {
		// TODO Remove debug
		System.out.println("An");

		mLoginDialog = new LoginDialog(this);
	}

	public void startService(final IUserSettingsProvider userSettingsProvider,
			final IBrowserSettingsProvider browserSettingsProvider) {
		// TODO Remove debug
		System.out.println("Starting Service");

		// TODO Implement
	}

	public void stopService() {
		// TODO Pass stop signal to service
	}

	public void stop() {
		// TODO Remove debug
		System.out.println("Aus");

		if (mLoginDialog != null && mLoginDialog.isActive()) {
			mLoginDialog.dispose();
			mLoginDialog = null;
		}

		// TODO Pass stop signal to service
	}

	public void shutdown() {
		stop();
		try {
			// TODO Correct error handling
			mTrayManager.removeTrayIcon();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Shutdown");
	}

	/**
	 * 
	 * @param args
	 *            Not supported
	 * @throws IOException
	 * @throws AWTException
	 */
	public static void main(final String[] args) throws IOException, AWTException {
		// TODO Correct error handling, don't use throw
		final BeedleBot beedleBot = new BeedleBot();
		beedleBot.initialize();
		beedleBot.start();
	}

}
