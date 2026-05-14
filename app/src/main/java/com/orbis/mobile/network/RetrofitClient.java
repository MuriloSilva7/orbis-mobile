package com.orbis.mobile.network;

import android.content.Context;

import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.AuthInterceptor;
import com.orbis.mobile.model.RefreshAuthenticator;
import com.orbis.mobile.model.TokenManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://orbis-5hnm.onrender.com/";

    private static RetrofitClient instance;

    private final OrbisApiService api;

    private RetrofitClient(Context context) {

        TokenManager tokenManager = new TokenManager(context);

        Retrofit refreshRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OrbisApiService refreshApi =
                refreshRetrofit.create(OrbisApiService.class);

        Interceptor authInterceptor = chain -> {

            Request original = chain.request();

            String accessToken = tokenManager.getAccessToken();

            Request.Builder builder = original.newBuilder();

            if (accessToken != null) {
                builder.header(
                        "Authorization",
                        "Bearer " + accessToken
                );
            }

            return chain.proceed(builder.build());
        };

        HttpLoggingInterceptor logging =
                new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .authenticator(
                        new RefreshAuthenticator(
                                tokenManager,
                                refreshApi
                        )
                )
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .build();

        api = retrofit.create(OrbisApiService.class);
    }

    public static synchronized RetrofitClient getInstance(Context context) {

        if (instance == null) {
            instance = new RetrofitClient(context);
        }

        return instance;
    }

    public OrbisApiService getApi() {
        return api;
    }
}
