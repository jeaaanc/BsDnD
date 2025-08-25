package BankSdNd.example.BsDnD;

import BankSdNd.example.BsDnD.menu.Menu;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.PersonService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class BsDnDApplication {

	public static void main(String[] args) {
		SpringApplication.run(BsDnDApplication.class, args);
	}
	@Bean
	CommandLineRunner run (PersonService personService, AccountService accountService, AuthService authService){
		return args -> {
			try(Scanner scanner = new Scanner(System.in)) {
				Menu menu = new Menu(personService, accountService, authService);
				menu.display(scanner);
				System.out.println("\nProcesso Finalizado\n");
			}
		};
	}

}
