package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.lhzw.searchlocmap.R;

public class ToggleButtonView extends View implements OnTouchListener {
	private int with;
	private int heigh;
	private Paint paintDef;
	private Paint paintPre;
	private Paint paintSlide;
	private boolean state;
	private Paint backgroud;
	private onToggleClickListener listener;

	public ToggleButtonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public ToggleButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public ToggleButtonView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		state = false;
		paintDef = new Paint();
		paintDef.setColor(getResources().getColor(R.color.gray_little1));
		paintSlide = new Paint();
		paintSlide.setColor(getResources().getColor(R.color.white));
		paintPre = new Paint();
		paintPre.setColor(getResources().getColor(R.color.green3));
		backgroud = paintDef;
		this.setOnTouchListener(this);
		setSliderState(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		with = getWidth();
		heigh = getHeight();
		canvas.drawRect(41, heigh / 2 - 38, with - 41, heigh / 2 + 38,
				backgroud);
		if (state) {
			canvas.drawCircle(41, heigh / 2, 39, backgroud);
			canvas.drawCircle(with - 40, heigh / 2, 38, backgroud);
			canvas.drawCircle(40 + 1, heigh / 2, 38, paintSlide);
		} else {
			canvas.drawCircle(40, heigh / 2, 38, backgroud);
			canvas.drawCircle(with - 41, heigh / 2, 39, backgroud);
			canvas.drawCircle(with - 40 - 1, heigh / 2, 38, paintSlide);
		}

	}

	public void setSliderState(boolean state) {
		this.state = state;
		if (state) {
			backgroud = paintPre;
		} else {
			backgroud = paintDef;
		}
		invalidate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (listener != null) {
				state = !state;
				setSliderState(state);
				listener.onToggleClick(v);
			}

			break;

		default:
			break;
		}
		return true;
	}

	public interface onToggleClickListener {
		public void onToggleClick(View view);
	}

	public void setOnToggleClickListener(onToggleClickListener listener) {
		this.listener = listener;
	}

}
