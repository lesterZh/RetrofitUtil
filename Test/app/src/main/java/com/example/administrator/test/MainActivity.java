package com.example.administrator.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.retrofitutil.user.APIFactory;
import com.example.administrator.retrofitutil.bean.VersionBean;
import com.example.administrator.retrofitutil.util.HttpUtil;
import com.example.administrator.retrofitutil.util.ProgressSubscriber;
import com.example.administrator.retrofitutil.util.SubscriberOnNextListenter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtInput;
    private Button mBtn1;
    private Button mBtn2;

    Activity mContext;
    private Button mBtn3;
    private Button mBtn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mEtInput = (EditText) findViewById(R.id.et_input);
        mBtn1 = (Button) findViewById(R.id.btn_1);

        mBtn1.setOnClickListener(this);
        mBtn2 = (Button) findViewById(R.id.btn_2);
        mBtn2.setOnClickListener(this);
        mBtn3 = (Button) findViewById(R.id.btn_3);
        mBtn3.setOnClickListener(this);
        mBtn4 = (Button) findViewById(R.id.btn_4);
        mBtn4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                getVersionForString();
                break;
            case R.id.btn_2:
                getVersionForBean();
                break;
            case R.id.btn_3:
                getVersionRxJava();
                break;
            case R.id.btn_4:
//                getVersionRxJava2();
                APIFactory.getInstance().getVersion(new SubscriberOnNextListenter<VersionBean>() {
                    @Override
                    public void next(VersionBean versionBean) {
                        Toast.makeText(mContext,"封装：\n"+ versionBean.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, mContext);
                break;
        }
    }

    private void submit() {
        // validate
        String input = mEtInput.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void getVersionForBean() {

        Call<VersionBean> call = HttpUtil.getApiService().getVersionBean();

        call.enqueue(new Callback<VersionBean>() {
            @Override
            public void onResponse(Call<VersionBean> call, Response<VersionBean> response) {
                Toast.makeText(mContext, "Bean:\n" + response.body().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<VersionBean> call, Throwable t) {
                Toast.makeText(mContext, "fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getVersionForString() {

        Call<ResponseBody> call = HttpUtil.getApiService().getVersion();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Toast.makeText(mContext, "String:\n" + response.body().string(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, "fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getVersionRxJava() {
        Observable<VersionBean> call = HttpUtil.getApiService().getVersionRxjava();

        call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VersionBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(VersionBean versionBean) {
                        Toast.makeText(mContext, "Rxjava:\n" + versionBean.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    //封装后的 此处参数应该和getVersionRxjava()一致的
    private void getVersionRxJava2() {
        //Rerofit中封装了超时处理机制，拦截器
        Observable<VersionBean> call = HttpUtil.getApiService().getVersionRxjava();

//        call.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .retryWhen(new RetryWhenNetworkException())//超时处理机制
//                .subscribe(new ProgressSubscriber<VersionBean>(new SubscriberOnNextListenter<VersionBean>() {
//                    @Override
//                    public void next(VersionBean o) {
//                        Toast.makeText(mContext, o.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }, mContext));

        HttpUtil.toSubscribe(call, new ProgressSubscriber<VersionBean>(new SubscriberOnNextListenter<VersionBean>() {
            @Override
            public void next(VersionBean o) {
                Toast.makeText(mContext, o.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mContext));
    }
}
