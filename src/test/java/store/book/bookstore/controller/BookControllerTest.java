package store.book.bookstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import store.book.bookstore.config.SecurityConfiguration;
import store.book.bookstore.dto.BookDto;
import store.book.bookstore.dto.BookSearchParametersDto;
import store.book.bookstore.dto.CreateBookRequestDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.security.JwtUtil;
import store.book.bookstore.service.BookService;

@WebMvcTest(BookController.class)
@Import(SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private BookDto buildBookDto() {
        BookDto dto = new BookDto();
        dto.setId(1L);
        dto.setTitle("Warriors: Into the Wild");
        dto.setAuthor("Erin Hunter");
        dto.setIsbn("9780329373528");
        dto.setPrice(BigDecimal.valueOf(29.99));
        dto.setCategoryIds(Set.of(1L));
        return dto;
    }

    private CreateBookRequestDto buildCreateRequest() {
        CreateBookRequestDto req = new CreateBookRequestDto();
        req.setTitle("Warriors: Into the Wild");
        req.setAuthor("Erin Hunter");
        req.setIsbn("9780329373528");
        req.setPrice(BigDecimal.valueOf(29.99));
        req.setCategoryIds(Set.of(1L));
        return req;
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /books/{id}: existing id → 200 with BookDto")
    void getBookById_existingId_returns200() throws Exception {
        BookDto dto = buildBookDto();
        when(bookService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Warriors: Into the Wild"))
                .andExpect(jsonPath("$.author").value("Erin Hunter"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /books/{id}: non-existing id → 404")
    void getBookById_nonExistingId_returns404() throws Exception {
        when(bookService.findById(99L))
                .thenThrow(new EntityNotFoundException("Cannot find Book by id: 99"));

        mockMvc.perform(get("/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /books/{id}: unauthenticated → 401/403")
    void getBookById_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /books/search: returns paginated books")
    void getAllBooks_returnsPaginatedBooks() throws Exception {
        BookDto dto = buildBookDto();
        when(bookService.search(any(BookSearchParametersDto.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/books/search"))  // ← /search
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Warriors: Into the Wild"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /books: valid request → 201 with created BookDto")
    void createBook_validRequest_returns201() throws Exception {
        CreateBookRequestDto request = buildCreateRequest();
        BookDto created = buildBookDto();
        when(bookService.save(any(CreateBookRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Warriors: Into the Wild"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /books: USER role → 403")
    void createBook_userRole_returns403() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /books: blank title → 400")
    void createBook_blankTitle_returns400() throws Exception {
        CreateBookRequestDto invalid = buildCreateRequest();
        invalid.setTitle("");

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /books/{id}: valid request → 200 with updated BookDto")
    void updateBook_validRequest_returns200() throws Exception {
        CreateBookRequestDto request = buildCreateRequest();
        BookDto updated = buildBookDto();
        updated.setTitle("Warriors: Into the Wild Updated");
        when(bookService.update(eq(1L), any(CreateBookRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Warriors: Into the Wild Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /books/{id}: non-existing id → 404")
    void updateBook_nonExistingId_returns404() throws Exception {
        when(bookService.update(eq(99L), any(CreateBookRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Can't find book by id: 99"));

        mockMvc.perform(put("/books/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /books/{id}: existing id → 204")
    void deleteBook_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /books/{id}: non-existing id → 404")
    void deleteBook_nonExistingId_returns404() throws Exception {
        doThrow(new EntityNotFoundException("Can't find book by id: 99"))
                .when(bookService).deleteById(99L);

        mockMvc.perform(delete("/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /books/{id}: USER role → 403")
    void deleteBook_userRole_returns403() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isForbidden());
    }
}