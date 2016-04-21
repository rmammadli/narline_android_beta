package com.newo.newoapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class SimpleTextListItem {
	public CharSequence title;

	public SimpleTextListItem() {
	}

	public SimpleTextListItem(CharSequence title) {
		this.title = title;
	}

	public CharSequence getTitle() {
		return this.title;
	}

	public void setTitle(CharSequence title) {
		this.title = title;
	}

}