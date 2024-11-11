package com.example.pifinity.controller;



import com.example.pifinity.entity.Transaction;
import com.example.pifinity.serviceInterface.ITransactionService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/transaction")
@AllArgsConstructor
public class TransactionController {

    //@Autowired
    private ITransactionService transactionService;


  @PostMapping("/paymentcreditcard")
    public void paymentcreditcard() {
        transactionService.paymentcreditcard();
    }

    @GetMapping("/alltransaction")
    public List<Transaction> findAllTransaction(){

        return transactionService.retrieveAllTransaction();}

    @GetMapping("/{idtransaction}")
    public Transaction findByIdTransaction(@PathVariable("idtransaction") int idtransaction){
        return transactionService.retrieveTransaction(idtransaction);}


    @PostMapping("/addtransaction/{numcard}/{password}/{confirmpassword}/{cvv}")
    public Transaction addTransaction(@PathVariable int numcard,@PathVariable int password,@PathVariable int confirmpassword,@PathVariable int cvv,@RequestBody Transaction transaction) throws MessagingException {
        return transactionService.addTransaction(numcard,password,confirmpassword,cvv,transaction);
    }

    @PostMapping("/addtr")
    public Transaction addtr(@RequestBody Transaction t){

        return transactionService.addtr(t);
    }

    @PostMapping("/addtransaction/{numcard}/{password}/{cvv}")
    public Transaction addt(@PathVariable int numcard,@PathVariable int password,@PathVariable int cvv,@RequestBody Transaction transaction) throws MessagingException {
        return transactionService.addt(numcard,password,cvv,transaction);
    }

    @PostMapping("/confirmsplitransaction/{a}/{numcard}")
    public Transaction confirmsplittransaction(@PathVariable int a,@PathVariable int numcard,@RequestBody Transaction t){
        return transactionService.confirmsplittransaction(a,t,numcard);
    }

    @GetMapping("/splittransaction/{code}/{amount}")
    public int splittransaction(@PathVariable("code") int code,@PathVariable("amount") int amount) throws MessagingException {

        return transactionService.splittransaction(code,amount);

    }


    /*

    @GetMapping("/confirmTransaction")
    public int confirmTransaction() throws MessagingException {
        return transactionService.confirmTransaction();
    }
*/

    @DeleteMapping("/delete/{idtransaction}")
    public void deleteBankAccount(@PathVariable("idtransaction") int idtransaction) {
        transactionService.deleteTransaction(idtransaction);
    }




    @GetMapping("/transactionbyaccount/{rib_s}")
    public List<Transaction> findByAccount(@PathVariable("rib_s") int rib_s){
        return transactionService.retrieveAllTransactionbyBankAccount(rib_s);
    }

    @GetMapping("/transactionbysubaccount/{rib_s}")
    public List<Transaction> findBySubBankAccount(@PathVariable("rib_s") int rib_s){
        return transactionService.retrieveAllTransactionbySubAccount(rib_s);
    }

    @GetMapping("/calculatetransaction/{rib_s}/{rib_d}")
    public int calculatetransaction(@PathVariable("rib_s") int rib_s,@PathVariable("rib_d") int rib_d){
        return transactionService.calculatetransaction(rib_s,rib_d);}


    @GetMapping("/epargne/{id}")
    public double calcule(@PathVariable("id") int id) {
        return transactionService.CalculeEpargne(id);}

}
