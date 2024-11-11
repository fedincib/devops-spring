package com.example.pifinity.serviceImpl;

import com.example.pifinity.entity.*;
import com.example.pifinity.repository.TransactionRepository;
import com.example.pifinity.serviceInterface.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
@AllArgsConstructor

public class TransactionServiceImpl implements ITransactionService {


    IVirtualBankCardService virtualBankCardService;
    IBankAccountService bankAccountService;
    TransactionRepository transactionRepository;
    ISubBankAccountService subBankAccountService;
    IConfigBankService configBankService;

    SmsServiceImpl s;
    public static int a;
    public static int aa;
    public static float bb;


    @Override
    public double CalculeEpargne(int id ){

        List<Transaction> transactions = retrieveAllTransactionbySubAccount(id);
        List<Configbank> cbs=configBankService.retrieveAllConfigBank();
        SubBankAccount subBankAccount= subBankAccountService.retrieveSubBankAccount(id);
        float goneamount=0;
        float recieveamount=0;
        float currentBalance = subBankAccount.getSolde();
        float firstamount=0;

        for (Transaction transaction : transactions) {
            if (transaction.getRIB_S() == subBankAccount.getIdSubBankAccount()) {
                goneamount += transaction.getAmount();
            }
            if (transaction.getRIB_D() == subBankAccount.getIdSubBankAccount()) {
                recieveamount += transaction.getAmount();
            }
        }

        firstamount= currentBalance + goneamount - recieveamount;
        System.out.println("firstamount: " + firstamount);
        double totalInterest = 0;

       /* Configbank cb=configBankService.retrieveConfigbank(1);

        float interet = cb.getInteret();
        float abb = interet/100;*/

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            LocalDateTime transactionDate = transaction.getDateTransaction();


            double dailyInterest;
            LocalDate startDate;
            LocalDate endDate;
            int days;
            if (i == 0) {
                startDate = LocalDate.of(transactionDate.getYear(), 1, 1);
                days = (int) ChronoUnit.DAYS.between(startDate, transactionDate.plusDays(1));
                final LocalDateTime az=startDate.atStartOfDay();
                Configbank lastConfigbank = cbs.stream()
                        .filter(configbank -> configbank.getDateCreation().isBefore(az))
                        .max(Comparator.comparing(Configbank::getDateCreation))
                        .orElse(null);
                float interset=lastConfigbank.getInteret();
                System.out.println(interset);
                float abb=interset/100;

                 dailyInterest = (firstamount * abb * days) / 360; // Assuming 360-day year
                System.out.println(i);
                System.out.println(dailyInterest);


            } else {
                LocalDateTime previousTransactionDate = transactions.get(i - 1).getDateTransaction();
                startDate = previousTransactionDate.plusDays(1).toLocalDate();
                days = (int) ChronoUnit.DAYS.between(startDate, transactionDate.plusDays(1));

                final LocalDateTime abz=startDate.atStartOfDay();
                Configbank lastConfigbank = cbs.stream()
                        .filter(configbank -> configbank.getDateCreation().isBefore(abz))
                        .max(Comparator.comparing(Configbank::getDateCreation))
                        .orElse(null);
                float interset=lastConfigbank.getInteret();
                System.out.println(interset);
                float abb=interset/100;

                dailyInterest = (firstamount * abb * days) / 360; // Assuming 360-day year
                System.out.println(i);
                System.out.println(dailyInterest);

            }


            totalInterest += dailyInterest;
            firstamount += transaction.getAmount(); // Update the current balance

            if (i==transactions.size()-1){
                LocalDateTime previousTransactionDate = transactions.get(i-1).getDateTransaction();
                startDate = previousTransactionDate.plusDays(1).toLocalDate();

                endDate = LocalDate.of(transactionDate.getYear(), 12, 31);
                days = (int) ChronoUnit.DAYS.between(startDate, endDate);

                final LocalDateTime abbz=startDate.atStartOfDay();
                Configbank lastConfigbank = cbs.stream()
                        .filter(configbank -> configbank.getDateCreation().isBefore(abbz))
                        .max(Comparator.comparing(Configbank::getDateCreation))
                        .orElse(null);
                float interset=lastConfigbank.getInteret();
                System.out.println(interset);

                float abb=interset/100;
                dailyInterest = (firstamount * abb * days) / 360; // Assuming 360-day year
                totalInterest += dailyInterest;
                firstamount += transaction.getAmount();
                System.out.println(i);
                System.out.println(dailyInterest);

            }


        }

        System.out.println(transactions.size());


        double hghg= totalInterest+currentBalance;
        System.out.println("Total interest: " + totalInterest);
        System.out.println("Current balance: " + hghg);


