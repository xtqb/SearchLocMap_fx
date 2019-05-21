package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.view.CompassView;

public class CompassActivity extends Activity {
	private float mDirection;
	private float mTargetDirection;
	private AccelerateInterpolator mInterpolator;
	private boolean mStopDrawing;
	protected final Handler compassHandler = new Handler();
	private final Handler OrienHandler = new Handler();
	private SensorManager mSensorManager;
	private Sensor mOrientationSensor;
	private final float MAX_ROATE_DEGREE = 1.0f;
	private CompassView im_compass_point;
	private Sensor aSensor;
	private Sensor mSensor;
	private float[] accelerometerValues = new float[3];
	private float[] magneticFieldValues = new float[3];
	private TextView tv_orientation;
	private TextView tv_orientation_degree;
	private SensorEvent sensorEvent;

	private String north;
	private String sorth;
	private String west;
	private String east;
	private String westNorth;
	private String eastNorth;
	private String eastSorth;
	private String westSorth;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		im_compass_point = (CompassView) findViewById(R.id.im_compass_point);
		tv_orientation = (TextView) findViewById(R.id.tv_orientation);
		tv_orientation_degree = (TextView) findViewById(R.id.tv_orientation_degree);

	}

	private void initData() {
		// TODO Auto-generated method stub
		initResources();
		initServices();
		initOrientation();
	}

	private void initOrientation() {
		// TODO Auto-generated method stub
		north = getString(R.string.orientation_north_note);
		sorth = getString(R.string.orientation_south_note);
		west = getString(R.string.orientation_west_note);
		east = getString(R.string.orientation_east_note);
		westNorth = getString(R.string.orientation_west_north_note);
		eastNorth = getString(R.string.orientation_east_north_note);
		eastSorth = getString(R.string.orientation_east_south_note);
		westSorth = getString(R.string.orientation_west_south_note);
	}

	private void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mStopDrawing = false;
		if (mOrientationSensor != null) {
			mSensorManager.registerListener(mOrientationSensorEventListener,
					mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
		}
		if (mSensorManager != null) {
			mSensorManager.registerListener(degreeEventListener, aSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			mSensorManager.registerListener(degreeEventListener, mSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		// 更新显示数据
		OrienHandler.postDelayed(orientUpdate, 10); // 跟新方向
		compassHandler.postDelayed(mCompassViewUpdater, 20); // 跟新指南针
	}

	private Runnable orientUpdate = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateOrientation();
			OrienHandler.postDelayed(orientUpdate, 40);
		}
	};

	protected Runnable mCompassViewUpdater = new Runnable() {
		@Override
		public void run() {
			if (im_compass_point != null && !mStopDrawing) {
				if (mDirection != mTargetDirection) {

					// calculate the short routine
					float to = mTargetDirection;
					if (to - mDirection > 180) {
						to -= 360;
					} else if (to - mDirection < -180) {
						to += 360;
					}

					// limit the max speed to MAX_ROTATE_DEGREE
					float distance = to - mDirection;
					if (Math.abs(distance) > MAX_ROATE_DEGREE) {
						distance = distance > 0 ? MAX_ROATE_DEGREE
								: (-1.0f * MAX_ROATE_DEGREE);
					}

					// need to slow down if the distance is short
					mDirection = normalizeDegree(mDirection
							+ ((to - mDirection) * mInterpolator
									.getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f
											: 0.3f)));

					im_compass_point.updateDirection(mDirection);
				}
				compassHandler.postDelayed(mCompassViewUpdater, 20);
			}
		}
	};

	private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			float direction = event.values[0] * -1.0f;
			mTargetDirection = normalizeDegree(direction);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	private SensorEventListener degreeEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			sensorEvent = event;
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	};

	private void updateOrientation() {
		if (sensorEvent != null) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				magneticFieldValues = sensorEvent.values;
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				accelerometerValues = sensorEvent.values;
			calculateOrientation();
		}
	}

	private float normalizeDegree(float degree) {
		return (degree + 720) % 360;
	}

	private void calculateOrientation() {
		float[] values = new float[3];
		float[] R = new float[9];
		SensorManager.getRotationMatrix(R, null, accelerometerValues,
				magneticFieldValues);
		SensorManager.getOrientation(R, values);
		// 要经过一次数据格式的转换，转换为度
		values[0] = (float) Math.toDegrees(values[0]);
		tv_orientation_degree.setText(getDegrees((int) values[0]) + "°");
		if (values[0] >= -5 && values[0] < 5) {
			tv_orientation.setText(north);
		} else if (values[0] >= 5 && values[0] < 85) {
			tv_orientation.setText(eastNorth);
		} else if (values[0] >= 85 && values[0] <= 95) {
			tv_orientation.setText(east);
		} else if (values[0] >= 95 && values[0] < 175) {
			tv_orientation.setText(eastSorth);
		} else if ((values[0] >= 175 && values[0] <= 180)
				|| (values[0]) >= -180 && values[0] < -175) {
			tv_orientation.setText(sorth);
		} else if (values[0] >= -175 && values[0] < -95) {
			tv_orientation.setText(westSorth);
		} else if (values[0] >= -95 && values[0] < -85) {
			tv_orientation.setText(west);
		} else if (values[0] >= -85 && values[0] < -5) {
			tv_orientation.setText(westNorth);
		}

	}

	private String getDegrees(int degrees) {
		if (degrees < 0)
			return degrees + 360 + "";
		return degrees + "";
	}

	private void initResources() {
		mDirection = 0.0f;
		mTargetDirection = 0.0f;
		mInterpolator = new AccelerateInterpolator();
		mStopDrawing = true;
	}

	private void initServices() {
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientationSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mStopDrawing = true;
		if (mOrientationSensor != null) {
			mSensorManager.unregisterListener(mOrientationSensorEventListener);
			mSensorManager.unregisterListener(degreeEventListener);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		north = null;
		sorth = null;
		west = null;
		east = null;
		westNorth = null;
		eastNorth = null;
		eastSorth = null;
		westSorth = null;
	}
}
