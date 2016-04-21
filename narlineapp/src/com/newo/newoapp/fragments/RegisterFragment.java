package com.newo.newoapp.fragments;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
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

import com.newo.newoapp.activities.CardFlipActivity;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.helper.EmailValidator;
import com.newo.newoapp.helper.UsernameValidator;
import com.newo.newoapp.narline.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment extends Fragment {

	private EditText txtUsername;
	private EditText txtEmail;
	private EditText txtPassword;
	private Button btnRegister;
	private TextView tvAgreement;
	private ImageView imgLogo;
	private LinearLayout llProgress;

	private String userName;
	private String email;
	private String password;

	private boolean register_button_status_username = false;
	private boolean register_button_status_email = false;
	private boolean register_button_status_password = false;

	// Validators
	private EmailValidator eValidator = new EmailValidator();
	private UsernameValidator uValidator = new UsernameValidator();

	public static Fragment newInstance(Context context) {

		RegisterFragment f = new RegisterFragment();
		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_register, null);

		// Companenets
		txtUsername = (EditText) root.findViewById(R.id.etxtRegisterUsername);
		txtEmail = (EditText) root.findViewById(R.id.etxtRegisterMail);
		txtPassword = (EditText) root.findViewById(R.id.etxtRegisterPassword);
		btnRegister = (Button) root.findViewById(R.id.btnRegister);
		tvAgreement = (TextView) root.findViewById(R.id.tvAgreement);
		tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
		imgLogo = (ImageView) root.findViewById(R.id.imgRegisterLogo);
		llProgress = (LinearLayout) root.findViewById(R.id.llProgressbar);

		// Set logo to imageView
		imgLogo.setImageDrawable(getResources().getDrawable(
				R.drawable.logo_narmobile));
		// Load
		// Preferences-------------------------------------------------------------------------------------------------------
		if (loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_NAME).trim().length() != 0) {
			Intent mainWin = new Intent(getActivity(), CardFlipActivity.class);
			startActivity(mainWin);
			getActivity().finish();
			return root;
		}

		// Get email address
		// automatically---------------------------------------------------------------------------------------
		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		Account[] accounts = AccountManager.get(getActivity()).getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;
				txtEmail.setText(possibleEmail);
			}
		}

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (register_button_status_username) {
					if (register_button_status_email) {
						if (register_button_status_password) {
							userName = txtUsername.getText().toString().trim();
							email = txtEmail.getText().toString().trim();
							password = txtPassword.getText().toString().trim();

							RegistrationTask task = new RegistrationTask();
							task.execute(userName, email, password);
						} else {
							txtPassword.setError("minimum 4 simbol");
						}
					} else {
						txtEmail.setError("Email " + txtEmail.getText()
								+ " uyğun deyil");
					}
				} else {
					txtUsername.setError("Username" + txtUsername.getText()
							+ " mümkün deyil");
				}

			}
		});

		txtUsername.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence username, int start,
					int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence username, int start,
					int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable username) {
				// TODO Auto-generated method stub
				validateEditTexts();
			}
		});

		txtEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence email, int start,
					int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence email, int start,
					int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable email) {
				// TODO Auto-generated method stub
				validateEditTexts();

			}
		});

		txtPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence password, int start,
					int before, int count) {
				// TODO Auto-generated method stub

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
		return root;
	}

	private void validateEditTexts() {

		if (uValidator.validate(txtUsername.getText().toString().trim())) {
			txtUsername.setError(null);
			register_button_status_username = true;
		} else {
			register_button_status_username = false;
		}

		if (eValidator.validate(txtEmail.getText().toString().trim())) {
			txtEmail.setError(null);
			register_button_status_email = true;
		} else {
			register_button_status_email = false;
		}

		if (txtPassword.getText().toString().trim().length() >= 4) {
			txtPassword.setError(null);
			register_button_status_password = true;
		} else {
			register_button_status_password = false;
		}

		if (register_button_status_username || register_button_status_email
				|| register_button_status_password) {
			btnRegister.setEnabled(true);
		} else {
			btnRegister.setEnabled(false);
		}

	}

	// On Resume of
	// Fragment----------------------------------------------------------
	public void onResume() {
		// Load Profile Info From Preferences

		if (register_button_status_username || register_button_status_email
				|| register_button_status_password) {
			btnRegister.setEnabled(true);
		} else {
			btnRegister.setEnabled(false);
		}

		super.onResume();
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

	class RegistrationTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			llProgress.setVisibility(View.VISIBLE);
			btnRegister.setEnabled(false);
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
						+ "UserServlet?action=userRegistration";

				// System.out.println("reqUrl: " + reqUrl);

				HttpPost post = new HttpPost(reqUrl);
				json.put("userName", params[0]);
				json.put("email", params[1]);
				json.put("password", params[2]);

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
				if (res == Constants.UserRegistration.USER_SUCCESSFULLY_REGISTERED) {
					Toast.makeText(getActivity(), "Qeydiyyat tamamlanmışdır",
							Toast.LENGTH_LONG).show();

					String userIdStr = "0";
					try {
						userIdStr = jObject.getString("userId");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.USER_ID, userIdStr);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.EMAIL_ADDRESS, email);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.EMAIL_ADDRESS, email);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.USER_NAME, userName);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.PASSWORD, password);
					Intent mainWin = new Intent(getActivity(),
							CardFlipActivity.class);
					startActivity(mainWin);
					getActivity().finish();
				} else if (res == Constants.UserRegistration.USERNAME_EXIST_ERROR) {
					Toast.makeText(getActivity(),
							"Belə istifadəçi adı mövcuddur", Toast.LENGTH_LONG)
							.show();
					txtUsername.setError("Artıq mövcuddur");
				} else if (res == Constants.UserRegistration.EMAIL_EXIST_ERROR) {
					Toast.makeText(getActivity(), "Such email already exist",
							Toast.LENGTH_LONG).show();
					txtEmail.setError("Artıq mövcuddur");
				} else {
					Toast.makeText(getActivity(), "Bilinməyən xəta",
							Toast.LENGTH_LONG).show();
				}
			}
			llProgress.setVisibility(View.GONE);
			btnRegister.setEnabled(true);
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
