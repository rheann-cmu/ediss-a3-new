package com.cmu.webappbff.service;

import com.cmu.webappbff.model.CustomerRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.http.ResponseEntity;

public interface CustomerClient {

    @RequestLine("GET /{id}")
    @Headers("Content-Type: application/json")
    ResponseEntity<?> getById(@Param("id") long id);

    @RequestLine("GET ?userId={userId}")
    ResponseEntity<?> getByUserId(@Param("userId") String userId);

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    ResponseEntity<?> addCustomer(CustomerRequest customer);
}
