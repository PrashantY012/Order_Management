package org.example.miniordermanagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MiniOrderManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniOrderManagementApplication.class, args);
    }

}
