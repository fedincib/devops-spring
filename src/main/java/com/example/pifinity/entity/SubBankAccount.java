package com.example.pifinity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SubBankAccount {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idSubBankAccount;
    private String Goals;

    private LocalDate DateCreation;
    private float solde;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    BankAccount bankAccount;

}
