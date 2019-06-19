package com.lhzw.searchlocmap.fragment;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.gtmap.api.IGeoPoint;
import com.gtmap.util.GeoPoint;
import com.gtmap.views.MapView;
import com.gtmap.views.overlay.OverlayItem;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PlotItemInfo;
import com.lhzw.searchlocmap.bean.SyncFireLine;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.view.ShowMesureDisDialog;

import java.util.List;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    private Toast mGlobalToast;
    protected boolean isSosFlash = false;
    private SoilderInfoChangeReceiver receiver;
    private Dao<PlotItemInfo, Integer> syncDao;
    private Toast searchToast;
    private TextView tv_search_content;

    protected abstract void initSoilderInfoList();

    protected abstract void drawSyncFireLine();

    protected abstract void initMessageNum();

    protected abstract void updateFeedback(int sendID);

    protected abstract void refleshBdSignal(Intent intent);

    private ShowMesureDisDialog mesureDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }


    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final MapView mapView, final GeoPoint gPoint, final List<OverlayItem> list) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();// 从开机到现在的毫秒数（手机睡眠时间不包括） final LatLng
        Point sPoint = mapView.getProjection().CGCS2000toPixels(gPoint, new Point());
        sPoint.offset(0, -100);
        final IGeoPoint IgPoint;
        IgPoint = mapView.getProjection().CGCS2000fromPixels(sPoint.x, sPoint.y);
        list.clear();
        list.add(new OverlayItem("", "", (GeoPoint) IgPoint));
        mapView.postInvalidate();
//        final long duration = 1500;
//        final Interpolator interpolator = new BounceInterpolator();
//        handler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed / duration);
//                double lng = t * IgPoint.getLongitudeE6()/ (10^6) + (1 - t)  * IgPoint.getLongitudeE6()/ (10^6);
//                double lat = t * IgPoint.getLatitudeE6()/ (10^6) + (1 - t) * IgPoint.getLatitudeE6()/ (10^6);
//                list.clear();
//                list.add(new OverlayItem("", "", new GeoPoint(lat, lng)));
//                mapView.postInvalidate();
//                Log.e("Tag", "sdkljfklajsdf");
//                if (t < 1.0) {
//                    handler.postDelayed(this, 16);
//                } else {
//                    list.clear();
//                    list.add(new OverlayItem("", "", gPoint));
//                    mapView.postInvalidate();
//                }
//            }
//        });
    }


    public void showToast(String text) {
        if (mGlobalToast == null) {
            mGlobalToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            mGlobalToast.show();
        } else {
            mGlobalToast.setText(text);
            mGlobalToast.setDuration(Toast.LENGTH_SHORT);
            mGlobalToast.show();
        }
    }

    protected void showMesureDistantNote() {
        mesureDialog = new ShowMesureDisDialog(getActivity());
        mesureDialog.show();
        mesureDialog.setListener(BaseFragment.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_input_distance:
                mesureDialog.dismiss();
                break;
            case R.id.tv_click_mesure:
                mesureDialog.dismiss();
                break;
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.lhzw.soildersos.change");
        filter.addAction(Constants.ACTION_SYNC_FIRELINE);
        filter.addAction(Constants.BD_Mms_ACTION);
        filter.addAction(Constants.ACTION_FEEDBACK);
        filter.addAction(Constants.BD_SIGNAL_LIST);
        receiver = new SoilderInfoChangeReceiver();
        getActivity().registerReceiver(receiver, filter);
    }

    private class SoilderInfoChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("com.lhzw.soildersos.change")) {
                isSosFlash = intent.getBooleanExtra("has_sos", false);
                Log.e("Tag", "sdfdsfsda");
                initSoilderInfoList();
            } else if (intent.getAction().equals(Constants.ACTION_SYNC_FIRELINE)) {
                String fireLine = intent.getStringExtra("fireLine");
                long time = intent.getLongExtra("time", System.currentTimeMillis());
                String num = intent.getStringExtra("num");
//                int tx_type = intent.getIntExtra("tx_type", Constants.TX_JZH);
                DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
                syncDao = helper.getPlotItemDao();
                Log.e("Tag", "receive sync fire line countent : " + fireLine);
                PlotItemInfo plot = new PlotItemInfo("", -1, time, fireLine, Constants.TX_SYNCFL, 0, Constants.TX_JZH, Constants.UPLOAD_STATE_ON);
                CommonDBOperator.saveToDB(syncDao, plot);
                //绘制下发火线
                drawSyncFireLine();
            } else if (intent.getAction().equals(Constants.BD_Mms_ACTION)) {
                initMessageNum();
            } else if(intent.getAction().equals(Constants.ACTION_FEEDBACK)){
                updateFeedback(intent.getIntExtra("sendID", -1));
            } else if(intent.getAction().equals(Constants.BD_SIGNAL_LIST)) {
                refleshBdSignal(intent);
            }
        }
    }

    /**
     * 69      * 将Toast封装在一个方法中，在其它地方使用时直接输入要弹出的内容即可
     * 70
     */
    protected void showSearchToast(String content) {
        //LayoutInflater的作用：对于一个没有被载入或者想要动态载入的界面，都需要LayoutInflater.inflate()来载入，LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
        if(searchToast == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();//调用Activity的getLayoutInflater()
            View view = inflater.inflate(R.layout.toast_search_end, null); //加載layout下的布局
            tv_search_content = (TextView) view.findViewById(R.id.tv_search_content);
            searchToast = new Toast(getActivity());
            searchToast.setGravity(Gravity.CENTER, 0, -400);//setGravity用来设置Toast显示的位置，相当于xml中的android:gravity或android:layout_gravity
            searchToast.setDuration(Toast.LENGTH_LONG);//setDuration方法：设置持续时间，以毫秒为单位。该方法是设置补间动画时间长度的主要方法
            searchToast.setView(view); //添加视图文件
        }
        tv_search_content.setText(content);
        searchToast.show();
    }
//    public void dimBackground(final float from, final float to) {
//        final Window window = getActivity().getWindow();
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
//        valueAnimator.setDuration(500);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                WindowManager.LayoutParams params = window.getAttributes();
//                params.alpha = (Float) animation.getAnimatedValue();
//                window.setAttributes(params);
//            }
//        });
//        valueAnimator.start();
//    }


}
