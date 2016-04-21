/*==============================================================================
Copyright (c) 2013 NeWo LLC.
All Rights Reserved.

This  is NeWo application code.

==============================================================================*/

package com.newo.newoapp.helper;

import android.util.Log;

/**
 * DebugLog is a support class for the NeWo application.
 * 
 * */

public class DebugLog {
	private static final String LOGTAG = "NeWo";

	public static final void LOGE(String nMessage) {
		Log.e(LOGTAG, nMessage);
	}

	public static final void LOGW(String nMessage) {
		Log.w(LOGTAG, nMessage);
	}

	public static final void LOGD(String nMessage) {
		Log.d(LOGTAG, nMessage);
	}

	public static final void LOGI(String nMessage) {
		Log.i(LOGTAG, nMessage);
	}
}
