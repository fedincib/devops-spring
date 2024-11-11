package com.example.pifinity.serviceInterface;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.SubBankAccount;

import java.util.List;

public interface IBankAccountService {

    int getNumberOfSubBankAccounts(int b);

    int getNumberOfVirtualBankCards(int c);


    List<BankAccount> retrieveAllBankAccount();

    BankAccount addBankAccount(BankAccount bankaccount);

    BankAccount updateBankAccount(int rib,BankAccount bankaccount);

    BankAccount retrieveBankAccount(int rib);

    void deleteBankAccount(int rib);


    List<BankAccount> retrieveAllBankAccountByOrder();


     long getTotalBankAccounts() ;






}
