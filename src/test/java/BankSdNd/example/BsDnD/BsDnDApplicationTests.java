package BankSdNd.example.BsDnD;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BsDnDApplicationTests {

	@MockBean
	private CommandLineRunner commandLineRunner;


	@Test
	void contextLoads() {
	}

}
