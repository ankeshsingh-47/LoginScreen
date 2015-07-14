package com.ysoserious.bliinder.animations;

import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

public class BlurAnimation extends Animation {

	private final ImageView imageView;
	private final Bitmap bitmap;
	private final float startValue;
	private final float stopValue;
	private final float difValue;

	private BlurAnimation(ImageView imageView, Bitmap bitmap, int startValue,
			int stopValue) {
		this.imageView = imageView;
		this.bitmap = bitmap;
		this.startValue = startValue;
		this.stopValue = stopValue;
		this.difValue = stopValue - startValue;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);

		int current = (int) (this.difValue * interpolatedTime + this.startValue + 0.5f);
		Bitmap blurred = quickBlur(this.bitmap, current);
		this.imageView.setImageBitmap(blurred);
	}

	public Bitmap quickBlur(Bitmap bitmap, int factor) {
		if (factor <= 0) {
			return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		}
		return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / factor,
				bitmap.getHeight() / factor, true);
	}
}