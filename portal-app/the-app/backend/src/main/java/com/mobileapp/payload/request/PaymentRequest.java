package com.mobileapp.payload.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull
    @Min(0)
    private Double amount;

    private String cardNumber;

    private String iban;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
} 