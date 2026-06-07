package com.bookstore.service;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            log.info("Database already seeded. Skipping.");
            return;
        }

        Book book1 = Book.builder()
                .isbn("978-0-13-468599-1")
                .title("Effective Java")
                .year(2018)
                .price(45.99)
                .genre("Programming")
                .build();

        Author author1 = Author.builder()
                .name("Joshua Bloch")
                .birthday(LocalDate.of(1961, 8, 28))
                .book(book1)
                .build();
        book1.getAuthors().add(author1);

        Book book2 = Book.builder()
                .isbn("978-0-20-135394-2")
                .title("Design Patterns")
                .year(1994)
                .price(54.99)
                .genre("Software Engineering")
                .build();

        List<Author> gangOfFour = List.of(
            Author.builder().name("Erich Gamma").birthday(LocalDate.of(1961, 3, 13)).book(book2).build(),
            Author.builder().name("Richard Helm").birthday(LocalDate.of(1960, 1, 1)).book(book2).build(),
            Author.builder().name("Ralph Johnson").birthday(LocalDate.of(1955, 1, 1)).book(book2).build(),
            Author.builder().name("John Vlissides").birthday(LocalDate.of(1961, 8, 2)).book(book2).build()
        );
        book2.getAuthors().addAll(gangOfFour);

        bookRepository.saveAll(List.of(book1, book2));
        log.info("Seeded {} sample books", 2);
    }
}
