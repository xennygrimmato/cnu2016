package com.devfactory.assignment4.controller;

import com.devfactory.assignment4.model.OrderProduct;
import com.devfactory.assignment4.repository.OrderProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by vaibhavtulsyan on 09/07/16.
 */

@RequestMapping("/api")
@RestController
public class OrderProductController {

    @Autowired
    OrderProductRepository orderProductRepo;

    @RequestMapping("/order_products")
    public Iterable<OrderProduct> orderProducts() {
        return orderProductRepo.findAll();
    }
}
