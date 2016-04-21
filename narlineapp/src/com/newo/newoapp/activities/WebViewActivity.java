package com.newo.newoapp.activities;


import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.newo.newoapp.narline.R;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends BaseActivity implements OnRefreshListener {

	private PullToRefreshLayout mPullToRefreshLayout;

	private WebView mWebView;
	private String mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_web_view);

		// Change actionbar
		// background-----------------------------------------------------------------------
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bitmap_blue_trans));
		getActionBar().setLogo(R.drawable.logo_narmobile);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Set Activity Title
		// -------------------------------------------------------------------------------
		getActionBar().setTitle(mTitle);

		// Setup
		// WebView------------------------------------------------------------------------
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new SampleWebViewClient());
		mWebView.loadUrl(this.getIntent().getDataString());
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setSupportMultipleWindows(true);

		// Now find the PullToRefreshLayout and set it
		// up-------------------------------------
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);
		mPullToRefreshLayout.setRefreshing(true);
	}

	// Options Menu
	// ----------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

	// Options Menu On Item
	// Sellect---------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_share_webview:
			shareApps(mWebView.getUrl());
			return true;

		}
		return (super.onOptionsItemSelected(menuItem));
	}

	// WebView Started
	// -----------------------------------------------------------------------------
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		mWebView.reload();

	}

	// Setup WebView With pull to
	// Refresh----------------------------------------------------------------------
	private class SampleWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// Return false so the WebView loads the url

			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			// If the PullToRefreshAttacher is refreshing, make it as complete
			if (mPullToRefreshLayout.isRefreshing()) {
				mPullToRefreshLayout.setRefreshComplete();
			}
		}
	}

	// Send Share Intent to Other
	// Apps------------------------------------------------------------------------
	private void shareApps(String link) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, link));
	}

	// Activity
	// onPause---------------------------------------------------------------
	@Override
	protected void onPause() {
		super.onPause();
		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);
	}

}
