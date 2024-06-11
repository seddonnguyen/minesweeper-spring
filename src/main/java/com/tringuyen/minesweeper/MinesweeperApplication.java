package com.tringuyen.minesweeper;

//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.info.Info;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@SpringBootApplication
//@OpenAPIDefinition(info = @Info(title = "Minesweeper API",
//                                version = "1.0",
//                                description = "Minesweeper Game API"))
//@EnableWebMvc
@SpringBootApplication
public class MinesweeperApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinesweeperApplication.class, args);
    }
}