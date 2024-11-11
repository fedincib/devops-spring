package com.example.pifinity.repository;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {


    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transaction t WHERE t.virtual_bank_card_num_card = :virtual_bank_card_num_card AND t.date_transaction >= :startDate AND t.date_transaction <= :endDate", nativeQuery = true)
    float calculateWeeklyTransactionAmount(@Param("virtual_bank_card_num_card") int virtual_bank_card_num_card, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "select * from transaction t WHERE t.rib_s = :rib_s || t.rib_d =:rib_s ",nativeQuery = true)
    List<Transaction> findAllBy(@Param("rib_s")int rib_s);


    @Query(value = "SELECT COALESCE(COUNT(t.id_transaction), 0) FROM transaction t WHERE t.rib_s = :rib_s AND t.rib_d= :rib_d", nativeQuery = true)
    int calculateTransaction(@Param("rib_s") int rib_s, @Param("rib_d") int rib_d);



}
