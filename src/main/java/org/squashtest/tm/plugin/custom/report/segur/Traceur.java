package org.squashtest.tm.plugin.custom.report.segur;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class Traceur {

	@Getter
	private List<Message> msg = new ArrayList<Message>();
	
	private int COUNTER_MSG = 0;
	
	@Getter
	private static final int MAX_MSG = 30;
	
	public synchronized void addMessage(Level level, String message) {
		if (COUNTER_MSG >= MAX_MSG) {return; }
		COUNTER_MSG ++;
		msg.add(new Message(level,message ));
	}
	
	public void reset() {
		COUNTER_MSG = 0;
		msg.clear();
		
	}
}
