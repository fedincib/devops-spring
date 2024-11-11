package com.example.pifinity.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Configbank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    float interet;

    float WeeklyLimiteSiliver;

    float WeeklyLimiteGold;

    private LocalDateTime DateCreation;

    float LimiteSilver;

    float LimiteGold;

    float LimitePremieum;
}
