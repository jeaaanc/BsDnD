package BankSdNd.example.BsDnD.controller.api;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.dto.AccountRequest;
import BankSdNd.example.BsDnD.dto.AccountResponse;
import BankSdNd.example.BsDnD.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountRestController {

    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody @Valid AccountRequest request) {

        Account account = accountService.accountCreate(request.cpf());

        AccountResponse response = new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getTitular().getName() + " " + account.getTitular().getLastName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {

        accountService.softDeleteAccount(id);

        return ResponseEntity.noContent().build();
    }
}
