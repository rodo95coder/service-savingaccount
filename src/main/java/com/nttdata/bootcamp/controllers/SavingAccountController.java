package com.nttdata.bootcamp.controllers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.bootcamp.exceptions.InsufficientAmountExcepcion;
import com.nttdata.bootcamp.exceptions.PersonalCreditNotFoundException;
import com.nttdata.bootcamp.models.PersonalCredit;
import com.nttdata.bootcamp.models.SavingAccount;
import com.nttdata.bootcamp.services.ISavingAccountService;
import com.nttdata.bootcamp.utils.Constants;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/savingAccount")
public class SavingAccountController {
	
	@Autowired
	private ISavingAccountService sarepo;
	
	@Autowired
    private WebClient.Builder webClientBuilder;
	
	@GetMapping("/findAll")
	public Flux<SavingAccount> findAll(){
		return sarepo.findAll();
	}
	
	@GetMapping("/findById/{id}")
	public Mono<SavingAccount> findById(@PathVariable String id){
		return sarepo.findById(id);
	}
	
	@PatchMapping("/update")
	public Mono<SavingAccount> update(@RequestBody SavingAccount savingAccount){
		return sarepo.findById(savingAccount.getId()).flatMap(s->{
			s.setAccountingBalance(savingAccount.getAccountingBalance());
			s.setMaintenance(savingAccount.getMaintenance());
			s.setMovementLimit(savingAccount.getMovementLimit());
			log.info("a SavingAccount was updated");
			return sarepo.save(s);
		});
	}                                                                                                                          
	
	@PostMapping("/save")
	public Mono<SavingAccount> save(@RequestBody SavingAccount savingAccount){
		
		Mono<PersonalCredit> personalCredit = webClientBuilder
                .baseUrl("http://service-product-personalcredit")
                .build()
                .get()
                .uri("/personalCredit/findByIdCustomerPerson/"+savingAccount.getIdCustomerPerson())
                .retrieve()
                .bodyToMono(PersonalCredit.class);
		
		return personalCredit.flatMap(p->{
			BigDecimal abBigDec = new BigDecimal(savingAccount.getAccountingBalance());
			if(abBigDec.compareTo(BigDecimal.ZERO)<0) {
				Mono.error(new InsufficientAmountExcepcion("The account balance can't be less than zero"));
			}
			if(p==null){
				Mono.error(new PersonalCreditNotFoundException("You need a product Personal Credit to create Saving Account"));
			}
			if(abBigDec.compareTo(new BigDecimal(Constants.AVERAGEAMOUNT))>0){
				savingAccount.setProfile(Constants.VIP);
			}else {
				savingAccount.setProfile(Constants.NONE);
			}
			return sarepo.save(savingAccount);
		});
	}
	
	@DeleteMapping("/delete")
	public Mono<Void> delete(@RequestBody SavingAccount savingAccount){
		return sarepo.delete(savingAccount);
	}
	
	@GetMapping("/findByIdCustomerPerson/{idCustomerPerson}")
	public Mono<SavingAccount> findByIdCustomerPerson(@PathVariable String idCustomerPerson){
		return sarepo.findByIdCustomerPerson(idCustomerPerson);
	}

}