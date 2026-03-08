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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import store.book.bookstore.config.SecurityConfiguration;
import store.book.bookstore.dto.BookDtoWithoutCategoryIds;
import store.book.bookstore.dto.CategoryDto;
import store.book.bookstore.dto.CategoryRequestDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.security.JwtAuthenticationFilter;
import store.book.bookstore.security.JwtUtil;
import store.book.bookstore.service.BookService;
import store.book.bookstore.service.CategoryService;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfiguration.class)
@EnableMethodSecurity
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private CategoryDto buildDto() {
        return new CategoryDto(1L, "Fantasy novel",
                "Fiction featuring magical or supernatural elements");
    }

    private CategoryRequestDto buildRequest() {
        return new CategoryRequestDto("Fantasy novel",
                "Fiction featuring magical or supernatural elements");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /categories: returns paginated CategoryDto")
    void getAll_returnsPaginatedDto() throws Exception {
        CategoryDto dto = buildDto();
        when(categoryService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Fantasy novel"));
    }

    @Test
    @DisplayName("GET /categories: unauthenticated → 401/403")
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /categories/{id}: existing id → 200 with CategoryDto")
    void getCategoryById_existingId_returns200() throws Exception {
        CategoryDto dto = buildDto();
        when(categoryService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fantasy novel"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /categories/{id}: non-existing id → 404")
    void getCategoryById_nonExistingId_returns404() throws Exception {
        when(categoryService.getById(99L))
                .thenThrow(new EntityNotFoundException("Category not found with id: 99"));

        mockMvc.perform(get("/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /categories/{id}/books: returns books for category")
    void getBooksByCategory_returnsBooksPage() throws Exception {
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(1L);
        bookDto.setTitle("Warriors: Into the Wild Updated");
        when(bookService.findAllByCategoryId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(bookDto)));

        mockMvc.perform(get("/categories/1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Warriors: Into the Wild Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /categories: valid request → 201 with created CategoryDto")
    void createCategory_validRequest_returns201() throws Exception {
        CategoryRequestDto request = buildRequest();
        CategoryDto created = buildDto();
        when(categoryService.save(any(CategoryRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fantasy novel"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /categories: USER role → 403")
    void createCategory_userRole_returns403() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /categories: blank name → 400")
    void createCategory_blankName_returns400() throws Exception {
        CategoryRequestDto invalid = new CategoryRequestDto("", "desc");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /categories/{id}: valid request → 200 with updated dto")
    void updateCategory_validRequest_returns200() throws Exception {
        CategoryRequestDto request = new CategoryRequestDto("Updated", "Updated desc");
        CategoryDto updated = new CategoryDto(1L, "Updated", "Updated desc");
        when(categoryService.update(eq(1L), any(CategoryRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /categories/{id}: non-existing id → 404")
    void updateCategory_nonExistingId_returns404() throws Exception {
        when(categoryService.update(eq(99L), any(CategoryRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Category not found with id: 99"));

        mockMvc.perform(put("/categories/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /categories/{id}: existing id → 204")
    void deleteCategory_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /categories/{id}: non-existing id → 404")
    void deleteCategory_nonExistingId_returns404() throws Exception {
        doThrow(new EntityNotFoundException("Category not found with id: 99"))
                .when(categoryService).deleteById(99L);

        mockMvc.perform(delete("/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /categories/{id}: USER role → 403")
    void deleteCategory_userRole_returns403() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isForbidden());
    }
}