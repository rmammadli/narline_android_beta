package com.newo.newoapp.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.newo.newoapp.narline.R;
import com.newo.newoapp.activities.CardFlipActivity;

import com.newo.newoapp.domain.Constants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class LoginFragment extends Fragment {

	private EditText txtUsername;
	private EditText txtPassword;
	private Button btnLogin;
	private TextView txtForgotPassword;
	private ImageView imgLogo;
	private LinearLayout llProgress;

	private String userName;
	private String email;
	private String password;
	private String txtForgotPasswordEmail;

	public static Fragment newInstance(Context context) {

		LoginFragment f = new LoginFragment();
		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login,
				null);
		// Companents
		txtUsername = (EditText) root.findViewById(R.id.etxtLoginUsername);
		txtPassword = (EditText) root.findViewById(R.id.etxtLoginPassword);
		btnLogin = (Button) root.findViewById(R.id.btnLogin);
		txtForgotPassword = (TextView) root.findViewById(R.id.tvForgotPassword);
		imgLogo = (ImageView) root.findViewById(R.id.imgLoginLogo);
		llProgress = (LinearLayout) root.findViewById(R.id.llProgressbar);
		btnLogin.setEnabled(false);

		// set logo to Imageview
		imgLogo.setImageDrawable(getResources().getDrawable(
				R.drawable.logo_narmobile));

		// login button click
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				userName = txtUsername.getText().toString().trim();
//				password = txtPassword.getText().toString().trim();
//
//				LoginTask task = new LoginTask();
//				task.execute(userName, password);
				Intent mainWin = new Intent(getActivity(),
						CardFlipActivity.class);

				// downloadImage("");

				startActivity(mainWin);
				getActivity().finish();
			}
		});

		txtUsername.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence username, int start,
					int before, int count) {
				// TODO Auto-generated method stub
				if (username.length() != 0
						&& txtPassword.getText().toString().trim().length() != 0) {
					btnLogin.setEnabled(true);
				} else {
					btnLogin.setEnabled(false);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence username, int start,
					int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable username) {
				// TODO Auto-generated method stub

			}
		});

		txtPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence password, int start,
					int before, int count) {
				// TODO Auto-generated method stub
				if (password.length() != 0
						&& txtUsername.getText().toString().trim().length() != 0) {
					btnLogin.setEnabled(true);
				} else {
					btnLogin.setEnabled(false);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence password, int start,
					int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable password) {
				// TODO Auto-generated method stub

			}
		});

		txtForgotPassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder alert = new AlertDialog.Builder(
						getActivity());

				// alert.setTitle("Forgot Password");
				alert.setMessage("Email: ");

				// Set an EditText view to get user input
				final EditText input = new EditText(getActivity());
				alert.setView(input);

				alert.setPositiveButton("Göndər",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								txtForgotPasswordEmail = input.getText()
										.toString().trim();
								if (android.util.Patterns.EMAIL_ADDRESS
										.matcher(txtForgotPasswordEmail)
										.matches() == true) {

									ForgotPasswordTask task = new ForgotPasswordTask();
									task.execute(txtForgotPasswordEmail);

								} else {
									Toast.makeText(getActivity(),
											"Email düzgün deyil",
											Toast.LENGTH_SHORT).show();

								}

								// Do something with value!
							}
						});

				alert.setNegativeButton("İmtina",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.show();

			}
		});
		return root;
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

	// On Resume of
	// Fragment----------------------------------------------------------
	public void onResume() {
		// Load Profile Info From Preferences

		if (txtPassword.getText().toString().trim().length() != 0
				&& txtUsername.getText().toString().trim().length() != 0) {
			btnLogin.setEnabled(true);
		} else {
			btnLogin.setEnabled(false);
		}

		super.onResume();
	}

	class LoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			llProgress.setVisibility(View.VISIBLE);
			btnLogin.setEnabled(false);
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
						+ "UserServlet?action=userLogin";

				HttpPost post = new HttpPost(reqUrl);
				json.put("userName", params[0]);
				json.put("password", params[1]);

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

				if (res == Constants.UserLogin.USER_SUCCESSFULLY_LOGIN) {
					Toast.makeText(getActivity(), "Giriş tamamlanmışdır",
							Toast.LENGTH_LONG).show();

					String userIdStr = "0";
					String profileImage = null;
					String emailAddress = "";
					String phoneNumber = "";
					String firstName = "";
					String lastName = "";
					String gender = "";
					String dateOfBirth = "";

					byte[] profileImageByte = null;
					try {
						userIdStr = jObject.getString("userId");
						profileImage = jObject.getString("profileImage");
						profileImageByte = Base64.decode(profileImage,
								Base64.URL_SAFE);
						Bitmap loadedImage = BitmapFactory.decodeByteArray(
								profileImageByte, 0, profileImageByte.length);
						;

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

						if (loadedImage != null) {
							FileOutputStream stream = null;
							try {
								stream = new FileOutputStream(imagePath);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							loadedImage.compress(CompressFormat.JPEG, 75,
									stream);
							try {
								stream.flush();
								stream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						emailAddress = jObject.has("emailAddress") ? jObject
								.getString("emailAddress") : "";
						phoneNumber = jObject.has("phoneNumber") ? jObject
								.getString("phoneNumber") : "";
						firstName = jObject.has("firstName") ? jObject
								.getString("firstName") : "";
						lastName = jObject.has("lastName") ? jObject
								.getString("lastName") : "";
						gender = jObject.has("gender") ? jObject
								.getString("gender") : "";
						dateOfBirth = jObject.has("dateOfBirth") ? jObject
								.getString("dateOfBirth") : "";
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.USER_ID, userIdStr);

					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.USER_NAME, userName);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.PASSWORD, password);

					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.EMAIL_ADDRESS, emailAddress);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.PHONE_NUMBER, phoneNumber);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.FIRST_NAME, firstName);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.LAST_NAME, lastName);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.GENDER, gender);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.DATE_OF_BIRTH, dateOfBirth);

					Intent mainWin = new Intent(getActivity(),
							CardFlipActivity.class);

					// downloadImage("");

					startActivity(mainWin);
					getActivity().finish();
				} else if (res == Constants.UserLogin.PASSWORD_INCORRECT
						|| res == Constants.UserLogin.USERNAME_NOT_EXIST_ERROR) {
					Toast.makeText(getActivity(),
							"İsitifadəçi adı və ya şifrə düzgün deyil", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(getActivity(), "Bilinməyən xəta",
							Toast.LENGTH_LONG).show();
				}
			}
			llProgress.setVisibility(View.GONE);
			btnLogin.setEnabled(true);
		}
	}

	class ForgotPasswordTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			llProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;

			int timeOutLength = 50000;

			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(),
					timeOutLength); // Timeout
			HttpConnectionParams
					.setSoTimeout(client.getParams(), timeOutLength);
			HttpResponse response;
			JSONObject json = new JSONObject();

			try {

				String reqUrl = Constants.ConstStrings.serverPath
						+ "ForgotPasswordServlet";

				HttpPost post = new HttpPost(reqUrl);
				json.put("email", params[0]);

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

				if (res == Constants.UserForgotPassword.PASSWORD_SUCCESSFULLY_SENDED) {
					Toast.makeText(getActivity(), "Şifrə emailə göndərildi.",
							Toast.LENGTH_LONG).show();

					String userIdStr = "0";
					try {
						userIdStr = jObject.getString("userId");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (res == Constants.UserForgotPassword.EMAIL_ADDRESS_INCORRECT) {
					Toast.makeText(getActivity(), "Email düzgün deyil.",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), "Bilinməyən xəta.",
							Toast.LENGTH_LONG).show();
				}
			}
			llProgress.setVisibility(View.GONE);
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
