package com.fitness.opp;

import com.fitness.opp.models.User;
import com.fitness.opp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class OppApplication {

	public static void main(String[] args) {
		SpringApplication.run(OppApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			User existingUser = userRepository.findByUsername("george_fit");
			if (existingUser != null) { userRepository.delete(existingUser); }

			User admin = new User();
			admin.setUsername("george_fit");
			admin.setPassword(passwordEncoder.encode("1234"));
			admin.setEmail("admin@fitness.com");
			admin.setRole("ADMIN");

			userRepository.save(admin);
			System.out.println(">>> SERVER STARTAT - LOGIN: george_fit / 1234");
		};
	}
}