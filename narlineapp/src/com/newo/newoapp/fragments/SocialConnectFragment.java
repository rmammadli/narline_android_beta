package com.newo.newoapp.fragments;

import java.util.Arrays;
import java.util.List;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.narline.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SocialConnectFragment extends Fragment {

	private ImageView imgFacebook;
//	private ImageView imgTwitter;
	private LinearLayout llSettings;
	private CheckBox checkFacebook;
//	private CheckBox checkTwitter;
	private TextView tvFaceHeader;
	private TextView tvFaceText;
	private TextView tvFacebookFooter;
	// private TextView tvTwitHeader;
	// private TextView tvTwitText;
	// private TextView tvTwitterFooter;
	private TextView tvHeader1;
	private TextView tvHeader2;
	private String checkFacebookStatus;

	protected static final String TAG = SocialConnectFragment.class.getName();
	private ProgressDialog mProgress;

	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	public static Fragment newInstance(Context context) {

		SocialConnectFragment f = new SocialConnectFragment();
		return f;

	}

	private static final List<String> PERMISSIONS = Arrays.asList(
			"publish_actions", "publish_checkins", "publish_stream", "uid");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		view = inflater.inflate(R.layout.fragment_social, container, false);

		// Initalize compananets on layout
		// TextViews
		tvFaceHeader = (TextView) view.findViewById(R.id.txtFacebookHeader);
		tvFaceText = (TextView) view.findViewById(R.id.txtFacebookText);
		tvFacebookFooter = (TextView) view
				.findViewById(R.id.txtSettingFacebookFooter);
//		tvTwitHeader = (TextView) view.findViewById(R.id.txtTwitterHeader);
//		tvTwitText = (TextView) view.findViewById(R.id.txtTwitterText);
//		tvTwitterFooter = (TextView) view
//				.findViewById(R.id.txtSettingTwitterFooter);
		tvHeader1 = (TextView) view.findViewById(R.id.txtSetting1);
		tvHeader2 = (TextView) view.findViewById(R.id.txtSetting2);

		// Images
		imgFacebook = (ImageView) view.findViewById(R.id.imgFacebook);
//		imgTwitter = (ImageView) view.findViewById(R.id.imgTwitter);

		// Buttons
		checkFacebook = (CheckBox) view.findViewById(R.id.switchFacebook);
//		checkTwitter = (CheckBox) view.findViewById(R.id.switchTwitter);
		// Layouts
		llSettings = (LinearLayout) view.findViewById(R.id.llSettings);

		// Apply Images
		imgFacebook.setImageDrawable(getResources().getDrawable(
				R.drawable.facebook_circle));

		// BitmapLoader bitmapLoader = new BitmapLoader();
		// bitmapLoader.loadBitmap(R.drawable.facebook_circle, imgFacebook);

//		imgTwitter.setImageDrawable(getResources().getDrawable(
//				R.drawable.twitter_circle));
//
//		checkTwitter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				// TODO Auto-generated method stub
//				if (isChecked) {
//					alertSocialDialog(getResources().getString(
//							R.string.social_dialog_message));
//					checkTwitter.setChecked(false);
//				} else {
//
//				}
//
//			}
//		});

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(getActivity(), null,
						statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(getActivity());
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}

		updateView();
		return view;
	}

	private void alertSocialDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());

		// Set a message
		final TextView input = new TextView(getActivity());
		// AboutDialog abd = new AboutDialog(getActivity());
		input.setTextColor(getResources().getColor(R.color.d_gray));
		// input.setText(Html.fromHtml(readRawTextFile(R.raw.about_social)));
		input.setText(message);
		input.setPadding(30, 60, 30, 30);
		input.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		builder.setView(input);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				// clearing the contents of edittext on click of OK button
				dialog.cancel();

			}
		});
		// Create the dialog
		AlertDialog alertdialog = builder.create();

		// show the alertdialog
		alertdialog.show();

		Button bq = alertdialog.getButton(DialogInterface.BUTTON_POSITIVE);
		bq.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_btn_simple_white));
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
		updateView();
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
		updateView();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(getActivity(), requestCode,
				resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			// textInstructionsOrLink.setText(URL_PREFIX_FRIENDS +
			// session.getAccessToken());
			tvFacebookFooter.setText("Qoşulub");
			checkFacebook.setChecked(true);

			checkFacebookStatus = "1";

			savePrefs(Constants.PrefNames.SOCIAL_STATUS,
					Constants.PrefKeys.SOCIAL_BUTTON_SCAN_SHARE,
					checkFacebookStatus);

			checkFacebook
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								onClickLogin();
							} else {
								onClickLogout();
							}

						}
					});

		} else {
			// textInstructionsOrLink.setText(R.string.instructions);
			tvFacebookFooter.setText("Qoş");
			checkFacebook.setChecked(false);
			checkFacebookStatus = null;

			savePrefs(Constants.PrefNames.SOCIAL_STATUS,
					Constants.PrefKeys.SOCIAL_BUTTON_SCAN_SHARE,
					checkFacebookStatus);
			checkFacebook
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								// Intent i = new Intent(getActivity(),
								// FacebookActivity.class);
								// startActivity(i);
								// getActivity().finish();
								// mSimpleFacebook.login(mOnLoginListener);
								onClickLogin();
							} else {
								onClickLogout();
								// mSimpleFacebook.logout(mOnLogoutListener);
							}

						}
					});
		}
	}

	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(getActivity(), this, true, statusCallback);
		}
		updateView();
	}

	private void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
		updateView();
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			updateView();
		}
	}

	private void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = getActivity().getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private String loadPrefs(String prefName, String key) {
		SharedPreferences sp = getActivity().getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

}
