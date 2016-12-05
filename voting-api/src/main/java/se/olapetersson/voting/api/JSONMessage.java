package se.olapetersson.voting.api;

import com.fasterxml.jackson.databind.JsonNode;

public class JSONMessage {
    private String type;
    private JsonNode payload;

    public JSONMessage(String type, JsonNode payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }
}
