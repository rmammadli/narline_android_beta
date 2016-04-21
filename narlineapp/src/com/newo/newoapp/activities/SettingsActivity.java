package com.newo.newoapp.activities;

import java.io.File;
import java.util.ArrayList;

import com.newo.newoapp.narline.R;
import com.newo.newoapp.SettingsListAdapter;
import com.newo.newoapp.SettingsListItem;
import com.newo.newoapp.domain.Constants;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class SettingsActivity extends Activity {

	private ArrayList<SettingsListItem> settingsItems;
	private SettingsListAdapter adapter;

	private String mAbout;
	private String mBlog;
	private String mPrivacy;
	private String mTerms;
	private String mLog;
	private ImageView imgNeWo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_settings);

		// Change actionbar
		// background-------------------------------------------------------
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bitmap_blue_trans));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Strings---------------------------------------------------------------------------
		mAbout = getResources().getString(R.string.activity_settings_about);
		mPrivacy = getResources().getString(R.string.activity_settings_privacy);
		mTerms = getResources().getString(R.string.activity_settings_terms);
		imgNeWo = (ImageView) findViewById(R.id.ibtnNeWo);

		// Companents
		ListView list = (ListView) findViewById(R.id.listSettings);
		settingsItems = new ArrayList<SettingsListItem>();
		settingsItems.add(new SettingsListItem(mAbout));
		settingsItems.add(new SettingsListItem(mPrivacy));
		settingsItems.add(new SettingsListItem(mTerms));
		settingsItems.add(new SettingsListItem("Çıxış"));

		adapter = new SettingsListAdapter(getApplicationContext(),
				settingsItems);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				doListAction(position);

			}
		});

		imgNeWo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intentNeWo = new Intent(SettingsActivity.this,
						WebViewActivity.class);
				intentNeWo.setData(Uri.parse("http://newo.co/"));
				startActivity(intentNeWo);

			}
		});

	}

	private void doListAction(int position) {
		// update the main content by replacing fragments
		switch (position) {

		case 0:
			Intent intent1 = new Intent(SettingsActivity.this,
					AboutCompetitonActivity.class);
			startActivity(intent1);
			break;

		case 1:

			Intent intent3 = new Intent(SettingsActivity.this,
					WebViewActivity.class);
			intent3.setData(Uri
					.parse("https://www.narmobile.az/narline/narline_privacy.html "));
			intent3.putExtra("privacy", mPrivacy);
			startActivity(intent3);
			break;

		case 2:
			Intent intent4 = new Intent(SettingsActivity.this,
					WebViewActivity.class);
			intent4.setData(Uri
					.parse("https://www.narmobile.az/narline/narline_terms_of_use.html"));
			intent4.putExtra("terms", mTerms);
			startActivity(intent4);
			break;

		case 3:

			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setCancelable(true); // This blocks the 'BACK' button

			ad.setTitle("");
			// ad.setIcon(R.drawable.alerts_and_states_warning);
			ad.setMessage("NarLine profilinizdən çıxmağa əminsinizmi?");
			ad.setButton("Bəli", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.USER_ID, "");

					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.USER_NAME, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.PASSWORD, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.EMAIL_ADDRESS, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.PHONE_NUMBER, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.FIRST_NAME, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.LAST_NAME, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.GENDER, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.DATE_OF_BIRTH, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.USER_TOTAL_POINTS, "");
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.USER_CURRENT_POINTS, "");

					File folder = new File(Constants.fileNames.MAIN_FOLDER);
					try {
						if (!folder.exists())
							folder.mkdirs();
					} catch (Exception ex) {
						if (ex.getMessage() != null)
							Log.e("makeFolderError", ex.getMessage());
					}
					String imagePath = Constants.fileNames.MAIN_FOLDER
							+ Constants.fileNames.PROFILE_PHOTO;

					File file = new File(imagePath);
					boolean deleted = file.delete();

					dialog.dismiss();

					Intent mainWin = new Intent(getApplicationContext(),
							DescriptionViewPagerActivity.class);
					startActivity(mainWin);

					CardFlipActivity.cardFilip.finish();
					finish();
				}

			});

			ad.setButton2("Xeyr", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}

			});
			ad.show();

			Button bp = ad.getButton(DialogInterface.BUTTON_POSITIVE);
			Button bn = ad.getButton(DialogInterface.BUTTON_NEGATIVE);
			bp.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.selector_btn_simple_white));
			bn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.selector_btn_simple_white));

			break;

		default:
			break;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			onBackPressed();
		}
		return (super.onOptionsItemSelected(menuItem));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			onBackPressed();
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// finish();

	}

	private void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = this.getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

}
