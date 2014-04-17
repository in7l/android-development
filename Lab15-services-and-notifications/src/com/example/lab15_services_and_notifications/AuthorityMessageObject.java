package com.example.lab15_services_and_notifications;

public class AuthorityMessageObject {
	private String message;
	private int imageResourceId;
	
	public AuthorityMessageObject(String message, int imageResourceId) {
		this.message = message;
		this.imageResourceId = imageResourceId;
	}

	public String getMessage() {
		return message;
	}

	public int getImageResourceId() {
		return imageResourceId;
	}
	
	
}
