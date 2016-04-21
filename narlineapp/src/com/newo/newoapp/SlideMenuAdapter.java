package com.newo.newoapp;

import java.util.ArrayList;

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
import com.newo.newoapp.narline.R;

public class SlideMenuAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<SlideMenuItem> slideMenuItems;

	public SlideMenuAdapter(Context context,
			ArrayList<SlideMenuItem> slideMenuItems) {
		this.context = context;
		this.slideMenuItems = slideMenuItems;
	}

	@Override
	public int getCount() {
		return slideMenuItems.size();
	}

	@Override
	public Object getItem(int position) {
		return slideMenuItems.get(position);
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
			convertView = mInflater
					.inflate(R.layout.slide_menu_list_item, null);
		}

		// Initalize ViewHolder
		holder = new ViewHolder();

		// Companents
		holder.imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
		holder.txtCount = (TextView) convertView.findViewById(R.id.counter);

		AssetManager assets = context.getAssets();
		holder.imgIcon.setImageResource(slideMenuItems.get(position).getIcon());
		holder.txtTitle.setText(slideMenuItems.get(position).getTitle());

		// displaying count
		// check whether it set visible or not
		if (slideMenuItems.get(position).getCounterVisibility()) {
			holder.txtCount.setText(slideMenuItems.get(position).getCount());
		} else {
			// hide the counter view
			holder.txtCount.setVisibility(View.GONE);
		}

		return convertView;
	}

	private static class ViewHolder {
		public TextView txtTitle;
		public TextView txtCount;
		public ImageView imgIcon;
	}

}
