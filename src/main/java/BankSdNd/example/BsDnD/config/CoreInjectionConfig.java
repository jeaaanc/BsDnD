package BankSdNd.example.BsDnD.config;

import BankSdNd.example.BsDnD.core.application.AccountService;
import BankSdNd.example.BsDnD.core.application.AuthService;
import BankSdNd.example.BsDnD.core.application.LoanService;
import BankSdNd.example.BsDnD.core.application.PersonService;
import BankSdNd.example.BsDnD.core.domain.service.FinancialCalculator;
import BankSdNd.example.BsDnD.core.domain.service.StandardFinancialCalculator;
import BankSdNd.example.BsDnD.core.port.out.AccountRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import BankSdNd.example.BsDnD.core.domain.service.AccountNumberGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreInjectionConfig {

    @Bean
    public FinancialCalculator financialCalculator() {
        return new StandardFinancialCalculator();
    }

    @Bean
    public AccountNumberGenerator accountNumberGenerator(AccountRepositoryPort accountRepository) {
        return new AccountNumberGenerator(accountRepository);
    }

    @Bean
    public AccountService accountService(AccountRepositoryPort accountRepository,
                                         BankUserRepositoryPort bankUserRepository,
                                         AccountNumberGenerator accountNumberGenerator,
                                         PasswordEncoderPort passwordEncoder) {
        return new AccountService(accountRepository, bankUserRepository, accountNumberGenerator, passwordEncoder);
    }

    @Bean
    public AuthService authService(BankUserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        return new AuthService(userRepository, passwordEncoder);
    }

    @Bean
    public LoanService loanService(AccountService accountService, 
                                   FinancialCalculator financialCalculator, 
                                   PasswordEncoderPort passwordEncoder) {
        return new LoanService(accountService, financialCalculator, passwordEncoder);
    }

    @Bean
    public PersonService personService(BankUserRepositoryPort personRepository, PasswordEncoderPort passwordEncoder) {
        return new PersonService(personRepository, passwordEncoder);
    }
}
