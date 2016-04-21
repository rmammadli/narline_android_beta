package com.newo.newoapp.fragments;

import com.newo.newoapp.narline.R;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PageFourFragment extends Fragment {

	Button btnGetStarted;
	ImageView imageIntro4;

	public static Fragment newInstance(Context context) {

		PageFourFragment f = new PageFourFragment();
		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_home_page_four, null);

		// Initalize TextViews
		TextView txtHeader = (TextView) root
				.findViewById(R.id.txtPageFourHeader);
		TextView txtContent = (TextView) root
				.findViewById(R.id.txtPageFourContent);

		// Initalize imageViews
		imageIntro4 = (ImageView) root.findViewById(R.id.imgHomePage4);

		// Apply images
		imageIntro4.setImageDrawable(getResources().getDrawable(
				R.drawable.hompage_share));

		return root;
	}

}
