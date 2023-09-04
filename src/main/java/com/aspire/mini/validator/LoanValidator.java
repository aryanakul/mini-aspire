package com.aspire.mini.validator;

import com.aspire.mini.dto.request.LoanRequestDTO;
import com.aspire.mini.dto.request.PaymentRequestDTO;

public class LoanValidator {

    public static void validateLoanId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid Loan Id: Loan Id must be a positive integer.");
        }
    }

    public static void validateLoanRequest(LoanRequestDTO loanRequestDTO) {
        if (loanRequestDTO == null
                || loanRequestDTO.getLoanAmount() <= 0.0
                || loanRequestDTO.getLoanTerm() <= 0) {
            throw new IllegalArgumentException("Invalid Loan Request: Loan amount and term must be positive values.");
        }
    }

    public static void validatePaymentRequest(Integer id, PaymentRequestDTO paymentRequestDTO) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid Loan Id: Loan Id must be a positive integer.");
        }
        if (paymentRequestDTO == null || paymentRequestDTO.getAmount() < 0.0) {
            throw new IllegalArgumentException("Invalid Payment Request: Payment amount must be a non-negative value.");
        }
    }
}
