package com.orbis.mobile.model;

import android.util.Log;

import com.orbis.mobile.api.OrbisApiService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class RefreshAuthenticator implements Authenticator {

    private final TokenManager tokenManager;
    private final OrbisApiService apiService;

    public RefreshAuthenticator(
            TokenManager tokenManager,
            OrbisApiService apiService
    ) {
        this.tokenManager = tokenManager;
        this.apiService = apiService;
    }

    @Override
    public Request authenticate(
            Route route,
            Response response
    ) throws IOException {

        Log.d("REFRESH", "TOKEN EXPIRADO");

        String refreshToken =
                tokenManager.getRefreshToken();

        if (refreshToken == null) {
            return null;
        }

        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        retrofit2.Response<LoginResponse> refreshResponse =
                apiService.refresh(body).execute();

        if (refreshResponse.isSuccessful()
                && refreshResponse.body() != null) {

            Log.d("REFRESH", "NOVO TOKEN GERADO");

            String newAccessToken =
                    refreshResponse.body().getAccessToken();

            String newRefreshToken =
                    refreshResponse.body().getRefreshToken();

            tokenManager.saveTokens(
                    newAccessToken,
                    newRefreshToken
            );

            return response.request()
                    .newBuilder()
                    .header(
                            "Authorization",
                            "Bearer " + newAccessToken
                    )
                    .build();
        }

        return null;
    }
}