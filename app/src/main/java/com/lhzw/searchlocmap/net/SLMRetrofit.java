package com.lhzw.searchlocmap.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.utils.SpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hecuncun on 2019/5/13
 */
public class SLMRetrofit {
    private static SLMRetrofit mService;
    private Api mApi;
    private static final int DEFAULT_TIME_OUT = 20;//超时时间
    private static final int DEFAULT_READ_TIME_OUT = 20;

    private SLMRetrofit() {
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.IP_ADD)
                .client(genericClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mApi = retrofit.create(Api.class);

    }

    private OkHttpClient genericClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
        //启用Log日志
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
//                        CacheControl.Builder builder = new CacheControl.Builder().maxAge(10, TimeUnit.MINUTES);
                        Request request = chain.request()
                                .newBuilder()
//                                .header("Cache-Control", builder.build().toString())
//                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .addHeader("Content-Type", "application/json; charset=UTF-8")
//                                .addHeader("Accept-Encoding", "gzip, deflate")
//                                .addHeader("Accept-Encoding", "gzip,sdch")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept", "*/*")
                                .addHeader("x-access-token", SpUtils.getString(Constants.HTTP_TOOKEN,""))
                                // .addHeader("Cookie", cookie)
                                //.addHeader("Authorization","APPCODE " + Constant.OCR_APP_CODE)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
        return httpClient;
    }


    public static SLMRetrofit getInstance() {
        if (mService == null) {
            synchronized (SLMRetrofit.class) {
                if (mService == null) {
                    mService = new SLMRetrofit();
                }
            }
        }
        return mService;
    }


    public Api getApi() {
        return mApi;
    }
}
