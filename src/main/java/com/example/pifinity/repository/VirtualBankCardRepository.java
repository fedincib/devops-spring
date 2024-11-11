package com.example.pifinity.repository;

import com.example.pifinity.entity.VirtualBankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VirtualBankCardRepository extends JpaRepository<VirtualBankCard,Integer> {



    @Query("SELECT v FROM VirtualBankCard v WHERE v.bankAccount.RIB = :rib")
    List<VirtualBankCard> findByBankAccountByRIB(int rib);
}
