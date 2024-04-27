package com.cmu.mobilebff.service;

import com.cmu.mobilebff.model.Book;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.http.ResponseEntity;

public interface BookClient {

    @RequestLine("GET /{isbn}")
    @Headers("Content-Type: application/json")
    ResponseEntity<?> findByIsbn(@Param("isbn") String isbn);

    @RequestLine("PUT /{isbn}")
    @Headers("Content-Type: application/json")
    ResponseEntity<?> updateBook(@Param("isbn") String isbn, Book book);

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    ResponseEntity<?> addBook(Book book);
}
