package com.demo.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonPropertyOrder({"error_code", "error_message", "data"})
public class DataResponse {
    @JsonProperty("error_code")
    private final String errorCode;
    @JsonProperty("error_message")
    private final String errorMessage;
    @JsonProperty("data")
    private Object data;
    private DataResponse.DataType dataType = DataResponse.DataType.NORMAL;
    private boolean isEscape = true;


    public DataResponse(String errorCode, String errorMessage, Object data, DataType dataType, boolean isEscape) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
        this.dataType = dataType;
        this.isEscape = isEscape;
    }

    @Override
    public String toString() {
        return DataResponse.toJsonString(this);
    }

    private static String toJsonString(DataResponse dataResponse) {
        try {

            ObjectMapper mapper = null;
            if (dataResponse.isEscape){

            }

        } catch (Exception ex) {

        }
        return "";

    }


    public enum DataType {
        NORMAL, JSON_STR, UNSURE
    }

    ;

}
