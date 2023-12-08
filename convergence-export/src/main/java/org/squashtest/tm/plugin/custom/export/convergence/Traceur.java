/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;


/**
 * The Class Traceur.
 */
@Component("convergence.traceur")
public class Traceur {

	@Getter
	private List<Message> msg = new ArrayList<Message>();

	private int COUNTER_MSG = 0;

	@Getter
	//private static final int MAX_MSG = 50;
	private static final int MAX_MSG = 500;

	/**
	 * Adds the message.
	 *
	 * @param level the level
	 * @param resId the res id
	 * @param message the message
	 */
	public synchronized void addMessage(Level level, Long resId, String message) {
		if (COUNTER_MSG >= MAX_MSG) {
			return;
		}
		COUNTER_MSG++;
		msg.add(new Message(level, String.valueOf(resId), message));
	}

	/**
	 * Adds the message.
	 *
	 * @param level the level
	 * @param resId the res id
	 * @param message the message
	 */
	public synchronized void addMessage(Level level, String resId, String message) {
		if (COUNTER_MSG >= MAX_MSG) {
			return;
		}
		COUNTER_MSG++;
		msg.add(new Message(level, resId, message));
	}

	/**
	 * Reset.
	 */
	public void reset() {
		COUNTER_MSG = 0;
		msg.clear();

	}
}
