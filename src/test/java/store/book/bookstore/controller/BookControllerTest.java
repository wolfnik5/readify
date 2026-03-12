package store.book.bookstore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import store.book.bookstore.dto.CreateBookRequestDto;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.Category;
import store.book.bookstore.repository.BookRepository;
import store.book.bookstore.repository.CategoryRepository;
import store.book.bookstore.security.JwtUtil;
import store.book.bookstore.util.TestDataHelper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String adminToken;
    private String userToken;
    private Category savedCategory;
    private Book savedBook;

    @BeforeEach
    void setUp() {
        adminToken = jwtUtil.generateToken(TestDataHelper.ADMIN_EMAIL);
        userToken = jwtUtil.generateToken(TestDataHelper.USER_EMAIL);

        hardDeleteAll();

        savedCategory = categoryRepository.save(TestDataHelper.buildCategory());
        savedBook = bookRepository.save(TestDataHelper.buildBook(savedCategory));
    }

    @AfterEach
    void tearDown() {
        hardDeleteAll();
    }

    private void hardDeleteAll() {
        jdbcTemplate.execute("DELETE FROM books_categories");
        jdbcTemplate.execute("DELETE FROM books");
        jdbcTemplate.execute("DELETE FROM categories");
    }

    @Test
    @DisplayName("GET /books/{id}: existing id → 200 with BookDto")
    void getBookById_existingId_returns200() throws Exception {
        mockMvc.perform(get("/books/{id}", savedBook.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBook.getId()))
                .andExpect(jsonPath("$.title").value(TestDataHelper.BOOK_TITLE))
                .andExpect(jsonPath("$.author").value(TestDataHelper.BOOK_AUTHOR));
    }

    @Test
    @DisplayName("GET /books/{id}: non-existing id → 404")
    void getBookById_nonExisting_returns404() throws Exception {
        mockMvc.perform(get("/books/99")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /books/{id}: no token → 401/403")
    void getBookById_noToken_returns401() throws Exception {
        mockMvc.perform(get("/books/{id}", savedBook.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /books/search: returns matching books")
    void searchBooks_returnsMatchingBooks() throws Exception {
        mockMvc.perform(get("/books/search")
                        .param("title", TestDataHelper.BOOK_TITLE)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(TestDataHelper.BOOK_TITLE));
    }

    @Test
    @DisplayName("POST /books: admin → 201 with created book")
    void createBook_admin_returns201() throws Exception {
        CreateBookRequestDto request = TestDataHelper.buildCreateBookRequest(savedCategory.getId());
        request.setIsbn("9780000000001");

        mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(TestDataHelper.BOOK_TITLE))
                .andExpect(jsonPath("$.author").value(TestDataHelper.BOOK_AUTHOR));
    }

    @Test
    @DisplayName("POST /books: user role → 403")
    void createBook_userRole_returns403() throws Exception {
        CreateBookRequestDto request = TestDataHelper.buildCreateBookRequest(savedCategory.getId());

        mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /books: blank title → 400")
    void createBook_blankTitle_returns400() throws Exception {
        CreateBookRequestDto invalid = TestDataHelper.buildCreateBookRequest(savedCategory.getId());
        invalid.setTitle("");

        mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /books/{id}: admin → 200 with updated book")
    void updateBook_admin_returns200() throws Exception {
        CreateBookRequestDto request = TestDataHelper.buildCreateBookRequest(savedCategory.getId());
        request.setTitle("Updated Title");

        mockMvc.perform(put("/books/{id}", savedBook.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("PUT /books/{id}: non-existing id → 404")
    void updateBook_nonExisting_returns404() throws Exception {
        CreateBookRequestDto request = TestDataHelper.buildCreateBookRequest(savedCategory.getId());

        mockMvc.perform(put("/books/99")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /books/{id}: admin → 204")
    void deleteBook_admin_returns204() throws Exception {
        mockMvc.perform(delete("/books/{id}", savedBook.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /books/{id}: user role → 403")
    void deleteBook_userRole_returns403() throws Exception {
        mockMvc.perform(delete("/books/{id}", savedBook.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /books/{id}: non-existing id → 404")
    void deleteBook_nonExisting_returns404() throws Exception {
        mockMvc.perform(delete("/books/99")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}