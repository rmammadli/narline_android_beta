package com.newo.newoapp;

import com.newo.newoapp.fragments.IntroFragment;
import com.newo.newoapp.fragments.LoginFragment;
import com.newo.newoapp.fragments.PageFourFragment;
import com.newo.newoapp.fragments.PageOneFragment;
import com.newo.newoapp.fragments.PageThreeFragment;
import com.newo.newoapp.fragments.PageTwoFragment;
import com.newo.newoapp.fragments.RegisterFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ProductTourAdapter extends FragmentPagerAdapter {
	private Context _context;
	public static int totalPage = 4;

	public ProductTourAdapter(Context context,
			android.support.v4.app.FragmentManager fm) {
		super(fm);
		_context = context;
	}

	@Override
	public android.support.v4.app.Fragment getItem(int position) {
		android.support.v4.app.Fragment f = new android.support.v4.app.Fragment();
		switch (position) {
		case 0:
			f = PageOneFragment.newInstance(_context);
			break;
		case 1:
			f = PageTwoFragment.newInstance(_context);
			break;
		case 2:
			f = PageThreeFragment.newInstance(_context);
			break;
		case 3:
			f = PageFourFragment.newInstance(_context);
			break;
		}
		return f;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return totalPage;
	}

}
