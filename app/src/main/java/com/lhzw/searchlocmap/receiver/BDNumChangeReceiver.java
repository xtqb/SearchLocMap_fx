package com.lhzw.searchlocmap.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lhzw.searchlocmap.bean.BaseBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.utils.ToastUtil;

import io.reactivex.Observable;

/**
 * 内外置北斗切换与北斗关闭的广播接收者
 */

public class BDNumChangeReceiver extends BroadcastReceiver {

    private Context mContext;
    private Intent mIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        //收到广播后去拿到北斗号
        String bdNum = BaseUtils.getDipperNum(mContext);
        LogUtil.e("收到了北斗内外置切换广播,切换后本机北斗号=="+bdNum);
        //本地北斗为空 不处理
        if (TextUtils.isEmpty(bdNum)) {
            ToastUtil.showToast("北斗卡号为空,请先安装北斗卡,并打开北斗开关");
            return;
        }
        //本地北斗未改变  不处理
        if (bdNum.equals(SpUtils.getString(Constants.BD_NUM_lOC_DEF, ""))) {
            ToastUtil.showToast("北斗卡号未改变");
            return;
        }
        mIntent = new Intent(Constants.BD_NUM_ISCHANGING);
        //改变了就保存新的北斗
        SpUtils.putString(Constants.BD_NUM_lOC_DEF, bdNum);
        //上传    判断有无网络
        if (BaseUtils.isNetConnected(mContext)) {
            //有网络  通知服务器北斗号
            Observable<BaseBean> observable = SLMRetrofit.getInstance().getApi().uploadMacAndBdNum(BaseUtils.getMacFromHardware(), bdNum);
            observable.compose(new ThreadSwitchTransformer<BaseBean>())
                    .subscribe(new CallbackListObserver<BaseBean>() {
                        @Override
                        protected void onSucceed(BaseBean bean) {
                            if ("0".equals(bean.getCode())) {
                                //上传成功
                                ToastUtil.showToast("北斗卡切换成功");
                                mIntent.putExtra("state",0);
                                mContext.sendBroadcast(mIntent);
                            } else {
                                ToastUtil.showToast("北斗卡切换失败,正在尝试使用北斗通信服务上传");
                                mIntent.putExtra("state",1);
                                mContext.sendBroadcast(mIntent);
                            }
                        }

                        @Override
                        protected void onFailed() {
                            ToastUtil.showToast("北斗卡切换失败,正在尝试使用北斗通信服务上传");
                            mIntent = new Intent(Constants.BD_NUM_ISCHANGING);
                            mIntent.putExtra("state",1);
                            mContext.sendBroadcast(mIntent);
                        }
                    });
        } else {
                //无网络  上传 北斗服务
              LogUtil.e("使用北斗通信服务上传");
              mIntent.putExtra("state",1);
              mContext.sendBroadcast(mIntent);

        }

    }
}
