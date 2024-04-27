package com.cmu.webappbff.controller;

import com.cmu.webappbff.JWTHelper;
import com.cmu.webappbff.model.CustomerRequest;
import com.cmu.webappbff.service.CustomerService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/customers")
public class CustomerBffController {

  private static final Logger logger = LoggerFactory.getLogger(CustomerBffController.class);
  CustomerService customerService = new CustomerService();

  @PostMapping
  public ResponseEntity<?> addCustomer(
      @RequestBody CustomerRequest customerRequest,
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestHeader("X-Client-Type") String xClientType) {

    logger.info("Requets to add customer received: "+customerRequest.toString());
    logger.info("xclient type: "+xClientType);
    if (StringUtils.isEmpty(xClientType) || !StringUtils.equalsIgnoreCase(xClientType, "Web")) {
      logger.info("xclient type is missing or incorrect " + xClientType);
      return ResponseEntity.status(401).body("xClientType missing or incorrect");
    }
    // Extract the JWT token from the header and verify it
    if (StringUtils.isEmpty(token) || !JWTHelper.verifyJWTToken(token)) {
      logger.error("Empty or Invalid JWT token");
      return ResponseEntity.status(401).body("Invalid JWT Token");
    }

    ResponseEntity<?> addedCustomer = customerService.addCustomer(customerRequest);
    logger.info("customer service response "+addedCustomer.toString());
    return addedCustomer;
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCustomerById(
      @PathVariable Long id,
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestHeader("X-Client-Type") String xClientType) {

    logger.info("Request to retrieve customer by id received for id: "+id);
    logger.info("xclient type: "+xClientType);
    if (StringUtils.isEmpty(xClientType) || !StringUtils.equalsIgnoreCase(xClientType, "Web")) {
      logger.info("xclient type is missing or incorrect " + xClientType);
      return ResponseEntity.status(401).body("xClientType missing or incorrect");
    }
    // Extract the JWT token from the header and verify it
    if (StringUtils.isEmpty(token) || !JWTHelper.verifyJWTToken(token)) {
      logger.error("Empty or Invalid JWT token");
      return ResponseEntity.status(401).body("Invalid JWT Token");
    }
    ResponseEntity<?> retreivedCustomer = customerService.getCustomerByID(id);
    logger.info("customer retrieval response "+retreivedCustomer.toString());
    return retreivedCustomer;
  }

  @GetMapping
  public ResponseEntity<?> getCustomerByUserId(
      @RequestParam String userId,
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestHeader("X-Client-Type") String xClientType) {
    logger.info("Request to get customer by userId received for userId: "+userId);
    logger.info("xclient type: "+xClientType);

    if (StringUtils.isEmpty(xClientType) || !StringUtils.equalsIgnoreCase(xClientType, "Web")) {
      logger.info("xclient type is missing or incorrect " + xClientType);
      return ResponseEntity.status(401).body("xClientType missing or incorrect");
    }
    // Extract the JWT token from the header and verify it
    if (StringUtils.isEmpty(token) || !JWTHelper.verifyJWTToken(token)) {
      logger.error("Empty or Invalid JWT token");
      return ResponseEntity.status(401).body("Invalid JWT Token");
    }
    ResponseEntity<?> retreivedCustomer = customerService.getCustomerByUserId(userId);
    logger.info("customer retrieval by userid response "+retreivedCustomer.toString());
    return retreivedCustomer;
  }
}
