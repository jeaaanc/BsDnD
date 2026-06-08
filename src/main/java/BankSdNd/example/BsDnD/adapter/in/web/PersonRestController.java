package BankSdNd.example.BsDnD.adapter.in.web;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.adapter.in.web.dto.PersonRequest;
import BankSdNd.example.BsDnD.adapter.in.web.dto.UserUpdateDtos;
import BankSdNd.example.BsDnD.core.port.in.CreatePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManagePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManageCredentialsUseCase;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class PersonRestController {

    private final CreatePersonUseCase createPersonUseCase;
    private final ManagePersonUseCase managePersonUseCase;
    private final ManageCredentialsUseCase manageCredentialsUseCase;

    public PersonRestController(CreatePersonUseCase createPersonUseCase, 
                                ManagePersonUseCase managePersonUseCase, 
                                ManageCredentialsUseCase manageCredentialsUseCase) {
        this.createPersonUseCase = createPersonUseCase;
        this.managePersonUseCase = managePersonUseCase;
        this.manageCredentialsUseCase = manageCredentialsUseCase;
    }

    @PostMapping
    public ResponseEntity<BankUser> createPerson(@RequestBody @Valid PersonRequest dto) {

        BankUser savedUser = createPersonUseCase.savePerson(new CreatePersonCommand(
                dto.name(),
                dto.lastName(),
                dto.cpf(),
                dto.phoneNumber(),
                dto.income(),
                dto.password(),
                dto.transactionPassword()
        ));

        URI location = URI.create("/api/users/" + savedUser.getId());

        return ResponseEntity.created(location).body(savedUser);
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<UserUpdateDtos.UserResponse> updateName(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDtos.Name request
    ) {

        BankUser updatedUser = managePersonUseCase.updateName(id, request.name(), request.lastName());

        return ResponseEntity.ok(mapToResponse(updatedUser));
    }

    @PatchMapping("/{id}/phone")
    public ResponseEntity<UserUpdateDtos.UserResponse> updatePhone(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDtos.Phone request
    ) {

        BankUser updatedUser = managePersonUseCase.updatePhoneNumber(id, request.phoneNumber());

        return ResponseEntity.ok(mapToResponse(updatedUser));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDtos.password request
    ) {

         manageCredentialsUseCase.updatePassword(id, request.oldPassword(), request.newPassword());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/transaction-password")
    public ResponseEntity<Void> updateTransactionPassword(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDtos.TransactionPassword request
    ) {

        manageCredentialsUseCase.updateTransactionPassword(id, request.oldTransactionPassword(), request.newTransactionPassword());

        return ResponseEntity.noContent().build();
    }

    private UserUpdateDtos.UserResponse mapToResponse(BankUser user) {
        return new UserUpdateDtos.UserResponse(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getPhoneNumber()
        );
    }
}
