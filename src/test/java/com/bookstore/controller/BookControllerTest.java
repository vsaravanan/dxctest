package com.bookstore.controller;

/**
 * @author Sarav on 07 Jun 2026
 * @project govtech
 * @package com.bookstore.controller
 * @class BookControllerTest
 */


import com.bookstore.config.SecurityConfig;
import com.bookstore.dto.AuthorDto;
import com.bookstore.dto.BookRequest;
import com.bookstore.dto.BookResponse;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateIsbnException;
import com.bookstore.exception.GlobalExceptionHandler;
import com.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


    /*
            @ExtendWith(MockitoExtension.class)
            class BookServiceTest {

                @Mock
                private BookRepository bookRepository;

                @InjectMocks
                private BookService bookService;

                private BookRequest sampleRequest;
     */



//@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
//@Import(SecurityConfig.class)   // Import your security config
@WebMvcTest(BookController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private BookRequest sampleRequest;
    private BookResponse sampleResponse;

    @Test
    void deleteBook_withNonExistentIsbn_returns404WithErrorDetails() throws Exception {
        String fakeIsbn = "999-FAKE";
        String errorMessage = "Book not found with ISBN: " + fakeIsbn;

        doThrow(new BookNotFoundException(fakeIsbn))
                .when(bookService).deleteBook(fakeIsbn);

        mockMvc.perform(delete("/api/v1/books/" + fakeIsbn)
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists());


    }
    @BeforeEach
    void setUp() {
        AuthorDto author = AuthorDto.builder()
                .name("Joshua Bloch")
                .birthday(LocalDate.of(1961, 8, 28))
                .build();

        sampleRequest = BookRequest.builder()
                .isbn("978-0-13-468599-1")
                .title("Effective Java")
                .year(2018)
                .price(45.99)
                .genre("Programming")
                .authors(List.of(author))
                .build();

        sampleResponse = BookResponse.builder()
                .isbn("978-0-13-468599-1")
                .title("Effective Java")
                .year(2018)
                .price(45.99)
                .genre("Programming")
                .authors(List.of(author))
                .build();
    }

    @Test
    void addBook_asUser_returns201() throws Exception {
        when(bookService.addBook(any(BookRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest))
                        .with(httpBasic("user", "password")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("978-0-13-468599-1"))
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }



    @Test
    void addBook_whenIsbnAlreadyExists_handlesExceptionWithGlobalHandler() throws Exception {
        when(bookService.addBook(any(BookRequest.class)))
                .thenThrow(new DuplicateIsbnException("978-0-13-468599-1"));

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest))
                        .with(httpBasic("user", "password")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Conflict")));
    }

    @Test
    void addBook_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest))
                .with(httpBasic("user", "blockit")))
                .andExpect(status().isUnauthorized());
    }

    // ── PUT /api/v1/books/{isbn} ────────────────────────────────


    @Test
//    @WithMockUser(roles = "USER")
    void updateBook_found_returns200() throws Exception {
        when(bookService.updateBook(eq("978-0-13-468599-1"), any())).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/books/978-0-13-468599-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest))
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("978-0-13-468599-1"));
    }

    @Test
//    @WithMockUser(roles = "USER")
    void updateBook_notFound_returns404() throws Exception {
        when(bookService.updateBook(any(), any())).thenThrow(new BookNotFoundException("999"));

        mockMvc.perform(put("/api/v1/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest))
                        .with(httpBasic("user", "password")))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/v1/books ───────────────────────────────────────

    @Test
    void findBooks_noParams_returnsAll() throws Exception {
        when(bookService.findBooks(null, null)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/books")
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ── DELETE /api/v1/books/{isbn} ─────────────────────────────


    @Test
    @WithMockUser(roles = "ADMIN") // This is not working with @Import(SecurityConfig.class).
//    @Import(SecurityConfig.class) is required to apply security context.
//            .with(httpBasic("admin", "admin123"))) is working with @Import(SecurityConfig.class)

    void deleteBook_asAdmin_returns204() throws Exception {
//        doNothing().when(bookService).deleteBook(any(String.class));

        mockMvc.perform(delete("/api/v1/books/978-0-13-468599-1")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/books/978-0-13-468599-1")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBook_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete("/api/v1/books/978-0-13-468599-1"))
                .andExpect(status().isUnauthorized());
    }


}


//@ExtendWith(MockitoExtension.class)
//class BookServiceDeleteTest {
//
//    @Mock
//    private BookRepository bookRepository;
//
//    @InjectMocks
//    private BookService bookService;