package com.example.administrator.retrofitutil.user;

import android.content.Context;
import android.util.Log;

import com.example.administrator.retrofitutil.bean.VersionBean;
import com.example.administrator.retrofitutil.util.HttpUtil;
import com.example.administrator.retrofitutil.util.ProgressCancelListener;
import com.example.administrator.retrofitutil.util.ProgressSubscriber;
import com.example.administrator.retrofitutil.util.SubscriberOnNextListenter;

import rx.Observable;

/**
 * Created by ZhangHaiTao on 2016/12/22.
 * 建议将网络的API接口都放在这个类里
 */
public class APIFactory {
    //单例模式
    public static APIFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }
    private static class SingletonHolder {
        private static final APIFactory INSTANCE = new APIFactory();
    }


    /**
     * @param nextListenter
     * @param mContext
     *
     * APIFactory.getInstance().getVersion(new SubscriberOnNextListenter<VersionBean>() {
            @Override
            public void next(VersionBean versionBean) {
                Toast.makeText(mContext,"封装：\n"+ versionBean.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mContext);
     */
    //如果有需要，传入更多参数para 用于网络请求
    public void getVersion(SubscriberOnNextListenter<VersionBean> nextListenter, Context mContext) {
        //Rerofit中封装了超时处理机制，拦截器
        Observable<VersionBean> call = HttpUtil.getApiService().getVersionRxjava();
//        HttpUtil.toSubscribe(call, new ProgressSubscriber<VersionBean>(nextListenter, mContext));
        HttpUtil.toSubscribe(call, new ProgressSubscriber<VersionBean>(nextListenter, new ProgressCancelListener() {
            @Override
            public void onCancelProgress() {
                Log.w("ZHT","dialog cancle");
            }
        }, mContext));
    }
}
