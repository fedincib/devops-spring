package com.example.pifinity.repository;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.SubBankAccount;
import com.example.pifinity.entity.VirtualBankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubBankAccountRepository extends JpaRepository<SubBankAccount,Integer> {

    @Query(value = "select * from sub_bank_account order by solde desc",nativeQuery = true)
    public List<SubBankAccount> getAllbyOrder() ;


    @Query("SELECT s FROM SubBankAccount s WHERE s.bankAccount.RIB = :rib")
    List<SubBankAccount> findByBankAccountByRIB(int rib);

}
