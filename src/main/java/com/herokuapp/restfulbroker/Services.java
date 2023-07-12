package com.herokuapp.restfulbroker;

public enum Services {

    BOOKING("booking"),
    AUTH ("auth/");
    private final String service;

    Services(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }
}
