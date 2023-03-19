package ru.zentsova.Dossier.dto;

public enum Theme {
    FINISH_REGISTRATION("FINISH-REGISTRATION"),
    CREATE_DOCUMENTS("CREATE-DOCUMENTS"),
    SEND_DOCUMENTS("SEND-DOCUMENTS"),
    SEND_SES("SEND-SES"),
    CREDIT_ISSUED("CREDIT-ISSUED"),
    APPLICATION_DENIED("APPLICATION-DENIED");

    private String value;

    Theme(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}