package com.newo.newoapp;

import java.util.ArrayList;
import java.util.Random;

import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.narline.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileCompanyListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<ProfileCompanyListItem> profileCompanyItems;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	String reqUrl = Constants.ConstStrings.serverPath
			+ "UserCompanyListServlet?action=getCompanyImage&companyId=";

	public ProfileCompanyListAdapter(Context context,
			ArrayList<ProfileCompanyListItem> profileCompanyItems,
			ImageLoader imageLoader, DisplayImageOptions options) {
		this.context = context;
		this.profileCompanyItems = profileCompanyItems;
		this.imageLoader = imageLoader;
		this.options = options;
	}

	@Override
	public int getCount() {
		return profileCompanyItems.size();
	}

	@Override
	public Object getItem(int position) {
		return profileCompanyItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(
					R.layout.profile_companies_list_item, null);
		}

		// Initalize ViewHolder
		holder = new ViewHolder();

		// Companents
		holder.imgLogo = (ImageView) convertView
				.findViewById(R.id.imgCompanyLogo);
		holder.txtName = (TextView) convertView
				.findViewById(R.id.tvCompanyName);
		holder.txtLocation = (TextView) convertView
				.findViewById(R.id.tvCompanyLocation);
		holder.txtLastUpdateTime = (TextView) convertView
				.findViewById(R.id.tvLastUpdateTime);
		holder.txtPoints = (TextView) convertView.findViewById(R.id.tvPoints);

		imageLoader.displayImage(reqUrl
				+ profileCompanyItems.get(position).getLogo(), holder.imgLogo,
				options);

		AssetManager assets = context.getAssets();

		holder.imgLogo.setImageResource(profileCompanyItems.get(position)
				.getLogo());
		holder.txtName.setText(profileCompanyItems.get(position).getCompany());
		holder.txtLocation.setText(profileCompanyItems.get(position)
				.getLocation());
		holder.txtLastUpdateTime.setText(profileCompanyItems.get(position)
				.getLastUpdateTime());

		// displaying count
		// check whether it set visible or not
		if (profileCompanyItems.get(position).getCounterVisibility()) {
			holder.txtPoints.setText(profileCompanyItems.get(position)
					.getPoints());
		} else {
			// hide the counter view
			holder.txtPoints.setVisibility(View.GONE);
		}

		return convertView;
	}

	private static class ViewHolder {
		public TextView txtName;
		public TextView txtLocation;
		public TextView txtLastUpdateTime;
		public TextView txtPoints;
		public ImageView imgLogo;
	}

}
