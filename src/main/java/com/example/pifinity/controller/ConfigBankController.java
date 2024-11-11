package com.example.pifinity.controller;



import com.example.pifinity.entity.Configbank;
import com.example.pifinity.serviceInterface.IConfigBankService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/config")
@AllArgsConstructor
public class ConfigBankController {

    private IConfigBankService configBankService;


    @PostMapping("/add")
    public Configbank addConfigBank(@RequestBody Configbank configbank) {
        return configBankService.addConfigBank(configbank);
    }

    @PutMapping("/update/{id}")
    public Configbank updateConfigBank(@PathVariable int id, @RequestBody Configbank configbank) {
        return configBankService.updateConfigBank(id, configbank);
    }

    @GetMapping("/getlastconfig")
    public Configbank getlastconfig() {
        return configBankService.getlastconfig();
    }

    @GetMapping("/{id}")
    public Configbank findByid(@PathVariable("id") int id) {
        return configBankService.retrieveConfigbank(id);
    }

    @GetMapping("/allconfigbank")
    public List<Configbank> findAllConfigBank(){
        return configBankService.retrieveAllConfigBank();
    }
}
