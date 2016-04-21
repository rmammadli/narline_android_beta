package com.newo.newoapp.activities;

import java.util.ArrayList;

import com.newo.newoapp.narline.R;
import com.newo.newoapp.SettingsListAdapter;
import com.newo.newoapp.SettingsListItem;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class AboutCompetitonActivity extends Activity {

	private ArrayList<SettingsListItem> settingsItems;
	private SettingsListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_about_competiton);

		// Change actionbar
		// background-------------------------------------------------------
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bitmap_blue_trans));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Companents
		ListView list = (ListView) findViewById(R.id.listAboutCompetition);
		settingsItems = new ArrayList<SettingsListItem>();
		settingsItems.add(new SettingsListItem("Müsabiqə şərtləri"));
		settingsItems.add(new SettingsListItem("FAQ"));

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

	private void doListAction(int position) {
		// update the main content by replacing fragments
		switch (position) {

		case 0:
			Intent intent1 = new Intent(AboutCompetitonActivity.this,
					WebViewActivity.class);
			intent1.setData(Uri
					.parse("https://www.narmobile.az/narline/narline_competition.html"));
			startActivity(intent1);
			break;

		case 1:

			Intent intent3 = new Intent(AboutCompetitonActivity.this,
					WebViewActivity.class);
			intent3.setData(Uri
					.parse("https://www.narmobile.az/narline/narline_faq.html"));
			startActivity(intent3);
			break;

		default:
			break;
		}

	}

}
