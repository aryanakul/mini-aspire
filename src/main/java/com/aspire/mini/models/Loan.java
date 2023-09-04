package com.aspire.mini.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.aspire.mini.enums.LoanStatus;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Loan {
    private Integer loanId;
    @NonNull
    private String userEmail;
    @NonNull
    private Double loanAmount;
    private Double remainingBalance;
    @NonNull
    private LocalDate loanStartDate;
    @NonNull
    private LoanStatus loanStatus;
    @NonNull
    private Integer paymentFrequency;
    @NonNull
    private Integer loanTerm;
    private List<Payment> payments;
}
