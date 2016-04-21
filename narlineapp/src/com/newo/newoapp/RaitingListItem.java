package com.newo.newoapp;

import android.graphics.Bitmap;

public class RaitingListItem {
	private String title;
	private int icon;
	private String count = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;

	private Bitmap iconBmp = null;

	public RaitingListItem() {
	}

	public RaitingListItem(String title, int icon, String count) {
		this.title = title;
		this.icon = icon;
		this.count = count;
	}

	public RaitingListItem(String title, Bitmap iconBmp, String count) {
		this.title = title;
		this.iconBmp = iconBmp;
		this.count = count;
	}

	public String getTitle() {
		return this.title;
	}

	public int getIcon() {
		return this.icon;
	}

	public String getCount() {
		return this.count;
	}

	public Bitmap getIconBmp() {
		return this.iconBmp;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public void setIconBmp(Bitmap iconBmp) {
		this.iconBmp = iconBmp;
	}

}
