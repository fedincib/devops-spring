package com.example.pifinity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class VirtualBankCard {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int NumCard;

    private int cvv;

    private int password;

    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private TypeCard typecard;

    private String statusCard;
@JsonIgnore
    @ToString.Exclude
    @ManyToOne
    BankAccount bankAccount;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "virtualBankCard")
    private Set<Transaction> transactions;

}
