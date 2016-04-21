package com.newo.newoapp.activities;

import com.newo.newoapp.narline.R;

import java.io.InputStream;
import java.security.acl.LastOwnerException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
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

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.newo.newoapp.APImethods;
import com.newo.newoapp.activities.WebViewActivity;
import com.newo.newoapp.domain.Constants;

import com.newo.newoapp.helper.BitmapLoader;
import com.newo.newoapp.helper.DebugLog;
import com.newo.newoapp.recognition.QCARSampleGLView;
import com.newo.newoapp.recognition.Texture;

import com.newo.newoapp.recognition.templates.videoplayback.VideoPlaybackRenderer;
import com.newo.newoapp.recognition.templates.videoplayback.VideoPlayerHelper;

import com.newo.newoapp.recognition.templates.videoplayback.VideoPlayerHelper.MEDIA_STATE;
import com.newo.newoapp.recognition.templates.videoplayback.VideoPlayerHelper.MEDIA_TYPE;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.qualcomm.QCAR.QCAR;

public class ScanActivity extends Activity {

	private InitCloudRecoTask mInitCloudRecoTask;
	private ProgressBar progress;
	private TextView tvProgressValue;
	// private ImageView imgTapHand;
	private Button mStartScanButton;
	private ImageButton imgHandle;
	private LinearLayout llSlidingDrawerBack;
	private CheckBox btnLight;

	LinearLayout llTabh = null;

	private View mLoadingDialogContainer;

	// Constants for Hiding/Showing Loading dialog
	static final int HIDE_LOADING_DIALOG = 0;
	static final int SHOW_LOADING_DIALOG = 1;

	// Helpers to detect events such as double tapping:
	private GestureDetector mGestureDetector = null;
	private SimpleOnGestureListener mSimpleListener = null;

	// Pointer to the current activity:
	private Activity mCurrentActivity = null;

	// Movie for the Targets:
	public static final int NUM_TARGETS = 2;
	public static final int STONES = 0;
	public static final int CHIPS = 1;
	private VideoPlayerHelper mVideoPlayerHelper[] = null;
	private int mSeekPosition[] = null;
	private boolean mWasPlaying[] = null;
	private String mMovieName[] = null;

	// A boolean to indicate whether we come from full screen:
	private boolean mReturningFromFullScreen = false;

	// Our OpenGL view:
	private QCARSampleGLView mGlView;

	// The StartupScreen view and the start button:
	private View mStartupView = null;
	private ImageView mStartButton = null;
	private boolean mStartScreenShowing = false;

	// The view to display the sample splash screen:
	// private ImageView mSplashScreenView;

	// The handler and runnable for the splash screen time out task:
	private Handler mSplashScreenHandler;
	private Runnable mSplashScreenRunnable;

	// The minimum time the splash screen should be visible:
	private static final long MIN_SPLASH_SCREEN_TIME = 2000;

	// The time when the splash screen has become visible:
	private static long mSplashScreenStartTime = 0;

	// Our renderer:
	private VideoPlaybackRenderer mRenderer;

	// Display size of the device:
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;

	// The current application status:
	private int mAppStatus = Constants.AppStatus.APPSTATUS_UNINITED;

	// The async tasks to initialize the QCAR SDK:
	private InitQCARTask mInitQCARTask;
	private LoadTrackerTask mLoadTrackerTask;

	// An object used for synchronizing QCAR initialization, dataset loading and
	// the Android onDestroy() life cycle event. If the application is destroyed
	// while a data set is still being loaded, then we wait for the loading
	// operation to finish before shutting down QCAR.
	private Object mShutdownLock = new Object();

	// QCAR initialization flags:
	private int mQCARFlags = 0;

	// The textures we will use for rendering:
	private Vector<Texture> mTextures;
	private int mSplashScreenImageResource = 0;

	// Slider state
	private boolean sliderState = false;
	// Current focus mode:
	private int mFocusMode;

	private boolean mFlash = false;
	private static Context mContext;

	private Handler repeatUpdateHandler = new Handler();
	private LinearLayout llAwardlayout;
	private TextView tvLikes;
	private TextView tvPoints;
	private TextView tvCompany;
	private int mCurrentPoint = 0;
	private int mPoint = 0;
	private String social_button_status = null;
	private boolean social_sharing_status = false;
	private boolean award_frame_isShowing = false;
	private ProgressBar progressPoints;
	private LinearLayout llFooter;
	private LinearLayout llStatistics;
	private LinearLayout llLikes;
	private ToggleButton toogleLike;
	private ToggleButton btnShare;
	private ActionBar actionBar;
	private String targetId = null;
	String targetName = null;

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	protected static final String TAG = ScanActivity.class.getName();

	/** Static initializer block to load native libraries on start-up. */
	static {
		loadLibrary(Constants.NativeLibNames.NATIVE_LIB_QCAR);
		loadLibrary(Constants.NativeLibNames.NATIVE_LIB_SAMPLE);
	}

	// ******************************************************************************
	// private Camera mCamera;
	// private CameraPreview mPreview;
	// Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean cameraview = false;
	LayoutInflater inflater = null;
	View mainView = null;
	ImageView imgScanBack;

	private String facebookLink = "https://www.facebook.com/narmobile";;
	private String twitterLink = "https://twitter.com/Nar_Mobile";
	private String webPageLink = "https://www.narmobile.az/";
	private String videoUrl = null;

	private int awardFrameLikeCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actionBar = getActionBar();
		// Load any sample specific textures:
		mTextures = new Vector<Texture>();
		loadTextures();

		// Query the QCAR initialization flags:
		mQCARFlags = getInitializationFlags();

		// Update the application status to start initializing application
		updateApplicationStatus(Constants.AppStatus.APPSTATUS_INIT_APP);

		// Create the gesture detector that will handle the single and
		// double taps:
		mSimpleListener = new SimpleOnGestureListener();
		mGestureDetector = new GestureDetector(this.getApplicationContext(),
				mSimpleListener);

		mVideoPlayerHelper = new VideoPlayerHelper[NUM_TARGETS];
		mSeekPosition = new int[NUM_TARGETS];
		mWasPlaying = new boolean[NUM_TARGETS];
		mMovieName = new String[NUM_TARGETS];

		// Create the video player helper that handles the playback of the movie
		// for the targets:
		for (int i = 0; i < NUM_TARGETS; i++) {
			mVideoPlayerHelper[i] = new VideoPlayerHelper(ScanActivity.this);
			mVideoPlayerHelper[i].init();
			mVideoPlayerHelper[i].setActivity(this);
		}

		mMovieName[STONES] = "VuforiaSizzleReel_1.m4v";
		// mMovieName[CHIPS] = "VuforiaSizzleReel_2.m4v";
		mMovieName[CHIPS] = "http://r7---sn-u0g3uxax3vh-nv46.c.youtube.com/videoplayback?ipbits=8&fexp=923435%2C916904%2C904829%2C900371%2C916623%2C929117%2C929121%2C929906%2C929907%2C929922%2C929127%2C929129%2C929131%2C929930%2C936403%2C925726%2C925720%2C925722%2C925718%2C929917%2C906945%2C929933%2C920302%2C913428%2C920605%2C919811%2C913563%2C904830%2C919373%2C930803%2C904122%2C938701%2C936308%2C909549%2C900816%2C912711%2C904494%2C904497%2C900375%2C936312%2C906001&itag=18&key=yt1&ip=88.255.147.83&ratebypass=yes&lowtc=yes&sver=3&mt=1378094916&id=de3bf5466d81542f&cp=U0hWTVFMVl9ITUNONl9JRlpEOnBjejFPLUtJU1c1&ms=au&sparams=cp%2Cid%2Cip%2Cipbits%2Citag%2Clowtc%2Cratebypass%2Csource%2Cupn%2Cexpire&expire=1378118333&source=youtube&upn=-kHachs6e2Y&mv=m&signature=D93E5DE444A068485AB9761A9B541386B2995FD6.4B5027571D50FD216E303B46C54C2580787AFDC2&title=Morra+-+One+Love+%28Radio+Edit%29";
		mCurrentActivity = this;

