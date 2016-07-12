package com.devfactory.assignment4.controller;

import com.devfactory.assignment4.model.Customer;
import com.devfactory.assignment4.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhavtulsyan on 09/07/16.
 */

@RequestMapping("/api")
@RestController
public class CustomerController {
    @Autowired
    CustomerRepository customerRepo;

    @RequestMapping("/customers")
    public List<Customer> customers() {
        List<Customer> customers = new ArrayList<Customer>();
        for(Customer customer : customerRepo.findAll()) {
            customers.add(customer);
        }
        return customers;
    }
}
