package com.devfactory.assignment4;

import com.devfactory.assignment4.controller.HealthController;
import com.devfactory.assignment4.model.Customer;
import com.devfactory.assignment4.model.OrderProduct;
import com.devfactory.assignment4.model.Orders;
import com.devfactory.assignment4.model.Product;
import com.devfactory.assignment4.repository.CustomerRepository;
import com.devfactory.assignment4.repository.OrderProductRepository;
import com.devfactory.assignment4.repository.OrdersRepository;
import com.devfactory.assignment4.repository.ProductRepository;
import com.devfactory.assignment4.service.CustomerService;
import com.devfactory.assignment4.service.OrderService;
import com.jayway.restassured.RestAssured;
import junit.framework.TestCase;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vaibhavtulsyan on 23/07/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource("classpath:test.properties")
public class OrdersControllerTest extends TestCase {

    public OrdersControllerTest() {}

    @Value("${local.server.port}")
    int port;

    @Autowired
    OrdersRepository ordersRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    OrderProductRepository orderProductRepo;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;
    private Product product1;
    private Customer customer1;
    private OrderProduct medium1;
    private String address;
    private static final int invalidId = -1;
    private static final String invalidCustomerName = "invalidName";
    private HealthController healthController;
    Orders order1;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsControllerTest.class);

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;

        order1 = new Orders();
        try {
            String customerName = "testname";
            try {
                customer1 = new Customer();
                customer1.setCompanyName(customerName);
                customer1.setFirstName("sdfsdfsdf");
                customerRepo.save(customer1);
            } catch(Exception e) {
                // maybe a UserCreationException object must be thrown
                LOGGER.error(e.getMessage());
                customer1 = new Customer();
                customer1.setCompanyName("vkhvkvjk");
                customerRepo.save(customer1);
            }
            Date date = new Date();
            order1.setDate(date);
            order1.setCustomerId(customer1.getId());
            order1.setStatus("Created");
            order1.setAmount(new BigDecimal(111.0));
            ordersRepo.save(order1);
            System.out.println("********************************************************************");
            System.out.println(order1.getOrderId());
            System.out.println("********************************************************************");

        } catch(Exception e) {
            e.printStackTrace();
        }

        product1 = new Product();
        product1.setDeleted(0);
        product1.setCode("abcd");
        product1.setName("test_product");
        productRepo.save(product1);

        medium1 = new OrderProduct();
        medium1.setProductId(product1.getId());
        if(order1 != null) medium1.setOrderId(order1.getOrderId());
        else medium1.setOrderId(3034);
        medium1.setQuantity(500);
        medium1.setSellingCost(new BigDecimal(100.0));
        medium1.setBuyingCost(new BigDecimal(50.0));
        orderProductRepo.save(medium1);
    }

    @Test
    public void testHealth() {
        RestAssured.
                when().
                get("/api/health").
                then().
                statusCode(HttpStatus.SC_OK);
    }



//    @Test
//    public void deleteOrder() {
//        int order1Id = orders1.getOrderId();
//       // Map<String, Integer> input = new HashMap<>();
//       // int product1Id = product1.getId();
//       // input.put("product_id", 1);
//     //   int qty = medium1.getQuantity();
//      //  input.put("qty", qty);
//        RestAssured.when().
//                delete("/api/orders/{pk}", orders1.getOrderId()).
//                then().
//                statusCode(HttpStatus.SC_OK);

//        RestAssured.given().
//                contentType("application/json").
//                body(input).
//                when().
//                patch("/api/orders/{pk}", order2Id).
//                then().
//                statusCode(HttpStatus.SC_NOT_FOUND);
//
//        RestAssured.when().
//                delete("/api/orders/{pk}", order2Id).
//                then().
//                statusCode(HttpStatus.SC_NOT_FOUND);
//
//
//        RestAssured.given().
//                contentType("application/json").
//                body(input).
//                when().
//                post("/api/orders/{pk}/orderLineItem", order2Id).
//                then().
//                statusCode(HttpStatus.SC_NOT_FOUND);
//
//
//        RestAssured.when().
//                get("/api/orders/{order_id}", order2Id).
//                then().
//                statusCode(HttpStatus.SC_NOT_FOUND);
//    }

    @Test
    public void getOrder() {                   // check if orderId exists or not.
        int orders1Id = order1.getOrderId();
        RestAssured.when().
                get("/api/orders/{id}", orders1Id).
                then().
                statusCode(HttpStatus.SC_OK).
                body("status", Matchers.is("Created"));
    }

    @Test
    public void getInvalidOrder() {                   // get invalid order.
        RestAssured.when().
                get("/api/orders/{id}", invalidId).
                then().
                statusCode(HttpStatus.SC_NOT_FOUND);

    }

    @Test
    public void postOrder() {                 // check if order created or not.
        orderService.createNewOrder("test_user");
        Map<String, Object> x = new HashMap<String, Object>();
        x.put("user_name", "testname_user");
        RestAssured.given().
                contentType("application/json").
                body(x).
                when().
                post("/api/orders").
                then().
                statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    public void addItemtoOrder() {               // check -> Add an item to an order-> Happy test case
        Map<String, Integer> product = new HashMap<>();
        int orders1Id = order1.getOrderId();
        int product1Id = product1.getId();
        orderService.addItem(product1Id, 10, orders1Id);

        Map<String, Object> x = new HashMap<String, Object>();
        x.put("product_id", product1Id);
        x.put("qty", 10);

        RestAssured.given().
                contentType("application/json").
                body(x).
                when().
                post("/api/orders/{pk}/orderLineItem", orders1Id).
                then().
                statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    public void submitOrderWithoutUserName() {                // Check if order submitted or not -> Username not given
        Map<String, String> input = new HashMap<>();
        int orders1Id = order1.getOrderId();
        input.put("address", address);
        RestAssured.given().
                contentType("application/json").
                body(input).
                when().
                patch("/api/orders/{pk}", orders1Id).
                then().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void testInvalidOrderIdPerformSubmit() {
        //ResponseEntity ret = orderService.performSubmit("addr", "cust", 10012);
        Map<String, Object> x = new HashMap<String, Object>();
        x.put("address", "addr");
        x.put("user_name", "userx");
        RestAssured.given().
                contentType("application/json").
                body(x).
                when().
                patch("/api/orders/{pk}", 10012).
                then().
                statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void testValidOrderIdPerformSubmit() {

        List<Orders> order_lst = orderService.getAllOrders();

        orderService.performSubmit("addr", "CustomerName", 3014);

        Map<String, Object> x = new HashMap<String, Object>();
        x.put("address", "addr");
        x.put("user_name", "userx");
        RestAssured.given().
                contentType("application/json").
                body(x).
                when().
                patch("/api/orders/{pk}", 3014).
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testAnotherInvalidOrderIdPerformSubmit() {
        orderService.performSubmit("addr", "CustomerName", 112);
        Map<String, Object> x = new HashMap<String, Object>();
        x.put("address", "addr");
        x.put("user_name", "userx");
        RestAssured.given().
                contentType("application/json").
                body(x).
                when().
                patch("/api/orders/{pk}", 112).
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testAddUser() {
        try {
            List<Customer> lst = customerService.getAllCustomers();
            RestAssured.when().
                    get("/api/customers").
                    then().
                    statusCode(HttpStatus.SC_OK);
        } catch(Exception e) {}
    }

    @Test
    public void testAddItemToOrder() {
        orderService.addItem(1,100,1000);
    }
}
