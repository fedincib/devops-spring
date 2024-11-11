package com.example.pifinity;

import com.example.pifinity.serviceImpl.TransactionServiceImpl;
import com.example.pifinity.serviceInterface.ITransactionService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class PifinityApplication {

    public static void main(String[] args) {





        SpringApplication.run(PifinityApplication.class, args);





    }



}
