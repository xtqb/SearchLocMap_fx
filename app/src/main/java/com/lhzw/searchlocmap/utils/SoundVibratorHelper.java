package com.lhzw.searchlocmap.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.constants.SPConstants;

public class SoundVibratorHelper {

	private static SoundVibratorHelper mSoundPlayHelper;
	private MediaPlayer mediaPlayerSuccess;
	private Vibrator mVibrator;
	private boolean isPlaySound;
	private boolean isVibrate;
	private long[] vibrateSuccess = { 0, 120 };

	private SoundVibratorHelper(Context context) {
		release();
		isPlaySound = SpUtils.getBoolean(SPConstants.SLIDE_SOUND, true);
		isVibrate = SpUtils.getBoolean(SPConstants.SLIDE_VIBRATOR, true);
		mediaPlayerSuccess = MediaPlayer.create(context, R.raw.beep);
		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public static synchronized SoundVibratorHelper getSoundPlayer(
			Context context) {
		mSoundPlayHelper = new SoundVibratorHelper(context);
		return mSoundPlayHelper;
	}

	public void playSuccess() {
		if (mediaPlayerSuccess != null && isPlaySound) {
			mediaPlayerSuccess.seekTo(0);
			mediaPlayerSuccess.start();
		}

		if (mVibrator != null && isVibrate) {
			mVibrator.vibrate(vibrateSuccess, -1);
		}
	}

	public void release() {
		if (mediaPlayerSuccess != null) {
			mediaPlayerSuccess.reset();
			mediaPlayerSuccess.release();
			mediaPlayerSuccess = null;
		}

		if (mVibrator != null) {
			mVibrator.cancel();
		}
	}
}
