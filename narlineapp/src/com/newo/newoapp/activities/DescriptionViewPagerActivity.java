package com.newo.newoapp.activities;

import java.util.Calendar;

import com.newo.newoapp.narline.R;
import com.newo.newoapp.ViewPagerAdapter;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.helper.ViewPagerParallax;
import com.viewpagerindicator.CirclePageIndicator;

import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DescriptionViewPagerActivity extends FragmentActivity {
	private ViewPagerParallax _mViewPager;
	private ViewPagerAdapter _adapter;
	private CirclePageIndicator mIndicator;
	private LinearLayout btnSignIn;
	private LinearLayout btnSignUp;
	private LinearLayout btnFacebook;
	private LinearLayout llButtons;
	private LinearLayout llFooter;
	private TextView txtBtnFacebook;
	private TextView txtBtnSignUp;
	private TextView txtBtnSignIn;
	private View vTop;
	private View vCenter;
	private View vMiddle;

	private int i = 0;
	private int backpress = 0;
	private int mInterval = 5000;
	private Handler mHandler;

	private Calendar today;
	private int hour;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_description);

		if (loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_NAME).trim().length() != 0) {

			Intent mainWin = new Intent(this, CardFlipActivity.class);
			startActivity(mainWin);
			finish();

		}

		// ViewPager Functions
		// _mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		_mViewPager = (ViewPagerParallax) findViewById(R.id.viewPager);
		_mViewPager.set_max_pages(7);
		_mViewPager.setBackgroundAsset(R.raw.baku_day_1280);
		_adapter = new ViewPagerAdapter(getApplicationContext(),
				getSupportFragmentManager());
		_mViewPager.setAdapter(_adapter);
		mHandler = new Handler();

		// get Screen
		// Resolution----------------------------------------------------------
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// Get Device time as hour and change slider background depends on time
		// of day
		today = Calendar.getInstance();
		hour = today.get(Calendar.HOUR_OF_DAY);
		if (hour > 6 && hour < 19) {
			_mViewPager.setBackgroundAsset(R.raw.baku_day_640);
			// if (metrics.heightPixels >= 1280) {
			// _mViewPager.setBackgroundAsset(R.raw.baku_day_1280);
			// } else if (metrics.heightPixels >= 800) {
			//
			// } else {
			// _mViewPager.setBackgroundAsset(R.raw.baku_day_640);
			// }
		} else {

			_mViewPager.setBackgroundAsset(R.raw.baku_night_640);
			// if (metrics.heightPixels >= 1280) {
			// _mViewPager.setBackgroundAsset(R.raw.baku_night_1280);
			// } else if (metrics.heightPixels >= 800) {
			//
			// } else {
			// _mViewPager.setBackgroundAsset(R.raw.baku_night_640);
			// }
		}
		// if (hour > 6 && hour < 19) {
		// if (metrics.heightPixels >= 1280) {
		// _mViewPager.setBackgroundAsset(R.raw.baku_day_1280);
		// } else if (metrics.heightPixels >= 800) {
		// _mViewPager.setBackgroundAsset(R.raw.baku_day_800);
		// } else {
		// _mViewPager.setBackgroundAsset(R.raw.baku_day_640);
		// }
		// } else {
		//
		// if (metrics.heightPixels >= 1280) {
		// _mViewPager.setBackgroundAsset(R.raw.baku_night_1280);
		// } else if (metrics.heightPixels >= 800) {
		// _mViewPager.setBackgroundAsset(R.raw.baku_night_800);
		// } else {
		// _mViewPager.setBackgroundAsset(R.raw.baku_night_640);
		// }
		// }

		// Companents in layout
		btnSignIn = (LinearLayout) findViewById(R.id.btnLogin);
		btnSignUp = (LinearLayout) findViewById(R.id.btnRegister);
		btnFacebook = (LinearLayout) findViewById(R.id.btnFacebook);
		llButtons = (LinearLayout) findViewById(R.id.llButtons);
		llFooter = (LinearLayout) findViewById(R.id.llDescriptionFooter);
		txtBtnFacebook = (TextView) findViewById(R.id.tvBtnFacebook);
		txtBtnSignUp = (TextView) findViewById(R.id.tvBtnRegister);
		txtBtnSignIn = (TextView) findViewById(R.id.tvBtnLogin);
		vTop = (View) findViewById(R.id.view1);
		vCenter = (View) findViewById(R.id.view2);
		vMiddle = (View) findViewById(R.id.view3V);

		// Other companenets
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		final Animation slideDown = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.slide_down_l);
		final Animation slideUp = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.slide_up_l);

		// Indicator setup
		mIndicator.setViewPager(_mViewPager);

		new Handler().postDelayed(new Runnable() {

			public void run() {
				llFooter.startAnimation(slideUp);
				llFooter.setVisibility(View.VISIBLE);
			}
		}, 2400);

		mIndicator
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						// TODO Auto-generated method stub

						if (position == 0) {
							btnSignIn.setVisibility(View.VISIBLE);
							btnSignUp.setVisibility(View.VISIBLE);

						} else if (position == 1) {
							btnSignIn.setVisibility(View.VISIBLE);
							btnSignUp.setVisibility(View.VISIBLE);

						} else if (position == 2) {
							btnSignIn.setVisibility(View.VISIBLE);
							btnSignUp.setVisibility(View.VISIBLE);

						} else if (position == 3) {

							btnSignIn.setVisibility(View.VISIBLE);
							btnSignUp.setVisibility(View.VISIBLE);
							// btnFacebook.setVisibility(View.GONE);
						} else if (position == 4) {

							btnSignIn.setVisibility(View.VISIBLE);
							btnSignUp.setVisibility(View.VISIBLE);
							// btnFacebook.setVisibility(View.GONE);
							vCenter.setVisibility(View.GONE);
							vMiddle.setVisibility(View.VISIBLE);
							// // llButtons.startAnimation(slideUp);
							// llButtons.setVisibility(View.VISIBLE);

						} else if (position == 5) {

							btnSignIn.setVisibility(View.VISIBLE);
							btnSignUp.setVisibility(View.GONE);
							// btnFacebook.setVisibility(View.VISIBLE);
							vCenter.setVisibility(View.VISIBLE);
							vMiddle.setVisibility(View.GONE);

							// stop changing fragments automatically
							stopRepeatingTask();
							// // llButtons.startAnimation(slideDown);
							// llButtons.setVisibility(View.VISIBLE);
						} else if (position == 6) {

							btnSignIn.setVisibility(View.GONE);
							btnSignUp.setVisibility(View.VISIBLE);
							// btnFacebook.setVisibility(View.VISIBLE);
							vCenter.setVisibility(View.VISIBLE);
							vMiddle.setVisibility(View.GONE);
							stopRepeatingTask();
							// // llButtons.startAnimation(slideDown);
							// llButtons.setVisibility(View.VISIBLE);
						}

					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub

					}
				});

		// start changing fragments automatically
		startRepeatingTask();

		// SignUp button
		btnSignUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				_mViewPager.setCurrentItem(5);
			}
		});

		// SignIn button
		btnSignIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				_mViewPager.setCurrentItem(6);
			}
		});

		savePrefs(Constants.PrefNames.APP_STATE,
				Constants.PrefKeys.IS_FIRST_TIME_OPEN, true);

		savePrefs(Constants.PrefNames.APP_STATE,
				Constants.PrefKeys.IS_FIRST_TIME_OPEN, true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);
	}

	Runnable mStatusChecker = new Runnable() {
		@Override
		public void run() {
			updateStatus(); // this function can change value of mInterval.
			mHandler.postDelayed(mStatusChecker, mInterval);
		}

		private void updateStatus() {
			// TODO Auto-generated method stub
			_mViewPager.setCurrentItem(i);
			i++;
		}
	};

	void startRepeatingTask() {
		mStatusChecker.run();
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(mStatusChecker);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("num_pages", 7);
		final ViewPagerParallax pager = (ViewPagerParallax) findViewById(R.id.viewPager);
		outState.putInt("current_page", pager.getCurrentItem());
	}

	private void savePrefs(String prefName, String key, boolean value) {
		SharedPreferences sp = getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	private String loadPrefs(String prefName, String key) {
		SharedPreferences sp = getSharedPreferences(prefName, MODE_PRIVATE);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			int item;
			item = _mViewPager.getCurrentItem();
			// do something on back.
			if (item == 0) {
				if (backpress == 1) {
					finish();
				} else {
					backpress++;
				}
			} else {
				_mViewPager.setCurrentItem(0);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
