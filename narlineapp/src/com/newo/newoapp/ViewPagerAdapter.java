package com.newo.newoapp;

import com.newo.newoapp.*;
import com.newo.newoapp.fragments.IntroFragment;
import com.newo.newoapp.fragments.LoginFragment;
import com.newo.newoapp.fragments.PageFourFragment;
import com.newo.newoapp.fragments.PageOneFragment;
import com.newo.newoapp.fragments.PageThreeFragment;
import com.newo.newoapp.fragments.PageTwoFragment;
import com.newo.newoapp.fragments.RegisterFragment;

import android.content.Context;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	private Context _context;
	public static int totalPage = 7;

	public ViewPagerAdapter(Context context,
			android.support.v4.app.FragmentManager fm) {
		super(fm);
		_context = context;

	}

	@Override
	public android.support.v4.app.Fragment getItem(int position) {
		android.support.v4.app.Fragment f = new android.support.v4.app.Fragment();
		switch (position) {
		case 0:
			f = IntroFragment.newInstance(_context);
			break;
		case 1:
			f = PageOneFragment.newInstance(_context);
			break;
		case 2:
			f = PageTwoFragment.newInstance(_context);
			break;
		case 3:
			f = PageThreeFragment.newInstance(_context);
			break;
		case 4:
			f = PageFourFragment.newInstance(_context);
			break;
		case 5:
			f = RegisterFragment.newInstance(_context);
			break;
		case 6:
			f = LoginFragment.newInstance(_context);
			break;
		}
		return f;
	}

	@Override
	public int getCount() {
		return totalPage;
	}

}
