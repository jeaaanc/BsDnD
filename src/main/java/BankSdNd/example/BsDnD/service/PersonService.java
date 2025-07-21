package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.model.BankUser;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private BankUserRepository personRepository;

    public BankUser savePerson (PersonDto dto) {

        if (personRepository.existsByCpf(dto.getCpf())){
            throw new RuntimeException("\nCPF já cadastrado\n");
        }

        if (personRepository.existsByPhoneNumber(dto.getPhoneNumber())){
            throw new RuntimeException("\nNúmero de telefone já cadastrado!!!!!\n");
        }

        if (!dto.getPassword().equals(dto.getConfirmedPassword())){
            throw new IllegalArgumentException("As senhas não coincidem");
        }

        BankUser person = new BankUser.Builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .cpf(dto.getCpf())
                .phoneNumber(dto.getPhoneNumber())
                .passWord(dto.getPassword())
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
}
