package com.newo.newoapp.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.newo.newoapp.NotificationListAdapter;
import com.newo.newoapp.NotificationListItem;
import com.newo.newoapp.narline.R;
import com.newo.newoapp.db.DatabaseHandler;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.entities.GCMInfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class NotificationActivity extends BaseActivity implements
		OnRefreshListener {

	private ListView listNotifications;
	private NotificationListAdapter listAdapter;
	private ArrayList<NotificationListItem> notificationListItems;

	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_notification);

		// Change actionbar
		// background-------------------------------------------------------------
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bitmap_blue_trans));
		getActionBar().setLogo(R.drawable.logo_narmobile);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// You will setup the action bar with pull to refresh
		// layout--------------------------------------------------------------------------------------------------
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pullFillUserInfo);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		// Get
		// Companents----------------------------------------------------------------------------
		listNotifications = (ListView) findViewById(R.id.list_Notifications);

		// Create List Items as
		// Arraylist-------------------------------------------------------------------------
		notificationListItems = new ArrayList<NotificationListItem>();

		List<GCMInfo> gcmInfo = new DatabaseHandler(getApplicationContext())
				.getAllGCMInfo();

		for (GCMInfo gcm : gcmInfo) {

			long diff = (new Date()).getTime() - gcm.getMessageDate();

			long diffSeconds = diff / 1000;
			long diffMinutes = diff / (60 * 1000);
			long diffHours = diff / (60 * 60 * 1000);
			long diffDays = diff / (24 * 60 * 60 * 1000);

			String diffStr = null;
			if (diffDays > 0)
				diffStr = new SimpleDateFormat("dd-MM-yyyy").format(new Date(
						(new Date()).getTime() - diff));// diffDays
														// +
														// " days ago";
			else if (diffHours > 0)
				diffStr = diffHours + " hours ago";
			else if (diffMinutes > 0)
				diffStr = diffMinutes + " minutes ago";
			else if (diffSeconds > 0)
				diffStr = diffSeconds + " seconds ago";

			notificationListItems.add(new NotificationListItem(
					gcm.getMessage(), diffStr, gcm.getCompanyId()));

		}
		/*
		 * notificationListItems.add(new NotificationListItem(
		 * "Azercell has lunched new campagin with Barama", "5 mins ago",
		 * getResources().getIdentifier("image_test_azercell", "drawable",
		 * "com.newo.newoapp"))); notificationListItems.add(new
		 * NotificationListItem( "Enrique wants to follow you", "9 mins ago",
		 * getResources().getIdentifier("image_test_person1", "drawable",
		 * "com.newo.newoapp"))); notificationListItems.add(new
		 * NotificationListItem( "Frank faved your discovery", "17 mins ago",
		 * getResources().getIdentifier("image_test_person2", "drawable",
		 * "com.newo.newoapp"), true, getResources()
		 * .getIdentifier("image_test_sky", "drawable", "com.newo.newoapp")));
		 * notificationListItems.add(new NotificationListItem(
		 * "Facebook faved your discovery", "20 mins ago",
		 * getResources().getIdentifier("facebook_circle", "drawable",
		 * "com.newo.newoapp"), false, getResources()
		 * .getIdentifier("image_test_sky", "drawable", "com.newo.newoapp")));
		 */

		// Setup List
		// Adapter--------------------------------------------------------------------------------------------
		listAdapter = new NotificationListAdapter(getApplicationContext(),
				notificationListItems);

		// Set Adapter of
		// ListView-------------------------------------------------------------------------------
		listNotifications.setAdapter(listAdapter);

		// Set Notifications is
		// Viewed---------------------------------------------------------------------------------------------------
		savePrefs(Constants.PrefNames.NOTIFICATION,
				Constants.PrefKeys.NOTIFICATION_BUTTON_STATUS, "0");

	}

	// On
	// Pause------------------------------------------------------------------------------------------------------
	@Override
	protected void onPause() {
		super.onPause();
		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);
	}

	// Options Menu Click
	// Event-----------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// Preferneces
	// ------------------------------------------------------------------------------------------------------------------------------------
	private void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(5000); // 5 seconds

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();

			}
		}.execute();

	}

}
