package BankSdNd.example.BsDnD.controller.api;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.TransferRequest;
import BankSdNd.example.BsDnD.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class TransferRestController {

    private final AccountService accountService;

    public TransferRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> transfer(
            @RequestBody @Valid TransferRequest request,
            @AuthenticationPrincipal BankUser loggedUser
            ) {
        accountService.transfer(
                request.sourceAccountNumber(),
                request.destinationAccountNumber(),
                request.amount(),
                request.password(),
                loggedUser
        );

        return ResponseEntity.ok(Map.of("message", "Transfer completed successfully!"));
    }
}
