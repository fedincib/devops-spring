package com.example.pifinity.controller;



import com.example.pifinity.entity.SubBankAccount;

import com.example.pifinity.entity.VirtualBankCard;
import com.example.pifinity.serviceInterface.ISubBankAccountService;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/subbankaccount")
@AllArgsConstructor
public class SubBankAccountController {


    private ISubBankAccountService subBankAccountService;

    @GetMapping("/allsubbankaccount")
    public List<SubBankAccount> findAllSubBankAccount(){

        return subBankAccountService.retrieveAllSubBankAccount();}

    @GetMapping("/{subbankaccountid}")
    public SubBankAccount findByIdSubBankAccount(@PathVariable("subbankaccountid") int id){
        return subBankAccountService.retrieveSubBankAccount(id);}

    @GetMapping("/s/{rib}")
    public List<SubBankAccount> retrieveAllSubaccountbybankaccount(@PathVariable("rib") int rib) {
        return subBankAccountService.retrieveAllSubaccountbybankaccount(rib);
    }




    @PostMapping("/addsubbankaccount/{rib}")
    public SubBankAccount addSubBankAccount(@PathVariable int rib,@RequestBody SubBankAccount subbankAccount) {
        return subBankAccountService.addSubBankAccount( rib,subbankAccount);
    }


    @PutMapping("/update/{subbankaccountid}")
    public SubBankAccount updateSubBankAccount(@PathVariable int subbankaccountid , @RequestBody SubBankAccount subbankAccount) {
        return subBankAccountService.updateSubBankAccount(subbankaccountid , subbankAccount);
    }

    @DeleteMapping("/delete/{subbankaccountid}")
    public void deleteBankAccount(@PathVariable("subbankaccountid") int subbankaccountid) {
        subBankAccountService.deleteSubBankAccount(subbankaccountid);
    }

   /*@GetMapping("/epargne/{id}")
    public double calcule(@PathVariable("id") int id) {
        return subBankAccountService.calcule(id);}*/


    @GetMapping("/allsubbankaccountbyorder")
    public List<SubBankAccount> findAllSubBankAccountByOrder(){

        return subBankAccountService.retrieveAllSubBankAccountByOrder();}


   @PostMapping("/simulateur/{capital}/{tauxInteretMensuel}/{VersementMensuel}/{Duree}")
    public double calculerEpargne(@PathVariable("capital")double capital,@PathVariable("tauxInteretMensuel") double tauxInteretMensuel, @PathVariable("VersementMensuel")double VersementMensuel,@PathVariable("Duree") int Duree) {
        return subBankAccountService.calculerEpargne(capital, tauxInteretMensuel, VersementMensuel, Duree);
    }
}
