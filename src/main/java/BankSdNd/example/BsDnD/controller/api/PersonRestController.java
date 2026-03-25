package BankSdNd.example.BsDnD.controller.api;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.dto.UserUpdateDtos;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class PersonRestController {

    private final PersonService personService;
    private final AuthService authService;

    public PersonRestController(PersonService personService, AuthService authService) {
        this.personService = personService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<BankUser> createPerson(@RequestBody @Valid PersonDto dto) {

        BankUser savedUser = personService.savePerson(dto);

        URI location = URI.create("/api/users/" + savedUser.getId());

        return ResponseEntity.created(location).body(savedUser);
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<UserUpdateDtos.UserResponse> updateName(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDtos.Name request,
            @AuthenticationPrincipal BankUser loggedUser
    ) {

        BankUser updatedUser = personService.updateName(id, request.name(), request.lastName(), loggedUser);

        return ResponseEntity.ok(mapToResponse(updatedUser));
    }

    @GetMapping
    public ResponseEntity<List<UserUpdateDtos.UserResponse>> listAll() {
        List<BankUser> users = personService.findAll();

        List<UserUpdateDtos.UserResponse> response = users.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/phone")
    public ResponseEntity<UserUpdateDtos.UserResponse> updatePhone(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDtos.Phone request,
            @AuthenticationPrincipal BankUser loggedUser
    ) {

        BankUser updatedUser = personService.updatePhoneNumber(id, request.phoneNumber(), loggedUser);

        return ResponseEntity.ok(mapToResponse(updatedUser));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDtos.password request,
            @AuthenticationPrincipal BankUser loggedUser
    ) {

         authService.updatePassword(id, request.oldPassword(), request.newPassword(), loggedUser);

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
