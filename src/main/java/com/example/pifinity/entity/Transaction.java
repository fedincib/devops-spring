package com.example.pifinity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
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
public class Transaction {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idTransaction;
    private float amount;

    private LocalDateTime DateTransaction;

    private int RIB_S ;
    private int RIB_D;
    private String description;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    VirtualBankCard virtualBankCard;


}
