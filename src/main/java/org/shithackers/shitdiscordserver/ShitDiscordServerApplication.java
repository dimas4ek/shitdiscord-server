package org.shithackers.shitdiscordserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"org.shithackers.shitdiscordserver.model"})
public class ShitDiscordServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShitDiscordServerApplication.class, args);
    }
}
