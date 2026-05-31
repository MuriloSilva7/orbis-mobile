package com.orbis.mobile.model;

import com.google.gson.annotations.SerializedName;

public class PredicaoAlertas {

    @SerializedName("maquinaId")
    private int maquinaId;

    @SerializedName("proximoAlerta")
    private AlertaPrevisao proximoAlerta;

    @SerializedName("ausenciaProximoAlerta")
    private AusenciaMotivo ausenciaProximoAlerta;

    @SerializedName("instabilidade")
    private AlertaPrevisao instabilidade;

    @SerializedName("ausenciaInstabilidade")
    private AusenciaMotivo ausenciaInstabilidade;

    @SerializedName("modeloIntegridade")
    private ModeloIntegridade modeloIntegridade;

    // Getters
    public int getMaquinaId() { return maquinaId; }
    public AlertaPrevisao getProximoAlerta() { return proximoAlerta; }
    public AusenciaMotivo getAusenciaProximoAlerta() { return ausenciaProximoAlerta; }
    public AlertaPrevisao getInstabilidade() { return instabilidade; }
    public AusenciaMotivo getAusenciaInstabilidade() { return ausenciaInstabilidade; }
    public ModeloIntegridade getModeloIntegridade() { return modeloIntegridade; }

    // -------------------------------------------------------
    public static class AlertaPrevisao {
        @SerializedName("tipo")
        private String tipo;

        @SerializedName("dataPrevista")
        private String dataPrevista;

        @SerializedName("integridadeLimiar")
        private Double integridadeLimiar;

        @SerializedName("confianca")
        private Double confianca;

        @SerializedName("fonteLimiar")
        private String fonteLimiar;

        @SerializedName("amostrasLimiar")
        private Integer amostrasLimiar;

        public String getTipo() { return tipo; }
        public String getDataPrevista() { return dataPrevista; }
        public Double getIntegridadeLimiar() { return integridadeLimiar; }
        public Double getConfianca() { return confianca; }
        public String getFonteLimiar() { return fonteLimiar; }
        public Integer getAmostrasLimiar() { return amostrasLimiar; }
    }

    // -------------------------------------------------------
    public static class ModeloIntegridade {
        @SerializedName("r2")
        private Double r2;

        @SerializedName("slope")
        private Double slope;

        @SerializedName("intercept")
        private Double intercept;

        @SerializedName("pontosUsados")
        private Integer pontosUsados;

        public Double getR2() { return r2; }
        public Double getSlope() { return slope; }
        public Double getIntercept() { return intercept; }
        public Integer getPontosUsados() { return pontosUsados; }
    }

    // -------------------------------------------------------
    public static class AusenciaMotivo {
        @SerializedName("motivo")
        private String motivo;

        public String getMotivo() { return motivo; }
    }
}
