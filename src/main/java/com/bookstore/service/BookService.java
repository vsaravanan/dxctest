package com.bookstore.service;

import com.bookstore.dto.BookRequest;
import com.bookstore.dto.BookResponse;

import java.util.List;

public interface BookService {
    BookResponse addBook(BookRequest request);
    BookResponse updateBook(String isbn, BookRequest request);
    List<BookResponse> findBooks(String title, String authorName);
    void deleteBook(String isbn);
}
