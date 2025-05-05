package com.apps.salaryfilegenerator.dto;

import java.util.List;

public class PasteEventDataObject {
    private String formType;
    private String itemId;
    private String fieldName;
    private List<String> payload;

    public PasteEventDataObject() {
    }

    public PasteEventDataObject(String formType, String itemId, String fieldName, List<String> payload) {
        this.formType = formType;
        this.itemId = itemId;
        this.fieldName = fieldName;
        this.payload = payload;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<String> getPayload() {
        return payload;
    }

    public void setPayload(List<String> payload) {
        this.payload = payload;
    }
}