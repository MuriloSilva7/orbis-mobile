package com.orbis.mobile.api;

import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.model.DashboardResumo;
import com.orbis.mobile.model.LeituraRequest;
import com.orbis.mobile.model.LoginRequest;
import com.orbis.mobile.model.LoginResponse;
import com.orbis.mobile.model.Manutencao;
import com.orbis.mobile.model.ManutencoesResponse;
import com.orbis.mobile.model.Maquina;
import com.orbis.mobile.model.Sensor;
import com.orbis.mobile.model.TecnicosResponse;
import com.orbis.mobile.model.Usuario;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
    Call<Void> saveDeviceToken(
            @Header("Authorization") String token,
            @Body Map<String, String> body
    );

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
    Call<ManutencoesResponse> getManutencoes(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("manutencoes")
    Call<Manutencao> createManutencao(
            @Body Map<String, Object> body
    );

    @PUT("manutencoes/{id}")
    Call<Manutencao> updateManutencao(
            @Path("id") int id,
            @Body Map<String, Object> body
    );

    @GET("manutencoes/alerta/{id}")
    Call<List<Manutencao>> getManutencoesByAlerta(@Path("id") int alertaId);

    // DASHBOARD
    @GET("dashboard/resumo")
    Call<DashboardResumo> getDashboard();

    //ALERTAS

    @GET("alertas")
    Call<List<Alerta>> getAlertas();
}
