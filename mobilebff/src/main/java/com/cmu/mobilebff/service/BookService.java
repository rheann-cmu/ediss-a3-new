package com.cmu.mobilebff.service;

import com.cmu.mobilebff.model.Book;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.codec.Decoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class BookService {
    private static final String BOOK_SERVICE_URL = "http://book-service.bookstore-ns.svc.cluster.local:3000/books";
//    private static final String BOOK_SERVICE_URL = "http://internal-InternalALB-1285315458.us-east-1.elb.amazonaws.com:3000/books";
//    private static final String BOOK_SERVICE_URL = "http://localhost:3000/books";

    //    private static BookClient bookClient ;
    private static BookClient bookClient = createBookClient();

    private static BookClient createBookClient() {
        ObjectFactory<HttpMessageConverters> messageConverters = () -> new HttpMessageConverters();
        Decoder feignDecoder = new ResponseEntityDecoder(new SpringDecoder(messageConverters));
        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(new CustomFieldNamingStrategy()) // Set the custom field naming strategy here
                .create();

        BookClient bookClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder(gson))
                .decoder(feignDecoder)
                .logger(new Slf4jLogger(BookClient.class))
                .logLevel(Logger.Level.FULL)
                .target(BookClient.class, BOOK_SERVICE_URL);

        return bookClient;
    }

    public ResponseEntity<?> findBookByIsbn(String ISBN) {
        try {
            ResponseEntity<?> bookResponseEntity = bookClient.findByIsbn(ISBN);
            if (bookResponseEntity.getStatusCode().is2xxSuccessful()) {
                // The response entity body contains the book object
                Object book = bookResponseEntity.getBody();
                replaceNonFiction(book);
                return ResponseEntity.ok(book);
            } else {
                // Handle the case where the book was not found (status code 404)
//                return ResponseEntity.notFound().build();
            }
        } catch (FeignException.BadRequest e) {
            // Handle 400 (Bad Request) error
            return ResponseEntity.badRequest().body("All fields are mandatory.");
        } catch (FeignException e) {
            // Handle other FeignExceptions if needed
            return ResponseEntity.status(e.status()).build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    //remove the "non-fiction" from the response
    public void replaceNonFiction(Object book) {
        if (book instanceof Map) {
            Map<String, Object> bookMap = (Map<String, Object>) book;
            for (Map.Entry<String, Object> entry : bookMap.entrySet()) {
                if (entry.getValue() instanceof String) {
                    String value = (String) entry.getValue();
                    if (value.contains("non-fiction")) {
                        entry.setValue(Integer.parseInt(value.replace("non-fiction", "3")));
                    }
                } else if (entry.getValue() instanceof Map) {
                    replaceNonFiction(entry.getValue());
                }
            }
        }
    }

    public ResponseEntity<?> addBook(Book book) {
        try {
            ResponseEntity<?> bookResponseEntity = bookClient.addBook(book);

            if (bookResponseEntity.getStatusCode().is2xxSuccessful()) {
                // The response entity body contains the book object
                Object addedBook = bookResponseEntity.getBody();
                return ResponseEntity.status(201).body(addedBook);
            } else {
                // Handle other status codes if needed
            }
        } catch (FeignException.BadRequest e) {
            // Handle 400 (Bad Request) error
            return ResponseEntity.badRequest().body("All fields are mandatory.");
        } catch (FeignException e) {
            // Handle other FeignExceptions if needed
            return ResponseEntity.status(e.status()).build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public ResponseEntity<?> updateBook(String isbn, Book book) {
        try {
            ResponseEntity<?> bookResponseEntity = bookClient.updateBook(isbn, book);

            if (bookResponseEntity.getStatusCode().is2xxSuccessful()) {
                // The response entity body contains the book object
                Object updatedBook = bookResponseEntity.getBody();
                return ResponseEntity.ok(updatedBook);
            } else {
                // Handle other status codes if needed
            }
        } catch (FeignException.BadRequest e) {
            // Handle 400 (Bad Request) error
            return ResponseEntity.badRequest().body("All fields are mandatory.");
        } catch (FeignException e) {
            // Handle other FeignExceptions if needed
            return ResponseEntity.status(e.status()).build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /*public ResponseEntity<?> addBook(Book book){
        ResponseEntity<?> bookResponseEntity = bookClient.addBook(book);
//        return  bookResponseEntity;
        if (bookResponseEntity.getStatusCode().is2xxSuccessful()) {
            // The response entity body contains the book object
            Object addedBook = bookResponseEntity.getBody();
            return ResponseEntity.ok(addedBook);
        } else {
            HttpStatusCode httpStatusCode = bookResponseEntity.getStatusCode();

            return ResponseEntity.status(httpStatusCode).build();
        }
    }*/

}
