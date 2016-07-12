package com.devfactory.assignment4.controller;

import com.devfactory.assignment4.model.Orders;
import com.devfactory.assignment4.repository.OrdersRepository;
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
public class OrdersController {

    @Autowired
    OrdersRepository ordersRepo;

    @RequestMapping("/orders")
    public List<Orders> orders() {
        List<Orders> ordersList = new ArrayList<Orders>();
        for(Orders order : ordersRepo.findAll()) {
            ordersList.add(order);
        }
        return ordersList;
    }
}