		// Set the double tap listener:
		mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

			/** Handle the double tap */
			@Override
			public boolean onDoubleTap(MotionEvent e) {

				// Do not react if the StartupScreen is being displayed:
				if (mStartScreenShowing)
					return false;

				for (int i = 0; i < NUM_TARGETS; i++) {
					// Verify that the tap happens inside the target:
					if (isTapOnScreenInsideTarget(i, e.getX(), e.getY())) {
						// Check whether we can play full screen at all:
						if (mVideoPlayerHelper[i].isPlayableFullscreen()) {

							// Pause all other media:
							pauseAll(i);

							// Request the playback in fullscreen:
							mVideoPlayerHelper[i].play(true,
									VideoPlayerHelper.CURRENT_POSITION);

						}

						// Even though multiple videos can be loaded only one
						// can be playing at any point in time. This break
						// prevents that, say, overlapping videos trigger
						// simultaneously playback.
						break;
					}
				}

				return true;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				// We do not react to this event
				return false;
			}

			/** Handle the single tap */
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				// Do not react if the StartupScreen is being displayed
				if (mStartScreenShowing)
					return false;

				// openWebSite("Salam");

				for (int i = 0; i < NUM_TARGETS; i++) {
					// Verify that the tap happened inside the target
					if (isTapOnScreenInsideTarget(i, e.getX(), e.getY())) {

						// Check if it is playable on texture
						if (mVideoPlayerHelper[i].isPlayableOnTexture()) {
							// We can play only if the movie was paused, ready
							// or stopped
							if ((mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PAUSED)
									|| (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.READY)
									|| (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.STOPPED)
									|| (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.REACHED_END)) {
								// Pause all other media
								pauseAll(i);

								// If it has reached the end then rewind
								if ((mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.REACHED_END))
									mSeekPosition[i] = 0;

								mVideoPlayerHelper[i].play(false,
										mSeekPosition[i]);
								mSeekPosition[i] = VideoPlayerHelper.CURRENT_POSITION;
							} else if (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING) {
								// If it is playing then we pause it
								mVideoPlayerHelper[i].pause();
							}
						} else if (mVideoPlayerHelper[i].isPlayableFullscreen()) {
							// If it isn't playable on texture
							// Either because it wasn't requested or because it
							// isn't supported then request playback fullscreen.
							mVideoPlayerHelper[i].play(true,
									VideoPlayerHelper.CURRENT_POSITION);
						}

						// Even though multiple videos can be loaded only one
						// can be playing at any point in time. This break
						// prevents that, say, overlapping videos trigger
						// simultaneously playback.
						break;
					}
				}

