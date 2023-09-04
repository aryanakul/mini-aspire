package com.aspire.mini.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.mini.dto.request.LoanRequestDTO;
import com.aspire.mini.dto.request.PaymentRequestDTO;
import com.aspire.mini.dto.response.LoanResponseDTO;
import com.aspire.mini.enums.UserRole;
import com.aspire.mini.service.LoanService;
import com.aspire.mini.utility.AppUtils;
import com.aspire.mini.validator.LoanValidator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final AppUtils appUtils;

    private final LoanService loanService;

    public LoanController(AppUtils appUtils, LoanService loanService) {
        this.appUtils = appUtils;
        this.loanService = loanService;
    }

    /**
     * Retrieve a loan by its unique identifier for a specific user.
     *
     * @param id    The unique identifier of the loan.
     * @param token The JWT authorization token.
     * @return A ResponseEntity containing the LoanResponseDTO if found, or a
     *         relevant error response.
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getLoanByIdAndEmail(
            @PathVariable Integer id,
            @RequestHeader(name = "Authorization") String token) {
        try {
            LoanValidator.validateLoanId(id);
            Jws<Claims> claims = this.appUtils.validateJWTAndReturnClaims(token);
            if (claims == null) {
                return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }
            String email = String.valueOf(claims.getBody().get("sub"));
            LoanResponseDTO loanResponseDTO = this.loanService.getLoanByIdAndEmail(id, email);
            if (loanResponseDTO != null) {
                return new ResponseEntity<LoanResponseDTO>(loanResponseDTO, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Request a new loan for a specific user.
     *
     * @param loanRequestDTO The LoanRequestDTO containing loan request details.
     * @param token          The JWT authorization token.
     * @return A ResponseEntity containing the LoanResponseDTO if the loan request
     *         is successful, or a relevant error response.
     */
    @PostMapping()
    public ResponseEntity<?> requestLoan(
            @RequestBody LoanRequestDTO loanRequestDTO,
            @RequestHeader(name = "Authorization") String token) {
        try {
            LoanValidator.validateLoanRequest(loanRequestDTO);
            Jws<Claims> claims = this.appUtils.validateJWTAndReturnClaims(token);
            if (claims == null) {
                return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }
            String email = String.valueOf(claims.getBody().get("sub"));
            LoanResponseDTO loanResponseDTO = this.loanService.requestLoan(email, loanRequestDTO);
            if (loanRequestDTO != null) {
                return new ResponseEntity<LoanResponseDTO>(loanResponseDTO, HttpStatus.CREATED);
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieve all loans associated with the authenticated user.
     *
     * @param token The JWT authorization token.
     * @return A ResponseEntity containing a list of LoanResponseDTOs if found, or a
     *         relevant error response.
     */
    @GetMapping()
    public ResponseEntity<?> getAllLoansByEmail(@RequestHeader(name = "Authorization") String token) {
        try {
            Jws<Claims> claims = this.appUtils.validateJWTAndReturnClaims(token);
            if (claims == null) {
                return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }
            String email = String.valueOf(claims.getBody().get("sub"));
            return new ResponseEntity<List<LoanResponseDTO>>(
                    this.loanService.getAllLoansByEmail(email), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieve all pending loans for approval.
     *
     * @param token The JWT authorization token.
     * @return A ResponseEntity containing a list of LoanResponseDTOs if found, or a
     *         relevant error response.
     */
    @GetMapping(path = "/approve")
    public ResponseEntity<?> getAllPendingLoans(
            @RequestHeader(name = "Authorization") String token) {
        try {
            Jws<Claims> claims = this.appUtils.validateJWTAndReturnClaims(token);
            if (claims == null
                    || !StringUtils.pathEquals(String.valueOf(claims.getBody().get("role")), UserRole.ADMIN.toString()))
                return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<List<LoanResponseDTO>>(
                    this.loanService.getAllPendingLoans(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    /**
     * Approve a loan request.
     *
     * @param id    The unique identifier of the loan to approve.
     * @param token The JWT authorization token.
     * @return A ResponseEntity containing the approved LoanResponseDTO if
     *         successful, or a relevant error response.
     */
    @PutMapping(path = "approve/{id}")
    public ResponseEntity<?> approveLoan(@PathVariable Integer id,
            @RequestHeader(name = "Authorization") String token) {
        try {
            Jws<Claims> claims = this.appUtils.validateJWTAndReturnClaims(token);
            if (claims == null
                    || !StringUtils.pathEquals(String.valueOf(claims.getBody().get("role")), UserRole.ADMIN.toString()))
                return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
            LoanResponseDTO loanResponseDTO = this.loanService.approveLoan(id);
            if (loanResponseDTO != null) {
                return new ResponseEntity<LoanResponseDTO>(loanResponseDTO, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Repay a loan by making a payment for the specified loan.
     *
     * @param id                The unique identifier of the loan to repay.
     * @param paymentRequestDTO The PaymentRequestDTO containing payment details.
     * @param token             The JWT authorization token.
     * @return A ResponseEntity containing the updated LoanResponseDTO if
     *         successful, or a relevant error response.
     */
    @PostMapping(path = "/{id}/repay")
    public ResponseEntity<?> repayLoan(@PathVariable Integer id,
            @RequestBody PaymentRequestDTO paymentRequestDTO,
            @RequestHeader(name = "Authorization") String token) {
        try {
            LoanValidator.validatePaymentRequest(id, paymentRequestDTO);
            Jws<Claims> claims = this.appUtils.validateJWTAndReturnClaims(token);
            if (claims == null) {
                return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }
            String email = String.valueOf(claims.getBody().get("sub"));
            LoanResponseDTO repaidLoan = this.loanService.repayLoan(id, email, paymentRequestDTO);
            if (repaidLoan != null) {
                return new ResponseEntity<LoanResponseDTO>(repaidLoan, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
