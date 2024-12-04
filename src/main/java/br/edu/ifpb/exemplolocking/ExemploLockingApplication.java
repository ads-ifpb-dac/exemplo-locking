package br.edu.ifpb.exemplolocking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class ExemploLockingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExemploLockingApplication.class, args);
    }

    @PersistenceContext
    private EntityManager em;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
        };
    }

}
