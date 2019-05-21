package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CusProgressView extends ImageView {
	private Drawable compass;
	private int mDirection;

	public CusProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public CusProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public CusProgressView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		mDirection = 0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		compass = getDrawable();
		compass.setBounds(0, 0, getWidth(), getHeight());
		canvas.save();
		mDirection = mDirection + 20;
		canvas.rotate(mDirection, getWidth() / 2, getHeight() / 2);
		compass.draw(canvas);
		canvas.restore();
		postInvalidateDelayed(100);
		if (mDirection == 360) {
			mDirection = 0;
		}
	}

}
