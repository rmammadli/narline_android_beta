package com.newo.newoapp.activities;

import java.util.List;

import com.newo.newoapp.narline.R;

import android.R.string;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ILoveNeWoActivity extends Activity {

	private TextView tvILoveHeader1;
	private TextView tvILoveHeader2;
	private TextView tvILoveGooglePlay;
	private TextView tvILoveFacebook;
	private TextView tvILoveTwitter;
	private RelativeLayout rlbtnGooglePlay;
	private RelativeLayout rlbtnFacebook;
	private RelativeLayout rlbtnTwitter;

	private string messageTwitter;
	private string messageFacebook;

	private LinearLayout llILove;

	private ActionBar actionbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_feedback_i_love_newo);

		// change actionbar background
		actionbar = getActionBar();
		actionbar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bitmap_blue_trans));
		actionbar.setLogo(R.drawable.logo_narmobile);
		actionbar.setDisplayHomeAsUpEnabled(true);

		// Initalize compananets on layout
		// TextViews
		tvILoveHeader1 = (TextView) findViewById(R.id.tvILove1);
		tvILoveHeader2 = (TextView) findViewById(R.id.tvILove2);
		tvILoveGooglePlay = (TextView) findViewById(R.id.tvILoveGooglePlay);
		tvILoveFacebook = (TextView) findViewById(R.id.tvILoveFacebook);
		tvILoveTwitter = (TextView) findViewById(R.id.tvILoveTwitter);

		// Layouts
		llILove = (LinearLayout) findViewById(R.id.llILove);
		rlbtnGooglePlay = (RelativeLayout) findViewById(R.id.rlILoveGooglePlay);
		rlbtnFacebook = (RelativeLayout) findViewById(R.id.rlILoveFacebook);
		rlbtnTwitter = (RelativeLayout) findViewById(R.id.rlILoveTwitter);

		// Google Play Button onClick
		rlbtnGooglePlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String appPackageName = getPackageName(); // getPackageName()
																// from Context
																// or Activity
																// object
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ appPackageName)));
				}

			}
		});

		// Facebook Button onClick
		rlbtnFacebook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String urlToShare = "http://narlineapp.com/ ";
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(
						Intent.EXTRA_TEXT,
						"NarLine tətbiqi ilə videolar izləyin, xallar toplayın və Nar Mobile-dan dəyərli hədiyyələr qazanın. #nar #narline"); // NB:
																																				// has
				// no effect!
				intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

				// See if official Facebook app is found
				boolean facebookAppFound = false;
				List<ResolveInfo> matches = getPackageManager()
						.queryIntentActivities(intent, 0);
				for (ResolveInfo info : matches) {
					if (info.activityInfo.packageName.toLowerCase().startsWith(
							"com.facebook")) {
						intent.setPackage(info.activityInfo.packageName);
						facebookAppFound = true;
						break;
					}
				}

				// As fallback, launch sharer.php in a browser
				if (!facebookAppFound) {
					String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u="
							+ urlToShare;
					intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(sharerUrl));
				}

				startActivity(intent);

			}
		});

		// Twitter Button onClick
		rlbtnTwitter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				String urlToShare = "NarLine tətbiqi ilə videolar izləyin, xallar toplayın və Nar Mobile-dan dəyərli hədiyyələr qazanın. #nar #narline ( http://narlineapp.com/ )";
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				// intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has
				// no effect!
				intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

				// See if official Facebook app is found
				boolean facebookAppFound = false;
				List<ResolveInfo> matches = getPackageManager()
						.queryIntentActivities(intent, 0);
				for (ResolveInfo info : matches) {
					if (info.activityInfo.packageName.toLowerCase().startsWith(
							"com.twitter")) {
						intent.setPackage(info.activityInfo.packageName);
						facebookAppFound = true;
						break;
					}
				}

				// As fallback, launch sharer.php in a browser
				if (!facebookAppFound) {
					String sharerUrl = "https://twitter.com/intent/tweet?source=webclient&text=TWEET+THIS!"
							+ urlToShare;
					intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(sharerUrl));
				}

				startActivity(intent);

			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();
		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public Intent findTwitterClient() {
		final String[] twitterApps = {
				// package // name - nb installs (thousands)
				"com.twitter.android", // official - 10 000
				"com.twidroid", // twidroyd - 5 000
				"com.handmark.tweetcaster", // Tweecaster - 5 000
				"com.thedeck.android" };// TweetDeck - 5 000 };
		Intent tweetIntent = new Intent();
		tweetIntent.setType("text/plain");
		final PackageManager packageManager = getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(
				tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

		for (int i = 0; i < twitterApps.length; i++) {
			for (ResolveInfo resolveInfo : list) {
				String p = resolveInfo.activityInfo.packageName;
				if (p != null && p.startsWith(twitterApps[i])) {
					tweetIntent.setPackage(p);
					return tweetIntent;
				}
			}
		}
		return null;
	}

	private void shareTwitter() {

		// try {
		// //Uri uri =
		// Uri.parse("android.resource://com.gobaby.app/drawable/back");
		// Intent intent = new Intent(Intent.ACTION_SEND);
		// intent.setType("/*");
		// intent.setClassName("com.twitter.android",
		// "com.twitter.android.PostActivity");
		// intent.putExtra(Intent.EXTRA_TEXT, "Thiws is a share message");
		// startActivity(intent);
		//
		// } catch (final ActivityNotFoundException e) {
		// Toast.makeText(ILoveNeWoActivity.this,
		// "You don't seem to have twitter installed on this device",
		// Toast.LENGTH_SHORT).show();
		// }

	}

}
