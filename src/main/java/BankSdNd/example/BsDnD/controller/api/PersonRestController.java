package BankSdNd.example.BsDnD.controller.api;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.dto.UserUpdateDtos;
import BankSdNd.example.BsDnD.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class PersonRestController {

    private final PersonService personService;

    public PersonRestController(PersonService personService) {
        this.personService = personService;
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
            @RequestBody @Valid UserUpdateDtos.Name request) {

        BankUser updatedUser = personService.changeName(id, request.name(), request.lastName());

        return ResponseEntity.ok(mapToResponse(updatedUser));
    }

    @PatchMapping("/{id}/phone")
    public ResponseEntity<UserUpdateDtos.UserResponse> updatePhone(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDtos.Phone request) {

        BankUser updatedUser = personService.changePhoneNumber(id, request.phoneNumber());

        return ResponseEntity.ok(mapToResponse(updatedUser));
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
