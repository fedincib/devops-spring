package com.example.pifinity.serviceImpl;


import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.repository.BankAccountRepository;
import com.example.pifinity.serviceInterface.IBankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class BankAccountServiceImpl implements IBankAccountService {


    BankAccountRepository bankAccountRepository;
    @Override
    public List<BankAccount> retrieveAllBankAccount() {
        return bankAccountRepository.findAll();
    }

    @Override
    public BankAccount addBankAccount(BankAccount bankaccount) {

        bankaccount.setDateCreation(LocalDate.now());
        bankaccount.setStatusBank("activated");
        return bankAccountRepository.save(bankaccount);
    }

    @Override
    public BankAccount updateBankAccount(int rib,BankAccount bankaccount) {

        BankAccount existingBankAccount =retrieveBankAccount(rib);
        existingBankAccount.setSolde(existingBankAccount.getSolde()+bankaccount.getSolde());
        existingBankAccount.setStatusBank(bankaccount.getStatusBank());

        return bankAccountRepository.save(existingBankAccount);
    }

    @Override
    public BankAccount retrieveBankAccount(int rib) {
        return bankAccountRepository.findById(rib).orElse(null);
    }

    @Override
    public void deleteBankAccount(int rib) {
        bankAccountRepository.deleteById(rib);
    }

    @Override
    public List<BankAccount> retrieveAllBankAccountByOrder() {
        return bankAccountRepository.getAllBankAccountbyOrder();
    }

    @Override
    public long getTotalBankAccounts() {
        return bankAccountRepository.count();
    }

    @Override
    public int getNumberOfVirtualBankCards(int b) {
        BankAccount bb=bankAccountRepository.findById(b).orElse(null);
        return bankAccountRepository.countVirtualBankCardsByBankAccount(bb);
    }
@Override
    public int getNumberOfSubBankAccounts(int c) {
        BankAccount bb=bankAccountRepository.findById(c).orElse(null);
        return bankAccountRepository.countSubBankAccountsByBankAccount(bb);
    }



}
