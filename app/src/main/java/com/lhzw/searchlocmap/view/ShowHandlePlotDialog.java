package com.lhzw.searchlocmap.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/3/25.
 */
public class ShowHandlePlotDialog extends Dialog {

    private Button dialog_plot_del;
    private Button dialog_plot_upload;

    public ShowHandlePlotDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public ShowHandlePlotDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    public ShowHandlePlotDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailog_handle_plot);
        init();
    }

    private void init() {
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_plot_upload = (Button) findViewById(R.id.dialog_plot_upload);
        dialog_plot_del = (Button) findViewById(R.id.dialog_plot_del);
    }

    public void setListner(View.OnClickListener listner){
        dialog_plot_upload.setOnClickListener(listner);
        dialog_plot_del.setOnClickListener(listner);
    }

    public void setUploadNote(){
        dialog_plot_upload.setEnabled(false);
    }
}
