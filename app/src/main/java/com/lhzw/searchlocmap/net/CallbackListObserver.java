package com.lhzw.searchlocmap.net;


import android.support.annotation.NonNull;

import com.lhzw.searchlocmap.utils.ToastUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * api请求的结果回调
 * data 里面是一个数组的解析回调
 */

public abstract class CallbackListObserver<T> implements Observer<T> {


    @Override
    public void onSubscribe(@NonNull Disposable d) {
        onStart();
    }

    @Override
    public void onNext(T bean){
        onSucceed(bean);
    }

    @Override
    public void onError(Throwable t) {
        ToastUtil.showToast(t.getLocalizedMessage());
        onFailed();
    }

    @Override
    public void onComplete() {
    }

    /**
     * 请求开始
     */
    protected void onStart() {

    }

    /**
     * 请求成功
     */
    protected abstract void onSucceed(T t);


    /**
     * 请求异常
     */
    protected void onException(Throwable t) {
//        DialogUtil.showLoadingDialog(HzlcApplication.getContext());
    }

    /**
     * 请求错误
     */
    protected abstract void onFailed();

}
