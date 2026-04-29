package com.example.orbis.model;

import java.util.List;

public class TecnicosResponse {

    private List<Usuario> data;
    private int total;
    private int page;
    private int limit;

    public List<Usuario> getData() {
        return data;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

}
