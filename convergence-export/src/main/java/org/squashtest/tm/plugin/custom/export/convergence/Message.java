/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence;

import lombok.Getter;
import lombok.Setter;


/**
 * The Class Message.
 */
@Getter
@Setter
public class Message {

	Level level;

	String resId;

	String msg;

	/**
	 * Instantiates a new message.
	 *
	 * @param level the level
	 * @param resId the res id
	 * @param message the message
	 */
	public Message(Level level, String resId, String message) {
		this.level = level;
		this.resId = resId;
		this.msg = message;
	}

}
