package com.aspire.mini.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.aspire.mini.enums.LoanStatus;
import com.aspire.mini.model.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {

    private Integer loanId;
    private String userEmail;
    private Double loanAmount;
    private Double remainingBalance;
    private LocalDate loanStartDate;
    private LoanStatus loanStatus;
    private Integer loanTerm;
    private List<Payment> payments;
}
