package com.newo.newoapp.activities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.newo.newoapp.APImethods;
import com.newo.newoapp.CropOption;
import com.newo.newoapp.CropOptionAdapter;
import com.newo.newoapp.narline.R;
import com.newo.newoapp.RoundedImageView;
import com.newo.newoapp.SimpleListTextAdapter;
import com.newo.newoapp.SimpleTextListItem;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.helper.EmailValidator;
import com.newo.newoapp.helper.PhoneValidator;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FillUserInfoActivity extends BaseActivity implements
		OnRefreshListener {

	private ScrollView scrollFillInfoLayout;
	// private LinearLayout llFillInfoLayout;
	private RoundedImageView imgProfile;
	private TextView txtRequired1;
	private TextView txtRequired2;
	private EditText etxtEmail;
	private Button btnChangePassword;
	private TextView txtPersonalInfo1;
	private TextView txtPersonalInfo2;
	private EditText etxtName;
	private EditText etxtPhone;
	private EditText etxtBirthday;
	private EditText etxtGender;

	String userId = "";
	String firstName = "";
	String lastName = "";
	String birthDay = "";
	String email = "";
	String gender = "";
	String phoneNumber = "";

	private Uri mImageCaptureUri;

	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;

	static final int DATE_DIALOG_ID = 0;
	private int mYear, mMonth, mDay;
	public int year, month, day;

	private boolean save_button_status_mail = false;
	private boolean save_button_status_name = false;
	private boolean save_button_status_phone = false;

	private PullToRefreshLayout mPullToRefreshLayout;

	public FillUserInfoActivity() {

		// Assign current Date and Time Values to Variables
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);
		// Request Permission to display the Progress
		// Bar...--------------------------------------------------------------------------
		setContentView(R.layout.activity_fill_user_info);

		// Change actionbar
		// background---------------------------------------------------------------------
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bitmap_blue_trans));
		getActionBar().setLogo(R.drawable.logo_narmobile);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// You will setup the action bar with pull to refresh
		// layout-------------------
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pullFillUserInfo);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		// Layout-------------------------------------------------------------------------------
		scrollFillInfoLayout = (ScrollView) findViewById(R.id.scrollFillUSerInfo);

		// Validators-------------------------------------------------------------
		final EmailValidator eValidator = new EmailValidator();
		final PhoneValidator pValidator = new PhoneValidator();

		// Companents
		// ----------------------------------------------------------------------
		imgProfile = (RoundedImageView) findViewById(R.id.rimgFillInfoUserImageProfile);
		txtRequired1 = (TextView) findViewById(R.id.tvFillInfoRequired1);
		txtRequired2 = (TextView) findViewById(R.id.tvFillInfoRequired2);
		etxtEmail = (EditText) findViewById(R.id.etxtFillInfoEmail);
		btnChangePassword = (Button) findViewById(R.id.btnFillInfoEditPassword);
		txtPersonalInfo1 = (TextView) findViewById(R.id.tvFillInfoPersonal1);
		txtPersonalInfo2 = (TextView) findViewById(R.id.tvFillInfoPersonal2);
		etxtName = (EditText) findViewById(R.id.etxtFillInfoName);
		etxtPhone = (EditText) findViewById(R.id.etxtFillInfoPhone);
		etxtBirthday = (EditText) findViewById(R.id.etxtFillInfoBirthday);
		etxtGender = (EditText) findViewById(R.id.etxtFillInfoGender);

		// Get phone number
		// -------------------------------------------------------------------------------
		TelephonyManager tMgr = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();

		// Image Selector
		// dialog---------------------------------------------------------------------------
		final ArrayList<SimpleTextListItem> imageSources = new ArrayList<SimpleTextListItem>();
		imageSources.add(new SimpleTextListItem("Kamera ilə çək"));
		imageSources.add(new SimpleTextListItem("Qalereyadan seç"));
		SimpleListTextAdapter adapter = new SimpleListTextAdapter(this,
				imageSources);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "tmp_avatar_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { // pick from file
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
				}
			}
		});

		// Options select image source
		final AlertDialog dialog = builder.create();
		// Change image by pressing on Image
		// View--------------------------------------------------------------------------
		imgProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.show();
			}
		});

		// Edit text
		// Email---------------------------------------------------------------------------
		etxtEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence email, int start,
					int before, int count) {
				// TODO Auto-generated method stub
				if (eValidator.validate(etxtEmail.getText().toString().trim()) == true) {

					etxtEmail.setError(null);

				}

			}

			@Override
			public void beforeTextChanged(CharSequence email, int start,
					int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable email) {
				// TODO Auto-generated method stub

				if (eValidator.validate(etxtEmail.getText().toString().trim()) == true) {

					save_button_status_mail = true;

				} else {

					save_button_status_mail = false;
				}

				if (etxtName.getText().toString().trim().length() > 3) {

					save_button_status_name = true;

				} else {
					save_button_status_name = false;
				}
				if (pValidator.validate(etxtPhone.getText().toString().trim())) {
					save_button_status_phone = true;
				} else {
					save_button_status_phone = false;
				}
			}
		});

		// Button Change
		// Password-----------------------------------------------------------------
		btnChangePassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent changePass = new Intent(FillUserInfoActivity.this,
						ChangePasswordActivity.class);
				startActivity(changePass);

			}
		});
		// Edit text
		// Name------------------------------------------------------------------------
		etxtName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence name, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				if (etxtName.getText().toString().trim().length() > 3) {

					etxtName.setError(null);

				}
			}

			@Override
			public void beforeTextChanged(CharSequence name, int start,
					int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable name) {
				// TODO Auto-generated method stub
				if (etxtName.getText().toString().trim().length() > 3) {

					save_button_status_name = true;

				} else {
					save_button_status_name = false;
				}
				if (etxtEmail.getText().toString().length() > 0) {
					if (save_button_status_mail != true) {
						etxtEmail.setError(etxtEmail.getText().toString()
								.trim()
								+ " is not valid email");
					}
				}

				if (pValidator.validate(etxtPhone.getText().toString().trim())) {
					save_button_status_phone = true;
				} else {
					save_button_status_phone = false;
				}
			}
		});

		// Edit text
		// Phone-----------------------------------------------------------------------
		etxtPhone
				.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {

					@Override
					public void onTextChanged(CharSequence phone, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						if (pValidator.validate(etxtPhone.getText().toString()
								.trim())) {
							etxtPhone.setError(null);
						}

					}

					@Override
					public void beforeTextChanged(CharSequence phone,
							int start, int count, int after) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable phone) {
						// TODO Auto-generated method stub

						if (pValidator.validate(etxtPhone.getText().toString()
								.trim())) {
							save_button_status_phone = true;
						} else {
							save_button_status_phone = false;
						}

						if (etxtEmail.getText().toString().trim().length() > 0) {
							if (save_button_status_mail) {

							} else {
								etxtEmail.setError(etxtEmail.getText()
										.toString().trim()
										+ " is not valid email");
							}
						}

						if (etxtName.getText().toString().trim().length() > 0) {
							if (save_button_status_name) {

							} else {
								etxtName.setError("Enter valid name&surname");
							}
						}
					}
				});

		// Edit text Pick
		// Birthday--------------------------------------------------------------------
		etxtBirthday.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);

				if (etxtEmail.getText().toString().trim().length() > 0) {
					if (save_button_status_mail) {

					} else {
						etxtEmail.setError(etxtEmail.getText().toString()
								.trim()
								+ " is not valid email");
					}
				}

				if (etxtName.getText().toString().trim().length() > 0) {
					if (save_button_status_name) {

					} else {
						etxtName.setError("Enter valid name&surname");
					}
				}

				if (etxtPhone.getText().toString().trim().length() > 0) {
					if (save_button_status_phone != true) {
						etxtPhone.setError(etxtPhone.getText().toString()
								.trim()
								+ " is not valid phone number");
					}
				}

			}
		});

		// Edit text Pick
		// Gender--------------------------------------------------------------------------
		etxtGender.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GenderAlertDialog();
			}
		});

		// Load Preferences
		// ----------------------------------------------------------------
		String imagePath = Constants.fileNames.MAIN_FOLDER
				+ Constants.fileNames.PROFILE_PHOTO;
		File imgFile = new File(imagePath);
		if (imgFile.exists()) {

			imgProfile.setImageDrawable(Drawable.createFromPath(imagePath));
			// imgProfile.setImageURI(Uri.fromFile(imgFile));

		}
		String firstName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.FIRST_NAME);
		String lastName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.LAST_NAME);

		if ((firstName + lastName).trim().length() != 0)
			etxtName.setText(firstName + " " + lastName);

		long birth = new Date().getTime();
		try {
			birth = Long.valueOf(loadPrefs(Constants.PrefNames.USER_INFO,
					Constants.PrefKeys.DATE_OF_BIRTH));
		} catch (Exception ex) {
			if (ex.getMessage() != null)
				System.out.println(ex.getMessage());
		}
		Date date = new Date(birth);
		Calendar calendar = Calendar.getInstance(); // creates a new calendar
													// instance
		calendar.setTime(date); // assigns calendar to given date

		day = mDay = calendar.get(Calendar.DAY_OF_MONTH);
		month = mMonth = calendar.get(Calendar.MONTH);
		year = mYear = calendar.get(Calendar.YEAR);
		etxtBirthday.setText(String.valueOf(mDay) + "/"
				+ String.valueOf(mMonth + 1) + "/" + String.valueOf(mYear));

		email = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.EMAIL_ADDRESS);
		if (email.trim().length() != 0)
			etxtEmail.setText(email);

		phoneNumber = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PHONE_NUMBER);
		if (phoneNumber.trim().length() != 0)
			etxtPhone.setText(phoneNumber);

		gender = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.GENDER);
		if (gender.equals("0"))
			etxtGender.setText("-");
		else if (gender.equals("1"))
			etxtGender.setText("male");
		else if (gender.equals(2)) {
			etxtGender.setText("female");
		}
	}

	// On
	// Pause------------------------------------------------------------------------------------------------------
	@Override
	protected void onPause() {
		super.onPause();
		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);
	}

	public static Bitmap imgProfileBitmap = null;

	// Activity result for pick
	// image----------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();

			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();

			doCrop();

			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();

			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				imgProfileBitmap = extras.getParcelable("data");

				imgProfile.setImageBitmap(photo);
			}

			File f = new File(mImageCaptureUri.getPath());

			if (f.exists())
				f.delete();

			break;

		}
	}

	// Crop
	// Image------------------------------------------------------------------------------
	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 256);
			intent.putExtra("outputY", 256);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getContentResolver().delete(mImageCaptureUri, null,
									null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	// Birthday DatePickerDialog
	// listener-------------------------------------------------------------------------
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// the callback received when the user "sets" the Date in the
		// DatePickerDialog
		public void onDateSet(DatePicker view, int yearSelected,
				int monthOfYear, int dayOfMonth) {
			year = yearSelected;
			month = monthOfYear;
			day = dayOfMonth;
			// Set the Selected Date in Select date Button
			etxtBirthday.setText(day + "/" + (month + 1) + "/" + year);
		}

	};

	// Pick Gender
	// Dialog-------------------------------------------------------------------------------
	public void GenderAlertDialog() {
		final String[] genderListArray;
		genderListArray = getResources().getStringArray(R.array.genders);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(genderListArray,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						etxtGender.setText(genderListArray[which].toString());

					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	// Method automatically gets Called when you call showDialog()
	// method------------------------
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// create a new DatePickerDialog with values you want to show
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	// Options
	// Menu-------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.fill_user_info, menu);
		return true;
	}

	// Optionsmenu
	// Listner-------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_save:
			if (save_button_status_mail) {

				if (save_button_status_name) {
					if (save_button_status_phone) {
						if (APImethods
								.hasActiveInternetConnection(FillUserInfoActivity.this)) {

							userId = loadPrefs(Constants.PrefNames.USER_INFO,
									Constants.PrefKeys.USER_ID);
							firstName = etxtName.getText().toString();
							lastName = "";
							birthDay = "0";
							email = etxtEmail.getText().toString();
							gender = etxtGender.getText().toString();
							phoneNumber = etxtPhone.getText().toString();

							gender = gender.toLowerCase().trim();
							if (gender.equals("male"))
								gender = "1";
							else if (gender.equals("female"))
								gender = "2";
							else
								gender = "0";

							ChangeProfileInfoTask task = new ChangeProfileInfoTask();
							task.execute(userId, firstName, lastName, birthDay,
									email, gender, phoneNumber);

						} else {
							Toast.makeText(getApplicationContext(),
									R.string.internet_connection_error,
									Toast.LENGTH_LONG).show();
							return true;
						}
					} else {

						etxtPhone.setError(etxtPhone.getText().toString()
								.trim()
								+ " is not valid phone number");

					}
				} else {

					etxtName.setError("Enter valid name&surname");
				}

			} else {
				etxtEmail.setError(etxtEmail.getText().toString().trim()
						+ " is not valid email");
			}
		}
		return (super.onOptionsItemSelected(menuItem));
	}

	// Back press
	// listener------------------------------------------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			onBackPressed();
		}
		return true;
	}

	// Back press
	// method--------------------------------------------------------------------------
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// finish();

	}

	class ChangeProfileInfoTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			mPullToRefreshLayout.setRefreshing(true);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			String userId = params[0];
			String firstName = params[1];
			String lastName = params[2];
			String birthDay = params[3];
			String email = params[4];
			String gender = params[5];
			String phoneNumber = params[6];
			int timeOutLength = 10000;

			// TODO Auto-generated method stub
			try {
				// ImageView image = (ImageView) findViewById(R.id.imageView1);
				byte[] data = null;
				if (imgProfile.getDrawable() != null
						&& imgProfileBitmap != null) {
					// imgProfile.buildDrawingCache();
					// Bitmap bm = imgProfile.getDrawingCache();
					// Drawable dr = (Drawable) imgProfile.getDrawable();
					// Bitmap bm = ((BitmapDrawable) dr)
					// .getBitmap();//
					// BitmapFactory.decodeFile("/sdcard/ManAds/53.jpg");
					// Bitmap bm =
					// Bitmap.createBitmap(imgProfile.getWidth(),imgProfile.getHeight(),Bitmap.Config.RGB_565);
					// Canvas canvas = new Canvas(bm);
					// imgProfile.draw(canvas);

					// Bitmap bm =
					// RoundedDrawable.drawableToBitmap(imgProfile.getDrawable());
					Bitmap bm = imgProfileBitmap;
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bm.compress(CompressFormat.JPEG, 75, bos);
					data = bos.toByteArray();
					// data = imgData;
				}

				HttpClient httpClient = new DefaultHttpClient();

				String reqUrl = Constants.ConstStrings.serverPath
						+ "UserServlet?action=changeProfileData";

				HttpPost postRequest = new HttpPost(reqUrl);

				// File file= new File("/mnt/sdcard/forest.png");
				// FileBody bin = new FileBody(file);
				MultipartEntity reqEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				if (data != null) {

					ByteArrayBody bab = new ByteArrayBody(data,
							"profilePhoto.jpg");
					reqEntity.addPart("profilePhoto", bab);
				}
				reqEntity.addPart("userId", new StringBody(userId));

				reqEntity.addPart("firstName", new StringBody(firstName,
						Charset.forName("UTF-8")));
				reqEntity.addPart("lastName",
						new StringBody(lastName, Charset.forName("UTF-8")));

				reqEntity.addPart(
						"birthDay",
						new StringBody(String.valueOf(APImethods.makeLongDate(
								day, month, year, 0, 0, 0))));
				reqEntity.addPart("phoneNumber", new StringBody(phoneNumber));

				reqEntity.addPart("email", new StringBody(email));
				reqEntity.addPart("gender", new StringBody(gender));

				postRequest.setEntity(reqEntity);

				HttpResponse response = httpClient.execute(postRequest);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				String sResponse;
				StringBuilder s = new StringBuilder();

				while ((sResponse = reader.readLine()) != null) {
					s = s.append(sResponse);
				}
				result = s.toString();
				System.out.println("Response: " + s);
			} catch (Exception e) {
				// handle exception here
				Log.e(e.getClass().getName(), e.getMessage());
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

				if (res == Constants.UserProfileDataChange.USER_PROFILE_DATA_CHANGED) {
					// savePrefs("userInfo", "userId", "");

					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.FIRST_NAME, firstName);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.LAST_NAME, lastName);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.EMAIL_ADDRESS, email);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.DATE_OF_BIRTH,
							String.valueOf(APImethods.makeLongDate(day, month,
									year, 0, 0, 0)));
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.GENDER, gender);
					savePrefs(Constants.PrefNames.USER_INFO,
							Constants.PrefKeys.PHONE_NUMBER, phoneNumber);

					Log.d("KeyHash:", mDay + " " + mMonth + " " + mYear);

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

					if (folder.exists() && imgProfileBitmap != null) {
						FileOutputStream stream = null;
						try {
							stream = new FileOutputStream(imagePath);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						imgProfileBitmap.compress(CompressFormat.JPEG, 75,
								stream);
						try {
							stream.flush();
							stream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					// setting downloaded into image view
					Toast.makeText(FillUserInfoActivity.this,
							R.string.change_profile_data_is_ok,
							Toast.LENGTH_SHORT).show();
					finish();

				} else {
					Toast.makeText(FillUserInfoActivity.this,
							R.string.change_profile_data_error,
							Toast.LENGTH_SHORT).show();
				}
			}
			// after completed finished the progressbar
			mPullToRefreshLayout.setRefreshComplete();

		}

	}

	private void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = this
				.getSharedPreferences(prefName, MODE_PRIVATE);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private String loadPrefs(String prefName, String key) {
		SharedPreferences sp = this.getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

	// Pull to Refresh Task
	// ------------------------------------------------------------------------------
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
