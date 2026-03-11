package BankSdNd.example.BsDnD.controller.api;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.dto.UserUpdateDtos;
import BankSdNd.example.BsDnD.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserUpdateDtos.UserResponse> login(@RequestBody @Valid LoginDto loginDto){
        BankUser user = authService.login(loginDto);

        UserUpdateDtos.UserResponse response = new UserUpdateDtos.UserResponse(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getPhoneNumber()
        );

        return ResponseEntity.ok(response);
    }
}
