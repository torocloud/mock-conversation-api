package com.torocloud.mock.conversation

enum Code {

	NO_SUCH_PARTICIPANT(1000),
	PARTICIPANT_ALREADY_EXISTS(2000);
	
	private final int value;
	
	Code(int value){
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
