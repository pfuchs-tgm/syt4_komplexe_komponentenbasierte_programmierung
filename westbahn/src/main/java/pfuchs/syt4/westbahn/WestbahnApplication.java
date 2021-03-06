package pfuchs.syt4.westbahn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pfuchs.syt4.westbahn.model.Bahnhof;
import pfuchs.syt4.westbahn.repositories.BahnhofRepository;

@SpringBootApplication(scanBasePackages = {"pfuchs.syt4.westbahn.web", "pfuchs.syt4.westbahn.model", "pfuchs.syt4.westbahn.repositories"})
public class WestbahnApplication {
	public static void main(String[] args) {
		SpringApplication.run(WestbahnApplication.class, args);
	}
}
