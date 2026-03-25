package BankSdNd.example.BsDnD.config;

import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final BankUserRepository userRepository;

    public SecurityFilter(TokenService tokenService, BankUserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var token = this.recoverToken(request);
        if (token != null) {

            var login = tokenService.validateToken(token);
            if (!login.isEmpty()) {

                userRepository.findByCpf(login).ifPresent(user -> {
                    //!!!!!! Como seu BankUser não implementa UserDetails, passamos null para authorities por enquanto
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, java.util.Collections.emptyList());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            return null;
        }
        return authHeader.substring(7).trim();
    }


}
