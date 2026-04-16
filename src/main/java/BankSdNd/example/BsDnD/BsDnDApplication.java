package BankSdNd.example.BsDnD;

import BankSdNd.example.BsDnD.controller.cli.ConsoleController;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

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
	CommandLineRunner run (ConsoleController consoleController){
		return args -> {
			if (Arrays.asList(args).contains("--cli")) {
				System.out.println("Iniciando em modo de interface de linha de comando (CLI)...\njava -jar target/BsDnD-0.0.1-SNAPSHOT.jar --cli");

				consoleController.display();

				System.out.println("\nProcesso Finalizado\n");

				System.exit(0);
			}else {
				System.out.println("Aplicação iniciada em modo servidor.");
				System.out.println("Para acessar o menu interativo, execute com o argumento: --cli\njava -jar target/BsDnD-0.0.1-SNAPSHOT.jar --cli   ");
			}
		};
	}
}
