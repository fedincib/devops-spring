package com.example.pifinity.serviceImpl;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.VirtualBankCard;
import com.example.pifinity.repository.BankAccountRepository;
import com.example.pifinity.repository.VirtualBankCardRepository;
import com.example.pifinity.serviceInterface.IBankAccountService;
import com.example.pifinity.serviceInterface.IVirtualBankCardService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;


@Service
@AllArgsConstructor
public class VirtualBankServiceImpl implements IVirtualBankCardService {

    VirtualBankCardRepository virtualBankCardRepository;
    IBankAccountService bankAccountService;




    @Override
    public List<VirtualBankCard> retrieveAllVirtualBankCard() {
        return virtualBankCardRepository.findAll();
    }

    @Override
    public VirtualBankCard addVirtualBankCard(int rib, VirtualBankCard virtualBankCard) {
        BankAccount bankAccount ;
        bankAccount=bankAccountService.retrieveBankAccount(rib);
        if (bankAccount != null) {
            // Set the bank account for the virtual bank card
            virtualBankCard.setBankAccount(bankAccount);
            virtualBankCard.setExpirationDate(LocalDate.now().plusYears(5));

            Random random = new Random();
            int a = random.nextInt(999,10000); // Generates a random integer
            int b =random.nextInt(99,1000);
            virtualBankCard.setStatusCard("activated");
            virtualBankCard.setCvv(b);
            virtualBankCard.setPassword(a);


            return virtualBankCardRepository.save(virtualBankCard);
        } else {
            throw new RuntimeException("BankAccount with RIB " + rib + " not found.");
        }
    }






    @Override
    public void deleteVirtualBankCard(int NumCard) {

        virtualBankCardRepository.deleteById(NumCard);
    }

    @Override
    public VirtualBankCard retrieveVirtualBankCard(int NumCard) {
        return virtualBankCardRepository.findById(NumCard).orElse(null);
    }

    @Override
    public List<VirtualBankCard> retrieveAllVirtualBankCardbybankaccount(int rib) {
        return virtualBankCardRepository.findByBankAccountByRIB(rib);
    }
}
