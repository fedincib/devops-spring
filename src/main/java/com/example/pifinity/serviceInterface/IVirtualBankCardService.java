package com.example.pifinity.serviceInterface;

import com.example.pifinity.entity.Transaction;
import com.example.pifinity.entity.VirtualBankCard;

import java.util.List;

public interface IVirtualBankCardService {


    List<VirtualBankCard> retrieveAllVirtualBankCard();

    VirtualBankCard addVirtualBankCard(int rib, VirtualBankCard virtualBankCard);

    void deleteVirtualBankCard(int NumCard);
    VirtualBankCard retrieveVirtualBankCard(int NumCard);

    List<VirtualBankCard> retrieveAllVirtualBankCardbybankaccount(int rib);

}
