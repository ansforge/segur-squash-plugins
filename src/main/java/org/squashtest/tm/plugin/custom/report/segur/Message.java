package org.squashtest.tm.plugin.custom.report.segur;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

	Level level;
	
	String resId;
	
	String msg;
	
	public Message(Level level, String resId, String message) {
		this.level = level;
		this.resId = resId;
		this.msg = message;
	}

}
