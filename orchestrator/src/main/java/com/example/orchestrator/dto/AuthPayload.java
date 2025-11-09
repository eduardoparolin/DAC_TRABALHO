package com.example.orchestrator.dto;

public class AuthPayload {
    private String requestedById;
    private String action;
    private String messageSource;
    private String sagaId;
    private AuthPayloadData data;

    public AuthPayload() {
        this.data = new AuthPayloadData();
    }

    public String getRequestedById() {
        return requestedById;
    }

    public void setRequestedById(String requestedById) {
        this.requestedById = requestedById;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String messageSource) {
        this.messageSource = messageSource;
    }

    public AuthPayloadData getData() {
        return data;
    }

    public void setData(AuthPayloadData data) {
        this.data = data;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }
}
