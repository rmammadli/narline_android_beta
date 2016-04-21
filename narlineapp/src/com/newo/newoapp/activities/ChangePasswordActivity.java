package com.newo.newoapp.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.newo.newoapp.narline.R;
import com.newo.newoapp.domain.Constants;

import com.newo.newoapp.helper.BitmapLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordActivity extends BaseActivity implements
		OnRefreshListener {

	private ScrollView scrollChangePass;
	// private LinearLayout llBackground;
	private TextView tvCurrentPassHeader1;
	private TextView tvCurrentPassHeader2;
	private TextView tvNewPassHeader1;
	private TextView tvNewPassHeader2;
	private EditText etxCurrentPass;
	private EditText etxNewPass;
	private EditText etxNewPassAgain;

	private boolean save_button_state_current_pass = false;
	private boolean save_button_state_new_pass1 = false;
	private boolean save_button_state_new_pass2 = false;

	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_change_password);

		// Change actionbar
		// background-------------------------------------------------
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bitmap_blue_trans));
		getActionBar().setLogo(R.drawable.logo_narmobile);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// /You will setup the action bar with pull to refresh
		// layout------------------
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pullChangePass);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		// Layout------------------------------------------------------------------------
		BitmapLoader bitmapLoader = new BitmapLoader();
		scrollChangePass = (ScrollView) findViewById(R.id.scrollChangePass);

		// Companents--------------------------------------------------------------------
		tvCurrentPassHeader1 = (TextView) findViewById(R.id.tvChangePassCurrent1);
		tvCurrentPassHeader2 = (TextView) findViewById(R.id.tvChangePassCurrent2);
		tvNewPassHeader1 = (TextView) findViewById(R.id.tvChangePassNew1);
		tvNewPassHeader2 = (TextView) findViewById(R.id.tvChangePassNew2);
		etxCurrentPass = (EditText) findViewById(R.id.etxtChangePassCurrent);
		etxNewPass = (EditText) findViewById(R.id.etxtChangePassNew1);
		etxNewPassAgain = (EditText) findViewById(R.id.etxtChangePassNew2);

		// Edit text Current password
		// listner--------------------------------------------
		etxCurrentPass.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

				validateEditTexts();

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				validateEditTexts();
			}
		});
		// Edit text new password
		// listener-----------------------------------------------
		etxNewPass.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence password, int start,
					int before, int count) {
				// TODO Auto-generated method stub
				validateEditTexts();

			}

			@Override
			public void beforeTextChanged(CharSequence password, int start,
					int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable password) {
				// TODO Auto-generated method stub
				validateEditTexts();

			}
		});

		// Edit text New Password again
		// listener---------------------------------------------
		etxNewPassAgain.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				validateEditTexts();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {

				validateEditTexts();

			}
		});
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

	// Create Options
	// menu------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_password, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.getItem(0).setEnabled(true); // here pass the index of
											// save menu item
		return super.onPrepareOptionsMenu(menu);

	}

	// Options menu
	// Listner---------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_save:
			// Toast.makeText(this, loadPrefs(Constants.PrefNames.USER_INFO,
			// Constants.PrefKeys.USER_ID),
			// Toast.LENGTH_SHORT).show();
			if (save_button_state_current_pass) {
				if (save_button_state_new_pass1) {
					if (save_button_state_new_pass2) {

						String currentPasswordStr = etxCurrentPass.getText()
								.toString();
						String newPasswordStr = etxNewPass.getText().toString();
						String newPasswordStrAgain = etxNewPassAgain.getText()
								.toString();
						String userId = loadPrefs(
								Constants.PrefNames.USER_INFO,
								Constants.PrefKeys.USER_ID);

						ChangePasswordTask task = new ChangePasswordTask();
						task.execute(userId, currentPasswordStr, newPasswordStr);
						return true;
					} else {
						etxNewPassAgain
								.setError("Yeni şifrə ilə eyni olmalıdır");
					}
				} else {
					etxNewPass.setError("Uyğun şifrə daxil edin");
				}
			} else {
				etxCurrentPass.setError("Düzgün şifrə daxil edin");
			}
		}
		return (super.onOptionsItemSelected(menuItem));
	}

	// Validator for Edit
	// Texts--------------------------------------------------------
	private void validateEditTexts() {

		if (etxCurrentPass.getText().toString().trim().length() > 0) {
			save_button_state_current_pass = true;
			etxCurrentPass.setError(null);
		} else {
			save_button_state_current_pass = false;
		}

		if (etxNewPass.getText().toString().trim().length() >= 4) {
			etxNewPass.setError(null);
			save_button_state_new_pass1 = true;

		} else {
			save_button_state_new_pass1 = false;
		}

		if (etxNewPassAgain.getText().toString().trim()
				.equals(etxNewPass.getText().toString().trim())) {
			etxNewPassAgain.setError(null);
			save_button_state_new_pass2 = true;
		} else {
			save_button_state_new_pass2 = false;
		}

	}

	// Back press
	// listener-------------------------------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			onBackPressed();
		}
		return true;
	}

	// Back press
	// method---------------------------------------------------------------
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// finish();

	}

	private String convertStreamToString(InputStream is) {

		String line = "";
		StringBuilder total = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (Exception e) {
			// Toast.makeText(this, "Stream Exception",
			// Toast.LENGTH_SHORT).show();
		}
		return total.toString();
	}

	// Async
	// Task---------------------------------------------------------------------------------
	class ChangePasswordTask extends AsyncTask<String, Void, String> {

		private String newPassword = null;

		@Override
		protected void onPreExecute() {

			mPullToRefreshLayout.setRefreshing(true);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;

			int timeOutLength = 10000;

			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(),
					timeOutLength); // Timeout
			HttpConnectionParams
					.setSoTimeout(client.getParams(), timeOutLength);
			HttpResponse response;
			JSONObject json = new JSONObject();

			try {

				String reqUrl = Constants.ConstStrings.serverPath
						+ "UserServlet?action=changeUserPassword";

				// System.out.println("reqUrl: " + reqUrl);

				HttpPost post = new HttpPost(reqUrl);
				json.put("userId", params[0]);
				json.put("oldPassword", params[1]);
				json.put("newPassword", params[2]);

				newPassword = params[2];

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

					result = convertStreamToString(inputstream);

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
				if (res == Constants.UserPasswordChange.USER_PASSWORD_CHANGED) {
					Toast.makeText(ChangePasswordActivity.this,
							"Şifrə uğurla dəyişdirilmişdir", Toast.LENGTH_LONG)
							.show();

					String userIdStr = "0";
					try {
						userIdStr = jObject.getString("userId");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.PASSWORD, newPassword);
					finish();
				} else if (res == Constants.UserPasswordChange.USER_PASSWORD_INCORRECT) {
					Toast.makeText(ChangePasswordActivity.this,
							"Daxil etdiyiniz şifrə yalnışdır",
							Toast.LENGTH_LONG).show();
					etxCurrentPass.setError("Şifrə yalnışdır");
				} else {
					Toast.makeText(ChangePasswordActivity.this,
							"Bilinməyən xəta", Toast.LENGTH_LONG).show();
				}
			}
			mPullToRefreshLayout.setRefreshComplete();

		}
	}

	private String loadPrefs(String prefName, String key) {
		SharedPreferences sp = this
				.getSharedPreferences(prefName, MODE_PRIVATE);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

	private void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = ChangePasswordActivity.this
				.getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		/**
		 * Below AsyncTask class is used to update the view Asynchronously
		 */
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(5000);
					// Here you can get the new text from DB or through a web
					// service
					// Then YOu can pass it to onPostExecute() method to
					// Update the view

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				// Here you can update the view
				// textView.setText(textView.getText().toString()+"--New Content Added");

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();
			}
		}.execute();
	}

}
