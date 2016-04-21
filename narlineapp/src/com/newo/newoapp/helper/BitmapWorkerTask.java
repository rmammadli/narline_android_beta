package com.newo.newoapp.helper;

import java.lang.ref.WeakReference;

import com.newo.newoapp.NeWoApp;
import com.newo.newoapp.imgview.ReduceLargeBitmaps;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	private final WeakReference<ImageView> imageViewReference;
	private int data = 0;

	public BitmapWorkerTask(ImageView imageView) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(Integer... params) {
		data = params[0];
		Bitmap bmp = ReduceLargeBitmaps.decodeSampledBitmapFromResource(NeWoApp
				.getInstance().getResources(), data);
		return bmp;
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			BitmapDrawable ob = new BitmapDrawable(bitmap);
			if (imageView != null) {
				imageView.setBackgroundDrawable(ob);
				// imageView.setImageBitmap(bitmap);
			}
		}
	}
}
