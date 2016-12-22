package com.example.administrator.retrofitutil.user;

import com.example.administrator.retrofitutil.bean.VersionBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by ZhangHaiTao on 2016/12/21.
 */
public interface ApiService {
    @GET("android/2.3.0")
    Call<ResponseBody>  getVersion();

    @GET("android/2.3.0")
    Call<VersionBean>  getVersionBean();

    @GET("android/2.3.0")
    Observable<VersionBean> getVersionRxjava();
}
