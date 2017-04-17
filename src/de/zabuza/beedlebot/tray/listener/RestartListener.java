package de.zabuza.beedlebot.tray.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class RestartListener implements ActionListener {
	private final BeedleBot mBeedleBot;
	private final ILogger mLogger;

	public RestartListener(BeedleBot beedleBot) {
		mBeedleBot = beedleBot;
		mLogger = LoggerFactory.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		mLogger.logInfo("Executing restart action");
		mBeedleBot.stop();
		mBeedleBot.start();
	}

}
