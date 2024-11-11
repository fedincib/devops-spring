package com.example.pifinity.repository;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.Configbank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ConfigBankRepository extends JpaRepository<Configbank,Integer> {

    @Query(value = "SELECT cb FROM Configbank cb WHERE cb.date_creation = :date" ,nativeQuery = true)
    Configbank findByDateCreation(@Param("date") LocalDate date);

}
