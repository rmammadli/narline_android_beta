package com.newo.newoapp.fragments;

import com.newo.newoapp.narline.R;
import com.newo.newoapp.activities.ILoveNeWoActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeedbackFragment extends Fragment {

	private TextView tvFeedbackHeader1;
	private TextView tvFeedbackHeader2;
	private TextView tvFeedbackILove;
	private TextView tvFeedbackSend;

	private LinearLayout llFeedback;
	private RelativeLayout rlbtnILove;
	private RelativeLayout rlbtnFeedback;

	public static Fragment newInstance(Context context) {

		FeedbackFragment f = new FeedbackFragment();
		return f;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view;
		view = inflater.inflate(R.layout.fragment_feedback, container, false);

		// Initalize compananets on layout
		// TextViews
		tvFeedbackHeader1 = (TextView) view.findViewById(R.id.tvFeedback1);
		tvFeedbackHeader2 = (TextView) view.findViewById(R.id.tvFeedback2);
		tvFeedbackILove = (TextView) view
				.findViewById(R.id.tvFeedbackILoveNeWo);
		tvFeedbackSend = (TextView) view
				.findViewById(R.id.tvFeedbackSendFeedback);

		// Layouts
		llFeedback = (LinearLayout) view.findViewById(R.id.llFeedback);
		rlbtnILove = (RelativeLayout) view.findViewById(R.id.rlFeedbackIlove);
		rlbtnFeedback = (RelativeLayout) view
				.findViewById(R.id.rlFeedbackSendFeedback);

		// btnLove On Click
		rlbtnILove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						ILoveNeWoActivity.class);
				startActivity(intent);
				// ILoveNeWoFragment fragment1 = new ILoveNeWoFragment();
				// getActivity().getSupportFragmentManager().beginTransaction()
				// .replace(R.id.container, fragment1).commit();

			}
		});

		// btnFeedback On Click
		rlbtnFeedback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// onClickChangeFragment(view, 2);

				Intent Email = new Intent(Intent.ACTION_SEND);
				Email.setType("text/email");
				Email.putExtra(Intent.EXTRA_EMAIL,
						new String[] { "digital@narmobile.az" });
				Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
				Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "");
				startActivity(Intent.createChooser(Email, "Send Feedback:"));

			}
		});

		return view;
	}

	public void onClickChangeFragment(View view, int index) {

		if (index == 1) {

		} else {

		}
	}
}
