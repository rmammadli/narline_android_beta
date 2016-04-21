package com.newo.newoapp.fragments;

import com.newo.newoapp.narline.R;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PageOneFragment extends Fragment {
	ImageView imageIntro1;

	public static Fragment newInstance(Context context) {
		PageOneFragment f = new PageOneFragment();

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_home_page_one, null);

		// Initalize Companennts
		TextView txtHeader = (TextView) root
				.findViewById(R.id.txtPageOneHeader);
		TextView txtContent = (TextView) root
				.findViewById(R.id.txtPageOneContent);

		// Inatilize ImageViews
		imageIntro1 = (ImageView) root.findViewById(R.id.imgHomePage1);

		// Image Logo download logo
		imageIntro1.setImageDrawable(getResources().getDrawable(
				R.drawable.hompage_scan));
		// Bitmap bmp = ReduceLargeBitmaps.decodeSampledBitmapFromResource(
		// getResources(), R.drawable.magazine);
		// imageIntro1.setImageBitmap(bmp);
		// R.drawable.magazine));

		return root;
	}
}
