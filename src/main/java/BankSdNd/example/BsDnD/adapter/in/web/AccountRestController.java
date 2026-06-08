package BankSdNd.example.BsDnD.adapter.in.web;

import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.adapter.in.web.dto.AccountCreateRequest;
import BankSdNd.example.BsDnD.adapter.in.web.dto.AccountResponse;
import BankSdNd.example.BsDnD.adapter.in.web.dto.UserUpdateDtos;
import BankSdNd.example.BsDnD.core.port.in.CreateAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManageAccountUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountRestController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final ManageAccountUseCase manageAccountUseCase;

    public AccountRestController(CreateAccountUseCase createAccountUseCase, 
                                 GetAccountUseCase getAccountUseCase, 
                                 ManageAccountUseCase manageAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.manageAccountUseCase = manageAccountUseCase;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@AuthenticationPrincipal(expression = "user") BankUser user, 
                                                       @RequestBody @Valid AccountCreateRequest request) {

        Account account = createAccountUseCase.createAccount(user.getCpf(), request.transactionPassword());
        return ResponseEntity.ok(mapToAccountResponse(account));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> listMyAccounts(@AuthenticationPrincipal(expression = "user") BankUser user) {
        List<Account> accounts = getAccountUseCase.findAllByUserCpf(user.getCpf());

        List<AccountResponse> responses = accounts.stream()
                .map(this::mapToAccountResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {

        manageAccountUseCase.softDeleteAccount(id);

        return ResponseEntity.noContent().build();
    }

    private AccountResponse mapToAccountResponse(Account account) {
        UserUpdateDtos.UserResponse userDto = new UserUpdateDtos.UserResponse(
                account.getHolder().getId(),
                account.getHolder().getName(),
                account.getHolder().getLastName(),
                account.getHolder().getPhoneNumber()
        );

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                userDto
        );
    }
}
