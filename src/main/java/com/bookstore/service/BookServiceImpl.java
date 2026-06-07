package com.bookstore.service;

import com.bookstore.dto.BookRequest;
import com.bookstore.dto.BookResponse;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateIsbnException;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookResponse addBook(BookRequest request) {
        if (bookRepository.existsById(request.getIsbn())) {
            throw new DuplicateIsbnException(request.getIsbn());
        }
        Book book = mapToEntity(request);
        return BookResponse.from(bookRepository.save(book));
    }

    @Override
    public BookResponse updateBook(String isbn, BookRequest request) {
        Book existing = getBookWithAuthors(request.getIsbn());

        existing.setTitle(request.getTitle());
        existing.setYear(request.getYear());
        existing.setPrice(request.getPrice());
        existing.setGenre(request.getGenre());

        List<Author> updatedAuthors = request.getAuthors().stream()
                .map(dto -> Author.builder()
                        .name(dto.getName())
                        .birthday(dto.getBirthday())
                        .book(existing)
                        .build())
                .toList();
        existing.setAuthors(updatedAuthors);

        return BookResponse.from(bookRepository.save(existing));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findBooks(String title, String authorName) {
        List<Book> books;

        if (title != null && authorName != null) {
            books = bookRepository.findByTitleAndAuthorName(title, authorName);
        } else if (title != null) {
            books = bookRepository.findByTitleIgnoreCase(title);
        } else if (authorName != null) {
            books = bookRepository.findByAuthorName(authorName);
        } else {
            books = bookRepository.findAll();
        }

        return books.stream().map(BookResponse::from).toList();
    }

    @Override
    public void deleteBook(String isbn) {
        if (!bookRepository.existsById(isbn)) {
            throw new BookNotFoundException(isbn);
        }
        bookRepository.deleteById(isbn);
    }

    private Book mapToEntity(BookRequest request) {
        Book book = Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .year(request.getYear())
                .price(request.getPrice())
                .genre(request.getGenre())
                .build();

        List<Author> authors = request.getAuthors().stream()
                .map(dto -> Author.builder()
                        .name(dto.getName())
                        .birthday(dto.getBirthday())
                        .book(book)
                        .build())
                .toList();
        book.getAuthors().addAll(authors);

        return book;
    }

    public Book getBookWithAuthors(String isbn) {
        // Inside transaction, Lazy loading works
        Book existing = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        existing.getAuthors().size(); // Triggers lazy loading
        return existing;
    }

}
