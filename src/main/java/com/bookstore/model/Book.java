package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @Column(name = "isbn", nullable = false, unique = true, length = 20)
    private String isbn;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Year is required")
    @Column(name = "\"year\"", nullable = false)
    private Integer year;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false)
    private Double price;

    @NotBlank(message = "Genre is required")
    @Column(nullable = false)
    private String genre;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Author> authors = new ArrayList<>();

    public void setAuthors(List<Author> authors) {
        this.authors.clear();
        if (authors != null) {
            authors.forEach(a -> a.setBook(this));
            this.authors.addAll(authors);
        }
    }

    // Business method to add author
    public void addAuthor(Author author) {
        author.setBook(this);
        authors.add(author);
    }

}



