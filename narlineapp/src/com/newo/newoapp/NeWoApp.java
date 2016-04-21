package com.newo.newoapp;

import android.app.Application;
import android.content.res.Resources;

public class NeWoApp extends Application {

	private static NeWoApp instance;

	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public static NeWoApp getInstance() {
		return instance;
	}
}
