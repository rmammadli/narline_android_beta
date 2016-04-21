package com.newo.newoapp.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.newo.newoapp.APImethods;
import com.newo.newoapp.NeWoApp;
import com.newo.newoapp.ProfileCompanyListAdapter;
import com.newo.newoapp.ProfileCompanyListItem;
import com.newo.newoapp.narline.R;
import com.newo.newoapp.RoundedImageView;
import com.newo.newoapp.activities.CardFlipActivity;
import com.newo.newoapp.activities.FillUserInfoActivity;
import com.newo.newoapp.activities.ScanActivity;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.helper.BitmapLoader;

public class ProfileFragment extends Fragment implements OnRefreshListener {

	private ProgressBar mProgressBar;
	private TextView tvNameSurname;
	private TextView tvUsername;
	private TextView tvPhone;
	private ImageButton ibtnScan;

	private LinearLayout llUSerInfo;
	private RoundedImageView imgProfile;

	private ListView lvCompanies;
	private ArrayList<ProfileCompanyListItem> profileCompanyItems;
	private ProfileCompanyListAdapter companyAdapter;

	// Hold a reference to the current animator,
	// so that it can be canceled mid-way.
	private Animator mCurrentAnimator;

	// The system "short" animation time duration, in milliseconds. This
	// duration is ideal for subtle animations or animations that occur
	// very frequently.
	private int mShortAnimationDuration;

	private PullToRefreshLayout mPullToRefreshLayout;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	private DisplayImageOptions options;
	private File imgFile;

	ImageLoader imageLoader = ImageLoader.getInstance();

