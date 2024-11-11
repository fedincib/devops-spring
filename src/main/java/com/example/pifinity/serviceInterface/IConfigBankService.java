package com.example.pifinity.serviceInterface;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.Configbank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IConfigBankService {

    Configbank addConfigBank(Configbank configbank);

    Configbank updateConfigBank(int id,Configbank configbank);

    Configbank retrieveConfigbank(int id);

    List<Configbank> retrieveAllConfigBank();

    Configbank getlastconfig();
    Configbank getConfigBankForDate(LocalDate date);



}
