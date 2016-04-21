package com.newo.newoapp.fragments;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.newo.newoapp.narline.R;
import com.newo.newoapp.RaitingListAdapter;
import com.newo.newoapp.RaitingListItem;
import com.newo.newoapp.activities.CardFlipActivity;
import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.helper.DebugLog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RaitingFragment extends Fragment implements OnRefreshListener {

	private PullToRefreshLayout mPullToRefreshLayout;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	private ListView listRaiting;
	private RaitingListAdapter listAdapter;
	private ArrayList<RaitingListItem> raitingListItems;

	protected static final String TAG = RaitingFragment.class.getName();

	private String userId;
	private String password;

	public static Fragment newInstance(Context context) {

		RaitingFragment f = new RaitingFragment();
		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		view = inflater.inflate(R.layout.fragment_raiting, container, false);

		// Now give the find the PullToRefreshLayout and set it up
		mPullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.pullRaitingFragment);
		ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);
		mPullToRefreshAttacher = ((CardFlipActivity) getActivity())
				.getPullToRefreshAttacher();

		// Get
		// Companents----------------------------------------------------------------------------
		listRaiting = (ListView) view.findViewById(R.id.listRaiting);

		// Create List Items as
		// Arraylist-------------------------------------------------------------------------
		raitingListItems = new ArrayList<RaitingListItem>();

		// Setup List
		// Adapter--------------------------------------------------------------------------------------------
		listAdapter = new RaitingListAdapter(getActivity(), raitingListItems);

		// Set Adapter of
		// ListView-------------------------------------------------------------------------------
		listRaiting.setAdapter(listAdapter);

		userId = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.USER_ID);
		password = loadPrefs(Constants.PrefNames.USER_INFO,
				Constants.PrefKeys.PASSWORD);

		LoginTask task = new LoginTask();
		task.execute(userId, password);

		return view;
	}

	@Override
	public void onRefreshStarted(View view) {

		DebugLog.LOGD("onRefreshStarted");
		// TODO Auto-generated method stub

		LoginTask task = new LoginTask();
		task.execute(userId, password);

	}

	class LoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			mPullToRefreshLayout.setRefreshing(true);
		}

		@Override
		protected String doInBackground(String... params) {
			// jsonResult = null;

			// jsonResult = "false";
			// TODO Auto-generated method stub
			String result = null;
			int timeOutLength = 10000;

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

				String wurl = Constants.ConstStrings.serverPath
						+ "GetTopUsers?action=userRating";

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

				JSONArray jsonArray = jObject.getJSONArray("userRating");

				int arraySize = jsonArray.length();

				raitingListItems = new ArrayList<RaitingListItem>();

				for (int i = 0; i < arraySize; i++) {

					JSONObject topUser = jsonArray.getJSONObject(i);

					String profileImage = null;
					byte[] profileImageByte = null;
					Bitmap loadedImage = null;
					try {

						profileImage = topUser.getString("profileImage");
						if (profileImage.trim().length() > 0) {
							profileImageByte = Base64.decode(profileImage,
									Base64.URL_SAFE);
							loadedImage = BitmapFactory.decodeByteArray(
									profileImageByte, 0,
									profileImageByte.length);
						}

						// DebugLog.LOGD("bitmapppp");

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (profileImage == null || profileImage.length() == 0) {
						raitingListItems.add(new RaitingListItem(topUser
								.getString("userName"), getResources()
								.getIdentifier("image_test_azercell",
										"drawable", "com.newo.newoapp"),
								topUser.getString("userPoints")));
					} else {

						raitingListItems.add(new RaitingListItem(topUser
								.getString("userName"), loadedImage, topUser
								.getString("userPoints")));
					}

				}

				// Create List Items as
				// Arraylist-------------------------------------------------------------------------

				// Setup List
				// Adapter--------------------------------------------------------------------------------------------
				listAdapter = new RaitingListAdapter(getActivity(),
						raitingListItems);

				// Set Adapter of
				// ListView-------------------------------------------------------------------------------
				listRaiting.setAdapter(listAdapter);

			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage() != null)
					Log.w("json", e.getMessage());
			}
			mPullToRefreshLayout.setRefreshComplete();
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

	private String loadPrefs(String prefName, String key) {
		SharedPreferences sp = getActivity().getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

}
