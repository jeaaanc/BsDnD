package BankSdNd.example.BsDnD;

import BankSdNd.example.BsDnD.adapter.in.cli.ConsoleController;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class BsDnDApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("./bsdnd")
				.ignoreIfMissing()
				.load();

		if (dotenv.get("DB_NAME") == null) {
			dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.load();
		}
		
		setSystemProperty(dotenv, "DB_NAME");
		setSystemProperty(dotenv, "DB_USER");
		setSystemProperty(dotenv, "DB_PASSWORD");
		setSystemProperty(dotenv, "JWT_SECRET");

		SpringApplication.run(BsDnDApplication.class, args);
	}

	private static void setSystemProperty(Dotenv dotenv, String key) {
		String value = dotenv.get(key);
		if (value != null) {
			System.setProperty(key, value);
		}
	}

	@Bean
	CommandLineRunner run(ConsoleController consoleController) {
		return args -> {
			if (Arrays.asList(args).contains("--cli")) {
				System.out.println("Starting in Command Line Interface (CLI) mode...\njava -jar target/BsDnD-0.0.1-SNAPSHOT.jar --cli");

				consoleController.display();

				System.out.println("\nProcess Finished\n");

				System.exit(0);
			} else {
				System.out.println("Application started in server mode.");
				System.out.println("To access the interactive menu, run with the argument: --cli\njava -jar target/BsDnD-0.0.1-SNAPSHOT.jar --cli");
			}
		};
	}
}
