package com.cloud.factory.net;

import android.text.TextUtils;

import com.cloud.common.Common;
import com.cloud.factory.Factory;
import com.cloud.factory.persistence.Account;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fmm on 2017/9/6.
 *
 */

public class Network {
    private static Retrofit sRetrofit;
    private static Network sNetwork;

    public static Network getInstance() {
        if (sNetwork == null) {
            sNetwork = new Network();
        }
        return sNetwork;
    }

    public Network() {

    }


    public static Retrofit getRetrifit() {
        if (sRetrofit != null) {
            return sRetrofit;
        }
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(1000*10, TimeUnit.MILLISECONDS);
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
//              Request request = chain.request();
//               Request.Builder builder = request.newBuilder();
//                if (!TextUtils.isEmpty(Account.getToken())) {
//                    // 注入一个token
//                    builder.addHeader("token", Account.getToken());
//                }
//                builder.addHeader("Content-Type", "application/json");
//                 Request newRequest = builder.build();
//                return chain.proceed(newRequest);

                // 拿到我们的请求
                Request original = chain.request();
                // 重新进行build
                Request.Builder builder = original.newBuilder();
                if (!TextUtils.isEmpty(Account.getToken())) {
                    // 注入一个token
                    builder.addHeader("token", Account.getToken());
                }
                builder.addHeader("Content-Type", "application/json");
                Request newRequest = builder.build();
                // 返回
                return chain.proceed(newRequest);
            }
        });
        clientBuilder.addInterceptor(new HttpLogger());
        clientBuilder.build();
        OkHttpClient client = clientBuilder.build();
        Retrofit.Builder builder = new Retrofit.Builder();

        sRetrofit = builder.baseUrl(Common.Constance.API_URL)
                .client(client)
                //.addCallAdapterFactory(new )
                // 设置Json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();

        return sRetrofit;
    }

    public RemoteService getService() {
        return getRetrifit().create(RemoteService.class);
    }
}
