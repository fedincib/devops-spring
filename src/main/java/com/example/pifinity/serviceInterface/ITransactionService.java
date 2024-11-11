package com.example.pifinity.serviceInterface;

import com.example.pifinity.entity.BankAccount;
import com.example.pifinity.entity.SubBankAccount;
import com.example.pifinity.entity.Transaction;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

public interface ITransactionService {
    List<Transaction> retrieveAllTransaction();

    List<Transaction> retrieveAllTransactionbyBankAccount(int id);

    List<Transaction> retrieveAllTransactionbySubAccount(int id);

    int calculatetransaction(int rib_s,int rib_d);

    int splittransaction(int code, int amount) throws MessagingException;

    Transaction confirmsplittransaction(int a, Transaction t, int numcard);

    Transaction addTransaction(int numcard, int password, int confirmpassword, int cvv, Transaction transaction) throws MessagingException;

    int confirmTransaction() throws MessagingException;
    Transaction addt(int numcard, int password,int cvv,Transaction transaction) throws MessagingException;
    void deleteTransaction(int idtransaction);
    Transaction retrieveTransaction(int idtransaction);

    Transaction addtr(Transaction t);

    void paymentcreditcard();
    double CalculeEpargne(int id);





}
