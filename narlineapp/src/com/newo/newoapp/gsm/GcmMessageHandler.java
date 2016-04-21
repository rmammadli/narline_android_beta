package com.newo.newoapp.gsm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.newo.newoapp.narline.R;
import com.newo.newoapp.activities.NotificationActivity;
import com.newo.newoapp.db.DatabaseHandler;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.entities.GCMInfo;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class GcmMessageHandler extends IntentService {

	String companyId;
	String title;
	String message;

	private Handler handler;

	private String notificationCount;
	private int notificationCountInt;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		companyId = extras.getString("companyId");
		title = extras.getString("title");
		message = extras.getString("message");
		showToast();

		// Load Notifications
		// Count---------------------------------------------------------------------------------------------------
		notificationCount = loadPrefs(Constants.PrefNames.NOTIFICATION,
				Constants.PrefKeys.NOTIFICATION_BUTTON_STATUS);

		if (notificationCount == null || notificationCount.trim().length() < 1)
			notificationCount = "1";

		notificationCountInt = Integer.parseInt(notificationCount) + 1;
		// Set Notifications
		// Count---------------------------------------------------------------------------------------------------
		savePrefs(Constants.PrefNames.NOTIFICATION,
				Constants.PrefKeys.NOTIFICATION_BUTTON_STATUS,
				Integer.toString(notificationCountInt));

		GcmBroadcastReceiver.completeWakefulIntent(intent);
		final String imagePath = Constants.fileNames.MAIN_FOLDER + "company_"
				+ companyId + ".jpg";
		File imgFile = new File(imagePath);
		if (!imgFile.exists()) {
			DownloadTask task = new DownloadTask();
			task.execute("avalizada", "Atilla1991", companyId);
		}

	}

	public void showToast() {
		handler.post(new Runnable() {
			public void run() {

				DatabaseHandler db = new DatabaseHandler(
						getApplicationContext());

				GCMInfo gcmInfo = new GCMInfo();
				gcmInfo.setCompanyId(Integer.parseInt(companyId));
				gcmInfo.setTitle(title);
				gcmInfo.setMessage(message);
				Date curDate = new Date();
				gcmInfo.setMessageDate(curDate.getTime());
				gcmInfo.setWasMessageSeen(1);
				db.addGCMInfo(gcmInfo);
				// Toast.makeText(getApplicationContext(),
				// companyId + "\n" + title + "\n" + message,
				// Toast.LENGTH_LONG).show();

				createNotification(title, message);
			}
		});

	}

	public void createNotification(String title, String message) {
		// Prepare intent which is triggered if the
		// notification is selected
		Intent intent = new Intent(getApplicationContext(),
				NotificationActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// Build notification
		// Actions are just fake
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		// Define sound URI
		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		// Build notification
		// Actions are just fake
		Notification noti = builder.setContentTitle(title)
				.setContentText(message)
				.setSmallIcon(R.drawable.logo_narmobile)
				.setContentIntent(pIntent).setSound(soundUri).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		Random rand = new Random();
		notificationManager.notify(rand.nextInt(1000000000), noti);

	}

	class DownloadTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String result = null;
			Bitmap bitmap = null;
			int timeOutLength = 50000;

			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(),
					timeOutLength); // Timeout
			HttpConnectionParams
					.setSoTimeout(client.getParams(), timeOutLength);
			HttpResponse response;
			JSONObject json = new JSONObject();

			try {

				String reqUrl = Constants.ConstStrings.serverPath
						+ "UserCompanyListServlet?action=getCompanyImage&companyId="
						+ params[2];

				HttpPost post = new HttpPost(reqUrl);
				json.put("userName", params[0]);
				json.put("password", params[1]);
				json.put("companyId", params[2]);

				StringEntity se = new StringEntity(json.toString());
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				post.setEntity(se);
				response = client.execute(post);

				/* Checking response */
				if (response != null) {
					// InputStream in = response.getEntity().getContent();
					// //Get
					// the data in the entity
					InputStream inputstream = response.getEntity().getContent();
					Header contentencoding = response
							.getFirstHeader("Content-Encoding");
					if (contentencoding != null
							&& contentencoding.getValue().equalsIgnoreCase(
									"gzip")) {
						inputstream = new GZIPInputStream(inputstream);
					}

					BitmapFactory.Options bmOptions = new BitmapFactory.Options();
					bmOptions.inSampleSize = 1;
					bitmap = BitmapFactory.decodeStream(inputstream, null,
							bmOptions);

					inputstream.close();

					// recvdref.setText(resultstring + "\n\n" +
					// post.toString().getBytes());
					// JSONObject recvdjson = new JSONObject(resultstring);
					// recvdref.setText(recvdjson.toString(2));

				}
			} catch (Exception e) {
				result = "0";
				e.printStackTrace();
				if (e.getMessage() != null)
					Log.w("json", e.getMessage());
				// onCreateDialog("Error", "Cannot Estabilish Connection");
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {

				try {
					FileOutputStream out = new FileOutputStream(
							Constants.fileNames.MAIN_FOLDER + "company_"
									+ companyId + ".jpg");
					result.compress(Bitmap.CompressFormat.JPEG, 90, out);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
	}

	// Preferneces
	// ------------------------------------------------------------------------------------------------------------------------------------
	private void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	// Load pref
	// ---------------------------------------------------------------------------------------------------------------
	private String loadPrefs(String prefName, String key) {
		SharedPreferences sp = getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

}
