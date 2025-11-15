package com.bank.manager.config;

public class ManagerSagaEvent {

    private String eventType; // e.g., "CREATE", "UPDATE", "DELETE"
    private String payload;   // JSON contendo os dados do manager

    public ManagerSagaEvent() {
    }

    public ManagerSagaEvent(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}