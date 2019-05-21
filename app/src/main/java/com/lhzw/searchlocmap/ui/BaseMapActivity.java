package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.widget.Toast;

import com.lhzw.searchlocmap.view.ProgressDialogShow;

public class BaseMapActivity extends Activity {
	private ProgressDialogShow progressDialog;
	private Toast mGlobalToast = null;

	/**
	 * Post ProgressDialog
	 * 
	 * @param isCancelable
	 * @param msg
	 * @param isCancelable
	 */
	public void showProgressDialog(String msg, boolean isCancelable) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialogShow(BaseMapActivity.this);
			progressDialog.dialogShow();
			progressDialog.setContent(msg);
		} else if (!progressDialog.isShowing()) {
			progressDialog.dialogShow();
			progressDialog.setContent(msg);
		}
	}



	/**
	 * Cancel ProgressDialog
	 */
	public void cancelProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		progressDialog = null;
	}

	/**
	 * Post Toast
	 * 
	 * @param text
	 */
	public void showToast(String text) {

		if (mGlobalToast == null) {
			mGlobalToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
			mGlobalToast.show();
		} else {
			mGlobalToast.setText(text);
			mGlobalToast.setDuration(Toast.LENGTH_SHORT);
			mGlobalToast.show();
		}
	}
}
