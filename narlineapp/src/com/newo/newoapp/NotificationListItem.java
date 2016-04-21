package com.newo.newoapp;

public class NotificationListItem {
	private String text;
	private String time;
	private int iconFirst;
	private int iconSecond;
	// boolean to set visiblity of the counter
	private boolean isIconSecondVisible = false;

	public NotificationListItem() {
	}

	public NotificationListItem(String text, String time, int iconFirst) {
		this.text = text;
		this.time = time;
		this.iconFirst = iconFirst;
	}

	public NotificationListItem(String text, String time, int iconFirst,
			boolean isIconSecondVisible, int iconSecond) {
		this.text = text;
		this.time = time;
		this.iconFirst = iconFirst;
		this.isIconSecondVisible = isIconSecondVisible;
		this.iconSecond = iconSecond;
	}

	public String getText() {
		return this.text;
	}

	public String getTime() {
		return this.time;
	}

	public int getIconFirst() {
		return this.iconFirst;
	}

	public int getIconSecond() {
		return this.iconSecond;
	}

	public boolean getIconSecondVisibility() {
		return this.isIconSecondVisible;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setIconFirst(int iconFirst) {
		this.iconFirst = iconFirst;
	}

	public void setIconSecond(int iconSecond) {
		this.iconSecond = iconSecond;
	}

	public void setIconSecondVisibility(boolean isIconSecondVisible) {
		this.isIconSecondVisible = isIconSecondVisible;
	}
}
