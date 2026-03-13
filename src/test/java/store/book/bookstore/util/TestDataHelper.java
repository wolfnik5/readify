package store.book.bookstore.util;

import java.math.BigDecimal;
import java.util.Set;
import store.book.bookstore.dto.BookDto;
import store.book.bookstore.dto.BookDtoWithoutCategoryIds;
import store.book.bookstore.dto.CategoryDto;
import store.book.bookstore.dto.CategoryRequestDto;
import store.book.bookstore.dto.CreateBookRequestDto;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.Category;

public class TestDataHelper {

    public static final String BOOK_TITLE = "Warriors: Into the Wild";
    public static final String BOOK_AUTHOR = "Erin Hunter";
    public static final String BOOK_ISBN = "9780329373528";
    public static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(29.99);

    public static final String BOOK_2_TITLE = "Effective Java";
    public static final String BOOK_2_AUTHOR = "Joshua Bloch";
    public static final String BOOK_2_ISBN = "9780134685991";
    public static final BigDecimal BOOK_2_PRICE = BigDecimal.valueOf(39.99);

    public static final String CATEGORY_NAME = "Fantasy novel";
    public static final String CATEGORY_DESCRIPTION =
            "Fiction featuring magical or supernatural elements";

    public static final String CATEGORY_2_NAME = "Science Fiction";
    public static final String CATEGORY_2_DESCRIPTION =
            "Sci-fi books";

    public static final String CATEGORY_3_NAME = "Horror";
    public static final String CATEGORY_3_DESCRIPTION =
            "Horror books";

    public static final String ADMIN_EMAIL = "admin@bookstore.com";
    public static final String ADMIN_PASSWORD = "admin1234";
    public static final String USER_EMAIL = "user@bookstore.com";
    public static final String USER_PASSWORD = "user1234";

    public static Category buildCategory() {
        Category category = new Category();
        category.setName(CATEGORY_NAME);
        category.setDescription(CATEGORY_DESCRIPTION);
        return category;
    }

    public static Category buildCategory(Long id) {
        Category category = buildCategory();
        category.setId(id);
        return category;
    }

    public static Category buildCategory2() {
        Category category = new Category();
        category.setName(CATEGORY_2_NAME);
        category.setDescription(CATEGORY_2_DESCRIPTION);
        return category;
    }

    public static Category buildCategory3() {
        Category category = new Category();
        category.setName(CATEGORY_3_NAME);
        category.setDescription(CATEGORY_3_DESCRIPTION);
        return category;
    }

    public static Book buildBook(Category category) {
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(BOOK_AUTHOR);
        book.setIsbn(BOOK_ISBN);
        book.setPrice(BOOK_PRICE);
        book.setCategories(Set.of(category));
        return book;
    }

    public static Book buildBook(Long id, Category category) {
        Book book = buildBook(category);
        book.setId(id);
        return book;
    }

    public static Book buildBook2(Category category) {
        Book book = new Book();
        book.setTitle(BOOK_2_TITLE);
        book.setAuthor(BOOK_2_AUTHOR);
        book.setIsbn(BOOK_2_ISBN);
        book.setPrice(BOOK_2_PRICE);
        book.setCategories(Set.of(category));
        return book;
    }

    public static CreateBookRequestDto buildCreateBookRequest(Long categoryId) {
        CreateBookRequestDto req = new CreateBookRequestDto();
        req.setTitle(BOOK_TITLE);
        req.setAuthor(BOOK_AUTHOR);
        req.setIsbn(BOOK_ISBN);
        req.setPrice(BOOK_PRICE);
        req.setCategoryIds(Set.of(categoryId));
        return req;
    }

    public static BookDto buildBookDto(Long id, Long categoryId) {
        BookDto dto = new BookDto();
        dto.setId(id);
        dto.setTitle(BOOK_TITLE);
        dto.setAuthor(BOOK_AUTHOR);
        dto.setIsbn(BOOK_ISBN);
        dto.setPrice(BOOK_PRICE);
        dto.setCategoryIds(Set.of(categoryId));
        return dto;
    }

    public static BookDtoWithoutCategoryIds buildBookDtoWithoutCategories(Long id) {
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(id);
        dto.setTitle(BOOK_TITLE);
        dto.setAuthor(BOOK_AUTHOR);
        dto.setIsbn(BOOK_ISBN);
        dto.setPrice(BOOK_PRICE);
        return dto;
    }

    public static CategoryRequestDto buildCategoryRequest() {
        return new CategoryRequestDto(CATEGORY_NAME, CATEGORY_DESCRIPTION);
    }

    public static CategoryDto buildCategoryDto(Long id) {
        return new CategoryDto(id, CATEGORY_NAME, CATEGORY_DESCRIPTION);
    }
}
