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
import store.book.bookstore.dto.CategoryRequestDto;
import store.book.bookstore.model.Category;
import store.book.bookstore.repository.BookRepository;
import store.book.bookstore.repository.CategoryRepository;
import store.book.bookstore.security.JwtUtil;
import store.book.bookstore.util.TestDataHelper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String adminToken;
    private String userToken;
    private Category savedCategory;

    @BeforeEach
    void setUp() {
        adminToken = jwtUtil.generateToken(TestDataHelper.ADMIN_EMAIL);
        userToken = jwtUtil.generateToken(TestDataHelper.USER_EMAIL);

        hardDeleteAll();

        savedCategory = categoryRepository.save(TestDataHelper.buildCategory());
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
    @DisplayName("GET /categories: user → 200 with paginated categories")
    void getAll_user_returns200() throws Exception {
        mockMvc.perform(get("/categories")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name")
                        .value(TestDataHelper.CATEGORY_NAME));
    }

    @Test
    @DisplayName("GET /categories: no token → 401/403")
    void getAll_noToken_returns401() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /categories/{id}: existing id → 200")
    void getById_existingId_returns200() throws Exception {
        mockMvc.perform(get("/categories/{id}", savedCategory.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCategory.getId()))
                .andExpect(jsonPath("$.name").value(TestDataHelper.CATEGORY_NAME));
    }

    @Test
    @DisplayName("GET /categories/{id}: non-existing id → 404")
    void getById_nonExisting_returns404() throws Exception {
        mockMvc.perform(get("/categories/99")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /categories/{id}/books: returns books page")
    void getBooksByCategory_returnsPage() throws Exception {
        mockMvc.perform(get("/categories/{id}/books", savedCategory.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("POST /categories: admin → 201 with created category")
    void create_admin_returns201() throws Exception {
        CategoryRequestDto request = new CategoryRequestDto("Science Fiction",
                "Sci-fi books");

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Science Fiction"));
    }

    @Test
    @DisplayName("POST /categories: user role → 403")
    void create_userRole_returns403() throws Exception {
        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataHelper.buildCategoryRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /categories: blank name → 400")
    void create_blankName_returns400() throws Exception {
        CategoryRequestDto invalid = new CategoryRequestDto("", "desc");

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /categories/{id}: admin → 200 with updated category")
    void update_admin_returns200() throws Exception {
        CategoryRequestDto request = new CategoryRequestDto("Updated Name",
                "Updated desc");

        mockMvc.perform(put("/categories/{id}", savedCategory.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("PUT /categories/{id}: non-existing id → 404")
    void update_nonExisting_returns404() throws Exception {
        mockMvc.perform(put("/categories/99")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataHelper.buildCategoryRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /categories/{id}: admin → 204")
    void delete_admin_returns204() throws Exception {
        mockMvc.perform(delete("/categories/{id}", savedCategory.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /categories/{id}: user role → 403")
    void delete_userRole_returns403() throws Exception {
        mockMvc.perform(delete("/categories/{id}", savedCategory.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /categories/{id}: non-existing id → 404")
    void delete_nonExisting_returns404() throws Exception {
        mockMvc.perform(delete("/categories/99")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
