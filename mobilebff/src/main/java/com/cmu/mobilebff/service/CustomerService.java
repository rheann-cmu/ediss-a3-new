package com.cmu.mobilebff.service;

import com.cmu.mobilebff.model.CustomerRequest;
import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.codec.Decoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import java.util.Map;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomerService {
    private static final String CUSTOMER_SERVICE_URL = "http://customer-service.bookstore-ns.svc.cluster.local:3000/customers";
//    private static final String CUSTOMER_SERVICE_URL = "http://internal-InternalALB-1285315458.us-east-1.elb.amazonaws.com:3000/customers";
//    private static final String CUSTOMER_SERVICE_URL = "http://localhost:3000/customers";
    private static CustomerClient customerClient = createCustomerClient();

    private static CustomerClient createCustomerClient() {
        ObjectFactory<HttpMessageConverters> messageConverters = () -> new HttpMessageConverters();
        Decoder feignDecoder = new ResponseEntityDecoder(new SpringDecoder(messageConverters));

        CustomerClient customerClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(feignDecoder)
                .logger(new Slf4jLogger(BookClient.class))
                .logLevel(Logger.Level.FULL)
                .target(CustomerClient.class, CUSTOMER_SERVICE_URL);

        return customerClient;
    }


    public ResponseEntity<?> addCustomer(CustomerRequest customerRequest) {
        try {
            ResponseEntity<?> custRespEntity = customerClient.addCustomer(customerRequest);
            if (custRespEntity.getStatusCode().is2xxSuccessful()) {
                // The response entity body contains the book object
                Object customer = custRespEntity.getBody();
                return ResponseEntity.status(201).body(customer);
            } else {
                // Handle the case where the book was not found (status code 404)
//                return ResponseEntity.notFound().build();
            }
        } catch (
                FeignException.BadRequest e) {
            // Handle 400 (Bad Request) error
            return ResponseEntity.badRequest().body("All fields are mandatory.");
        } catch (FeignException e) {
            // Handle other FeignExceptions if needed
            return ResponseEntity.status(e.status()).build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public ResponseEntity<?> getCustomerByID(long id) {
        try {
            ResponseEntity<?> custRespEntity = customerClient.getById(id);
            if (custRespEntity.getStatusCode().is2xxSuccessful()) {
                // The response entity body contains the book object
                Object customer = custRespEntity.getBody();
                Map<String, Object> customerMap = (Map<String, Object>) customer;

                // Remove the specified attributes
                customerMap.remove("address");
                customerMap.remove("address2");
                customerMap.remove("city");
                customerMap.remove("state");
                customerMap.remove("zipcode");
                return ResponseEntity.ok(customerMap);
            } else {
                // Handle the case where the book was not found (status code 404)
//                return ResponseEntity.notFound().build();
            }
        } catch (
                FeignException.BadRequest e) {
            // Handle 400 (Bad Request) error
            return ResponseEntity.badRequest().body("All fields are mandatory.");
        } catch (FeignException e) {
            // Handle other FeignExceptions if needed
            return ResponseEntity.status(e.status()).build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public ResponseEntity<?> getCustomerByUserId(String userId) {
        try {
            ResponseEntity<?> custRespEntity = customerClient.getByUserId(userId);
            if (custRespEntity.getStatusCode().is2xxSuccessful()) {
                // The response entity body contains the book object
                Object customer = custRespEntity.getBody();
                Map<String, Object> customerMap = (Map<String, Object>) customer;

                // Remove the specified attributes
                customerMap.remove("address");
                customerMap.remove("address2");
                customerMap.remove("city");
                customerMap.remove("state");
                customerMap.remove("zipcode");
                return ResponseEntity.ok(customerMap);
            } else {
                // Handle the case where the book was not found (status code 404)
//                return ResponseEntity.notFound().build();
            }
        } catch (
                FeignException.BadRequest e) {
            // Handle 400 (Bad Request) error
            return ResponseEntity.badRequest().body("All fields are mandatory.");
        } catch (FeignException e) {
            // Handle other FeignExceptions if needed
            return ResponseEntity.status(e.status()).build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
