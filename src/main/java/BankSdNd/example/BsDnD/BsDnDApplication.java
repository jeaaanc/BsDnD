package BankSdNd.example.BsDnD;

import BankSdNd.example.BsDnD.menu.Menu;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.PersonService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
public class BsDnDApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
		System.setProperty("DB_USER", dotenv.get("DB_USER"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

		SpringApplication.run(BsDnDApplication.class, args);
	}

	@Bean
	CommandLineRunner run (PersonService personService, AccountService accountService, AuthService authService, PasswordEncoder passwordEncoder){
		return args -> {
			if (Arrays.asList(args).contains("--cli")) {
				System.out.println("Iniciando em modo de interface de linha de comando (CLI)...\njava -jar target/BsDnD-0.0.1-SNAPSHOT.jar --cli");

				try (Scanner scanner = new Scanner(System.in)) {
					Menu menu = new Menu(personService, accountService, authService, passwordEncoder);
					menu.display(scanner);
					System.out.println("\nProcesso Finalizado\n");
				}
				System.exit(0);
			}else {
				System.out.println("Aplicação iniciada em modo servidor.");
				System.out.println("Para acessar o menu interativo, execute com o argumento: --cli\njava -jar target/BsDnD-0.0.1-SNAPSHOT.jar --cli   ");
			}
		};
	}

}
