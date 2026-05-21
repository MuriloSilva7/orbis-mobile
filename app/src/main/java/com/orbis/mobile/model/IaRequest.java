package com.orbis.mobile.model;

import java.util.List;

public class IaRequest {
    private String pergunta;
    private List<ChatMessage> historico;

    public IaRequest(String pergunta, List<ChatMessage> historico) {
        this.pergunta = pergunta;
        this.historico = historico;
    }
}
