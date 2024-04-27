package com.cmu.webappbff.resource;

import com.cmu.webappbff.model.Book;

public class BookResource {

    private Book book;

    public BookResource(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
