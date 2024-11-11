package com.example.pifinity.serviceInterface;


import com.example.pifinity.entity.SubBankAccount;
import com.example.pifinity.entity.VirtualBankCard;

import java.util.List;

public interface ISubBankAccountService {
    List<SubBankAccount> retrieveAllSubBankAccount();

    SubBankAccount addSubBankAccount(int rib,SubBankAccount subBankAccount);

    SubBankAccount updateSubBankAccount(int id,SubBankAccount subBankAccount);

    SubBankAccount retrieveSubBankAccount(int id);

    void deleteSubBankAccount(int id);

    List<SubBankAccount> retrieveAllSubBankAccountByOrder();

    // double calcule(int id);

    double calculerEpargne(double capital,double tauxInteretMensuel,double VersementMensuel,int Duree);


    List<SubBankAccount> retrieveAllSubaccountbybankaccount(int rib);

}
