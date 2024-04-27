package com.cmu.customerwebservice.controller;

import com.cmu.customerwebservice.entities.CustomerRequest;
import com.cmu.customerwebservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * This is the CustomerController that handles all customer related requests
 * It redirects the control to the CustomerService that performs business logic
 *
 * @author Rheann Sequeira
 */
@RestController
@Validated
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * @param customerRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody CustomerRequest customerRequest) {
        System.out.println("Received request to add following customer: " + customerRequest.toString());

        return customerService.addCustomer(customerRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        System.out.println("Request to get customer by id: " + id);
        return customerService.getCustomerById(id);
    }

    @GetMapping
    public ResponseEntity<?> getCustomerByUserId(@RequestParam String userId) {
        System.out.println("Request to get customer by userid:" + userId);
        return customerService.getCustomerByUserId(userId);
    }


}
