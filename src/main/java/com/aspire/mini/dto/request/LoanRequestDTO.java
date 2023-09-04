package com.aspire.mini.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {
    private Double loanAmount;
    private LocalDate loanStartDate;
    private Integer loanTerm;
}
