package com.newo.newoapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class APImethods {

	public static String app_version = "beta_2.1.0";

	public static void Load() {
		try {
			System.loadLibrary("myjni");
		} catch (Exception ex) {
			if (ex.getMessage() != null)
				System.out.println(ex.getMessage());
		}
	}

	public static native String getMainHost();

	public static String getAppVersion() {
		return app_version;
	}

	public static int getVersionCode() {
		int versionCode = 0;

		PackageInfo pInfo = null;
		try {
			pInfo = NeWoApp.getInstance().getPackageManager()
					.getPackageInfo(NeWoApp.getInstance().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		versionCode = pInfo.versionCode;
		;
		return versionCode;
	}

	public static String getMainHost(Context ctx) {
		String mainHost = null;
		Load();
		mainHost = "http://192.168.1.106:8084/ManadsWebAppBeta";// getMainHost();//
																// ctx.getResources().getString(R.string.mainHost);
		return mainHost;
	}

	public static boolean checkCorrectPhoneNumber(String phoneNumber) {
		if (phoneNumber.trim().length() != 10)
			return false;
		try {
			long l = Long.parseLong(phoneNumber);
		} catch (Exception ex) {
			return false;
		}
		return true;

	}

	public static long makeLongDate(int day, int month, int year, int hour,
			int minute, int second) {
		Calendar c = new GregorianCalendar();
		c.set(Calendar.YEAR, year); // anything 0 - 23
		c.set(Calendar.MONTH, month); // anything 0 - 23
		c.set(Calendar.DAY_OF_MONTH, day); // anything 0 - 23
		c.set(Calendar.HOUR_OF_DAY, hour); // anything 0 - 23
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime().getTime();
	}

	public static long getSecond(long milliSecond) {
		return milliSecond / 1000;
	}

	public static long getMinute(long milliSecond) {
		long dt = 1000L * 60L;
		return milliSecond / dt;
	}

	public static long getHour(long milliSecond) {
		long dt = 1000L * 60L * 60L;
		return milliSecond / dt;
	}

	public static long getDay(long milliSecond) {
		long dt = 1000L * 60L * 60L * 24L;
		return milliSecond / dt;
	}

	public static long getYear(long milliSecond) {
		long dt = 1000L * 60L * 60L * 24L * 360L;
		return milliSecond / dt;
	}

	public static boolean isOnline(Context context) {
		boolean isOK = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				isOK = true;
			}
		}
		return isOK;
	}

	public static boolean hasActiveInternetConnection(Context context) {
		/*
		 * int timeOutLength; try { timeOutLength =
		 * Integer.valueOf(context.getResources()
		 * .getString(R.string.time_out_length)); } catch (Exception ex) {
		 * timeOutLength = 5000; if (ex.getMessage() != null) Log.w("json",
		 * ex.getMessage()); } if (isOnline(context)) { try { HttpURLConnection
		 * urlc = (HttpURLConnection) (new URL(
		 * "http://www.google.com").openConnection());
		 * urlc.setRequestProperty("User-Agent", "Test");
		 * urlc.setRequestProperty("Connection", "close");
		 * urlc.setConnectTimeout(timeOutLength); urlc.connect(); boolean
		 * isResponsed = (urlc.getResponseCode() == 200); return isResponsed; }
		 * catch (IOException e) { //Log.e("internet",
		 * "Error checking internet connection", e); if(e.getMessage()!=null)
		 * Log.d("internet",
		 * "Error checking internet connection"+e.getMessage()); } } else {
		 * Log.d("internet", "No network available!" ); } return false;
		 */
		return isOnline(context);
	}

	public static boolean isServiceActive(Context context) {
		/*
		 * int timeOutLength; try { timeOutLength =
		 * Integer.valueOf(context.getResources()
		 * .getString(R.string.time_out_length)); } catch (Exception ex) {
		 * timeOutLength = 5000; if (ex.getMessage() != null) Log.w("json",
		 * ex.getMessage()); } if (isOnline(context)) { try { String url =
		 * context
		 * .getResources().getString(R.string.mainHost)+"/ManadsWebApp/SendImages"
		 * ; HttpURLConnection urlc = (HttpURLConnection) (new URL(
		 * "http://www.google.com").openConnection());
		 * urlc.setRequestProperty("User-Agent", "Test");
		 * urlc.setRequestProperty("Connection", "close");
		 * urlc.setConnectTimeout(timeOutLength); urlc.connect(); return
		 * (urlc.getResponseCode() == 200); } catch (IOException e) {
		 * //Log.e("internet", "Error checking internet connection", e);
		 * 
		 * Log.d("internet", "Error checking internet connection!"); } } else {
		 * Log.d("internet", "No network available!"); } return false;
		 */
		return isOnline(context);
	}

	public static String convertStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (Exception e) {
			// Toast.makeText(this, "Stream Exception",
			// Toast.LENGTH_SHORT).show();
		}
		return total.toString();
	}

	public static void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = NeWoApp.getInstance().getSharedPreferences(
				prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public static String loadPrefs(String prefName, String key) {
		SharedPreferences sp = NeWoApp.getInstance().getSharedPreferences(
				prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

}
