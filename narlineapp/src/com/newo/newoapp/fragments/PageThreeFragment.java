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

public class PageThreeFragment extends Fragment {

	Button btnGetStarted;
	ImageView imageIntro3;

	public static Fragment newInstance(Context context) {

		PageThreeFragment f = new PageThreeFragment();
		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_home_page_three, null);

		// initalize companents

		// Initalize TextViews
		TextView txtHeader = (TextView) root
				.findViewById(R.id.txtPageThreeHeader);
		TextView txtContent = (TextView) root
				.findViewById(R.id.txtPageThreeContent);

		// Initalize imageViews
		imageIntro3 = (ImageView) root.findViewById(R.id.imgHomePage3);

		// Apply images
		imageIntro3.setImageDrawable(getResources().getDrawable(
				R.drawable.hompage_point));

		return root;
	}

}
