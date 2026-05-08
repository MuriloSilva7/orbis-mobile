package com.orbis.mobile.model;

import java.util.List;

public class TecnicosResponse {

    private List<Usuario> dados;
    private int total;
    private int page;
    private int totalPages;

    public List<Usuario> getDados() {
        return dados;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
