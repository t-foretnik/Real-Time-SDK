package com.oraise.client;

import java.util.Map;

import com.oraise.model.Data;

class DataImpl implements Data {

    private final String identifier;
    private final Map<Integer, String> data;

    public DataImpl(String identifer, Map<Integer, String> data) {
        this.identifier = identifer;
        this.data = data;
    }

    @Override
    public Map<Integer, String> data() {
        return data;
    }

    @Override
    public String identifier() {
        return identifier;
    }
}
