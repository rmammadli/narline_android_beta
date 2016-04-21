package com.newo.newoapp.activities;

import java.util.Calendar;

import com.newo.newoapp.ProductTourAdapter;
import com.newo.newoapp.narline.R;
import com.newo.newoapp.helper.ViewPagerParallax;
import com.viewpagerindicator.CirclePageIndicator;

import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;

public class ProductTourActivity extends FragmentActivity {

	private ViewPagerParallax _mViewPager;
	private ProductTourAdapter _adapter;
	private ActionBar actionBar;

	private int i = 0;
	private int mInterval = 5000;
	private Handler mHandler;

	private Calendar today;
	private int hour;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_product_tour);

		actionBar = getActionBar();

		// change actionbar background
		// actionBar.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.blue_trans_bitmap));
		actionBar.setLogo(R.drawable.logo_narmobile);
		actionBar.setDisplayHomeAsUpEnabled(true);

		_mViewPager = (ViewPagerParallax) findViewById(R.id.viewPagerPT);
		_mViewPager.set_max_pages(4);
		_mViewPager.setBackgroundAsset(R.raw.baku_day_1280);
		_adapter = new ProductTourAdapter(getApplicationContext(),
				getSupportFragmentManager());
		_mViewPager.setAdapter(_adapter);
		mHandler = new Handler();

		final CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicatorPT);
		mIndicator.setViewPager(_mViewPager);

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

		// start changing fragments automatically
		startRepeatingTask();
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
			if (i == 4) {
				i = 0;
			}
		}
	};

	void startRepeatingTask() {
		mStatusChecker.run();
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(mStatusChecker);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("num_pages", 4);
		final ViewPagerParallax pager = (ViewPagerParallax) findViewById(R.id.viewPagerPT);
		outState.putInt("current_page", pager.getCurrentItem());
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
}
