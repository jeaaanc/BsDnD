package BankSdNd.example.BsDnD.adapter.in.web;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.adapter.in.web.dto.LoginRequest;
import BankSdNd.example.BsDnD.adapter.in.web.dto.LoginResponseDto;
import BankSdNd.example.BsDnD.core.port.in.AuthenticateUserUseCase;
import BankSdNd.example.BsDnD.core.port.in.dto.LoginCommand;
import BankSdNd.example.BsDnD.adapter.out.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final TokenService tokenService;

    public AuthRestController(AuthenticateUserUseCase authenticateUserUseCase, TokenService tokenService) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequest data){
        BankUser user = authenticateUserUseCase.login(new LoginCommand(data.cpf(), data.password()));

        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
