package com.cmu.customerwebservice.service;

import com.cmu.customerwebservice.entities.Customer;
import com.cmu.customerwebservice.entities.CustomerRequest;
import com.cmu.customerwebservice.entities.UserMessage;
import com.cmu.customerwebservice.respositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Customer Service
 *
 * @author Rheann Sequeira
 */
@Service
public class CustomerService {
    private static final String STATE_REGEX = "^(?i:AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|TX|UT|VT|VI|VA|WA|WV|WI|WY)$";

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;


    public ResponseEntity<?> addCustomer(@RequestBody CustomerRequest customerRequest) {
        if (isAnyFieldBlank(customerRequest)) {
            System.out.println("Some field is invalid for add customer request: " + customerRequest.toString());
            return ResponseEntity.badRequest().body(new ErrorResponse("All fields are mandatory."));
        }

        if (!isValidState(customerRequest.getState()) || !isValidEmail(customerRequest.getUserId())) {
            System.out.println("State or Email field is invalid for add customer request: " + customerRequest.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Illegal, missing, or malformed input");
        }

        Optional<Customer> existingCustomer = customerRepository.findByUserId(customerRequest.getUserId());
        if (existingCustomer.isPresent()) {
            System.out.println("User id already exists 422 for add customer request:" + customerRequest.toString());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("This user ID already exists in the system.");
        }

        Customer customer = new Customer();
        customer.setUserId(customerRequest.getUserId());
        customer.setName(customerRequest.getName());
        customer.setPhone(customerRequest.getPhone());
        customer.setAddress(customerRequest.getAddress());
        customer.setAddress2(customerRequest.getAddress2());
        customer.setCity(customerRequest.getCity());
        customer.setState(customerRequest.getState());
        customer.setZipcode(customerRequest.getZipcode());

        Customer savedCustomer = customerRepository.save(customer);

        //if customer is saved, we call the KafkaProducer
        if (savedCustomer != null) {
            UserMessage message = new UserMessage(savedCustomer.getUserId(), savedCustomer.getName());
            kafkaProducerService.produce(message);
        }

        System.out.println("Saved customer: " + savedCustomer.toString());
        try {
            return ResponseEntity.created(new URI("/customers/" + savedCustomer.getId()))
                    .body(savedCustomer);
        } catch (URISyntaxException e) {
            System.out.println("Internal server error for the request:" + customerRequest.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        System.out.println("Customer found: " + customer.toString());
        return customer.map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> getCustomerByUserId(@RequestParam String userId) {
        if (!isValidEmail(userId)) {
            System.out.println("Invalid find customer by user id request: " + userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email id");
        }
        Optional<Customer> customer = customerRepository.findByUserId(userId.trim());
        System.out.println("Customer found: " + customer.toString());
        return customer.map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private boolean isValidEmail(String email) {

        return email != null && email.matches(EMAIL_REGEX);
    }

    private boolean isValidState(String state) {

        return state != null && state.matches(STATE_REGEX);
    }

    /**
     * @param request
     * @return whether or any field is blank
     */
    private boolean isAnyFieldBlank(CustomerRequest request) {
        return request.getAddress() == null || "null".equals(request.getAddress()) ||
                request.getCity() == null || "null".equals(request.getCity()) ||
                request.getName() == null || "null".equals(request.getName()) ||
                request.getPhone() == null || "null".equals(request.getPhone()) ||
                request.getState() == null || "null".equals(request.getState()) ||
                request.getUserId() == null || "null".equals(request.getUserId()) ||
                request.getZipcode() == null || "null".equals(request.getZipcode());
    }

    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
