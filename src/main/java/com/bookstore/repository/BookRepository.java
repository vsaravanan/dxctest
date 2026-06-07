package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    // Find by exact title
    List<Book> findByTitleIgnoreCase(String title);

    // Find by exact author name
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) = LOWER(:authorName)")
    List<Book> findByAuthorName(@Param("authorName") String authorName);

    // Explicitly load authors when needed
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.isbn = :isbn")
    Optional<Book> findByIsbnWithAuthors(@Param("isbn") String isbn);

    // Or use EntityGraph
    @EntityGraph(attributePaths = "authors")
    Optional<Book> findByIsbn(String isbn);

    // Find by both title AND author name
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a " +
           "WHERE LOWER(b.title) = LOWER(:title) AND LOWER(a.name) = LOWER(:authorName)")
    List<Book> findByTitleAndAuthorName(@Param("title") String title,
                                        @Param("authorName") String authorName);
}
