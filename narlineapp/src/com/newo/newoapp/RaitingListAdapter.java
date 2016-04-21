package com.newo.newoapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.narline.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RaitingListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<RaitingListItem> raitingListItems;

	public RaitingListAdapter(Context context,
			ArrayList<RaitingListItem> raitingListItems) {
		this.context = context;
		this.raitingListItems = raitingListItems;
	}

	@Override
	public int getCount() {
		return raitingListItems.size();
	}

	@Override
	public Object getItem(int position) {
		return raitingListItems.get(position);
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
			convertView = mInflater.inflate(R.layout.raiting_list_item, null);
		}

		// Initalize ViewHolder
		holder = new ViewHolder();

		// Companents
		holder.imgIcon = (ImageView) convertView
				.findViewById(R.id.icon_raiting_user);
		holder.txtTitle = (TextView) convertView
				.findViewById(R.id.tv_raiting_name);
		holder.txtCount = (TextView) convertView
				.findViewById(R.id.tv_raiting_points);

		AssetManager assets = context.getAssets();

		if (raitingListItems.get(position).getIconBmp() != null) {
			holder.imgIcon.setImageBitmap(raitingListItems.get(position)
					.getIconBmp());
		} else {
			holder.imgIcon.setImageResource(raitingListItems.get(position)
					.getIcon());
		}

		holder.txtTitle.setText(raitingListItems.get(position).getTitle());
		holder.txtCount.setText(raitingListItems.get(position).getCount());

		return convertView;
	}

	private static class ViewHolder {
		public TextView txtTitle;
		public TextView txtCount;
		public ImageView imgIcon;
	}

}