        return totalInterest;


    }



    @Override
    public List<Transaction> retrieveAllTransaction() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> retrieveAllTransactionbyBankAccount(int id) {

        BankAccount ba=bankAccountService.retrieveBankAccount(id);
        return transactionRepository.findAllBy(ba.getRIB());
    }

    @Override
    public List<Transaction> retrieveAllTransactionbySubAccount(int id) {
        SubBankAccount ba=subBankAccountService.retrieveSubBankAccount(id);
        return transactionRepository.findAllBy(ba.getIdSubBankAccount());
    }

    @Override
    public int calculatetransaction(int rib_s, int rib_d) {
        return transactionRepository.calculateTransaction(rib_s,rib_d);
    }


    @Override
    public Transaction addt(int numcard,int password ,int cvv,Transaction transaction) throws MessagingException {
    VirtualBankCard virtualBankCard = virtualBankCardService.retrieveVirtualBankCard(numcard);

    if (virtualBankCard == null) {
        throw new RuntimeException("Virtual bank card with numcard " + numcard + " not found.");
    }

    if (virtualBankCard.getCvv() != cvv || virtualBankCard.getPassword() != password) {
        throw new RuntimeException("Wrong card details provided.");
    }

    float currentBalance = virtualBankCard.getBankAccount().getSolde();
    if (currentBalance < transaction.getAmount()) {
        throw new RuntimeException("Insufficient funds.");
    }


    transaction.setVirtualBankCard(virtualBankCard);
    transaction.setRIB_S(virtualBankCard.getBankAccount().getRIB());
    transaction.setDateTransaction(LocalDateTime.now());

    Configbank cb=configBankService.retrieveConfigbank(1);


    float weeklyLimit = 0;
    switch (virtualBankCard.getTypecard()) {
        case silver:
            weeklyLimit = cb.getWeeklyLimiteSiliver();
            break;
        case gold:
            weeklyLimit = cb.getWeeklyLimiteGold();
            break;
        case premieum:
            // No limit for premium cards
            break;
    }

    // Check if the transaction amount exceeds the weekly limit
    float weeklyTransactionAmount = transactionRepository.calculateWeeklyTransactionAmount(virtualBankCard.getNumCard(), LocalDate.now(),LocalDate.now());
    if (weeklyLimit > 0 && weeklyTransactionAmount + transaction.getAmount() > weeklyLimit) {
        throw new RuntimeException("Transaction amount exceeds the weekly limit for this card type.");
    }

        a=confirmTransaction();

        return transaction;
}




    @Override
    public Transaction addTransaction(int numcard, int password, int confirmpassword,int cvv, Transaction transaction) throws MessagingException {


        VirtualBankCard virtualBankCard = virtualBankCardService.retrieveVirtualBankCard(numcard);
        float currentBalance = virtualBankCard.getBankAccount().getSolde();
        int confirmedPassword = a;
        if (confirmpassword != a) {
            throw new RuntimeException("Passwords do not match. Expected: " + confirmedPassword + ", Actual: " + confirmpassword);
        }


        virtualBankCard.getBankAccount().setSolde(currentBalance - transaction.getAmount());
        return transactionRepository.save(addt(numcard,password,cvv,transaction));
    }



    @Override
    public void deleteTransaction(int idtransaction) {
        transactionRepository.deleteById(idtransaction);
    }


    @Override
    public Transaction retrieveTransaction(int idtransaction) {
        return transactionRepository.findById(idtransaction).orElse(null);
    }


    // transaction bin compte kbir w compte shir li y5tarou
    @Override
    public Transaction addtr(Transaction t) {

        BankAccount bankAccount=bankAccountService.retrieveBankAccount(t.getRIB_S());
        SubBankAccount subBankAccount=subBankAccountService.retrieveSubBankAccount(t.getRIB_D());
        if (bankAccount == null) {
            bankAccount = bankAccountService.retrieveBankAccount(t.getRIB_D());
            subBankAccount=subBankAccountService.retrieveSubBankAccount(t.getRIB_S());
        }

        float currentBalance = bankAccount.getSolde();
        float currentBalance1=subBankAccount.getSolde();
        if (currentBalance < t.getAmount()) {
            throw new RuntimeException("Insufficient funds.");
        }

        t.setDateTransaction(LocalDateTime.now());

        bankAccount.setSolde(currentBalance-t.getAmount());
        subBankAccount.setSolde(currentBalance1+t.getAmount());




        return transactionRepository.save(t);
    }

    @Scheduled(cron = "0 0 0 1 * ?") //// Exécution à minuit le premier jour de chaque mois
    @Override
    public void paymentcreditcard() {
        Configbank cb = configBankService.retrieveConfigbank(1);
        List<VirtualBankCard> vbc = virtualBankCardService.retrieveAllVirtualBankCard();
        for (int i = 0; i < vbc.size(); i++) {
            Transaction t = new Transaction(); // Créer un nouvel objet Transaction à chaque itération
            t.setDescription("paymentcreditcard");
            t.setDateTransaction(LocalDateTime.now());
            t.setRIB_S(vbc.get(i).getNumCard());
            t.setVirtualBankCard(vbc.get(i));
            if (vbc.get(i).getTypecard() == TypeCard.premieum) {
                t.setAmount(cb.getLimitePremieum());
            } else if (vbc.get(i).getTypecard() == TypeCard.gold) {
                t.setAmount(cb.getLimiteGold());
            } else if (vbc.get(i).getTypecard() == TypeCard.silver) {
                t.setAmount(cb.getLimiteSilver());
            }
            BankAccount bankAccount = bankAccountService.retrieveBankAccount(vbc.get(i).getBankAccount().getRIB());
            bankAccount.setSolde(bankAccount.getSolde() - t.getAmount());
            t.setRIB_S(bankAccount.getRIB());
            transactionRepository.save(t); // Sauvegarde la transaction pour chaque carte
        }
    }


    @Override
    public Transaction confirmsplittransaction(int a ,Transaction t,int numcard){

        VirtualBankCard virtualBankCard = virtualBankCardService.retrieveVirtualBankCard(numcard);
        t.setAmount(bb);
        if(aa==a) {
            float currentBalance = virtualBankCard.getBankAccount().getSolde();
            t.setDateTransaction(LocalDateTime.now());
            t.setVirtualBankCard(virtualBankCard);
            t.setRIB_S(virtualBankCard.getBankAccount().getRIB());

            t.setDescription("split_transaction");
            virtualBankCard.getBankAccount().setSolde(currentBalance - t.getAmount());
        }
        return  transactionRepository.save(t);
    }

    @Override
    public  int splittransaction(int code, int amount) throws MessagingException {



        List<String> recipients = Arrays.asList("ncib.fedi@esprit.tn", "manar.boukhris@esprit.tn", "imen.mnejja@esprit.tn","ncib.yasmine@esprit.tn");



        float b=(float)amount/recipients.size();
        String subject="your code for a split transaction";
        for (String recipient : recipients) {


            String msg = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
                    + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n"
                    + "<head>\n"
                    + "<!--[if gte mso 9]>\n"
                    + "<xml>\n"
                    + "  <o:OfficeDocumentSettings>\n"
                    + "    <o:AllowPNG/>\n"
                    + "    <o:PixelsPerInch>96</o:PixelsPerInch>\n"
                    + "  </o:OfficeDocumentSettings>\n"
                    + "</xml>\n"
                    + "<![endif]-->\n"
                    + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                    + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                    + "  <meta name=\"x-apple-disable-message-reformatting\">\n"
                    + "  <!--[if !mso]><!--><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><!--<![endif]-->\n"
                    + "  <title></title>\n"
                    + "  \n"
                    + "    <style type=\"text/css\">\n"
                    + "      table, td { color: #000000; } a { color: #0000ee; text-decoration: underline; } @media (max-width: 480px) { #u_content_image_1 .v-src-width { width: auto !important; } #u_content_image_1 .v-src-max-width { max-width: 55% !important; } #u_content_image_2 .v-src-width { width: auto !important; } #u_content_image_2 .v-src-max-width { max-width: 60% !important; } #u_content_text_1 .v-container-padding-padding { padding: 30px 30px 30px 20px !important; } #u_content_button_1 .v-container-padding-padding { padding: 10px 20px !important; } #u_content_button_1 .v-size-width { width: 100% !important; } #u_content_button_1 .v-text-align { text-align: left !important; } #u_content_button_1 .v-padding { padding: 15px 40px !important; } #u_content_text_3 .v-container-padding-padding { padding: 30px 30px 80px 20px !important; } #u_content_text_5 .v-text-align { text-align: center !important; } #u_content_text_4 .v-text-align { text-align: center !important; } }\n"
                    + "@media only screen and (min-width: 570px) {\n"
                    + "  .u-row {\n"
                    + "    width: 550px !important;\n"
                    + "  }\n"
                    + "  .u-row .u-col {\n"
                    + "    vertical-align: top;\n"
                    + "  }\n"
                    + "\n"
                    + "  .u-row .u-col-50 {\n"
                    + "    width: 275px !important;\n"
                    + "  }\n"
                    + "\n"
                    + "  .u-row .u-col-100 {\n"
                    + "    width: 550px !important;\n"
                    + "  }\n"
                    + "\n"
                    + "}\n"
                    + "\n"
                    + "@media (max-width: 570px) {\n"
                    + "  .u-row-container {\n"
                    + "    max-width: 100% !important;\n"
                    + "    padding-left: 0px !important;\n"
                    + "    padding-right: 0px !important;\n"
                    + "  }\n"
                    + "  .u-row .u-col {\n"
                    + "    min-width: 320px !important;\n"
                    + "    max-width: 100% !important;\n"
                    + "    display: block !important;\n"
                    + "  }\n"
                    + "  .u-row {\n"
                    + "    width: calc(100% - 40px) !important;\n"
                    + "  }\n"
                    + "  .u-col {\n"
                    + "    width: 100% !important;\n"
                    + "  }\n"
                    + "  .u-col > div {\n"
                    + "    margin: 0 auto;\n"
                    + "  }\n"
                    + "}\n"
                    + "body {\n"
                    + "  margin: 0;\n"
                    + "  padding: 0;\n"
                    + "}\n"
                    + "\n"
                    + "table,\n"
                    + "tr,\n"
                    + "td {\n"
                    + "  vertical-align: top;\n"
                    + "  border-collapse: collapse;\n"
                    + "}\n"
                    + "\n"
                    + "p {\n"
                    + "  margin: 0;\n"
                    + "}\n"
                    + "\n"
                    + ".ie-container table,\n"
                    + ".mso-container table {\n"
                    + "  table-layout: fixed;\n"
                    + "}\n"
                    + "\n"
                    + "* {\n"
                    + "  line-height: inherit;\n"
                    + "}\n"
                    + "\n"
                    + "a[x-apple-data-detectors='true'] {\n"
                    + "  color: inherit !important;\n"
                    + "  text-decoration: none !important;\n"
                    + "}\n"
                    + "\n"
                    + "</style>\n"
                    + "  \n"
                    + "  \n"
                    + "\n"
                    + "<!--[if !mso]><!--><link href=\"https://fonts.googleapis.com/css?family=Crimson+Text:400,700&display=swap\" rel=\"stylesheet\" type=\"text/css\"><!--<![endif]-->\n"
                    + "\n"
                    + "</head>\n"
                    + "\n"
                    + "<body class=\"clean-body u_body\" style=\"margin: 0;padding: 0;-webkit-text-size-adjust: 100%;background-color: #fbeeb8;color: #000000\">\n"
                    + "  <!--[if IE]><div class=\"ie-container\"><![endif]-->\n"
                    + "  <!--[if mso]><div class=\"mso-container\"><![endif]-->\n"
                    + "  <table style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;min-width: 320px;Margin: 0 auto;background-color: #fbeeb8;width:100%\" cellpadding=\"0\" cellspacing=\"0\">\n"
                    + "  <tbody>\n"
                    + "  <tr style=\"vertical-align: top\">\n"
                    + "    <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\n"
                    + "    <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\" style=\"background-color: #fbeeb8;\"><![endif]-->\n"
                    + "    \n"
                    + "\n"
                    + "<div class=\"u-row-container\" style=\"padding: 0px;background-color: #ffffff\">\n"
                    + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">\n"
                    + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                    + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: #ffffff;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->\n"
                    + "      \n"
                    + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"550\" style=\"width: 550px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->\n"
                    + "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 550px;display: table-cell;vertical-align: top;\">\n"
                    + "  <div style=\"width: 100% !important;\">\n"
                    + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->\n"
                    + "  \n"
                    + "<table id=\"u_content_image_1\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                    + "  <tbody>\n"
                    + "    <tr>\n"
                    + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:30px 10px 33px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                    + "        \n"
                    + "\n"
                    + "      </td>\n"
                    + "    </tr>\n"
                    + "  </tbody>\n"
                    + "</table>\n"
                    + "\n"
                    + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                    + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                    + "    </div>\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "\n"
                    + "\n"
                    + "\n"
                    + "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
                    + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: transparent;\">\n"
                    + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                    + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: transparent;\"><![endif]-->\n"
                    + "      \n"
                    + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"550\" style=\"width: 550px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                    + "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 550px;display: table-cell;vertical-align: top;\">\n"
                    + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                    + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                    + "  \n"
                    + "<table style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                    + "  <tbody>\n"
                    + "    <tr>\n"
                    + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:20px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                    + "        \n"
                    + "  <table height=\"0px\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;border-top: 0px solid #BBBBBB;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%\">\n"
                    + "    <tbody>\n"
                    + "      <tr style=\"vertical-align: top\">\n"
                    + "        <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top;font-size: 0px;line-height: 0px;mso-line-height-rule: exactly;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%\">\n"
                    + "          <span>&#160;</span>\n"
                    + "        </td>\n"
                    + "      </tr>\n"
                    + "    </tbody>\n"
                    + "  </table>\n"
                    + "\n"
                    + "      </td>\n"
                    + "    </tr>\n"
                    + "  </tbody>\n"
                    + "</table>\n"
                    + "\n"
                    + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                    + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                    + "    </div>\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "\n"
                    + "\n"
                    + "\n"
                    + "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
                    + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">\n"
                    + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                    + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->\n"
                    + "      \n"
                    + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"542\" style=\"width: 542px;padding: 0px;border-top: 4px solid #d9d8d8;border-left: 4px solid #d9d8d8;border-right: 4px solid #d9d8d8;border-bottom: 4px solid #d9d8d8;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                    + "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 550px;display: table-cell;vertical-align: top;\">\n"
                    + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                    + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 4px solid #d9d8d8;border-left: 4px solid #d9d8d8;border-right: 4px solid #d9d8d8;border-bottom: 4px solid #d9d8d8;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                    + "  \n"
                    + "<table id=\"u_content_image_2\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                    + "  <tbody>\n"
                    + "    <tr>\n"
                    + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:40px 10px 10px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                    + "        \n"
                    + "\n"
                    + "\n"
                    + "      </td>\n"
                    + "    </tr>\n"
                    + "  </tbody>\n"
                    + "</table>\n"
                    + "\n"
                    + "<table id=\"u_content_text_1\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                    + "  <tbody>\n"
                    + "    <tr>\n"
                    + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:10px 30px 30px 40px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                    + "        \n"
                    + "  <div class=\"v-text-align\" style=\"color: #333333; line-height: 140%; text-align: left; word-wrap: break-word;\">\n"
                    + "    <p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-family: 'Crimson Text', serif; font-size: 14px; line-height: 19.6px;\"><strong><span style=\"font-size: 22px; line-height: 30.8px;\">pifinity</span></strong></span></p>\n"
                    + "<p style=\"font-size: 14px; line-height: 140%;\">&nbsp;</p>\n"
                    + "<p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-size: 24px; line-height: 25.2px; font-family: 'Crimson Text', serif;\">You have been invited for a split transaction  , your part is "+b+" dinar, your code is  </span></p>\n"
                    + "\n"
                    + "  </div>\n"
                    + "\n"
                    + "      </td>\n"
                    + "    </tr>\n"
                    + "  </tbody>\n"
                    + "</table>\n"
                    + "\n"
                    + "<table id=\"u_content_button_1\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                    + "  <tbody>\n"
                    + "    <tr>\n"
                    + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:10px 40px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                    + "        \n"
                    + "<div class=\"v-text-align\" align=\"left\">\n"
                    + "  <!--[if mso]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;font-family:arial,helvetica,sans-serif;\"><tr><td class=\"v-text-align\" style=\"font-family:arial,helvetica,sans-serif;\" align=\"left\"><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"https://unlayer.com\" style=\"height:47px; v-text-anchor:middle; width:456px;\" arcsize=\"6.5%\" strokecolor=\"#ced4d9\" strokeweight=\"3px\" fillcolor=\"#91a5e2\"><w:anchorlock/><center style=\"color:#000000;font-family:arial,helvetica,sans-serif;\"><![endif]-->\n"
                    + "    <a target=\"_blank\" class=\"v-size-width\" style=\"box-sizing: border-box;display: inline-block;font-family:arial,helvetica,sans-serif;text-decoration: none;-webkit-text-size-adjust: none;text-align: center;color: #000000; background-color: #91a5e2; border-radius: 3px;-webkit-border-radius: 3px; -moz-border-radius: 3px; width:100%; max-width:100%; overflow-wrap: break-word; word-break: break-word; word-wrap:break-word; mso-border-alt: none;border-top-color: #ced4d9; border-top-style: solid; border-top-width: 3px; border-left-color: #ced4d9; border-left-style: solid; border-left-width: 3px; border-right-color: #ced4d9; border-right-style: solid; border-right-width: 3px; border-bottom-color: #ced4d9; border-bottom-style: solid; border-bottom-width: 3px;\">\n"
                    + "      <span class=\"v-padding\" style=\"display:block;padding:15px 40px;line-height:120%;\"><span style=\"font-size: 28px; line-height: 16.8px;\">" + code + "</span></span>\n"
                    + "    </a>\n"
                    + "  <!--[if mso]></center></v:roundrect></td></tr></table><![endif]-->\n"
                    + "</div>\n"
                    + "\n"
                    + "      </td>\n"
                    + "    </tr>\n"
                    + "  </tbody>\n"
                    + "</table>\n"
                    + "\n"
                    + "<table id=\"u_content_text_3\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                    + "  <tbody>\n"
                    + "    <tr>\n"
                    + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:30px 30px 80px 40px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                    + "        \n"
                    + "  <div class=\"v-text-align\" style=\"color: #333333; line-height: 140%; text-align: left; word-wrap: break-word;\">\n"
                    + "    <p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-size: 18px; line-height: 25.2px; font-family: 'Crimson Text', serif;\"> A unique Code to confirm  for you. please confirm your split transaction if you was really consirned</span></p>\n"
                    + "\n"
                    + "  </div>\n"
                    + "\n"
                    + "      </td>\n"
                    + "    </tr>\n"
                    + "  </tbody>\n"
                    + "</table>\n"
                    + "\n"
                    + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                    + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                    + "    </div>\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "\n"
                    + "\n"
                    + "\n"
                    + "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
                    + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: transparent;\">\n"
                    + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                    + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: transparent;\"><![endif]-->\n"
                    + "      \n"
                    + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"550\" style=\"width: 550px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                    + "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 550px;display: table-cell;vertical-align: top;\">\n"
                    + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                    + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                    + "  \n"
                    + "<table style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                    + "  <tbody>\n"
                    + "    <tr>\n"
                    + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:50px 10px 30px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                    + "        \n"
                    + "<div align=\"center\">\n"
                    + "  <div style=\"display: table; max-width:170px;\">\n"
                    + "  <!--[if (mso)|(IE)]><table width=\"170\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"border-collapse:collapse;\" align=\"center\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse; mso-table-lspace: 0pt;mso-table-rspace: 0pt; width:170px;\"><tr><![endif]-->\n"
                    + "  \n"
                    + "  \n"
                    + "    \n"
                    + "\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "\n"
                    + "      </td>\n"
                    + "    </tr>\n"
                    + "  </tbody>\n"
                    + "</table>\n"
                    + "\n"
                    + "\n"
                    + "\n"
                    + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                    + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                    + "    </div>\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "\n"
                    + "\n"
                    + "\n"
                    + "<div class=\"u-row-container\" style=\"padding: 0px 0px 11px;background-color: transparent\">\n"
                    + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: transparent;\">\n"
                    + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                    + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px 0px 11px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: transparent;\"><![endif]-->\n"
                    + "      \n"
                    + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"275\" style=\"width: 275px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                    + "<div class=\"u-col u-col-50\" style=\"max-width: 320px;min-width: 275px;display: table-cell;vertical-align: top;\">\n"
                    + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                    + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                    + "\n"
                    + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                    + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"275\" style=\"width: 275px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                    + "<div class=\"u-col u-col-50\" style=\"max-width: 320px;min-width: 275px;display: table-cell;vertical-align: top;\">\n"
                    + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                    + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                    + "  \n"
                    + "\n"
                    + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                    + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                    + "    </div>\n"
                    + "  </div>\n"
                    + "</div>\n"
                    + "\n"
                    + "\n"
                    + "    <!--[if (mso)|(IE)]></td></tr></table><![endif]-->\n"
                    + "    </td>\n"
                    + "  </tr>\n"
                    + "  </tbody>\n"
                    + "  </table>\n"
                    + "  <!--[if mso]></div><![endif]-->\n"
                    + "  <!--[if IE]></div><![endif]-->\n"
                    + "</body>\n"
                    + "\n"
                    + "</html>";





            String body = msg;
            sendEmail(recipient, subject, body);

        }
        aa=code;
        bb=b;
        return  aa;

    }




    @Override
    public int confirmTransaction() throws MessagingException {
        int a = (int) (Math.random() * 1000);
       /* String smsNumber = "+21623519100"; // Replace with the actual phone number
        String smsMessage = "please confirm your transaction ";

        String status = s.sendSms(smsNumber, smsMessage, a);
*/


        String msg = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n"
                + "<head>\n"
                + "<!--[if gte mso 9]>\n"
                + "<xml>\n"
                + "  <o:OfficeDocumentSettings>\n"
                + "    <o:AllowPNG/>\n"
                + "    <o:PixelsPerInch>96</o:PixelsPerInch>\n"
                + "  </o:OfficeDocumentSettings>\n"
                + "</xml>\n"
                + "<![endif]-->\n"
                + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "  <meta name=\"x-apple-disable-message-reformatting\">\n"
                + "  <!--[if !mso]><!--><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><!--<![endif]-->\n"
                + "  <title></title>\n"
                + "  \n"
                + "    <style type=\"text/css\">\n"
                + "      table, td { color: #000000; } a { color: #0000ee; text-decoration: underline; } @media (max-width: 480px) { #u_content_image_1 .v-src-width { width: auto !important; } #u_content_image_1 .v-src-max-width { max-width: 55% !important; } #u_content_image_2 .v-src-width { width: auto !important; } #u_content_image_2 .v-src-max-width { max-width: 60% !important; } #u_content_text_1 .v-container-padding-padding { padding: 30px 30px 30px 20px !important; } #u_content_button_1 .v-container-padding-padding { padding: 10px 20px !important; } #u_content_button_1 .v-size-width { width: 100% !important; } #u_content_button_1 .v-text-align { text-align: left !important; } #u_content_button_1 .v-padding { padding: 15px 40px !important; } #u_content_text_3 .v-container-padding-padding { padding: 30px 30px 80px 20px !important; } #u_content_text_5 .v-text-align { text-align: center !important; } #u_content_text_4 .v-text-align { text-align: center !important; } }\n"
                + "@media only screen and (min-width: 570px) {\n"
                + "  .u-row {\n"
                + "    width: 550px !important;\n"
                + "  }\n"
                + "  .u-row .u-col {\n"
                + "    vertical-align: top;\n"
                + "  }\n"
                + "\n"
                + "  .u-row .u-col-50 {\n"
                + "    width: 275px !important;\n"
                + "  }\n"
                + "\n"
                + "  .u-row .u-col-100 {\n"
                + "    width: 550px !important;\n"
                + "  }\n"
                + "\n"
                + "}\n"
                + "\n"
                + "@media (max-width: 570px) {\n"
                + "  .u-row-container {\n"
                + "    max-width: 100% !important;\n"
                + "    padding-left: 0px !important;\n"
                + "    padding-right: 0px !important;\n"
                + "  }\n"
                + "  .u-row .u-col {\n"
                + "    min-width: 320px !important;\n"
                + "    max-width: 100% !important;\n"
                + "    display: block !important;\n"
                + "  }\n"
                + "  .u-row {\n"
                + "    width: calc(100% - 40px) !important;\n"
                + "  }\n"
                + "  .u-col {\n"
                + "    width: 100% !important;\n"
                + "  }\n"
                + "  .u-col > div {\n"
                + "    margin: 0 auto;\n"
                + "  }\n"
                + "}\n"
                + "body {\n"
                + "  margin: 0;\n"
                + "  padding: 0;\n"
                + "}\n"
                + "\n"
                + "table,\n"
                + "tr,\n"
                + "td {\n"
                + "  vertical-align: top;\n"
                + "  border-collapse: collapse;\n"
                + "}\n"
                + "\n"
                + "p {\n"
                + "  margin: 0;\n"
                + "}\n"
                + "\n"
                + ".ie-container table,\n"
                + ".mso-container table {\n"
                + "  table-layout: fixed;\n"
                + "}\n"
                + "\n"
                + "* {\n"
                + "  line-height: inherit;\n"
                + "}\n"
                + "\n"
                + "a[x-apple-data-detectors='true'] {\n"
                + "  color: inherit !important;\n"
                + "  text-decoration: none !important;\n"
                + "}\n"
                + "\n"
                + "</style>\n"
                + "  \n"
                + "  \n"
                + "\n"
                + "<!--[if !mso]><!--><link href=\"https://fonts.googleapis.com/css?family=Crimson+Text:400,700&display=swap\" rel=\"stylesheet\" type=\"text/css\"><!--<![endif]-->\n"
                + "\n"
                + "</head>\n"
                + "\n"
                + "<body class=\"clean-body u_body\" style=\"margin: 0;padding: 0;-webkit-text-size-adjust: 100%;background-color: #fbeeb8;color: #000000\">\n"
                + "  <!--[if IE]><div class=\"ie-container\"><![endif]-->\n"
                + "  <!--[if mso]><div class=\"mso-container\"><![endif]-->\n"
                + "  <table style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;min-width: 320px;Margin: 0 auto;background-color: #fbeeb8;width:100%\" cellpadding=\"0\" cellspacing=\"0\">\n"
                + "  <tbody>\n"
                + "  <tr style=\"vertical-align: top\">\n"
                + "    <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\n"
                + "    <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\" style=\"background-color: #fbeeb8;\"><![endif]-->\n"
                + "    \n"
                + "\n"
                + "<div class=\"u-row-container\" style=\"padding: 0px;background-color: #ffffff\">\n"
                + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">\n"
                + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: #ffffff;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->\n"
                + "      \n"
                + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"550\" style=\"width: 550px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->\n"
                + "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 550px;display: table-cell;vertical-align: top;\">\n"
                + "  <div style=\"width: 100% !important;\">\n"
                + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->\n"
                + "  \n"
                + "<table id=\"u_content_image_1\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                + "  <tbody>\n"
                + "    <tr>\n"
                + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:30px 10px 33px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                + "        \n"
                + "\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody>\n"
                + "</table>\n"
                + "\n"
                + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                + "  </div>\n"
                + "</div>\n"
                + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                + "    </div>\n"
                + "  </div>\n"
                + "</div>\n"
                + "\n"
                + "\n"
                + "\n"
                + "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
                + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: transparent;\">\n"
                + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: transparent;\"><![endif]-->\n"
                + "      \n"
                + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"550\" style=\"width: 550px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                + "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 550px;display: table-cell;vertical-align: top;\">\n"
                + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                + "  \n"
                + "<table style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                + "  <tbody>\n"
                + "    <tr>\n"
                + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:20px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                + "        \n"
                + "  <table height=\"0px\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;border-top: 0px solid #BBBBBB;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%\">\n"
                + "    <tbody>\n"
                + "      <tr style=\"vertical-align: top\">\n"
                + "        <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top;font-size: 0px;line-height: 0px;mso-line-height-rule: exactly;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%\">\n"
                + "          <span>&#160;</span>\n"
                + "        </td>\n"
                + "      </tr>\n"
                + "    </tbody>\n"
                + "  </table>\n"
                + "\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody>\n"
                + "</table>\n"
                + "\n"
                + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                + "  </div>\n"
                + "</div>\n"
                + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                + "    </div>\n"
                + "  </div>\n"
                + "</div>\n"
                + "\n"
                + "\n"
                + "\n"
                + "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
                + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">\n"
                + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->\n"
                + "      \n"
                + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"542\" style=\"width: 542px;padding: 0px;border-top: 4px solid #d9d8d8;border-left: 4px solid #d9d8d8;border-right: 4px solid #d9d8d8;border-bottom: 4px solid #d9d8d8;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                + "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 550px;display: table-cell;vertical-align: top;\">\n"
                + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 4px solid #d9d8d8;border-left: 4px solid #d9d8d8;border-right: 4px solid #d9d8d8;border-bottom: 4px solid #d9d8d8;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                + "  \n"
                + "<table id=\"u_content_image_2\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                + "  <tbody>\n"
                + "    <tr>\n"
                + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:40px 10px 10px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                + "        \n"
                + "\n"
                + "\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody>\n"
                + "</table>\n"
                + "\n"
                + "<table id=\"u_content_text_1\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                + "  <tbody>\n"
                + "    <tr>\n"
                + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:10px 30px 30px 40px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                + "        \n"
                + "  <div class=\"v-text-align\" style=\"color: #333333; line-height: 140%; text-align: left; word-wrap: break-word;\">\n"
                + "    <p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-family: 'Crimson Text', serif; font-size: 14px; line-height: 19.6px;\"><strong><span style=\"font-size: 22px; line-height: 30.8px;\">pifinity</span></strong></span></p>\n"
                + "<p style=\"font-size: 14px; line-height: 140%;\">&nbsp;</p>\n"
                + "<p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-size: 24px; line-height: 25.2px; font-family: 'Crimson Text', serif;\">You have requested for transaction </span></p>\n"
                + "\n"
                + "  </div>\n"
                + "\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody>\n"
                + "</table>\n"
                + "\n"
                + "<table id=\"u_content_button_1\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                + "  <tbody>\n"
                + "    <tr>\n"
                + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:10px 40px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                + "        \n"
                + "<div class=\"v-text-align\" align=\"left\">\n"
                + "  <!--[if mso]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;font-family:arial,helvetica,sans-serif;\"><tr><td class=\"v-text-align\" style=\"font-family:arial,helvetica,sans-serif;\" align=\"left\"><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"https://unlayer.com\" style=\"height:47px; v-text-anchor:middle; width:456px;\" arcsize=\"6.5%\" strokecolor=\"#ced4d9\" strokeweight=\"3px\" fillcolor=\"#91a5e2\"><w:anchorlock/><center style=\"color:#000000;font-family:arial,helvetica,sans-serif;\"><![endif]-->\n"
                + "    <a target=\"_blank\" class=\"v-size-width\" style=\"box-sizing: border-box;display: inline-block;font-family:arial,helvetica,sans-serif;text-decoration: none;-webkit-text-size-adjust: none;text-align: center;color: #000000; background-color: #91a5e2; border-radius: 3px;-webkit-border-radius: 3px; -moz-border-radius: 3px; width:100%; max-width:100%; overflow-wrap: break-word; word-break: break-word; word-wrap:break-word; mso-border-alt: none;border-top-color: #ced4d9; border-top-style: solid; border-top-width: 3px; border-left-color: #ced4d9; border-left-style: solid; border-left-width: 3px; border-right-color: #ced4d9; border-right-style: solid; border-right-width: 3px; border-bottom-color: #ced4d9; border-bottom-style: solid; border-bottom-width: 3px;\">\n"
                + "      <span class=\"v-padding\" style=\"display:block;padding:15px 40px;line-height:120%;\"><span style=\"font-size: 28px; line-height: 16.8px;\">" + a + "</span></span>\n"
                + "    </a>\n"
                + "  <!--[if mso]></center></v:roundrect></td></tr></table><![endif]-->\n"
                + "</div>\n"
                + "\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody>\n"
                + "</table>\n"
                + "\n"
                + "<table id=\"u_content_text_3\" style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                + "  <tbody>\n"
                + "    <tr>\n"
                + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:30px 30px 80px 40px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                + "        \n"
                + "  <div class=\"v-text-align\" style=\"color: #333333; line-height: 140%; text-align: left; word-wrap: break-word;\">\n"
                + "    <p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-size: 18px; line-height: 25.2px; font-family: 'Crimson Text', serif;\"> A unique Code to confirm  for you. please confirm your transaction if it was you</span></p>\n"
                + "\n"
                + "  </div>\n"
                + "\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody>\n"
                + "</table>\n"
                + "\n"
                + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                + "  </div>\n"
                + "</div>\n"
                + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                + "    </div>\n"
                + "  </div>\n"
                + "</div>\n"
                + "\n"
                + "\n"
                + "\n"
                + "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
                + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: transparent;\">\n"
                + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: transparent;\"><![endif]-->\n"
                + "      \n"
                + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"550\" style=\"width: 550px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                + "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 550px;display: table-cell;vertical-align: top;\">\n"
                + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                + "  \n"
                + "<table style=\"font-family:arial,helvetica,sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
                + "  <tbody>\n"
                + "    <tr>\n"
                + "      <td class=\"v-container-padding-padding\" style=\"overflow-wrap:break-word;word-break:break-word;padding:50px 10px 30px;font-family:arial,helvetica,sans-serif;\" align=\"left\">\n"
                + "        \n"
                + "<div align=\"center\">\n"
                + "  <div style=\"display: table; max-width:170px;\">\n"
                + "  <!--[if (mso)|(IE)]><table width=\"170\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"border-collapse:collapse;\" align=\"center\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse; mso-table-lspace: 0pt;mso-table-rspace: 0pt; width:170px;\"><tr><![endif]-->\n"
                + "  \n"
                + "  \n"
                + "    \n"
                + "\n"
                + "  </div>\n"
                + "</div>\n"
                + "\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody>\n"
                + "</table>\n"
                + "\n"
                + "\n"
                + "\n"
                + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                + "  </div>\n"
                + "</div>\n"
                + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                + "    </div>\n"
                + "  </div>\n"
                + "</div>\n"
                + "\n"
                + "\n"
                + "\n"
                + "<div class=\"u-row-container\" style=\"padding: 0px 0px 11px;background-color: transparent\">\n"
                + "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 550px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: transparent;\">\n"
                + "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">\n"
                + "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px 0px 11px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:550px;\"><tr style=\"background-color: transparent;\"><![endif]-->\n"
                + "      \n"
                + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"275\" style=\"width: 275px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                + "<div class=\"u-col u-col-50\" style=\"max-width: 320px;min-width: 275px;display: table-cell;vertical-align: top;\">\n"
                + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                + "\n"
                + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                + "  </div>\n"
                + "</div>\n"
                + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                + "<!--[if (mso)|(IE)]><td align=\"center\" width=\"275\" style=\"width: 275px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\" valign=\"top\"><![endif]-->\n"
                + "<div class=\"u-col u-col-50\" style=\"max-width: 320px;min-width: 275px;display: table-cell;vertical-align: top;\">\n"
                + "  <div style=\"width: 100% !important;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\">\n"
                + "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;border-radius: 0px;-webkit-border-radius: 0px; -moz-border-radius: 0px;\"><!--<![endif]-->\n"
                + "  \n"
                + "\n"
                + "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->\n"
                + "  </div>\n"
                + "</div>\n"
                + "<!--[if (mso)|(IE)]></td><![endif]-->\n"
                + "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\n"
                + "    </div>\n"
                + "  </div>\n"
                + "</div>\n"
                + "\n"
                + "\n"
                + "    <!--[if (mso)|(IE)]></td></tr></table><![endif]-->\n"
                + "    </td>\n"
                + "  </tr>\n"
                + "  </tbody>\n"
                + "  </table>\n"
                + "  <!--[if mso]></div><![endif]-->\n"
                + "  <!--[if IE]></div><![endif]-->\n"
                + "</body>\n"
                + "\n"
                + "</html>";







        String to="ncib.fedi@esprit.tn";
        String subject="confirm transaction";
        String body=  "Your code to confirm transaction: " + msg;
        sendEmail( to,  subject,  body);
        return a;
    }






    private void sendEmail(String to, String subject, String body) throws MessagingException {
        // Send an email
        // ...
        String from = "techwork414@gmail.com";
        String password = "pacrvzlvscatwwkb";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        //   message.setText(body);
        message.setContent(body, "text/html");

        Transport.send(message);
    }












}
