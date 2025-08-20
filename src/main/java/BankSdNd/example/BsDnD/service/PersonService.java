package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
@Service
public class PersonService {
    private BankUserRepository personRepository;

    private final PasswordValidationService passwordValidationService;
    private final PasswordEncoder passwordEncoder;

    public PersonService(BankUserRepository personRepository, PasswordValidationService passwordValidationService,
                         PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordValidationService = passwordValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    public BankUser savePerson (PersonDto dto) {

        if (personRepository.existsByCpf(dto.getCpf())){
            throw new RuntimeException("\nCPF já cadastrado\n");
        }

        if (personRepository.existsByPhoneNumber(dto.getPhoneNumber())){
            throw new RuntimeException("\nNúmero de telefone já cadastrado!!!!!\n");
        }

        if (!dto.getRawpassword().equals(dto.getConfirmedrawPassword())){
            throw new IllegalArgumentException("As senhas não coincidem");
        }

        String encodedPassword = passwordEncoder.encode(dto.getRawpassword());

        BankUser person = new BankUser.Builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .cpf(dto.getCpf())
                .income(dto.getIncome())
                .phoneNumber(dto.getPhoneNumber())
                .passWord(encodedPassword)
                .build();

        return personRepository.save(person);
    }

    public BankUser login (LoginDto dto){
        Optional<BankUser> optional = personRepository.findByCpf(dto.getCpf());

        if (optional.isEmpty()){
            throw new IllegalArgumentException("\nCPF não encontrado\n");
        }

        BankUser person = optional.get();

        if (!person.getPassword().equals(dto.getPassword())){
            throw new IllegalArgumentException("\nSenha Incorreta\n");
        }

        return person;
    }

    public boolean checkUserPassword(Long userId, String rawPassword){
        BankUser user =personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return passwordValidationService.validatePassword(rawPassword, user.getPassword());
    }

}
