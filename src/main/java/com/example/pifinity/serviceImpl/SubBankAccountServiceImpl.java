package com.example.pifinity.serviceImpl;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.SubBankAccount;
import com.example.pifinity.entity.Transaction;
import com.example.pifinity.repository.SubBankAccountRepository;
import com.example.pifinity.serviceInterface.IBankAccountService;
import com.example.pifinity.serviceInterface.ISubBankAccountService;
import com.example.pifinity.serviceInterface.ITransactionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class SubBankAccountServiceImpl implements ISubBankAccountService {

    SubBankAccountRepository subBankAccountRepository;
    IBankAccountService bankAccountService;




    @Override
    public List<SubBankAccount> retrieveAllSubBankAccount() {
        return subBankAccountRepository.findAll();
    }

    @Override
    public SubBankAccount addSubBankAccount(int rib, SubBankAccount subBankAccount) {

        BankAccount bankAccount ;
        bankAccount=bankAccountService.retrieveBankAccount(rib);
        if (bankAccount != null) {

            subBankAccount.setBankAccount(bankAccount);
            subBankAccount.setDateCreation(LocalDate.now());



        } else {
            throw new RuntimeException("BankAccount with RIB " + rib + " not found.");
        }

        return subBankAccountRepository.save(subBankAccount);
    }





    @Override
    public SubBankAccount updateSubBankAccount(int id, SubBankAccount subBankAccount) {

        SubBankAccount existingSubBankAccount =retrieveSubBankAccount(id);
        existingSubBankAccount.setSolde(subBankAccount.getSolde());
        existingSubBankAccount.setGoals(subBankAccount.getGoals());

        return subBankAccountRepository.save(existingSubBankAccount);



    }

    @Override
    public SubBankAccount retrieveSubBankAccount(int id) {
        return subBankAccountRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteSubBankAccount(int id) {
        subBankAccountRepository.deleteById(id);
    }


    public List<SubBankAccount> retrieveAllSubBankAccountByOrder() {
        return subBankAccountRepository.getAllbyOrder();
    }

    @Override
    public double calculerEpargne(double capital, double tauxInteretMensuel, double VersementMensuel, int Duree) {
        for (int i = 0; i < Duree; i++) {
            capital += VersementMensuel;
            capital *= (1 +tauxInteretMensuel / 12);
        }

        return capital;
    }

    @Override
    public List<SubBankAccount> retrieveAllSubaccountbybankaccount(int rib) {
        return subBankAccountRepository.findByBankAccountByRIB(rib);
    }


}
