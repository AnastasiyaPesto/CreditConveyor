package ru.zentsova.deal.dto;

public enum Theme {
    FINISH_REGISTRATION("Завершение регистрации"),
    CREATE_DOCUMENTS("Формирование документов"),
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