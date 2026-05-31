package com.orbis.mobile.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PredicaoRisco {

    @SerializedName("maquinaId")
    private int maquinaId;

    @SerializedName("modeloVersao")
    private String modeloVersao;

    @SerializedName("riscos")
    private Riscos riscos;

    @SerializedName("fatoresPrincipais")
    private List<String> fatoresPrincipais;

    @SerializedName("confiancaGeral")
    private Double confiancaGeral;

    @SerializedName("metadados")
    private Metadados metadados;

    // Getters
    public int getMaquinaId() { return maquinaId; }
    public String getModeloVersao() { return modeloVersao; }
    public Riscos getRiscos() { return riscos; }
    public List<String> getFatoresPrincipais() { return fatoresPrincipais; }
    public Double getConfiancaGeral() { return confiancaGeral; }
    public Metadados getMetadados() { return metadados; }

    // -------------------------------------------------------
    public static class Riscos {
        @SerializedName("instabilidade")
        private BlocoRisco instabilidade;

        @SerializedName("alerta")
        private BlocoRisco alerta;

        @SerializedName("manutencao")
        private BlocoRisco manutencao;

        public BlocoRisco getInstabilidade() { return instabilidade; }
        public BlocoRisco getAlerta() { return alerta; }
        public BlocoRisco getManutencao() { return manutencao; }
    }

    // -------------------------------------------------------
    public static class BlocoRisco {
        @SerializedName("24h")
        private Double h24;

        @SerializedName("72h")
        private Double h72;

        @SerializedName("classificacao")
        private String classificacao;

        @SerializedName("motivoAusencia")
        private String motivoAusencia;

        public Double getH24() { return h24; }
        public Double getH72() { return h72; }
        public String getClassificacao() { return classificacao; }
        public String getMotivoAusencia() { return motivoAusencia; }

        public boolean isDisponivel() {
            return classificacao != null;
        }
    }

    // -------------------------------------------------------
    public static class Metadados {
        @SerializedName("pontosHistoricoIntegridade")
        private Integer pontosHistoricoIntegridade;

        @SerializedName("leiturasConsideradas")
        private Integer leiturasConsideradas;

        @SerializedName("alertasRecentesConsiderados")
        private Integer alertasRecentesConsiderados;

        public Integer getPontosHistoricoIntegridade() { return pontosHistoricoIntegridade; }
        public Integer getLeiturasConsideradas() { return leiturasConsideradas; }
        public Integer getAlertasRecentesConsiderados() { return alertasRecentesConsiderados; }
    }
}
