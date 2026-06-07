package com.bookstore.dto;

import com.bookstore.model.Book;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {

    private String isbn;
    private String title;
    private Integer year;
    private Double price;
    private String genre;
    private List<AuthorDto> authors;

    public static BookResponse from(Book book) {
        List<AuthorDto> authorDtos = book.getAuthors().stream()
                .map(a -> AuthorDto.builder()
                        .name(a.getName())
                        .birthday(a.getBirthday())
                        .build())
                .toList();

        return BookResponse.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .year(book.getYear())
                .price(book.getPrice())
                .genre(book.getGenre())
                .authors(authorDtos)
                .build();
    }
}
