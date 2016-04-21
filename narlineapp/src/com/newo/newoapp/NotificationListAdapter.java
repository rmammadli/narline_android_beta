package com.newo.newoapp;

import java.io.File;
import java.util.ArrayList;

import com.newo.newoapp.domain.Constants;
import com.newo.newoapp.narline.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<NotificationListItem> notificationListItems;

	public NotificationListAdapter(Context context,
			ArrayList<NotificationListItem> notificationListItems) {
		this.context = context;
		this.notificationListItems = notificationListItems;
	}

	@Override
	public int getCount() {
		return notificationListItems.size();
	}

	@Override
	public Object getItem(int position) {
		return notificationListItems.get(position);
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
			convertView = mInflater.inflate(R.layout.notification_list_item,
					null);
		}

		// Initalize ViewHolder
		holder = new ViewHolder();

		// Companents
		holder.imgIconFirst = (RoundedImageView) convertView
				.findViewById(R.id.icon_notification_first);
		holder.imgIconSecond = (ImageView) convertView
				.findViewById(R.id.icon_notification_second);
		holder.tvText = (TextView) convertView
				.findViewById(R.id.tvtext_notification);
		holder.tvTime = (TextView) convertView.findViewById(R.id.tvtext_time);

		AssetManager assets = context.getAssets();

		// holder.imgIconFirst.setImageResource(notificationListItems
		// .get(position).getIconFirst());

		final String imagePath = Constants.fileNames.MAIN_FOLDER + "company_"
				+ notificationListItems.get(position).getIconFirst() + ".jpg";
		File imgFile = new File(imagePath);
		if (imgFile.exists()) {

			Drawable dr = Drawable.createFromPath(imagePath);
			holder.imgIconFirst.setImageDrawable(dr);
			// imgProfile.setImageURI(Uri.fromFile(imgFile));

		}

		holder.tvText.setText(notificationListItems.get(position).getText());
		holder.tvTime.setText(notificationListItems.get(position).getTime());

		// displaying count
		// check whether it set visible or not
		if (notificationListItems.get(position).getIconSecondVisibility()) {
			holder.imgIconSecond.setImageResource(notificationListItems.get(
					position).getIconSecond());
		} else {
			// hide the counter view
			holder.imgIconSecond.setVisibility(View.GONE);
		}

		return convertView;
	}

	private static class ViewHolder {
		public TextView tvText;
		public TextView tvTime;
		public RoundedImageView imgIconFirst;
		public ImageView imgIconSecond;
	}

}
