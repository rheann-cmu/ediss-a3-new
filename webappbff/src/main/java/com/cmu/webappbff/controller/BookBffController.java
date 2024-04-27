package com.cmu.webappbff.controller;

import com.cmu.webappbff.JWTHelper;
import com.cmu.webappbff.model.Book;
import com.cmu.webappbff.service.BookService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/books")
public class BookBffController {
  private static final Logger logger = LoggerFactory.getLogger(BookBffController.class);
  BookService bookService = new BookService();

  @GetMapping({"/isbn/{ISBN}", "/{ISBN}"})
  public ResponseEntity<?> getBookByISBN(
      @PathVariable String ISBN,
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestHeader("X-Client-Type") String xClientType) {
    logger.info("Request to get book by ISBN received: "+ISBN+" xclienttype: "+xClientType);
    if (StringUtils.isEmpty(xClientType) || !StringUtils.equalsIgnoreCase(xClientType, "Web")) {
      logger.info("xclient type is missing or incorrect " + xClientType);
      return ResponseEntity.status(401).body("xClientType missing or incorrect");
    }
    // Extract the JWT token from the header and verify it
    if (StringUtils.isEmpty(token) || !JWTHelper.verifyJWTToken(token)) {
      logger.error("Empty or Invalid JWT token");
      return ResponseEntity.status(401).body("Invalid JWT Token");
    }

    ResponseEntity<?> book = bookService.findBookByIsbn(ISBN);
    logger.info("Bookservice response for isbn: "+ISBN+ " "+book.toString());

    return book;
  }

  @PostMapping
  public ResponseEntity<?> addBook(
      @RequestBody Book book,
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestHeader("X-Client-Type") String xClientType) {

    logger.info("Request to add book received: "+book.toString());
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

    ResponseEntity<?> addedBook = bookService.addBook(book);
    logger.info("addBook service response "+addedBook.toString());
    return addedBook;
  }

  @PutMapping("/{ISBN}")
  public ResponseEntity<?> updateBook(
      @PathVariable String ISBN,
      @RequestBody Book updatedBook,
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestHeader("X-Client-Type") String xClientType) {

      logger.info("Request to update book received: "+updatedBook.toString());
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

    ResponseEntity<?> updatedBook1 = bookService.updateBook(ISBN, updatedBook);
    logger.info("Update book bookservice response "+updatedBook1.toString());
    return updatedBook1;
  }
}
