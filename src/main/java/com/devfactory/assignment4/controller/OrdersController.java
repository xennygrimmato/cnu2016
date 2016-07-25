package com.devfactory.assignment4.controller;

import com.devfactory.assignment4.model.Orders;
import com.devfactory.assignment4.repository.CustomerRepository;
import com.devfactory.assignment4.repository.OrdersRepository;
import com.devfactory.assignment4.service.OrderService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vaibhavtulsyan on 09/07/16.
 */

@RequestMapping("/api")
@RestController
public class OrdersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class.getName());

    @Autowired
    OrdersRepository ordersRepo;

    @Autowired
    OrderService orderService;

    @Autowired
    CustomerRepository customerRepo;

    @RequestMapping("/orders")
    public ResponseEntity getOrders() {
        List<Orders> allOrders= orderService.getAllOrders();
        return new ResponseEntity(allOrders, HttpStatus.OK);
    }

    // Create an order
    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public ResponseEntity createOrder(@RequestBody Map<String, Object> requestBody) {

        try {
            String customerName;
            String userName = requestBody.get("user_name").toString();
            if (StringUtils.isBlank(userName)) {
                customerName = "";
            } else {
                customerName = userName;
            }

            Boolean orderAdded = orderService.createNewOrder(customerName);
            if (orderAdded) {
                return new ResponseEntity(null, HttpStatus.CREATED);
            } else {
                return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
            }
        } catch(Exception e) {}

        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value="/orders/{id}", method=RequestMethod.GET)
    public ResponseEntity<Object> getProduct(@PathVariable int id) {
        Orders order = ordersRepo.findOne(id);

        if(order == null) {
            Map<String,String> detailObject = new HashMap<String,String>();
            detailObject.put("detail", "Not found.");
            return new ResponseEntity<Object>(detailObject, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Object>(order, HttpStatus.OK);
    }

    @RequestMapping(value = "/orders/{id}/orderLineItem", method = RequestMethod.POST)
    public ResponseEntity addItemToOrder(@PathVariable Integer id, @RequestBody Map<String, Object> requestBody) {
        Integer productId = Integer.parseInt(requestBody.get("product_id").toString());
        Integer qty = Integer.parseInt(requestBody.get("qty").toString());
        Orders order = ordersRepo.findOne(id);

        if(productId == null) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
        if(qty == null) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
        return orderService.addItem(productId, qty, id);
    }

    // Submit an order
    @RequestMapping(value = "/orders/{id}", method = RequestMethod.PATCH)
    public ResponseEntity submitOrder(@PathVariable Integer id, @RequestBody Map<String, Object> requestBody) {

        try {
            String address = requestBody.get("address").toString();
            String customerName = requestBody.get("user_name").toString();

            if (address == null) {
                return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
            }

            Orders order = ordersRepo.findOne(id);
            if (order == null) {
                return new ResponseEntity(null, HttpStatus.NOT_FOUND);
            }

            if(customerName == null) {
                customerName = customerRepo.findOne(order.getCustomerId()).getCompanyName();
            }
            return orderService.performSubmit(address, customerName, id);
        } catch(Exception e) {}
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }
}
