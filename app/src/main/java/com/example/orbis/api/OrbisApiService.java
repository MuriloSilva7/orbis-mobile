package com.example.orbis.api;

import com.example.orbis.model.DashboardResumo;
import com.example.orbis.model.LeituraRequest;
import com.example.orbis.model.LoginRequest;
import com.example.orbis.model.LoginResponse;
import com.example.orbis.model.Manutencao;
import com.example.orbis.model.Maquina;
import com.example.orbis.model.Sensor;
import com.example.orbis.model.TecnicosResponse;
import com.example.orbis.model.Usuario;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrbisApiService {

    // AUTH
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("auth/refresh")
    Call<LoginResponse> refresh(@Body Map<String, String> body);

    @POST("auth/logout")
    Call<Void> logout(@Body Map<String, String> body);

    // PERFIL
    @GET("perfil")
    Call<Usuario> getPerfil();

    @GET("usuarios/{id}")
    Call<Usuario> getUsuario(@Path("id") int id);

    @GET("tecnicos")
    Call<TecnicosResponse> getTecnicos(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @PUT("perfil")
    Call<Usuario> updatePerfil(@Body Map<String, Object> body);

    @POST("perfil/device-token")
    Call<Void> saveDeviceToken(@Body Map<String, String> body);

    // MÁQUINAS
    @GET("maquinas")
    Call<List<Maquina>> getMaquinas();

    @GET("maquinas/{id}")
    Call<Maquina> getMaquina(@Path("id") int id);

    @POST("maquinas")
    Call<Maquina> createMaquina(@Body Map<String, Object> body);

    @PUT("maquinas/{id}")
    Call<Maquina> updateMaquina(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("maquinas/{id}")
    Call<Void> deleteMaquina(@Path("id") int id);

    // SENSORES
    @GET("sensores")
    Call<List<Sensor>> getSensores();

    @GET("sensores/{id}")
    Call<Sensor> getSensor(@Path("id") int id);

    // LEITURAS (sem autenticação)
    @POST("leituras")
    Call<Void> postLeitura(@Body LeituraRequest body);

    @GET("leituras")
    Call<List<Object>> getLeituras();

    // MANUTENÇÕES
    @GET("manutencoes")
    Call<List<Manutencao>> getManutencoes(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @PUT("manutencoes/{id}")
    Call<Manutencao> updateManutencao(
            @Path("id") int id,
            @Body Map<String, Object> body
    );

    // DASHBOARD
    @GET("dashboard/resumo")
    Call<DashboardResumo> getDashboard();
}
