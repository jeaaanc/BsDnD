package BankSdNd.example.BsDnD.controller.api;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.dto.LoginResponseDto;
import BankSdNd.example.BsDnD.dto.UserUpdateDtos;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.TokenService;
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
    private final TokenService tokenService;

    public AuthRestController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginDto data){
        BankUser user = authService.login(data);

        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
