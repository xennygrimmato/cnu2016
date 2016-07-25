package com.devfactory.assignment4.service;

import com.devfactory.assignment4.controller.ProductController;
import com.devfactory.assignment4.model.*;
import com.devfactory.assignment4.repository.CustomerRepository;
import com.devfactory.assignment4.repository.OrderProductRepository;
import com.devfactory.assignment4.repository.OrdersRepository;
import com.devfactory.assignment4.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vaibhavtulsyan on 12/07/16.
 */

@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class.getName());

    @Autowired
    OrdersRepository ordersRepo;

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    CustomerService customerService;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    OrderProductRepository orderProductRepo;

    public List<Orders> getAllOrders() {
        List<Orders> ordersList = new ArrayList<Orders>();
        for(Orders order : ordersRepo.findAll()) {
            ordersList.add(order);
        }
        return ordersList;
    }

    public Boolean createNewOrder(String customerName) {
        try {
            Customer customer = customerRepo.findUniqueByCompanyName(customerName);
            if (customer == null) {
                Customer userAdded = customerService.addUser(customerName);
                customer = customerRepo.findUniqueByCompanyName(customerName);
            }
            Date date = new Date();
            Orders order = new Orders();
            order.setDate(date);
            order.setCustomerId(customer.getId());
            order.setStatus("Created");
            order.setAmount(new BigDecimal(0));
            ordersRepo.save(order);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public ResponseEntity addItem(Integer productId, Integer quantity, Integer orderId) {

        try {
            // product must exist
            // quantity must be <= remaining stock of product
            Product product = productRepo.findOne(productId);
            if (product == null) {
                return new ResponseEntity(null, HttpStatus.NOT_FOUND);
            }
            Orders order = ordersRepo.findOne(orderId);
            if (order == null) { return new ResponseEntity(null, HttpStatus.NOT_FOUND);}

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrderId(orderId);
            orderProduct.setProductId(productId);
            orderProduct.setSellingCost(new BigDecimal(0));
            OrderProductId orderProductId = new OrderProductId();
            orderProductId.setOrderId(order.getOrderId());
            orderProductId.setProductId(product.getId());
            OrderProduct existing = orderProductRepo.findOne(orderProductId);

            if (existing != null) {
                Integer existingQuantity = existing.getQuantity();
                Integer newQuantity = existingQuantity + quantity;
                if(newQuantity < 0) newQuantity = 0;
                existing.setQuantity(newQuantity);
                orderProductRepo.save(existing);
                return new ResponseEntity(null, HttpStatus.CREATED);
            } else {
                // save to repo
                OrderProduct newOrderProduct = new OrderProduct();

                newOrderProduct.setOrderId(orderId);
                newOrderProduct.setProductId(productId);
                quantity = ((quantity < 0) ? 0 : quantity);
                newOrderProduct.setQuantity(quantity);

                orderProductRepo.save(newOrderProduct);
                return new ResponseEntity(null, HttpStatus.CREATED);
            }
        } catch(Exception e) {}
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity performSubmit(String address, String customerName, Integer orderId) {

        try {
            Orders order = ordersRepo.findOne(orderId);

            if (order == null) {
                return new ResponseEntity(null, HttpStatus.NOT_FOUND);
            }
            Boolean cancelled = false;

            for (OrderProduct orderProduct : order.getOrderToProductMap()) {
                Product _product_ = productRepo.findOne(orderProduct.getProductId());

                Integer remaining = _product_.getRemaining();

                if(orderId == orderProduct.getOrderId()) {
                    Integer quantityOrdered = orderProduct.getQuantity();
                    if (quantityOrdered > remaining) {
                        // cancel order
                        cancelled = true;
                        // change status
                        order.setStatus("Cancelled");
                        ordersRepo.save(order);
                        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            if (!cancelled) {
                for (OrderProduct orderProduct : order.getOrderToProductMap()) {
                    Product _product_ = productRepo.findOne(orderProduct.getProductId());
                    Integer remaining = _product_.getRemaining();
                    Integer quantityOrdered = orderProduct.getQuantity();
                    int newRemaining = remaining - quantityOrdered; // null
                    if (newRemaining < 0) { newRemaining = 0;}
                    _product_.setRemaining(newRemaining);
                    productRepo.save(_product_);
                }
            }

            if (customerName != null) {
                Customer customer = customerRepo.findUniqueByCompanyName(customerName);
                if (customer != null) {
                    customer.setAddrLine1(address);
                    order.setStatus("checkout");
                    ordersRepo.save(order);
                } else {
                    customer = new Customer();
                    customer.setCompanyName(customerName);
                    customer.setAddrLine1(address);
                    customerRepo.save(customer);
                    customer = customerRepo.findUniqueByCompanyName(customerName);
                    order.setStatus("checkout");
                    ordersRepo.save(order);
                }
                return new ResponseEntity(null, HttpStatus.OK);
            }

            else {
                order.setStatus("checkout");
                ordersRepo.save(order);
                return new ResponseEntity(null, HttpStatus.OK);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }
}
