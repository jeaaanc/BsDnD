package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.model.BankUser;
import BankSdNd.example.BsDnD.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Scanner;

public class Cadastro {

    @Autowired
    private final PersonService personService;

    public Cadastro(PersonService personService) {
        this.personService = personService;
    }

    public void register(Scanner scanner) {
        int opcao = 1;
        while (true){
            System.out.println("\nTELA DE CADASTRO!!!!!\n");
            System.out.println("Nome:");
            String name = scanner.nextLine();
            System.out.println("Sobrenome:");
            String lastName = scanner.nextLine();

            System.out.println("CPF:");
            String cpf = scanner.nextLine();

            System.out.println("Celular:");
            String phoneNumber = scanner.nextLine();

            System.out.println("Senha:");
            String password = scanner.nextLine();

            System.out.println("Confirma a Senha:");
            String confirmedPassword = scanner.nextLine();


            PersonDto dto = new PersonDto(name, lastName, cpf, phoneNumber, password, confirmedPassword);

            try {
                BankUser person = personService.savePerson(dto);
                System.out.println("\nNovo usuário criado\n");
            }catch (IllegalArgumentException e){
                System.out.println("Erro de Validação: " + e.getMessage());
            }

            System.out.println("\n1/ Para Cadastrar outro usuário ou 2/ para parar");
            opcao = scanner.nextInt();
            scanner.nextLine();
        }
    }
}
