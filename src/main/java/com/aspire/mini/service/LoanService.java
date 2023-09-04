package com.aspire.mini.service;

import java.util.List;

import com.aspire.mini.dto.request.LoanRequestDTO;
import com.aspire.mini.dto.request.PaymentRequestDTO;
import com.aspire.mini.dto.response.LoanResponseDTO;

public interface LoanService {

    LoanResponseDTO getLoanByIdAndEmail(Integer id, String email);

    LoanResponseDTO requestLoan(String email, LoanRequestDTO loanRequestDTO);

    List<LoanResponseDTO> getAllLoansByEmail(String email);

    List<LoanResponseDTO> getAllPendingLoans();

    LoanResponseDTO approveLoan(Integer id);

    LoanResponseDTO repayLoan(Integer id, String email, PaymentRequestDTO paymentRequestDTO);
}
