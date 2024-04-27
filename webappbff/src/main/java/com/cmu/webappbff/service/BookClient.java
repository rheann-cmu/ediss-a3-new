package com.cmu.webappbff.service;

import com.cmu.webappbff.model.Book;
import com.cmu.webappbff.resource.BookResource;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
