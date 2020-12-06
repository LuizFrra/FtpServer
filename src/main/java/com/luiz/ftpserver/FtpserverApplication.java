package com.luiz.ftpserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class FtpserverApplication {

    public static void main(String[] args) {
        System.setProperty("jdk.tls.server.protocols", "TLSv1.2");
        SpringApplication.run(FtpserverApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return (args) -> {
            SaltedPasswordEncryptor saltedPasswordEncryptor = new SaltedPasswordEncryptor();
            log.info(saltedPasswordEncryptor.encrypt(""));
        };
    }
}
