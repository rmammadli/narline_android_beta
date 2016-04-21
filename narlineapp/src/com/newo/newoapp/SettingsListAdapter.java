package com.newo.newoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;
import android.graphics.Typeface;
import com.newo.newoapp.narline.R;

import java.util.ArrayList;

/**
 * Adapter for crop option list.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 */
public class SettingsListAdapter extends ArrayAdapter<SettingsListItem> {
	private ArrayList<SettingsListItem> mOptions;
	private LayoutInflater mInflater;

	public SettingsListAdapter(Context context,
			ArrayList<SettingsListItem> options) {
		super(context, R.layout.settings_list_item, options);

		mOptions = options;

		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.settings_list_item, null);

		SettingsListItem item = mOptions.get(position);

		if (item != null) {
			((TextView) convertView
					.findViewById(R.id.tvSimpleTextListItem_title))
					.setText(item.title);

			return convertView;
		}

		return null;
	}
}