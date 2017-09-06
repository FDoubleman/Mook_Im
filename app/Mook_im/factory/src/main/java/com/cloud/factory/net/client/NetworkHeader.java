package com.cloud.factory.net.client;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "NETWORK_HEADER".
 */
public class NetworkHeader {

    private Long id;
    /** Not-null value. */
    private String uuid;
    private String headerType;
    private String headerKey;
    private String headerValue;

    public NetworkHeader() {
    }

    public NetworkHeader(Long id) {
        this.id = id;
    }

    public NetworkHeader(Long id, String uuid, String headerType, String headerKey, String headerValue) {
        this.id = id;
        this.uuid = uuid;
        this.headerType = headerType;
        this.headerKey = headerKey;
        this.headerValue = headerValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUuid() {
        return uuid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHeaderType() {
        return headerType;
    }

    public void setHeaderType(String headerType) {
        this.headerType = headerType;
    }

    public String getHeaderKey() {
        return headerKey;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

}