				return true;
			}
		});

		overridePendingTransition(R.anim.right_to_left,
				R.anim.activity_close_scale);

		setContentView(R.layout.fragment_card_front);

		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.trans_bitmap));
		actionBar.setLogo(R.drawable.logo_narmobile);
		actionBar.setDisplayHomeAsUpEnabled(true);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
	}

	// Listner-------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return (super.onOptionsItemSelected(menuItem));
	}

	/*
	 * // Back press //
	 * listener--------------------------------------------------
	 * ----------------------
	 * 
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * ((keyCode == KeyEvent.KEYCODE_BACK)) { onBackPressed(); } return true; }
	 */

	// ***************************************************************************************

	/** An async task to initialize cloud-based recognition asynchronously. */
	private class InitCloudRecoTask extends AsyncTask<Void, Integer, Boolean> {
		// Initialize with invalid value
		private int mInitResult = -1;

		protected Boolean doInBackground(Void... params) {
			// Prevent the onDestroy() method to overlap:
			synchronized (mShutdownLock) {
				// Init cloud-based recognition:
				mInitResult = initCloudReco();
				return mInitResult == Constants.CloudReco.INIT_SUCCESS;
			}
		}

		protected void onPostExecute(Boolean result) {

			if (result) {
				// Done loading the tracker, update application status:
				updateApplicationStatus(Constants.AppStatus.APPSTATUS_INITED);

				// Hides the Loading Dialog
				// loadingDialogHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);

				// mUILayout.setBackgroundColor(Color.TRANSPARENT);
			} else {
				// Create dialog box for display error:
				AlertDialog dialogError = new AlertDialog.Builder(
						ScanActivity.this).create();
				dialogError.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Exiting application
								// System.exit(1);
							}
						});

				// Show dialog box with error message:
				String logMessage = "Bilinməyən xəta.";

				// NOTE: Check if initialization failed because the device is
				// not supported. At this point the user should be informed
				// with a message.
				if (mInitResult == Constants.CloudReco.INIT_ERROR_NO_NETWORK_CONNECTION)
					logMessage = // "Failed to initialize NeWoApp because "
					"İnternet bağlantısı yoxdur.";
				else if (mInitResult == Constants.CloudReco.INIT_ERROR_SERVICE_NOT_AVAILABLE)
					logMessage = "Servis aktiv deyil.";

				dialogError.setMessage(logMessage);
				dialogError.show();
			}

			// enterScanningModeNative();
		}
	}

	// private Handler loadingDialogHandler = new LoadingDialogHandler(this);

	/** An async task to initialize QCAR asynchronously. */
	private class InitQCARTask extends AsyncTask<Void, Integer, Boolean> {
		// Initialize with invalid value:
		private int mProgressValue = -1;

		protected Boolean doInBackground(Void... params) {

			// Prevent the onDestroy() method to overlap with initialization:
			synchronized (mShutdownLock) {
				//QCAR.setInitParameters(ScanActivity.this, mQCARFlags);
				QCAR.setInitParameters(ScanActivity.this, mQCARFlags,"83f340bf42e24085b08b90c3967d314a");

				do {
					// QCAR.init() blocks until an initialization step is
					// complete, then it proceeds to the next step and reports
					// progress in percents (0 ... 100%).
					// If QCAR.init() returns -1, it indicates an error.
					// Initialization is done when progress has reached 100%.
					mProgressValue = QCAR.init();

					// Publish the progress value:
					publishProgress(mProgressValue);

					// We check whether the task has been canceled in the
					// meantime (by calling AsyncTask.cancel(true))
					// and bail out if it has, thus stopping this thread.
					// This is necessary as the AsyncTask will run to completion
					// regardless of the status of the component that
					// started is.
				} while (!isCancelled() && mProgressValue >= 0
						&& mProgressValue < 100);

				return (mProgressValue > 0);
			}
		}

		protected void onProgressUpdate(Integer... values) {
			// Do something with the progress value "values[0]", e.g. update
			// splash screen, progress bar, etc.
		}

		protected void onPostExecute(Boolean result) {
			// Done initializing QCAR, proceed to next application
			// initialization status:
			if (result) {

				updateApplicationStatus(Constants.AppStatus.APPSTATUS_INIT_TRACKER);
			} else {
				// Create dialog box for display error:
				final AlertDialog dialogError = new AlertDialog.Builder(
						ScanActivity.this).create();
				dialogError.setButton(DialogInterface.BUTTON_POSITIVE, "Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Exiting application:
								// System.exit(1);
								dialogError.dismiss();

							}
						});

				String logMessage;

				// NOTE: Check if initialization failed because the device is
				// not supported. At this point the user should be informed
				// with a message.
				if (mProgressValue == QCAR.INIT_DEVICE_NOT_SUPPORTED) {
					logMessage = "Cihaz bu funksiyanı dəstəkləmir.";
				} else {
					logMessage = "Başlanğıcda xəta.";
				}

				// Log error:
				DebugLog.LOGE("InitQCARTask::onPostExecute: " + logMessage
						+ " Exiting.");

				// Show dialog box with error message:
				dialogError.setMessage(logMessage);
				dialogError.show();
			}
		}
	}

	/** An async task to load the tracker data asynchronously. */
	private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean> {
		protected Boolean doInBackground(Void... params) {
			// Prevent the onDestroy() method to overlap:
			synchronized (mShutdownLock) {
				// Load the tracker data set:
				return (loadTrackerData() > 0);
			}
		}

		protected void onPostExecute(Boolean result) {

			if (result) {
				// Done loading the tracker, update application status:
				updateApplicationStatus(Constants.AppStatus.APPSTATUS_INIT_CLOUDRECO);
			} else {
				// Create dialog box for display error:
				final AlertDialog dialogError = new AlertDialog.Builder(
				// VideoPlayback.this
						ScanActivity.this).create();

				dialogError.setButton(DialogInterface.BUTTON_POSITIVE, "Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Exiting application
								System.exit(1);
							}
						});

				// Show dialog box with error message:
				dialogError.setMessage("Failed to load tracker data.");
				dialogError.show();
			}
		}
	}

	private void storeScreenDimensions() {
		// Query display dimensions
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
	}

	// ************************************************************************************

	/**
	 * We want to load specific textures from the APK, which we will later use
	 * for rendering.
	 */
	private void loadTextures() {
		mTextures.add(Texture.loadTextureFromApk("VuforiaSizzleReel_1.png",
				this.getAssets()));
		mTextures.add(Texture.loadTextureFromApk("video_backround.png",
				this.getAssets()));
		mTextures.add(Texture.loadTextureFromApk("play.png", this.getAssets()));
		mTextures.add(Texture.loadTextureFromApk("busy.png", this.getAssets()));
		mTextures
				.add(Texture.loadTextureFromApk("error.png", this.getAssets()));
		mTextures.add(Texture.loadTextureFromApk("",
				this.getAssets()));
		mTextures.add(Texture.loadTextureFromApk("",
				this.getAssets()));
		mTextures.add(Texture.loadTextureFromApk("",
				this.getAssets()));

		/*
		 * for(Texture t : mTextures) { System.out.println("imageSize X: " +
		 * t.mWidth + " Y: " + t.mHeight); }
		 */
	}

	/** Configure QCAR with the desired version of OpenGL ES. */
	private int getInitializationFlags() {
		return QCAR.GL_20;
	}

	/**
	 * Generates a texture for the book data fecthing the book info from the
	 * specified book URL
	 */

	boolean isTextureCreated = false;

	public void openWebSite(int augmentedButtonCode) {

		if (augmentedButtonCode == Constants.AugmentedButtonCodes.WEBSITE) {

			Intent intent = new Intent(this, WebViewActivity.class);
			intent.setData(Uri.parse(webPageLink));
			startActivity(intent);

		} else if (augmentedButtonCode == Constants.AugmentedButtonCodes.FACEBOOK) {

			Intent intent = new Intent(this, WebViewActivity.class);
			intent.setData(Uri.parse(facebookLink));
			startActivity(intent);

		} else if (augmentedButtonCode == Constants.AugmentedButtonCodes.TWITTER) {

			Intent intent = new Intent(this, WebViewActivity.class);
			intent.setData(Uri.parse(twitterLink));
			startActivity(intent);

		}
	}

	public void createProductTexture(String bookJSONUrl) {
		
		Log.d("videoLink", "videoLink: " + bookJSONUrl );
		
		isTextureCreated = false;
		// gets book url from parameters
		String mBookJSONUrl = bookJSONUrl.trim();

		/*
		 * // Cleans old texture reference if necessary if (mBookDataTexture !=
		 * null) { mBookDataTexture = null;
		 * 
		 * System.gc(); }
		 */

		// Searches for the book data in an AsyncTask

		String userId = APImethods.loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_ID);
		String versionCode = String.valueOf(APImethods.getVersionCode());

		GetBookDataTask mGetBookDataTask = new GetBookDataTask();
		mGetBookDataTask.execute(userId, mBookJSONUrl, versionCode);

		/*
		 * while(!isTextureCreated) {
		 * 
		 * }
		 */
	}

	/** Gets the book data from a JSON Object */
	private class GetBookDataTask extends AsyncTask<String, Void, String> {
		private String mBookDataJSONFullUrl;
		private static final String CHARSET = "UTF-8";

		protected void onPreExecute() {
			// mIsLoadingBookData = true;

			for (int i = 0; i < NUM_TARGETS; i++) {
				mVideoPlayerHelper[i].unload();
			}

			// Initialize the current book full url to search
			// for the data
			StringBuilder sBuilder = new StringBuilder();
			// sBuilder.append(mServerURL);
			// sBuilder.append(mBookJSONUrl);

			mBookDataJSONFullUrl = sBuilder.toString();

			mBookDataJSONFullUrl = "http://newoapp.jelastic.dogado.eu/newomobileapp/targetInfo";

			// Shows the loading dialog
			// loadingDialogHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);

		}

		protected String doInBackground(String... params) {
			// HttpURLConnection connection = null;

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
						+ "targetInfo";

				HttpPost post = new HttpPost(reqUrl);
				json.put("userId", params[0]);
				json.put("targetId", params[1]);
				json.put("versionCode", params[2]);

				targetId = params[1];

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

					result = APImethods.convertStreamToString(inputstream);

					inputstream.close();

					// recvdref.setText(resultstring + "\n\n" +
					// post.toString().getBytes());
					// JSONObject recvdjson = new JSONObject(resultstring);
					// recvdref.setText(recvdjson.toString(2));

				}

				/*
				 * 
				 * // Cleans any old reference to mBookData if (mBookData !=
				 * null) { mBookData = null;
				 * 
				 * }
				 * 
				 * JSONObject jsonObject = new JSONObject(builder.toString());
				 * 
				 * // Generates a new Book Object with the JSON object data
				 * mBookData = new Book();
				 * 
				 * mBookData.setTitle(jsonObject.getString("title"));
				 * mBookData.setAuthor(jsonObject.getString("author"));
				 * mBookData.setBookUrl(jsonObject.getString("bookurl"));
				 * mBookData.setPriceList(jsonObject.getString("list price"));
				 * mBookData.setPriceYour(jsonObject.getString("your price"));
				 * mBookData
				 * .setRatingAvg(jsonObject.getString("average rating"));
				 * mBookData
				 * .setRatingTotal(jsonObject.getString("# of ratings"));
				 * 
				 * // Gets the book thumb image byte[] thumb =
				 * downloadImage(jsonObject.getString("thumburl"));
				 * 
				 * if (thumb != null) {
				 * 
				 * Bitmap bitmap = BitmapFactory.decodeByteArray(thumb, 0,
				 * thumb.length); mBookData.setThumb(bitmap); }
				 */

			} catch (Exception e) {
			} finally {
				// connection.disconnect();
			}

			return result;
		}

		protected void onProgressUpdate(Void... values) {

		}

		protected void onPostExecute(String result) {

			
			Log.d("videoLink " , "videoLink " + result);
			
			try {

				JSONObject jsonObject = new JSONObject(result);

				// int numberOfPoints = jsonObject.getInt("numberOfPoints");

				String companyName = jsonObject.getString("companyName");

				targetName = jsonObject.getString("targetName");

				// APImethods.savePrefs(Constants.PrefNames.USER_INFO,
				// Constants.PrefKeys.USER_TOTAL_POINTS,
				// String.valueOf(numberOfPoints));

				int currentPoints = jsonObject.getInt("currentPoints");
				int userTargetLike = jsonObject.getInt("userTargetLike");
				int userTargetLikeCount = jsonObject
						.getInt("userTargetLikeCount");

				APImethods.savePrefs(Constants.PrefNames.USER_INFO,
						Constants.PrefKeys.USER_CURRENT_POINTS,
						String.valueOf(currentPoints));

				String targetInfo = jsonObject.getString("targetInfo");

				// Toast.makeText(
				// this,
				// "currentPoints " + currentPoints + " /n numberOfPoints"
				// + numberOfPoints, Toast.LENGTH_SHORT).show();

				// System.out.println("currentPoints " + currentPoints
				// + " /n numberOfPoints" + numberOfPoints);

				jsonObject = new JSONObject(targetInfo);

				// System.out.println("jsonReult: " + result);

				int targetInfoTypeId = jsonObject.getInt("targetInfoTypeId");
				String targetInfoValue = jsonObject
						.getString("targetInfoValue");

				jsonObject = new JSONObject(targetInfoValue);

				videoUrl = jsonObject.getString("videoUrl");
				
				Log.d("videoUrl", "videoUrl: " + videoUrl + "\n  targetInfoTypeId: " + targetInfoTypeId);

				String facebookLink = null;
				String twitterLink = null;
				String webPageLink = null;

				if (jsonObject.has("facebookLink"))
					facebookLink = jsonObject.getString("facebookLink");
				if (jsonObject.has("twitterLink"))
					twitterLink = jsonObject.getString("twitterLink");
				if (jsonObject.has("webPageLink"))
					webPageLink = jsonObject.getString("webPageLink");

				initTargetWebInfo(facebookLink, twitterLink, webPageLink);

				for (int i = 0; i < NUM_TARGETS; i++) {
					mMovieName[i] = videoUrl;

					mVideoPlayerHelper[i].unload();
					mRenderer.setVideoPlayerHelper(i, mVideoPlayerHelper[i]);
					mRenderer.requestLoad(i, mMovieName[i], 0, false);
					mVideoPlayerHelper[i].load(mMovieName[i],
							MEDIA_TYPE.ON_TEXTURE_FULLSCREEN, false, 0);
					mVideoPlayerHelper[i].setQcTargetId(targetId);
					mVideoPlayerHelper[i].setWasPointAdded(false);
				}

				isTextureCreated = true;

				// showAwardFrame(String.valueOf(currentPoints), companyName,
				// targetId, userTargetLike, userTargetLikeCount);

			} catch (Exception ex) {

			}
		}
	}

	public void addPoints(String qcTargetId) {

		String userId = APImethods.loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_ID);
		String versionCode = String.valueOf(APImethods.getVersionCode());

		AddPointsForScanTask addPointsTask = new AddPointsForScanTask();
		addPointsTask.execute(userId, qcTargetId, versionCode);
	}

	/** Gets the book data from a JSON Object */
	private class AddPointsForScanTask extends AsyncTask<String, Void, String> {
		private String mBookDataJSONFullUrl;
		private static final String CHARSET = "UTF-8";
		private String qcTargetId = null;

		protected void onPreExecute() {
			// mIsLoadingBookData = true;

			// Initialize the current book full url to search
			// for the data
			StringBuilder sBuilder = new StringBuilder();
			// sBuilder.append(mServerURL);
			// sBuilder.append(mBookJSONUrl);

			mBookDataJSONFullUrl = sBuilder.toString();

			mBookDataJSONFullUrl = "http://newoapp.jelastic.dogado.eu/newomobileapp/targetInfo";

			// Shows the loading dialog
			// loadingDialogHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
		}

		protected String doInBackground(String... params) {
			// HttpURLConnection connection = null;

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
						+ "AddPointsServlet?action=Scan";

				HttpPost post = new HttpPost(reqUrl);
				json.put("userId", params[0]);
				json.put("targetId", params[1]);
				json.put("versionCode", params[2]);

				qcTargetId = params[1];

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

					result = APImethods.convertStreamToString(inputstream);

					inputstream.close();

				}

			} catch (Exception e) {
			} finally {
				// connection.disconnect();
			}

			return result;
		}

		protected void onProgressUpdate(Void... values) {

		}

		protected void onPostExecute(String result) {

			try {

				JSONObject jsonObject = new JSONObject(result);

				// int numberOfPoints = jsonObject.getInt("numberOfPoints");

				String companyName = jsonObject.getString("companyName");

				// APImethods.savePrefs(Constants.PrefNames.USER_INFO,
				// Constants.PrefKeys.USER_TOTAL_POINTS,
				// String.valueOf(numberOfPoints));

				int currentPoints = jsonObject.getInt("currentPoints");
				int targetId = jsonObject.getInt("targetId");

				int userTargetLike = jsonObject.getInt("userTargetLike");
				int userTargetLikeCount = jsonObject
						.getInt("userTargetLikeCount");

				APImethods.savePrefs(Constants.PrefNames.USER_INFO,
						Constants.PrefKeys.USER_CURRENT_POINTS,
						String.valueOf(currentPoints));

				String targetInfo = jsonObject.getString("targetInfo");

				jsonObject = new JSONObject(targetInfo);

				// System.out.println("jsonReult: " + result);

				int targetInfoTypeId = jsonObject.getInt("targetInfoTypeId");
				String targetInfoValue = jsonObject
						.getString("targetInfoValue");

				jsonObject = new JSONObject(targetInfoValue);

				videoUrl = jsonObject.getString("videoUrl");

				String facebookLink = null;
				String twitterLink = null;
				String webPageLink = null;

				if (jsonObject.has("facebookLink"))
					facebookLink = jsonObject.getString("facebookLink");
				if (jsonObject.has("twitterLink"))
					twitterLink = jsonObject.getString("twitterLink");
				if (jsonObject.has("webPageLink"))
					webPageLink = jsonObject.getString("webPageLink");

				initTargetWebInfo(facebookLink, twitterLink, webPageLink);

				/*
				 * for (int i = 0; i < NUM_TARGETS; i++) { mMovieName[i] =
				 * videoUrl; mVideoPlayerHelper[i].unload();
				 * mRenderer.setVideoPlayerHelper(i, mVideoPlayerHelper[i]);
				 * mRenderer.requestLoad(i, mMovieName[i], 0, false);
				 * mVideoPlayerHelper[i].load(mMovieName[i],
				 * MEDIA_TYPE.ON_TEXTURE_FULLSCREEN, false, 0);
				 * //mVideoPlayerHelper[i].setQcTargetId(targetId); }
				 */

				// isTextureCreated = true;

				showAwardFrame(String.valueOf(currentPoints), companyName,
						qcTargetId, userTargetLike, userTargetLikeCount);

			} catch (Exception ex) {

			}
		}
	}

	/** Gets the book data from a JSON Object */
	private class AddPointsForShareTask extends AsyncTask<String, Void, String> {

		protected void onPreExecute() {

		}

		protected String doInBackground(String... params) {
			// HttpURLConnection connection = null;

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
						+ "AddPointsServlet?action=Share";

				HttpPost post = new HttpPost(reqUrl);
				json.put("userId", params[0]);
				json.put("targetId", params[1]);
				json.put("versionCode", params[2]);

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

					result = APImethods.convertStreamToString(inputstream);

					inputstream.close();

				}

			} catch (Exception e) {
			} finally {
				// connection.disconnect();
			}

			return result;
		}

		protected void onProgressUpdate(Void... values) {

		}

		protected void onPostExecute(String result) {

			try {

				JSONObject jsonObject = new JSONObject(result);

				// int numberOfPoints = jsonObject.getInt("numberOfPoints");

				String companyName = jsonObject.getString("result");

			} catch (Exception ex) {

			}
		}
	}

	class UserOperationTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

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
						+ "UserOperationServlet?action=userTargetLike";

				HttpPost post = new HttpPost(reqUrl);
				json.put("userId", params[0]);
				json.put("password", params[1]);
				json.put("targetId", params[2]);
				json.put("operationType", params[3]);

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

					result = APImethods.convertStreamToString(inputstream);

					inputstream.close();

				}
			} catch (Exception e) {
				result = "0";
				e.printStackTrace();
				if (e.getMessage() != null)
					Log.w("json", e.getMessage());

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

			}
		}
	}

	public void initTargetWebInfo(String facebookLink, String twitterLink,
			String webPageLink) {

		System.out.println("initTargetWebInfo");

		if (facebookLink != null)
			this.facebookLink = facebookLink;
		else
			this.facebookLink = "https://www.facebook.com/newoapp";
		if (twitterLink != null)
			this.twitterLink = twitterLink;
		else
			this.twitterLink = "https://twitter.com/NeWo_Azerbaijan";
		if (webPageLink != null)
			this.webPageLink = webPageLink;
		else
			this.webPageLink = "http://www.newo.co";

	}

	public native int initCloudReco();

	public native void deinitCloudReco();

	public native void enterScanningModeNative();

	public native void stopScanningModeNative();

	public native boolean activateFlash(boolean flash);

	/** Native tracker initialization and deinitialization. */
	public native int initTracker();

	public native void deinitTracker();

	/** Native functions to load and destroy tracking data. */
	public native int loadTrackerData();

	public native void destroyTrackerData();

	/** Native sample initialization. */
	public native void onQCARInitializedNative();

	/** Native methods for starting and stopping the camera. */
	private native void startCamera();

	private native void stopCamera();

	/**
	 * Native method for setting / updating the projection matrix for AR content
	 * rendering
	 */
	private native void setProjectionMatrix();

	private native boolean isTapOnScreenInsideTarget(int target, float x,
			float y);

	/** Called when the activity will start interacting with the user. */
	public void onResume() {
		super.onResume();

		actionBar.setLogo(R.drawable.logo_narmobile);
		// QCAR-specific resume operation
		QCAR.onResume();

		// We may start the camera only if the QCAR SDK has already been
		// initialized:
		if (mAppStatus == Constants.AppStatus.APPSTATUS_CAMERA_STOPPED) {
			updateApplicationStatus(Constants.AppStatus.APPSTATUS_CAMERA_RUNNING);
			// Reactivate flash if it was active before pausing the app
			if (mFlash) {
				boolean result = activateFlash(mFlash);
			}
		}

		// setupStartScreen();

		// Setup the start button:
		setupStartButton();

		// Resume the GL view:
		if (mGlView != null) {
			mGlView.setVisibility(View.VISIBLE);
			mGlView.onResume();
		}

		// updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);

		// Do not show the startup screen if we're returning from full screen:
		if (!mReturningFromFullScreen)
			showStartupScreen();

		// Reload all the movies
		if (mRenderer != null) {
			for (int i = 0; i < NUM_TARGETS; i++) {
				if (!mReturningFromFullScreen) {
					mRenderer.requestLoad(i, mMovieName[i], mSeekPosition[i],
							false);
				} else {
					mRenderer.requestLoad(i, mMovieName[i], mSeekPosition[i],
							mWasPlaying[i]);
				}
			}
		}

		mReturningFromFullScreen = false;
	}

	/** Called when returning from the full screen player */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == android.app.Activity.RESULT_OK) {
				// The following values are used to indicate the position in
				// which the video was being played and whether it was being
				// played or not:
				String movieBeingPlayed = data.getStringExtra("movieName");
				mReturningFromFullScreen = true;

				// Find the movie that was being played full screen
				for (int i = 0; i < NUM_TARGETS; i++) {
					if (movieBeingPlayed.compareTo(mMovieName[i]) == 0) {
						mSeekPosition[i] = data.getIntExtra(
								"currentSeekPosition", 0);
						mWasPlaying[i] = data.getBooleanExtra("playing", false);
					}
				}

			}
		}
	}

	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);

		storeScreenDimensions();

		// Set projection matrix:
		if (QCAR.isInitialized()
				&& (mAppStatus == Constants.AppStatus.APPSTATUS_CAMERA_RUNNING))
			setProjectionMatrix();
	}

	/** Called when the system is about to start resuming a previous activity. */
	public void onPause() {
		super.onPause();

		// closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.left_to_right);

		if (award_frame_isShowing) {

			hideAwardFrame();
		}

		if (mStartupView != null) {
			mStartupView.setVisibility(View.GONE);
			mStartupView = null;

		}

		if (mGlView != null) {
			mGlView.setVisibility(View.INVISIBLE);
			mGlView.onPause();
		}

		// if (imgTapHand != null)
		// imgTapHand = null;
		if (mStartButton != null)
			mStartButton = null;
		if (imgScanBack != null)
			imgScanBack = null;

		if (mAppStatus == Constants.AppStatus.APPSTATUS_CAMERA_RUNNING) {
			updateApplicationStatus(Constants.AppStatus.APPSTATUS_CAMERA_STOPPED);
		}

		// Store the playback state of the movies and unload them:
		for (int i = 0; i < NUM_TARGETS; i++) {
			// If the activity is paused we need to store the position in which
			// this was currently playing:
			if (mVideoPlayerHelper[i].isPlayableOnTexture()) {
				mSeekPosition[i] = mVideoPlayerHelper[i].getCurrentPosition();
				mWasPlaying[i] = mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING ? true
						: false;
			}

			// We also need to release the resources used by the helper, though
			// we don't need to destroy it:
			if (mVideoPlayerHelper[i] != null)
				mVideoPlayerHelper[i].unload();
		}

		// Hide the Startup View:
		hideStartupScreen();

		mReturningFromFullScreen = false;

		// Disable flash when paused
		if (mFlash) {
			mFlash = false;
			activateFlash(mFlash);
		}

		// QCAR-specific pause operation:
		QCAR.onPause();
	}

	/** Native function to deinitialize the application. */
	private native void deinitApplicationNative();

	/** The final call you receive before your activity is destroyed. */
	public void onDestroy() {

		// Deinit code for Cloud Reco service
		if (mInitCloudRecoTask != null
				&& mInitCloudRecoTask.getStatus() != InitCloudRecoTask.Status.FINISHED) {
			mInitCloudRecoTask.cancel(true);
			mInitCloudRecoTask = null;
		}

		super.onDestroy();

		for (int i = 0; i < NUM_TARGETS; i++) {
			// If the activity is destroyed we need to release all resources:
			if (mVideoPlayerHelper[i] != null)
				mVideoPlayerHelper[i].deinit();
			mVideoPlayerHelper[i] = null;
		}

		// Dismiss the splash screen time out handler:
		if (mSplashScreenHandler != null) {
			mSplashScreenHandler.removeCallbacks(mSplashScreenRunnable);
			mSplashScreenRunnable = null;
			mSplashScreenHandler = null;
		}

		// Cancel potentially running tasks:
		if (mInitQCARTask != null
				&& mInitQCARTask.getStatus() != InitQCARTask.Status.FINISHED) {
			mInitQCARTask.cancel(true);
			mInitQCARTask = null;
		}

		if (mLoadTrackerTask != null
				&& mLoadTrackerTask.getStatus() != LoadTrackerTask.Status.FINISHED) {
			mLoadTrackerTask.cancel(true);
			mLoadTrackerTask = null;
		}

		// Ensure that all asynchronous operations to initialize QCAR and
		// loading the tracker datasets do not overlap:
		synchronized (mShutdownLock) {

			// Deinit Cloud Reco
			deinitCloudReco();

			// Do application deinitialization in native code:
			deinitApplicationNative();

			// Unload texture:
			mTextures.clear();
			mTextures = null;

			// Destroy the tracking data set:
			destroyTrackerData();

			// Deinit the tracker:
			deinitTracker();

			// Deinitialize QCAR SDK:
			QCAR.deinit();
		}

		System.gc();
	}

	/**
	 * NOTE: this method is synchronized because of a potential concurrent
	 * access by VideoPlayback::onResume() and InitQCARTask::onPostExecute().
	 */
	private synchronized void updateApplicationStatus(int appStatus) {
		// Exit if there is no change in status:
		if (mAppStatus == appStatus)
			return;

		// Store new status value:
		mAppStatus = appStatus;

		// Execute application state-specific actions:
		switch (mAppStatus) {
		case Constants.AppStatus.APPSTATUS_INIT_APP:
			// Initialize application elements that do not rely on QCAR
			// initialization:
			initApplication();

			// Proceed to next application initialization status:
			updateApplicationStatus(Constants.AppStatus.APPSTATUS_INIT_QCAR);
			break;

		case Constants.AppStatus.APPSTATUS_INIT_QCAR:
			// Initialize QCAR SDK asynchronously to avoid blocking the
			// main (UI) thread.
			//
			// NOTE: This task instance must be created and invoked on the
			// UI thread and it can be executed only once!
			try {
				mInitQCARTask = new InitQCARTask();
				mInitQCARTask.execute();
			} catch (Exception e) {
			}
			break;

		case Constants.AppStatus.APPSTATUS_INIT_TRACKER:
			// Initialize the ImageTracker:
			if (initTracker() > 0) {
				// Proceed to next application initialization status:
				updateApplicationStatus(Constants.AppStatus.APPSTATUS_INIT_APP_AR);
			}
			break;

		case Constants.AppStatus.APPSTATUS_INIT_APP_AR:
			// Initialize Augmented Reality-specific application elements
			// that may rely on the fact that the QCAR SDK has been
			// already initialized:
			initApplicationAR();

			// Proceed to next application initialization status:
			updateApplicationStatus(Constants.AppStatus.APPSTATUS_LOAD_TRACKER);
			break;

		case Constants.AppStatus.APPSTATUS_LOAD_TRACKER:
			// Load the tracking data set:
			//
			// NOTE: This task instance must be created and invoked on the
			// UI thread and it can be executed only once!
			try {
				mLoadTrackerTask = new LoadTrackerTask();
				mLoadTrackerTask.execute();
			} catch (Exception e) {
			}
			break;

		case Constants.AppStatus.APPSTATUS_INIT_CLOUDRECO:
			try {
				mInitCloudRecoTask = new InitCloudRecoTask();
				mInitCloudRecoTask.execute();
			} catch (Exception e) {
			}
			break;

		case Constants.AppStatus.APPSTATUS_INITED:
			// Hint to the virtual machine that it would be a good time to
			// run the garbage collector.
			//
			// NOTE: This is only a hint. There is no guarantee that the
			// garbage collector will actually be run.
			System.gc();

			// Native post initialization:
			onQCARInitializedNative();

			// The elapsed time since the splash screen was visible:
			long splashScreenTime = System.currentTimeMillis()
					- mSplashScreenStartTime;
			long newSplashScreenTime = 0;
			if (splashScreenTime < MIN_SPLASH_SCREEN_TIME) {
				newSplashScreenTime = MIN_SPLASH_SCREEN_TIME - splashScreenTime;
			}

			// Request a callback function after a given timeout to dismiss
			// the splash screen:
			mSplashScreenHandler = new Handler();
			mSplashScreenRunnable = new Runnable() {
				public void run() {
					// Hide the splash screen:
					// mSplashScreenView.setVisibility(View.INVISIBLE);

					// Activate the renderer:
					mRenderer.mIsActive = true;

					// Now add the GL surface view. It is important
					// that the OpenGL ES surface view gets added
					// BEFORE the camera is started and video
					// background is configured.
					ScanActivity.this.addContentView(mGlView, new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT));

					// Setup the start screen:
					// setupStartScreen();

					// Setup the start button:
					setupStartButton();

					// Start the camera:
					updateApplicationStatus(Constants.AppStatus.APPSTATUS_CAMERA_RUNNING);

					// Show the startup screen
					showStartupScreen();

				}
			};
			// enterScanningModeNative();
			mSplashScreenHandler.postDelayed(mSplashScreenRunnable,
					newSplashScreenTime);
			break;

		case Constants.AppStatus.APPSTATUS_CAMERA_STOPPED:
			// Call the native function to stop the camera:
			stopCamera();
			break;

		case Constants.AppStatus.APPSTATUS_CAMERA_RUNNING:
			// Call the native function to start the camera:
			mSplashScreenHandler = new Handler();
			mSplashScreenRunnable = new Runnable() {
				public void run() {
					setupStartScreen();
				}
			};
			mSplashScreenHandler.postDelayed(mSplashScreenRunnable, 20);

			// setupStartScreen();
			startCamera();
			setProjectionMatrix();

			// Set continuous auto-focus if supported by the device,
			// otherwise default back to regular auto-focus mode.
			mFocusMode = Constants.FocusMode.FOCUS_MODE_CONTINUOUS_AUTO;
			if (!setFocusMode(Constants.FocusMode.FOCUS_MODE_CONTINUOUS_AUTO)) {
				mFocusMode = Constants.FocusMode.FOCUS_MODE_NORMAL;
				setFocusMode(Constants.FocusMode.FOCUS_MODE_NORMAL);
			}

			break;

		default:
			throw new RuntimeException("Invalid application state");
		}
	}

	/**
	 * This call sets the start screen up, adds it to the view and pads the text
	 * to something nice
	 */
	private void setupStartScreen() {

		if (mStartupView == null) {

			// Inflate the view from the xml file:
			mStartupView = this.getLayoutInflater().inflate(
					R.layout.fragment_card_front, null);

			// Add it to the content view:
			this.addContentView(mStartupView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			btnLight = (CheckBox) mStartupView.findViewById(R.id.switchLight);

			btnLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						boolean result = activateFlash(true);
					} else {
						boolean result = activateFlash(false);
					}

				}
			});

			imgScanBack = (ImageView) mStartupView
					.findViewById(R.id.imageScanBack);

			BitmapLoader bitmapLoader = new BitmapLoader();
			bitmapLoader.loadBitmap(R.drawable.image_scan_frame, imgScanBack);

			// Initalize companents
			TextView textSwitchLight = (TextView) mStartupView
					.findViewById(R.id.txtScannerLight);
			TextView tvTap = (TextView) mStartupView
					.findViewById(R.id.txtTapToScan);
			progress = (ProgressBar) mStartupView.findViewById(R.id.progress);
			tvProgressValue = (TextView) mStartupView
					.findViewById(R.id.tvScanPageProgress);
			progress.setVisibility(View.INVISIBLE);
			llSlidingDrawerBack = (LinearLayout) mStartupView
					.findViewById(R.id.llSlidingDrawerContent);

			// Sliding drawer set back
			bitmapLoader.loadBitmap(R.drawable.back_small, llSlidingDrawerBack);

			// Set Value of TextViews
			tvTap.setText(this.getResources().getString(R.string.tap_to_scan));

			// Sliding Drawer
			SlidingDrawer sMenu = (SlidingDrawer) mStartupView
					.findViewById(R.id.slidingDrawer);
			final ImageButton btnHandle = (ImageButton) mStartupView
					.findViewById(R.id.imgbtnHandle);
			setupAwardFrame();

			sMenu.setOnDrawerOpenListener(new OnDrawerOpenListener() {

				@Override
				public void onDrawerOpened() {
					btnHandle
							.setBackgroundResource(R.drawable.icon_nar_arrow_down);
					sliderState = true;
				}
			});

			sMenu.setOnDrawerCloseListener(new OnDrawerCloseListener() {

				public void onDrawerClosed() {
					btnHandle
							.setBackgroundResource(R.drawable.icon_nar_arrow_up);
					sliderState = true;
				}
			});

		}

		mStartScreenShowing = true;

	}

	/** This call sets the start button variable up */
	private void setupStartButton() {
		/*
		 * mStartButton = (ImageView) findViewById(R.id.start_button);
		 * 
		 * if (mStartButton != null) { // Setup a click listener that hides the
		 * StartupScreen: mStartButton.setOnClickListener(new
		 * ImageView.OnClickListener() { public void onClick(View arg0) {
		 * hideStartupScreen(); enterScanningModeNative(); } }); }
		 */

		// Inflate the view from the xml file:
		llTabh = (LinearLayout) findViewById(R.id.llTapHandle);

		if (llTabh != null) {
			// Setup a click listener that hides the StartupScreen:
			llTabh.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					if (mStartScreenShowing) {
						hideStartupScreen();
						enterScanningModeNative();
						llTabh.setClickable(false);
						// showStartupScreen();

					}

				}
			});

		}

	}

	/** Show the startup screen */
	private void showStartupScreen() {
		if (mStartupView != null) {
			/*
			 * mGlView.setVisibility(View.INVISIBLE);
			 * mStartupView.setVisibility(View.VISIBLE); mStartScreenShowing =
			 * true;
			 */
			if (award_frame_isShowing) {
				hideAwardFrame();
			}

			actionBar.setLogo(getResources().getDrawable(
					R.drawable.logo_narmobile));
			stopScanningModeNative();
			mGlView.setVisibility(View.VISIBLE);
			mStartupView.setVisibility(View.VISIBLE);
			// mStartupView.setVisibility(View.INVISIBLE);
			mStartScreenShowing = true;
			// //////////////////////////////////////////////
			mStartupView.setBackgroundColor(Color.TRANSPARENT);
			mStartupView.bringToFront();
			// ((CardFlipActivity) getActivity()).getSlidingMenu()
			// .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}
	}

	/** Hide the startup screen */
	private void hideStartupScreen() {
		if (mStartupView != null) {
			mGlView.setVisibility(View.VISIBLE);
			mStartupView.setVisibility(View.INVISIBLE);
			mStartScreenShowing = false;
			actionBar.setLogo(getResources().getDrawable(
					R.drawable.icon_cancel_white));

		}
	}

	/**
	 * Pause all movies except one if the value of 'except' is -1 then do a
	 * blanket pause
	 */
	private void pauseAll(int except) {
		// And pause all the playing videos:
		for (int i = 0; i < NUM_TARGETS; i++) {
			// We can make one exception to the pause all calls:
			if (i != except) {
				// Check if the video is playable on texture
				if (mVideoPlayerHelper[i] != null
						&& mVideoPlayerHelper[i].isPlayableOnTexture()) {
					// If it is playing then we pause it
					mVideoPlayerHelper[i].pause();
				}
			}
		}
	}

	/** Do not exit immediately and instead show the startup screen */
	@Override
	public void onBackPressed() {

		// If this is the first time the back button is pressed
		// show the StartupScreen and pause all media:
		if (!mStartScreenShowing) {
			// Show the startup screen:
			showStartupScreen();

			llTabh.setClickable(true);

			pauseAll(-1);
		} else // if this is the second time the user pressed the back button
		{
			// And exit:
			super.onBackPressed();

		}
	}

	public boolean getmStartScreenShowing() {
		return mStartScreenShowing;
	}

	/** Tells native code whether we are in portait or landscape mode */
	private native void setActivityPortraitMode(boolean isPortrait);

	/** Initialize application GUI elements that are not related to AR. */
	private void initApplication() {
		// Set the screen orientation:
		// int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

		// Apply screen orientation:
		this.setRequestedOrientation(screenOrientation);

		// Pass on screen orientation info to native code:
		setActivityPortraitMode(screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		storeScreenDimensions();

		// As long as this window is visible to the user, keep the device's
		// screen turned on and bright:
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Create and add the splash screen view:
		/*
		 * mSplashScreenView = new ImageView(this);
		 * mSplashScreenView.setImageResource(mSplashScreenImageResource);
		 * addContentView(mSplashScreenView, new LayoutParams(
		 * LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		 * 
		 * mSplashScreenStartTime = System.currentTimeMillis();
		 */

	}

	/** Native function to initialize the application. */
	private native void initApplicationNative(int width, int height);

	/** Initializes AR application components. */
	private void initApplicationAR() {
		// Do application initialization in native code (e.g. registering
		// callbacks, etc.):
		initApplicationNative(mScreenWidth, mScreenHeight);

		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = QCAR.requiresAlpha();

		mGlView = new QCARSampleGLView(this);
		mGlView.init(mQCARFlags, translucent, depthSize, stencilSize);

		mRenderer = new VideoPlaybackRenderer();

		// The renderer comes has the OpenGL context, thus, loading to texture
		// must happen when the surface has been created. This means that we
		// can't load the movie from this thread (GUI) but instead we must
		// tell the GL thread to load it once the surface has been created.
		for (int i = 0; i < NUM_TARGETS; i++) {
			mRenderer.setVideoPlayerHelper(i, mVideoPlayerHelper[i]);
			mRenderer.requestLoad(i, mMovieName[i], 0, false);
		}

		mGlView.setRenderer(mRenderer);

		// Add button for Cloud Reco:
		/*
		 * mStartScanButton = new Button(this);
		 * mStartScanButton.setText("Start Scanning");
		 * 
		 * mStartScanButton.setOnClickListener(new View.OnClickListener() {
		 * public void onClick(View v) { enterScanningModeNative();
		 * mStartScanButton.setVisibility(View.GONE); } });
		 */

		// mUILayout.addView(mStartScanButton,
		// new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));
	}

	public void showStartScanButton() {
		/*
		 * this.runOnUiThread(new Runnable() { public void run() { if
		 * (mStartScanButton != null)
		 * mStartScanButton.setVisibility(View.VISIBLE); } });
		 */
	}

	/**
	 * Invoked every time before the options menu gets displayed to give the
	 * Activity a chance to populate its Menu with menu items.
	 */
	/*
	 * public boolean onPrepareOptionsMenu(Menu menu) {
	 * super.onPrepareOptionsMenu(menu);
	 * 
	 * menu.clear();
	 * 
	 * if(mFocusMode == FOCUS_MODE_CONTINUOUS_AUTO)
	 * menu.add(MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS); else
	 * menu.add(MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS);
	 * 
	 * menu.add(MENU_ITEM_TRIGGER_AUTO_FOCUS);
	 * 
	 * return true; }
	 */

	private native boolean autofocus();

	private native boolean setFocusMode(int mode);

	/** Returns the number of registered textures. */
	public int getTextureCount() {
		return mTextures.size();
	}

	/** Returns the texture object at the specified index. */
	public Texture getTexture(int i) {
		return mTextures.elementAt(i);
	}

	/** A helper for loading native libraries stored in "libs/armeabi*". */
	public static boolean loadLibrary(String nLibName) {
		try {
			System.loadLibrary(nLibName);
			return true;
		} catch (UnsatisfiedLinkError ulee) {
		} catch (SecurityException se) {
		}

		return false;
	}

	/**
	 * We do not handle the touch event here, we just forward it to the gesture
	 * detector
	 */

	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	// Award Frame
	// --------------------------------------------------------------------------------------------------------------------------------
	private void setupAwardFrame() {

		// Inflate the view from the xml file:
		View mAwardView = this.getLayoutInflater().inflate(
				R.layout.layout_scan_award_frame, null);

		// Add it to the content view:
		this.addContentView(mAwardView, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mAwardView.setClickable(false);

		// Add it to the content view:
		llAwardlayout = (LinearLayout) mAwardView
				.findViewById(R.id.llScanPageAward);
		tvPoints = (TextView) mAwardView.findViewById(R.id.tvScanPagePoint);
		tvCompany = (TextView) mAwardView.findViewById(R.id.tvScanPageCompany);
		llFooter = (LinearLayout) mAwardView
				.findViewById(R.id.llAwardFrameFooter);
		progressPoints = (ProgressBar) mAwardView
				.findViewById(R.id.progressAwardFrame);
		llStatistics = (LinearLayout) mAwardView
				.findViewById(R.id.llAwardFrameStatics);
		llLikes = (LinearLayout) mAwardView
				.findViewById(R.id.llAwardFrameLikes);
		tvLikes = (TextView) mAwardView.findViewById(R.id.tvAwardFrameLikes);
		toogleLike = (ToggleButton) mAwardView
				.findViewById(R.id.toggleAwardFrameLike);
		btnShare = (ToggleButton) mAwardView
				.findViewById(R.id.btnAwardFrameShare);

	}

	private void showAwardFrame(String point, String company,
			final String targetId, int userTargetLike,
			final int userTargetLikeCount) {

		// animation
		final Animation slideUp = AnimationUtils.loadAnimation(this,
				R.anim.slide_up_scan);
		tvCompany.setText(company);
		mPoint = Integer.parseInt(point);

		awardFrameLikeCount = userTargetLikeCount;
		award_frame_isShowing = true;

		new Handler().postDelayed(new Runnable() {

			public void run() {

				llAwardlayout.setVisibility(View.VISIBLE);
				llAwardlayout.setAnimation(slideUp);
			}
		}, 400);

		repeatUpdateHandler.post(new RptUpdater());

		if (userTargetLike == 1)
			toogleLike.setChecked(true);
		else
			toogleLike.setChecked(false);

		// Like Button listner
		// ---------------------------------------------------------------------------------------------------------------
		toogleLike
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						String userId = loadPrefs(
								Constants.PrefNames.USER_INFO,
								Constants.PrefKeys.USER_ID);
						String password = loadPrefs(
								Constants.PrefNames.USER_INFO,
								Constants.PrefKeys.PASSWORD);
						// TODO Auto-generated method stub
						if (isChecked) {
							System.out.println("toogleLike");
							UserOperationTask userOperationTask = new UserOperationTask();
							userOperationTask.execute(
									userId,
									password,
									targetId,
									String.valueOf(Constants.targetLikeOperation.LIKE));

							// Set likes
							awardFrameLikeCount = awardFrameLikeCount + 1;
							tvLikes.setText(String.valueOf(awardFrameLikeCount)
									+ " faves");
						} else {
							System.out.println("toogleUnLike");
							UserOperationTask userOperationTask = new UserOperationTask();
							userOperationTask.execute(
									userId,
									password,
									targetId,
									String.valueOf(Constants.targetLikeOperation.DISLIKE));

							// Set likes
							awardFrameLikeCount = awardFrameLikeCount - 1;
							tvLikes.setText(String.valueOf(awardFrameLikeCount)
									+ " faves");

						}
					}
				});
		tvLikes.setText(String.valueOf(awardFrameLikeCount) + " faves");
		// Restart Button
		// Handler----------------------------------------------------------------------------------------------------------------
		btnShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					if (loadPrefs(Constants.PrefNames.SOCIAL_STATUS,
							Constants.PrefKeys.SOCIAL_BUTTON_SCAN_SHARE).trim()
							.equals("1")) {
						publishStory();
					} else {
						alertSocialDialog();
						btnShare.setChecked(false);
					}
				} else {

				}

			}
		});

	}

	class RptUpdater implements Runnable {
		public void run() {

			if (mCurrentPoint < mPoint) {
				increment();
				repeatUpdateHandler.postDelayed(new RptUpdater(), 500);
			}
			// else {
			//
			// final Animation zoomOut = AnimationUtils.loadAnimation(
			// ScanActivity.this, R.anim.zoom_out);
			// // llAwardlayout.setAnimation(zoomOut);
			// new Handler().postDelayed(new Runnable() {
			//
			// public void run() {
			// // llAwardlayout.clearAnimation();
			// // llAwardlayout.setVisibility(View.GONE);
			// // mCurrentPoint = 0;
			// // social_sharing_status = true;
			//
			// }
			// }, 4000);

			// }

		}
	}

	public void increment() {
		mCurrentPoint++;
		tvPoints.setText("" + mCurrentPoint);
	}

	public void hideAwardFrame() {

		llAwardlayout.clearAnimation();
		llAwardlayout.setVisibility(View.GONE);
		mCurrentPoint = 0;
		award_frame_isShowing = false;

	}

	private void alertSocialDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
		// builder.setIcon(R.drawable.ok);
		// Set a message
		final TextView input = new TextView(this);
		// AboutDialog abd = new AboutDialog(this);
		input.setTextColor(getResources().getColor(R.color.d_gray));
		input.setText("Zəhmət olmasa “Sosial Şəbəkələr” bölməsinə daxil olaraq tətbiqi Facebook-a qoşun.");
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

	// Publish Story on
	// Facebook-------------------------------------------------------------------------------
	public void publishStory() {
		Session session = Session.getActiveSession();

		if (session == null) {
			// try to restore from cache
			session = Session.openActiveSessionFromCache(ScanActivity.this);
		}

		if (session != null) {

			String shareUrl = videoUrl;
			shareUrl = videoUrl.substring(videoUrl.lastIndexOf("/"), videoUrl.lastIndexOf("/") + 10);
			shareUrl = new StringBuilder().append("https://vimeo.com/").append(shareUrl).toString();			
			targetName = targetName.substring(0, targetName.lastIndexOf("_"));

			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();
			postParams.putString("name", targetName);
			postParams.putString("caption", "Əyləncənin yeni yolu.");
			postParams
					.putString(
							"description",
							"NarLine tətbiqi ilə videolar izləyin, xallar toplayın və Nar Mobile-dan dəyərli hədiyyələr qazanın. #nar #narline ");
			postParams.putString("link", shareUrl);
			// postParams.putString("picture",
			// "http://www.youtube.com/watch?v=--wqYPKAFEg");

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject()
							.getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i(TAG, "JSON error " + e.getMessage());
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(
								ScanActivity.this.getApplicationContext(),
								error.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(
								ScanActivity.this.getApplicationContext(),
								"Facebook da paylaşıldı...", Toast.LENGTH_LONG).show();
						String userId = APImethods.loadPrefs(
								Constants.PrefNames.USER_INFO,
								Constants.PrefKeys.USER_ID);
						String versionCode = String.valueOf(APImethods
								.getVersionCode());

						AddPointsForShareTask addPointsTask = new AddPointsForShareTask();
						addPointsTask.execute(userId, targetId, versionCode);

						btnShare.setChecked(true);
						btnShare.setClickable(false);
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams,
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	private void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = this.getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private String loadPrefs(String prefName, String key) {
		SharedPreferences sp = this.getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

}
