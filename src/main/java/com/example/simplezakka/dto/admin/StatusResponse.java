package com.example.simplezakka.dto.admin;

public class StatusResponse {
    private boolean loggedIn;

    public StatusResponse() {}

    public StatusResponse(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}