	public static Fragment newInstance(Context context) {

		ProfileFragment f = new ProfileFragment();
		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view;
		view = inflater.inflate(R.layout.fragment_profile, container, false);

		View llListHeaderUserInfo = View.inflate(getActivity(),
				R.layout.layout_profile_list_header, null);

		// Change actionbar background
		getActivity().getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bitmap_blue_trans));
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);

		// Now give the find the PullToRefreshLayout and set it up
		mPullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.pullProfileFragment);
		ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);
		mPullToRefreshAttacher = ((CardFlipActivity) getActivity())
				.getPullToRefreshAttacher();

		mPullToRefreshLayout.isRefreshing();

		// Bitmap Loader
		BitmapLoader bitmapLoader = new BitmapLoader();

		// Companenets
		tvNameSurname = (TextView) llListHeaderUserInfo
				.findViewById(R.id.tvNameProfile);
		tvUsername = (TextView) llListHeaderUserInfo
				.findViewById(R.id.tvUsernameProfile);
		tvPhone = (TextView) llListHeaderUserInfo
				.findViewById(R.id.tvPhoneNumberProfile);
		llUSerInfo = (LinearLayout) llListHeaderUserInfo
				.findViewById(R.id.llUserInfoProfile);
		imgProfile = (RoundedImageView) llListHeaderUserInfo
				.findViewById(R.id.rimgUserImageProfile);
		ibtnScan = (ImageButton) view.findViewById(R.id.ibtnProfileScan);

		lvCompanies = (ListView) view.findViewById(R.id.listProfileCompanies);
		profileCompanyItems = new ArrayList<ProfileCompanyListItem>();
		lvCompanies.addHeaderView(llListHeaderUserInfo);
		lvCompanies.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.image_nar_profile));
		// bitmapLoader.loadBitmap(R.drawable.image_nar_profile, lvCompanies);

		// Load Profile Info From Preferences
		final String imagePath = Constants.fileNames.MAIN_FOLDER
				+ Constants.fileNames.PROFILE_PHOTO;
		imgFile = new File(imagePath);
		if (imgFile.exists()) {

			imgProfile.setImageDrawable(Drawable.createFromPath(imagePath));

		}

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_list_company)
				.showImageForEmptyUri(R.drawable.icon_list_company)
				.showImageOnFail(R.drawable.icon_list_company)
				.cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();

		imageLoader.init(ImageLoaderConfiguration.createDefault(NeWoApp
				.getInstance()));

		String firstName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.FIRST_NAME);
		String lastName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.LAST_NAME);

		final String userId = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_ID).trim();
		final String password = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PASSWORD).trim();

		final boolean hasActive = APImethods
				.hasActiveInternetConnection(getActivity());

		if ((firstName + lastName).trim().length() != 0)
			tvNameSurname.setText(firstName + " " + lastName);

		String phoneNumber = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PHONE_NUMBER);
		if (phoneNumber.trim().length() != 0)
			tvPhone.setText(phoneNumber);

		String username = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_NAME);
		if (username.trim().length() != 0)
			tvUsername.setText("@" + username);

		// Profile Image on Click listner
		imgProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (imgFile.exists()) {

					zoomImageFromThumb(imgProfile,
							Drawable.createFromPath(imagePath));

				} else {
					Intent intent = new Intent(getActivity(),
							FillUserInfoActivity.class);
					startActivity(intent);
				}

			}
		});

		companyAdapter = new ProfileCompanyListAdapter(getActivity(),
				profileCompanyItems, imageLoader, options);

		// Set Adapater
		lvCompanies.setAdapter(companyAdapter);

		// duration for animation default from android
		mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		if (hasActive) {
			CompanyInfoTask task = new CompanyInfoTask();
			task.execute(userId, password);
		} else {
			Toast.makeText(getActivity(), R.string.internet_connection_error,
					Toast.LENGTH_LONG).show();
		}

		ibtnScan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getActivity(), ScanActivity.class);
				startActivity(intent);
			}
		});

		return view;
	}

	// --------------------------------------------------------------------------------------------

	class CompanyInfoTask extends AsyncTask<String, Void, String> {

		private Bitmap resultPhoto = null;

		@Override
		protected void onPreExecute() {

			mPullToRefreshLayout.setRefreshing(true);

		}

		@Override
		protected String doInBackground(String... params) {
			// jsonResult = null;
			String wurl = Constants.ConstStrings.serverPath
					+ "UserCompanyListServlet?action=getCompanyInfo";
			// jsonResult = "false";
			// TODO Auto-generated method stub
			String result = null;
			int timeOutLength;
			try {
				timeOutLength = 10000;
			} catch (Exception ex) {
				timeOutLength = 5000;
				if (ex.getMessage() != null)
					Log.w("json", ex.getMessage());
			}
			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(),
					timeOutLength); // Timeout
			HttpConnectionParams.setConnectionTimeout(client.getParams(),
					timeOutLength); // Timeout
			// HttpConnectionParams.setSoTimeout(client.getParams(), 20000);
			// // Timeout
			// Limit
			HttpResponse response;
			JSONObject json = new JSONObject();

			try {
				HttpPost post = new HttpPost(wurl);
				Log.w("json", wurl);
				json.put("userId", params[0]);

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
					try {
						assignRes(result);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					inputstream.close();

					// recvdref.setText(resultstring + "\n\n" +
					// post.toString().getBytes());
					// JSONObject recvdjson = new JSONObject(resultstring);
					// recvdref.setText(recvdjson.toString(2));

				}
			} catch (Exception e) {
				try {
					e.printStackTrace();
					if (e.getMessage() != null) {
						System.out.println(e.getMessage());
						Log.w("json", e.getMessage() + wurl);
					}
				} finally {

				}

			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				JSONObject jObject = new JSONObject(result);

				JSONArray jsonArray = jObject.getJSONArray("userCompanyInfo");

				int arraySize = jsonArray.length();
				profileCompanyItems = new ArrayList<ProfileCompanyListItem>();

				for (int i = 0; i < arraySize; i++) {
					JSONObject companyInfo = jsonArray.getJSONObject(i);

					String companyId = companyInfo.getString("companyId");
					String companyName = companyInfo.getString("companyName");
					String operationDate = companyInfo
							.getString("operationDate");
					String numberOfPoints = companyInfo
							.getString("numberOfPoints");
					String address = companyInfo.getString("address");

					Date lastOperationDate = new Date(
							Long.valueOf(operationDate));
					Date currentDate = new Date();
					// in milliseconds
					long diff = lastOperationDate.getTime();

					System.out.println("difference: " + diff);

					long diffSeconds = diff / 1000;
					long diffMinutes = diff / (60 * 1000);
					long diffHours = diff / (60 * 60 * 1000);
					long diffDays = diff / (24 * 60 * 60 * 1000);

					String diffStr = null;
					if (diffDays > 0)
						diffStr = new SimpleDateFormat("dd-MM-yyyy")
								.format(new Date((new Date()).getTime() - diff));// diffDays
																					// +
																					// " days ago";
					else if (diffHours > 0)
						diffStr = diffHours + " hours ago";
					else if (diffMinutes > 0)
						diffStr = diffMinutes + " minutes ago";
					else if (diffSeconds > 0)
						diffStr = diffSeconds + " seconds ago";

					// Fill the item with some bogus information
					profileCompanyItems
							.add(new ProfileCompanyListItem(companyName,
									address, diffStr, Integer
											.parseInt(companyId), true,
									numberOfPoints));
				}

				imageLoader.clearMemoryCache();

				/*
				 * options = new DisplayImageOptions.Builder()
				 * .showImageOnLoading(R.drawable.ic_stub)
				 * .showImageForEmptyUri(R.drawable.ic_empty)
				 * .showImageOnFail(R.drawable.ic_error)
				 * .cacheInMemory(true).cacheOnDisc(true)
				 * .considerExifParams(true) .displayer(new
				 * RoundedBitmapDisplayer(20)).build();
				 * 
				 * imageLoader.init(ImageLoaderConfiguration.createDefault(NeWoApp
				 * .getInstance()));
				 */

				// Configure Adapter for listView
				companyAdapter = new ProfileCompanyListAdapter(getActivity(),
						profileCompanyItems, imageLoader, options);

				// Set Adapater
				lvCompanies.setAdapter(companyAdapter);
				companyAdapter.notifyDataSetChanged();

				// // Configure Adapter for listView
				// if (profileCompanyItems == null) {
				//
				// lvCompanies.setVisibility(View.GONE);
				// // llEmptyDataset.setVisibility(View.VISIBLE);
				// } else {
				// // llEmptyDataset.setVisibility(View.GONE);
				// lvCompanies.setVisibility(View.VISIBLE);
				// }

			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage() != null)
					Log.w("json", e.getMessage());
			}

			mPullToRefreshLayout.setRefreshComplete();
		}

	}

	private String completeString(String str, int cnt) {
		for (int i = str.length(); i <= cnt; i++) {
			str = str;
		}
		return str;
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

	public synchronized void assignRes(String res) throws JSONException {
		try {
			JSONObject jObject = new JSONObject(res);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null)
				Log.w("json", e.getMessage());
		}
	}

	// Call Scan Fragment from
	// Profile---------------------------------------------------------------------
	public void btnScanOnClick(View view) {
		CardFlipActivity cardflip = new CardFlipActivity();
		cardflip.displayView(1);
	}

	private void zoomImageFromThumb(final View thumbView, Drawable imageRes) {
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		final ImageView expandedImageView = (ImageView) getActivity()
				.findViewById(R.id.expanded_image);
		expandedImageView.setImageDrawable(imageRes);

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the final bounds are the global visible rectangle of the
		// container
		// view. Also set the container view's offset as the origin for the
		// bounds, since that's the origin for the positioning animation
		// properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		getActivity().findViewById(R.id.container).getGlobalVisibleRect(
				finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
				.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
		thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(
				ObjectAnimator.ofFloat(expandedImageView, View.X,
						startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
						startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
						startScale, 1f))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
						startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		final float startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
					getActivity().getActionBar().setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.bitmap_blue_trans));
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their original values.
				AnimatorSet set = new AnimatorSet();
				set.play(
						ObjectAnimator.ofFloat(expandedImageView, View.X,
								startBounds.left))
						.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
								startBounds.top))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});
	}

	// On Resume of Fragment
	public void onResume() {
		// Load Profile Info From Preferences
		final String imagePath = Constants.fileNames.MAIN_FOLDER
				+ Constants.fileNames.PROFILE_PHOTO;
		imgFile = new File(imagePath);
		if (imgFile.exists()) {

			imgProfile.setImageDrawable(Drawable.createFromPath(imagePath));
			// imgProfile.setImageURI(Uri.fromFile(imgFile));

		}
		String firstName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.FIRST_NAME);
		String lastName = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.LAST_NAME);

		if ((firstName + lastName).trim().length() != 0)
			tvNameSurname.setText(firstName + " " + lastName);

		String phoneNumber = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PHONE_NUMBER);
		if (phoneNumber.trim().length() != 0)
			tvPhone.setText(phoneNumber);

		String username = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_NAME);
		if (username.trim().length() != 0)
			tvUsername.setText("@" + username);

		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.profile, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_edit:
			Intent intent = new Intent(getActivity(),
					FillUserInfoActivity.class);
			startActivity(intent);
			return true;

			// case R.id.action_refresh:
			// // Refresh Button
			// return true;
		default:
			return super.onOptionsItemSelected(item);
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

	private void addStrings(int count) {

	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub

		final String userId = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_ID).trim();
		final String password = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PASSWORD).trim();

		new AsyncTask<Void, Void, String>() {

			/*
			 * @Override protected String doInBackground(Void... params) { try {
			 * 
			 * 
			 * Thread.sleep(5000); } catch (InterruptedException e) {
			 * e.printStackTrace(); } return null; }
			 */

			@Override
			protected String doInBackground(Void... params) {
				// jsonResult = null;
				String wurl = Constants.ConstStrings.serverPath
						+ "UserCompanyListServlet?action=getCompanyInfo";
				// jsonResult = "false";
				// TODO Auto-generated method stub
				String result = null;
				int timeOutLength;
				try {
					timeOutLength = 10000;
				} catch (Exception ex) {
					timeOutLength = 5000;
					if (ex.getMessage() != null)
						Log.w("json", ex.getMessage());
				}
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(),
						timeOutLength); // Timeout
				HttpConnectionParams.setConnectionTimeout(client.getParams(),
						timeOutLength); // Timeout
				// HttpConnectionParams.setSoTimeout(client.getParams(), 20000);
				// // Timeout
				// Limit
				HttpResponse response;
				JSONObject json = new JSONObject();

				try {
					HttpPost post = new HttpPost(wurl);
					Log.w("json", wurl);
					json.put("userId", userId);

					json.put("password", password);
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
						InputStream inputstream = response.getEntity()
								.getContent();
						Header contentencoding = response
								.getFirstHeader("Content-Encoding");
						if (contentencoding != null
								&& contentencoding.getValue().equalsIgnoreCase(
										"gzip")) {
							inputstream = new GZIPInputStream(inputstream);
						}

						result = convertStreamToString(inputstream);
						try {
							assignRes(result);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						inputstream.close();

						// recvdref.setText(resultstring + "\n\n" +
						// post.toString().getBytes());
						// JSONObject recvdjson = new JSONObject(resultstring);
						// recvdref.setText(recvdjson.toString(2));

					}
				} catch (Exception e) {
					try {
						e.printStackTrace();
						if (e.getMessage() != null) {
							System.out.println(e.getMessage());
							Log.w("json", e.getMessage() + wurl);
						}
					} finally {

					}

				}

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				try {
					JSONObject jObject = new JSONObject(result);

					JSONArray jsonArray = jObject
							.getJSONArray("userCompanyInfo");

					int arraySize = jsonArray.length();
					profileCompanyItems = new ArrayList<ProfileCompanyListItem>();

					for (int i = 0; i < arraySize; i++) {
						JSONObject companyInfo = jsonArray.getJSONObject(i);

						String companyId = companyInfo.getString("companyId");
						String companyName = companyInfo
								.getString("companyName");
						String operationDate = companyInfo
								.getString("operationDate");
						String numberOfPoints = companyInfo
								.getString("numberOfPoints");
						String address = companyInfo.getString("address");

						Date lastOperationDate = new Date(
								Long.valueOf(operationDate));
						Date currentDate = new Date();

						// in milliseconds
						long diff = lastOperationDate.getTime();

						System.out.println("difference: " + diff);

						long diffSeconds = diff / 1000;
						long diffMinutes = diff / (60 * 1000);
						long diffHours = diff / (60 * 60 * 1000);
						long diffDays = diff / (24 * 60 * 60 * 1000);

						String diffStr = null;
						if (diffDays > 0)
							diffStr = diffDays + " days ago";
						else if (diffHours > 0)
							diffStr = diffHours + " hours ago";
						else if (diffMinutes > 0)
							diffStr = diffMinutes + " minutes ago";
						else if (diffSeconds > 0)
							diffStr = diffSeconds + " seconds ago";

						// Fill the item with some bogus information
						profileCompanyItems.add(new ProfileCompanyListItem(
								companyName, address, diffStr, Integer
										.parseInt(companyId), true,
								numberOfPoints));
					}

					imageLoader.clearMemoryCache();

					// Configure Adapter for listView
					companyAdapter = new ProfileCompanyListAdapter(
							getActivity(), profileCompanyItems, imageLoader,
							options);

					// Set Adapater
					lvCompanies.setAdapter(companyAdapter);
					companyAdapter.notifyDataSetChanged();

				} catch (Exception e) {
					e.printStackTrace();
					if (e.getMessage() != null)
						Log.w("json", e.getMessage());
				}

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();
			}
		}.execute();
	}

}
