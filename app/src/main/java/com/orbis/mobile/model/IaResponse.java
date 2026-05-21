package com.orbis.mobile.model;

public class IaResponse {

    private String pergunta;
    private String resposta;
    private boolean fallback;

    public String getPergunta() {
        return pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public boolean isFallback() {
        return fallback;
    }
}
