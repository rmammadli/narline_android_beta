package com.newo.newoapp.fragments;

import com.newo.newoapp.narline.R;

import android.content.Context;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class IntroFragment extends Fragment implements AnimationListener {

	private static final int SPLASH_DISPLAY_TIME = 3000; /* 3 seconds */

	private ImageView imgLogo;
	private TextView txtSlogan;
	private Animation animFadeIn;
	private Animation animZoomIn;
	private Animation animBounce;
	private Bitmap bmp;
	private Bitmap bmp1;

	public static Fragment newInstance(Context context) {

		IntroFragment f = new IntroFragment();
		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_intro,
				null);

		// Initalize companents
		imgLogo = (ImageView) root.findViewById(R.id.imgSplashIcon);
		txtSlogan = (TextView) root.findViewById(R.id.txtSplashSlogan);

		// load the animations
		animFadeIn = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.fade_in);
		animZoomIn = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.zoom_in);

		animBounce = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.bounce);

		// set animation listener
		animFadeIn.setAnimationListener(this);

		new Handler().postDelayed(new Runnable() {

			public void run() {

				imgLogo.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.logo_narmobile));
				// start anim
				txtSlogan.setVisibility(View.VISIBLE);
				txtSlogan.startAnimation(animFadeIn);
				imgLogo.startAnimation(animBounce);
			}
		}, 800);

		return root;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

}
