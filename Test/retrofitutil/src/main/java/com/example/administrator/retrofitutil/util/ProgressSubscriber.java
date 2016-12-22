package com.example.administrator.retrofitutil.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by ZhangHaiTao on 2016/12/21.
 */



public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private SubscriberOnNextListenter mSubscriberOnNextListenter;
    private Context context;
    private ProgressDialogHandler mProgressDialogHandler;
    private ProgressCancelListener mProgressCancelListener;

    public ProgressSubscriber(SubscriberOnNextListenter mSubscriberOnNextListenter, Context context) {
        this.mSubscriberOnNextListenter = mSubscriberOnNextListenter;
        this.context = context;

        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    public ProgressSubscriber(SubscriberOnNextListenter mSubscriberOnNextListenter,
                              ProgressCancelListener progressCancelListener,  Context context) {
        this.mSubscriberOnNextListenter = mSubscriberOnNextListenter;
        this.context = context;

        mProgressCancelListener = progressCancelListener;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }

        Log.w("ZHT", "start");
    }

    @Override
    public void onCompleted() {
        Log.w("ZHT", "end");
        dismissProgressDialog();
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Toast.makeText(context, "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
            Toast.makeText(context, "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dismissProgressDialog();
    }

    @Override
    public void onNext(T t) {
        mSubscriberOnNextListenter.next(t);
    }

    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }

        if (mProgressCancelListener != null) {
            mProgressCancelListener.onCancelProgress();
        }
    }


    class ProgressDialogHandler extends Handler{

        public static final int SHOW_PROGRESS_DIALOG = 1;
        public static final int DISMISS_PROGRESS_DIALOG = 2;

        private ProgressDialog pd;

        private Context context;
        private boolean cancelable;
        private ProgressCancelListener mProgressCancelListener;

        public ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener,
                                     boolean cancelable) {
            super();
            this.context = context;
            this.mProgressCancelListener = mProgressCancelListener;
            this.cancelable = cancelable;
        }

        private void initProgressDialog(){
            if (pd == null) {
                pd = new ProgressDialog(context);

                pd.setCancelable(cancelable);

                if (cancelable) {
                    pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            if (mProgressCancelListener != null)
                                mProgressCancelListener.onCancelProgress();
                        }
                    });
                }

                if (!pd.isShowing()) {
                    pd.show();
                }
            }
        }

        private void dismissProgressDialog(){
            if (pd != null) {
                pd.dismiss();
                pd = null;
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PROGRESS_DIALOG:
                    initProgressDialog();
                    break;
                case DISMISS_PROGRESS_DIALOG:
                    dismissProgressDialog();
                    break;
            }
        }
    }

    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }
}
