/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.newo.newoapp.activities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.newo.newoapp.APImethods;
import com.newo.newoapp.ConnectionDetector;
import com.newo.newoapp.narline.R;
import com.newo.newoapp.RoundedImageView;
import com.newo.newoapp.SlideMenuAdapter;
import com.newo.newoapp.SlideMenuItem;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.fragments.FeedbackFragment;
import com.newo.newoapp.fragments.ProfileFragment;
import com.newo.newoapp.fragments.RaitingFragment;

import com.newo.newoapp.fragments.SocialConnectFragment;

import com.newo.newoapp.helper.DebugLog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CardFlipActivity extends BaseMainActivity {
	/**
	 * A handler object, used for deferring UI operations.
	 */
	/**
	 * Whether or not we're showing the back of the card (otherwise showing the
	 * front).
	 */

	private SocialConnectFragment socialConnectFragment = null;

	private ListView slideList;
	private TextView txtSlideUsername;
	private TextView txtSlideName;
	private RoundedImageView imgSlideProfile;

	private ArrayList<SlideMenuItem> slideMenuItems;
	private SlideMenuAdapter slideMenuAdapter;
	private TypedArray slideMenuIcons;
	private LinearLayout llUserInfo;
	private ToggleButton tbNotification;
	private TextView tvNotificationCounts;
	private String toogleNotificationStatus;
	private RelativeLayout rlNotificationSlideMenu;
	private ActionBar actionBar;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String username;
	private String userId;
	private String password;
	private String mPhoneNumber;
	private String mCarier;

	private PullToRefreshAttacher mPullToRefreshAttacher;

	public static Activity cardFilip;

	private boolean backCounter;
	private static String imagePath;

	String user_ID = null;
	String profileName = null;
	String facebookId = null;

	GoogleCloudMessaging gcm;
	String regid;
	String PROJECT_NUMBER = "422392305160";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_card_flip);
		cardFilip = this;

		// Actionbar----------------------------------------------------------------------
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_blue_bright));
		actionBar.setLogo(R.drawable.logo_narmobile);
		actionBar.setDisplayHomeAsUpEnabled(true);

		final Context context = this;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_slide_menu, null, true);

		// Slide Menu
		// ListView-------------------------------------------------------------------------
		slideList = (ListView) v.findViewById(R.id.list_slidermenu);

		// Slide Menu
		// Companents-------------------------------------------------------------------------
		llUserInfo = (LinearLayout) v.findViewById(R.id.llUserInfoSlideMenu);
		txtSlideUsername = (TextView) v.findViewById(R.id.tvUsernameSlideMenu);
		txtSlideName = (TextView) v.findViewById(R.id.tvNameSlideMenu);
		imgSlideProfile = (RoundedImageView) v
				.findViewById(R.id.rimgUserImageSlideMenu);
		rlNotificationSlideMenu = (RelativeLayout) v
				.findViewById(R.id.rlNotificationSlideMenu);
		tbNotification = (ToggleButton) v
				.findViewById(R.id.toogleNotificationSlideMenu);
		tvNotificationCounts = (TextView) v
				.findViewById(R.id.tvNotificationCountSlideMenu);

		// nav drawer icons from
		// resources-------------------------------------------------------------------------
		slideMenuIcons = getResources().obtainTypedArray(
				R.array.nav_drawer_icons);

		slideMenuItems = new ArrayList<SlideMenuItem>();
		slideMenuItems.add(new SlideMenuItem("Profil", slideMenuIcons
				.getResourceId(0, 1), true, "0"));
		slideMenuItems.add(new SlideMenuItem("Skan", slideMenuIcons
				.getResourceId(1, 1)));
		slideMenuItems.add(new SlideMenuItem("Top 10", slideMenuIcons
				.getResourceId(2, 1)));
		slideMenuItems.add(new SlideMenuItem("Sosial şəbəkələr", slideMenuIcons
				.getResourceId(3, 1), true, "1"));
		slideMenuItems.add(new SlideMenuItem("Rəy", slideMenuIcons
				.getResourceId(4, 1)));
		slideMenuItems.add(new SlideMenuItem("İzah", slideMenuIcons
				.getResourceId(5, 1)));
		slideMenuItems.add(new SlideMenuItem("Əlavə", slideMenuIcons
				.getResourceId(6, 1)));

		// Recycle the typed
		// array-----------------------------------------------------------------
		slideMenuIcons.recycle();
		slideMenuAdapter = new SlideMenuAdapter(getApplicationContext(),
				slideMenuItems);

		// Sliding
		// Menu--------------------------------------------------------------------------
		setBehindContentView(v);
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.drawer_shadow);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slide_menu_offset);
		getSlidingMenu().setFadeDegree(0.35f);
		setSlidingActionBarEnabled(true);

		// Set Adapter to Sliding menu
		// list-------------------------------------------------------------------------------------
		slideList.setAdapter(slideMenuAdapter);
		slideList.setItemChecked(1, true);
		displayView(0);

		// Pull to Refresh
		// Attacher------------------------------------------------------------------------------------
		mPullToRefreshAttacher = getPullToRefreshAttacher();

		// Get User info from Shared Preferences and set in Slide
		// menu------------------------------------------
		imagePath = Constants.fileNames.MAIN_FOLDER
				+ Constants.fileNames.PROFILE_PHOTO;
		File imgFile = new File(imagePath);
		if (imgFile.exists()) {

			imgSlideProfile
					.setImageDrawable(Drawable.createFromPath(imagePath));
			// imgProfile.setImageURI(Uri.fromFile(imgFile));

		}
		firstName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.FIRST_NAME);
		lastName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.LAST_NAME);

		if ((firstName + lastName).trim().length() != 0)
			txtSlideName.setText(firstName + " " + lastName);

		phoneNumber = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PHONE_NUMBER);

		username = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_NAME);

		userId = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_ID);
		password = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PASSWORD);

		if (username.trim().length() != 0)
			txtSlideUsername.setText("@" + username);

		// Get phone number and Network
		// -------------------------------------------------------------------------------

		if (facebookId == null)
			facebookId = "";

		// Slide menu
		// lsitener------------------------------------------------------------------------------------
		getSlidingMenu().setOnOpenListener(new OnOpenListener() {

			@Override
			public void onOpen() {

				// Get Current
				// Points------------------------------------------------------------------------------
				String currentPoints = loadPrefs(Constants.PrefNames.USER_INFO,
						Constants.PrefKeys.USER_CURRENT_POINTS).toString();
				if (currentPoints != null) {
					updateView(0, currentPoints);
				}
				onResume();
			}
		});

		getSlidingMenu().setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {

			}
		});

		// Slide menu List onClick
		// Listner------------------------------------------------------------------------------------------------
		slideList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				displayView(position);
			}
		});

		// Notification Button Click
		// Listner-------------------------------------------------------------------------------------------------
		rlNotificationSlideMenu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(CardFlipActivity.this,
						NotificationActivity.class));

			}
		});
		// Notification Toogle
		// Listner---------------------------------------------------------------------------------------------------
		tbNotification
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {

						} else {

						}

					}
				});

		// User Info Click
		// Listner--------------------------------------------------------------------------------
		llUserInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				displayView(0);

			}
		});

		// Send current user data to Web
		// App------------------------------------------------------------------------------------
		new Handler().postDelayed(new Runnable() {

			public void run() {

				ConnectionDetector cd = new ConnectionDetector(
						getApplicationContext());
				if (cd.isConnectingToInternet()) {
					getRegId();
				}
			}
		}, 15000);

	}

	public void getRegId() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(PROJECT_NUMBER);
					msg = regid;
					Log.i("GCM", msg);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				DebugLog.LOGD("RegId : " + msg);
				UserOperationTask userOperationTask = new UserOperationTask();
				userOperationTask.execute(userId, password, phoneNumber,
						mCarier, facebookId, msg);
			}
		}.execute(null, null, null);
	}

	class UserOperationTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;

			int timeOutLength = 5000;

			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(),
					timeOutLength); // Timeout
			HttpConnectionParams
					.setSoTimeout(client.getParams(), timeOutLength);
			HttpResponse response;
			JSONObject json = new JSONObject();

			try {

				String reqUrl = Constants.ConstStrings.serverPath
						+ "UserOperationServlet?action=userInitData";

				HttpPost post = new HttpPost(reqUrl);
				json.put("userId", params[0]);
				json.put("password", params[1]);
				json.put("phoneNumber", params[2]);
				json.put("mccMnc", params[3]);
				json.put("facebookId", params[4]);
				json.put("gcmRegId", params[5]);

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

					result = APImethods.convertStreamToString(inputstream);

					inputstream.close();

				}
			} catch (Exception e) {
				result = "0";
				e.printStackTrace();
				if (e.getMessage() != null)
					Log.w("json", e.getMessage());
				// onCreateDialog("Error", "Cannot Estabilish Connection");
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				JSONObject jObject = null;
				String jsonResult = "0";
				try {
					jObject = new JSONObject(result);
					jsonResult = jObject.getString("result");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int res = Integer.valueOf(jsonResult);

			}
		}
	}

	// On Pause
	// ----------------------------------------------------------------------------------------------
	@Override
	protected void onPause() {
		super.onPause();

		DebugLog.LOGD("CardFlipActivityLifeCycle - onPause");

		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	// On
	// Resume----------------------------------------------------------------------------------------------------
	public void onResume() {

		// load notification
		// status------------------------------------------------------------------------------------
		toogleNotificationStatus = loadPrefs(Constants.PrefNames.NOTIFICATION,
				Constants.PrefKeys.NOTIFICATION_BUTTON_STATUS);

		if (toogleNotificationStatus == null
				|| toogleNotificationStatus.trim().length() < 1)
			toogleNotificationStatus = "0";

		if (Integer.parseInt(toogleNotificationStatus) > 0) {

			tbNotification.setChecked(true);
			tvNotificationCounts.setVisibility(View.VISIBLE);
			tvNotificationCounts.setText(toogleNotificationStatus);

		} else {

			tbNotification.setChecked(false);
			tvNotificationCounts.setVisibility(View.GONE);
			tvNotificationCounts.setText("");
		}

		// Get User info from Shared Preferences and set in Slide menu
		final String imagePath = Constants.fileNames.MAIN_FOLDER
				+ Constants.fileNames.PROFILE_PHOTO;
		File imgFile = new File(imagePath);
		if (imgFile.exists()) {

			imgSlideProfile
					.setImageDrawable(Drawable.createFromPath(imagePath));
			// imgProfile.setImageURI(Uri.fromFile(imgFile));

		}
		String firstName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.FIRST_NAME);
		String lastName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.LAST_NAME);

		if ((firstName + lastName).trim().length() != 0)
			txtSlideName.setText(firstName + " " + lastName);

		String phoneNumber = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PHONE_NUMBER);

		String username = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_NAME);
		if (username.trim().length() != 0)
			txtSlideUsername.setText("@" + username);
		super.onResume();
	}

	// Update list
	// View-----------------------------------------------------------------------------------------------------------
	private void updateView(int index, String text) {
		View v = slideList.getChildAt(index
				- slideList.getFirstVisiblePosition());
		TextView counter = (TextView) v.findViewById(R.id.counter);
		
		if (v == null)
			counter.setVisibility(View.GONE);

		counter.setVisibility(View.VISIBLE);
		counter.setText("*");
	}

	// Options
	// Menu------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		return true;
	}

	// Options Menu Click
	// Event-----------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (loadPrefs(Constants.PrefNames.APP_STATE,
					Constants.PrefKeys.IS_SCANING).equals("1")) {

				// cardFrontFragment.onBackPressed();

			} else {
				getSlidingMenu().toggle();
			}
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	// Slide menu item click
	// listener------------------------------------------------------------------------------------------------
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	// Diplaying fragment view for selected nav drawer list
	// item---------------------------------------------------------------------
	public void displayView(int position) {
		// update the main content by replacing fragments
		int i = 0;
		Fragment fragment = new Fragment();
		switch (position) {
		case 0:
			fragment = ProfileFragment.newInstance(getApplicationContext());
			i++;
			backCounter = false;
			break;
		case 1:
			Intent scan = new Intent(CardFlipActivity.this, ScanActivity.class);
			startActivity(scan);
			backCounter = false;
			break;
		case 2:
			fragment = RaitingFragment.newInstance(getApplicationContext());
			i++;
			backCounter = false;
			break;
		case 3:
			fragment = socialConnectFragment
					.newInstance(getApplicationContext());
			i++;
			backCounter = false;
			break;
		case 4:
			fragment = FeedbackFragment.newInstance(getApplicationContext());
			i++;
			backCounter = false;
			break;
		case 5:
			Intent pta = new Intent(CardFlipActivity.this,
					ProductTourActivity.class);
			startActivity(pta);
			backCounter = false;
			break;
		case 6:
			Intent seta = new Intent(CardFlipActivity.this,
					SettingsActivity.class);
			startActivity(seta);
			backCounter = false;
			break;
		default:
			break;
		}

		if (i != 0) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction ft = fragmentManager
					.beginTransaction();

			ft.replace(R.id.container, fragment);
			ft.addToBackStack(null);
			ft.commit();

			// update selected item and title, then close the drawer
			slideList.setItemChecked(position, true);
			slideList.setSelection(position);

			getSlidingMenu().toggle();
		} else {
			// error in creating fragment
			slideList.setItemChecked(position, false);
			slideList.setSelection(position);
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

	// Back Button
	// --------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		if (loadPrefs(Constants.PrefNames.APP_STATE,
				Constants.PrefKeys.IS_SCANING).equals("1"))
			;
		// cardFrontFragment.onBackPressed();
		else if (!backCounter) {
			togle();
			backCounter = true;
		} else {

			finish();
		}

	}

	// Toggle the sliding
	// menu-------------------------------------------------------------------------------------------
	public void togle() {

		getSlidingMenu().toggle();
	}

	// Activity Result
	// ---------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (socialConnectFragment != null)
			socialConnectFragment.onActivityResult(requestCode, resultCode,
					data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

}
