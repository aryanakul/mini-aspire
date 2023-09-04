package com.aspire.mini.model;

import java.time.LocalDate;

import com.aspire.mini.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Payment {

    @NonNull
    private LocalDate paymentDate;
    @NonNull
    private PaymentStatus paymentStatus;
    @NonNull
    private Double amount;
}
