package com.newo.newoapp.entities;

public class GCMInfo {

	int id;
	int companyId;
	String title;
	String message;
	long messageDate;
	int wasMessageSeen;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(long messageDate) {
		this.messageDate = messageDate;
	}

	public int getWasMessageSeen() {
		return wasMessageSeen;
	}

	public void setWasMessageSeen(int wasMessageSeen) {
		this.wasMessageSeen = wasMessageSeen;
	}

}
