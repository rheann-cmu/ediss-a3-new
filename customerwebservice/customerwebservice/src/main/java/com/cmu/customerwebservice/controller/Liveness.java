package com.cmu.customerwebservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Liveness {

    @GetMapping({"/status"})
    public ResponseEntity<?> liveness() {
        return ResponseEntity.ok("OK");
    }
}
