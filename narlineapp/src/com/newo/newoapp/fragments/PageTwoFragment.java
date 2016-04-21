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

public class PageTwoFragment extends Fragment {

	ImageView imageIntro2;

	public static Fragment newInstance(Context context) {

		PageTwoFragment f = new PageTwoFragment();

		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_home_page_two, null);

		// Initalize Companennts
		TextView txtHeader = (TextView) root
				.findViewById(R.id.txtPageTwoHeader);
		TextView txtContent = (TextView) root
				.findViewById(R.id.txtPageTwoContent);

		// Initalize ImageViews
		imageIntro2 = (ImageView) root.findViewById(R.id.imgHomePage2);

		// Apply images
		imageIntro2.setImageDrawable(getResources().getDrawable(
				R.drawable.hompage_search));

		return root;
	}
}
