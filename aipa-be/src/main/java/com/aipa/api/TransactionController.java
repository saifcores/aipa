package com.aipa.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.aipa.api.dto.TransactionResponse;
import com.aipa.application.TransactionQueryService;

import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Transactions")
@SecurityRequirement(name = "ApiKey")
public class TransactionController {

    private final TransactionQueryService transactionQueryService;

    public TransactionController(TransactionQueryService transactionQueryService) {
        this.transactionQueryService = transactionQueryService;
    }

    @GetMapping
    @Operation(summary = "List all payment transactions (read-only)")
    @Observed(name = "aipa.transactions.list", contextualName = "list-transactions")
    public List<TransactionResponse> list() {
        return transactionQueryService.findAll().stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a payment transaction by technical id")
    @Observed(name = "aipa.transactions.get", contextualName = "get-transaction")
    public TransactionResponse getById(@PathVariable UUID id) {
        return transactionQueryService.findById(id)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found: " + id));
    }
}
