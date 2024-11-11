package com.example.pifinity.repository;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.SubBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository <BankAccount,Integer> {

    @Query(value = "select * from bank_account order by solde desc",nativeQuery = true)
    public List<BankAccount> getAllBankAccountbyOrder() ;


    @Query("SELECT COUNT(vbc) FROM VirtualBankCard vbc WHERE vbc.bankAccount = :bankAccount")
    int countVirtualBankCardsByBankAccount(@Param("bankAccount") BankAccount bankAccount);

    @Query("SELECT COUNT(sba) FROM SubBankAccount sba WHERE sba.bankAccount = :bankAccount")
    int countSubBankAccountsByBankAccount(@Param("bankAccount") BankAccount bankAccount);

    long count();





}
