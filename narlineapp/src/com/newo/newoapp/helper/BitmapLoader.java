package com.newo.newoapp.helper;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.newo.newoapp.NeWoApp;
import com.newo.newoapp.imgview.ReduceLargeBitmaps;

public class BitmapLoader {

	public class BitmapWorkerTaskImageView extends
			AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;

		public BitmapWorkerTaskImageView(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			Bitmap bmp = ReduceLargeBitmaps.decodeSampledBitmapFromResource(
					NeWoApp.getInstance().getResources(), data);
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

	public class BitmapWorkerTaskLinearLayout extends
			AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<LinearLayout> linearLayoutReference;
		private int data = 0;

		public BitmapWorkerTaskLinearLayout(LinearLayout linearLayout) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			linearLayoutReference = new WeakReference<LinearLayout>(
					linearLayout);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			Bitmap bmp = ReduceLargeBitmaps.decodeSampledBitmapFromResource(
					NeWoApp.getInstance().getResources(), data);
			return bmp;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (linearLayoutReference != null && bitmap != null) {
				final LinearLayout linearLayout = linearLayoutReference.get();
				BitmapDrawable ob = new BitmapDrawable(bitmap);
				if (linearLayout != null) {
					linearLayout.setBackgroundDrawable(ob);
					// imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	public class BitmapWorkerTaskListView extends
			AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ListView> listViewReference;
		private int data = 0;

		public BitmapWorkerTaskListView(ListView listView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			listViewReference = new WeakReference<ListView>(listView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			Bitmap bmp = ReduceLargeBitmaps.decodeSampledBitmapFromResource(
					NeWoApp.getInstance().getResources(), data);
			return bmp;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (listViewReference != null && bitmap != null) {
				final ListView listView = listViewReference.get();
				BitmapDrawable ob = new BitmapDrawable(bitmap);
				if (listView != null) {
					listView.setBackgroundDrawable(ob);
					// imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	public class BitmapWorkerTaskViewPager extends
			AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ViewPagerParallax> viewPagerReference;
		private int data = 0;

		public BitmapWorkerTaskViewPager(ViewPagerParallax viewPager) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			viewPagerReference = new WeakReference<ViewPagerParallax>(viewPager);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			Bitmap bmp = ReduceLargeBitmaps.decodeSampledBitmapFromResource(
					NeWoApp.getInstance().getResources(), data);
			return bmp;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (viewPagerReference != null && bitmap != null) {
				final ViewPagerParallax viewPager = viewPagerReference.get();
				BitmapDrawable ob = new BitmapDrawable(bitmap);
				if (viewPager != null) {
					viewPager.setBackgroundDrawable(ob);
					// imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	public class BitmapWorkerTaskScrollView extends
			AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ScrollView> scrollViewReference;
		private int data = 0;

		public BitmapWorkerTaskScrollView(ScrollView scrollView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			scrollViewReference = new WeakReference<ScrollView>(scrollView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			Bitmap bmp = ReduceLargeBitmaps.decodeSampledBitmapFromResource(
					NeWoApp.getInstance().getResources(), data);
			return bmp;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (scrollViewReference != null && bitmap != null) {
				final ScrollView scrollView = scrollViewReference.get();
				BitmapDrawable ob = new BitmapDrawable(bitmap);
				if (scrollView != null) {
					scrollView.setBackgroundDrawable(ob);
					// imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	public void loadBitmap(int resId, ImageView imageView) {
		BitmapWorkerTaskImageView task = new BitmapWorkerTaskImageView(
				imageView);
		task.execute(resId);
	}

	public void loadBitmap(int resId, LinearLayout linearLayout) {
		BitmapWorkerTaskLinearLayout task = new BitmapWorkerTaskLinearLayout(
				linearLayout);
		task.execute(resId);
	}

	public void loadBitmap(int resId, ViewPagerParallax viewPager) {
		BitmapWorkerTaskViewPager task = new BitmapWorkerTaskViewPager(
				viewPager);
		task.execute(resId);

	}

	public void loadBitmap(int resId, ListView listView) {
		BitmapWorkerTaskListView task = new BitmapWorkerTaskListView(listView);
		task.execute(resId);

	}

	public void loadBitmap(int resId, ScrollView scrollView) {
		BitmapWorkerTaskScrollView task = new BitmapWorkerTaskScrollView(
				scrollView);
		task.execute(resId);

	}

}
