package org.squashtest.tm.plugin.custom.report.segur;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

	Level level;
	
	String msg;
	
	public Message(Level level, String message) {
		this.level = level;
		this.msg = message;
	}

}
