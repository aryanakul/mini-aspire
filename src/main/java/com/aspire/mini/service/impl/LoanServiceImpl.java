package com.aspire.mini.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.aspire.mini.dto.request.LoanRequestDTO;
import com.aspire.mini.dto.request.PaymentRequestDTO;
import com.aspire.mini.dto.response.LoanResponseDTO;
import com.aspire.mini.enums.LoanStatus;
import com.aspire.mini.enums.PaymentStatus;
import com.aspire.mini.service.LoanService;

import lombok.extern.slf4j.Slf4j;

import com.aspire.mini.model.Loan;
import com.aspire.mini.model.Payment;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

    private final List<Loan> loans;

    private final ModelMapper modelMapper;

    private final int PAYMENT_FREQUENCY = 7;

    private final int DOUBLE_SCALE = 2;

    public LoanServiceImpl(List<Loan> loans, ModelMapper modelMapper) {
        this.loans = loans;
        this.modelMapper = modelMapper;
    }

    /**
     * Approves a loan with the specified ID, if it exists and is in a pending
     * status.
     *
     * @param id The ID of the loan to be approved.
     * @return A LoanResponseDTO representing the approved loan, or null if the loan
     *         is not found or cannot be approved.
     */
    @Override
    public LoanResponseDTO approveLoan(Integer id) {
        try {
            log.info("Loan approval for id " + id);
            Loan loanToApprove = this.loans.stream()
                    .filter(loan -> loan.getLoanId().equals(id)
                            && loan.getLoanStatus().equals(LoanStatus.PENDING))
                    .findFirst()
                    .orElse(null);

            if (loanToApprove != null) {
                loanToApprove.setLoanStatus(LoanStatus.APPROVED);
                return loanToDto(loanToApprove);
            } else {
                log.error("Loan not found for id " + id);
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a loan by its ID and user email, if it exists.
     *
     * @param id    The ID of the loan to retrieve.
     * @param email The email of the user associated with the loan.
     * @return A LoanResponseDTO representing the loan, or null if the loan is not
     *         found.
     */
    @Override
    public LoanResponseDTO getLoanByIdAndEmail(Integer id, String email) {
        try {
            Loan matchingLoan = this.loans.stream()
                    .filter(loan -> loan.getLoanId().equals(id)
                            && loan.getUserEmail().equals(email))
                    .findFirst()
                    .orElse(null);

            if (matchingLoan != null) {
                return loanToDto(matchingLoan);
            } else {
                log.error("Loan not found for id " + id);
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * Creates a new loan based on the provided loan request and associates it with
     * the user's email.
     *
     * @param email          The email of the user requesting the loan.
     * @param loanRequestDTO The loan request data.
     * @return A LoanResponseDTO representing the newly created loan, or null if
     *         there was an error.
     */
    @Override
    public LoanResponseDTO requestLoan(String email, LoanRequestDTO loanRequestDTO) {
        try {
            Loan loan = new Loan();
            loan.setLoanId(this.loans.size() + 1);
            loan.setUserEmail(email);
            loan.setLoanAmount(loanRequestDTO.getLoanAmount());
            loan.setRemainingBalance(loanRequestDTO.getLoanAmount());
            loan.setLoanStartDate(loanRequestDTO.getLoanStartDate());
            loan.setLoanStatus(LoanStatus.PENDING);
            loan.setPaymentFrequency(PAYMENT_FREQUENCY);
            loan.setLoanTerm(loanRequestDTO.getLoanTerm());
            loan.setPayments(createPayments(
                    loanRequestDTO.getLoanAmount(),
                    loanRequestDTO.getLoanTerm(),
                    loanRequestDTO.getLoanStartDate(),
                    loan.getPaymentFrequency()));
            this.loans.add(loan);
            return loanToDto(loan);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * Converts a double value to the specified decimal scale.
     *
     * @param value The double value to be scaled.
     * @return The scaled double value.
     */
    private Double doubleWithScale(Double value) {
        return BigDecimal.valueOf(value).setScale(DOUBLE_SCALE, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Creates a list of Payment objects for a loan based on the loan amount, loan
     * term, start date, and payment frequency.
     *
     * @param loanAmount       The total loan amount.
     * @param loanTerm         The loan term in weeks.
     * @param loanStartDate    The start date of the loan.
     * @param paymentFrequency The frequency of payments in days.
     * @return A List of Payment objects representing the loan payments, or null if
     *         there was an error.
     */
    private List<Payment> createPayments(
            double loanAmount, int loanTerm, LocalDate loanStartDate, int paymentFrequency) {
        try {
            List<Payment> payments = new ArrayList<Payment>();
            double remainingPayment = loanAmount;
            for (int i = 1; i <= loanTerm; i++) {
                Double paymentAmount = doubleWithScale(loanAmount / loanTerm);
                Payment payment = new Payment(
                        loanStartDate.plusDays(i * paymentFrequency),
                        PaymentStatus.PENDING,
                        paymentAmount);
                remainingPayment -= paymentAmount;
                payments.add(payment);
            }
            if (remainingPayment > 0.0) {
                payments.get(0).setAmount(doubleWithScale(payments.get(0).getAmount() + remainingPayment));
            }
            return payments;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

    /**
     * Retrieves a list of loans associated with a user's email.
     *
     * @param email The email of the user whose loans are to be retrieved.
     * @return A List of LoanResponseDTO objects representing the user's loans, or
     *         an empty list if none are found.
     */
    @Override
    public List<LoanResponseDTO> getAllLoansByEmail(String email) {
        try {
            List<LoanResponseDTO> customerLoans = this.loans.stream()
                    .filter(loan -> loan.getUserEmail().equals(email))
                    .map(this::loanToDto)
                    .collect(Collectors.toList());

            return customerLoans;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    private LoanResponseDTO loanToDto(Loan loan) {
        return this.modelMapper.map(loan, LoanResponseDTO.class);
    }

    /**
     * Retrieves a list of all pending loans.
     *
     * @return A List of LoanResponseDTO objects representing pending loans, or an
     *         empty list if none are found.
     */
    @Override
    public List<LoanResponseDTO> getAllPendingLoans() {
        try {
            List<LoanResponseDTO> pendingLoans = this.loans.stream()
                    .filter(loan -> loan.getLoanStatus().equals(LoanStatus.PENDING))
                    .map(this::loanToDto)
                    .collect(Collectors.toList());

            return pendingLoans;

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Closes a loan by marking pending payments as paid and updating the loan's
     * status.
     *
     * @param loanToClosePosition The position of the loan to close in the list of
     *                            loans.
     * @param loanToClose         The loan to be closed.
     * @param amount              The amount used to pay off the remaining balance
     *                            of the loan.
     * @return A LoanResponseDTO representing the closed loan, or null if the loan
     *         was not found.
     */
    private LoanResponseDTO closeLoan(int loanToClosePosition, Loan loanToClose, double amount) {
        int prepayTerm = -1;
        for (int i = 0; i < loanToClose.getPayments().size(); i++) {
            if (loanToClose.getPayments().get(i).getPaymentStatus().equals(PaymentStatus.PENDING)) {
                loanToClose.getPayments().get(i).setAmount(amount);
                loanToClose.getPayments().get(i).setPaymentStatus(PaymentStatus.PAID);
                prepayTerm = i;
                break;
            }
        }
        if (prepayTerm != -1 || prepayTerm != loanToClose.getPayments().size() - 1) {
            loanToClose.setPayments(loanToClose.getPayments().subList(0, prepayTerm + 1));
        }
        loanToClose.setLoanStatus(LoanStatus.PAID);
        this.loans.set(loanToClosePosition, loanToClose);
        return loanToDto(loanToClose);
    }

    /**
     * Repays a loan with the specified ID and updates its payment status and
     * remaining balance.
     *
     * @param id                The ID of the loan to repay.
     * @param email             The email of the user making the repayment.
     * @param paymentRequestDTO The payment details including the amount to repay.
     * @return A LoanResponseDTO representing the updated loan, or null if the loan
     *         was not found or if the
     *         payment could not be processed.
     */
    @Override
    public LoanResponseDTO repayLoan(Integer id, String email, PaymentRequestDTO paymentRequestDTO) {
        Loan loanToRepay = null;
        int loanToRepayPosition = -1;
        for (int i = 0; i < this.loans.size(); i++) {
            if (this.loans.get(i).getLoanId().equals(id)
                    && this.loans.get(i).getLoanStatus().equals(LoanStatus.APPROVED)
                    && this.loans.get(i).getUserEmail().equals(email)) {
                loanToRepay = this.loans.get(i);
                loanToRepayPosition = i;
                break;
            }
        }
        if (loanToRepay == null) {
            return null;
        } else {
            loanToRepay.setRemainingBalance(
                    doubleWithScale(loanToRepay.getRemainingBalance() - paymentRequestDTO.getAmount()));
            // if remaining balance is zero close the loan
            if (loanToRepay.getRemainingBalance() <= 0.0) {
                // close loan
                return closeLoan(loanToRepayPosition, loanToRepay, paymentRequestDTO.getAmount());
            } else {
                Payment currentPayment = null;
                int currentPaymentPosition = -1;
                for (int i = 0; i < loanToRepay.getPayments().size(); i++) {
                    if (loanToRepay.getPayments().get(i).getPaymentStatus().equals(PaymentStatus.PENDING)) {
                        currentPayment = loanToRepay.getPayments().get(i);
                        currentPaymentPosition = i;
                        break;
                    }
                }
                if (currentPayment == null) {
                    return null;
                } else {
                    if (currentPayment.getAmount().equals(paymentRequestDTO.getAmount())) {
                        // no need to recalculate remaining payments
                        currentPayment.setPaymentStatus(PaymentStatus.PAID);
                        loanToRepay.getPayments().set(currentPaymentPosition, currentPayment);
                        this.loans.set(loanToRepayPosition, loanToRepay);
                        return loanToDto(loanToRepay);
                    } else {
                        // bigger payment made than required
                        // updating this so that helps in future reference
                        currentPayment.setAmount(paymentRequestDTO.getAmount());
                        currentPayment.setPaymentStatus(PaymentStatus.PAID);
                        loanToRepay.getPayments().set(currentPaymentPosition, currentPayment);
                        List<Payment> paidPayments = loanToRepay
                                .getPayments()
                                .subList(0, currentPaymentPosition + 1);
                        List<Payment> newPayments = createPayments(
                                loanToRepay.getRemainingBalance(),
                                loanToRepay.getLoanTerm() - currentPaymentPosition - 1,
                                paymentRequestDTO.getPaymentDate(),
                                loanToRepay.getPaymentFrequency());
                        loanToRepay.setPayments(paidPayments);
                        loanToRepay.getPayments().addAll(newPayments);
                        this.loans.set(currentPaymentPosition, loanToRepay);
                        return loanToDto(loanToRepay);
                    }
                }
            }
        }
    }
}
