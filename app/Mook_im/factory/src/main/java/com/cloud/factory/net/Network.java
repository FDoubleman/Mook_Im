package com.cloud.factory.net;

import com.cloud.common.Common;
import com.cloud.factory.Factory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fmm on 2017/9/6.
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
