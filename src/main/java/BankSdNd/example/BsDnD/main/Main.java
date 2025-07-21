package BankSdNd.example.BsDnD.main;
import BankSdNd.example.BsDnD.menu.Menu;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.PersonService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;


@SpringBootApplication(scanBasePackages = "BankSdNd.example.BsDnD")

public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    @Bean
    CommandLineRunner run (PersonService personService, AccountService accountService){
        return args -> {
            Scanner scanner = new Scanner(System.in);

            Menu menu = new Menu(personService, accountService);
            menu.display(scanner);

            System.out.println("\nProcesso Finalizado\n");

        };
    }
}
