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
public class ShowDialogSearch extends Dialog {

    private Button dialog_timer_search;
    private Button dialog_total_search;
    private Button dialog_history;

    public ShowDialogSearch(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public ShowDialogSearch(Context context, int themeResId) {
        super(context, themeResId);
    }
    public ShowDialogSearch(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailog_search_function);
        init();
    }

    private void init() {
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_timer_search = (Button) findViewById(R.id.dialog_timer_search);
        dialog_total_search = (Button) findViewById(R.id.dialog_total_search);
        dialog_history = (Button) findViewById(R.id.dialog_history);
    }

    public void setListner(View.OnClickListener listner){
        dialog_timer_search.setOnClickListener(listner);
        dialog_total_search.setOnClickListener(listner);
        dialog_history.setOnClickListener(listner);
    }
}
