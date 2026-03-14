package store.book.bookstore.util;

import java.math.BigDecimal;
import java.util.Set;
import lombok.experimental.UtilityClass;
import store.book.bookstore.dto.BookDto;
import store.book.bookstore.dto.BookDtoWithoutCategoryIds;
import store.book.bookstore.dto.CategoryDto;
import store.book.bookstore.dto.CategoryRequestDto;
import store.book.bookstore.dto.CreateBookRequestDto;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.Category;

@UtilityClass
public class TestDataHelper {

    public final String BOOK_TITLE = "Warriors: Into the Wild";
    public final String BOOK_AUTHOR = "Erin Hunter";
    public final String BOOK_ISBN = "9780329373528";
    public final BigDecimal BOOK_PRICE = BigDecimal.valueOf(29.99);

    public final String BOOK_SECOND_TITLE = "Effective Java";
    public final String BOOK_SECOND_AUTHOR = "Joshua Bloch";
    public final String BOOK_SECOND_ISBN = "9780134685991";
    public final BigDecimal BOOK_SECOND_PRICE = BigDecimal.valueOf(39.99);

    public final String CATEGORY_NAME = "Fantasy novel";
    public final String CATEGORY_DESCRIPTION =
            "Fiction featuring magical or supernatural elements";

    public final String CATEGORY_SECOND_NAME = "Science Fiction";
    public final String CATEGORY_SECOND_DESCRIPTION =
            "Sci-fi books";

    public final String CATEGORY_THIRD_NAME = "Horror";
    public final String CATEGORY_THIRD_DESCRIPTION =
            "Horror books";

    public final String ADMIN_EMAIL = "admin@bookstore.com";
    public final String ADMIN_PASSWORD = "admin1234";
    public final String USER_EMAIL = "user@bookstore.com";
    public final String USER_PASSWORD = "user1234";

    public Category buildCategory() {
        Category category = new Category();
        category.setName(CATEGORY_NAME);
        category.setDescription(CATEGORY_DESCRIPTION);
        return category;
    }

    public Category buildCategory(Long id) {
        Category category = buildCategory();
        category.setId(id);
        return category;
    }

    public Category buildCategory2() {
        Category category = new Category();
        category.setName(CATEGORY_SECOND_NAME);
        category.setDescription(CATEGORY_SECOND_DESCRIPTION);
        return category;
    }

    public Category buildCategory3() {
        Category category = new Category();
        category.setName(CATEGORY_THIRD_NAME);
        category.setDescription(CATEGORY_THIRD_DESCRIPTION);
        return category;
    }

    public Book buildBook(Category category) {
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(BOOK_AUTHOR);
        book.setIsbn(BOOK_ISBN);
        book.setPrice(BOOK_PRICE);
        book.setCategories(Set.of(category));
        return book;
    }

    public Book buildBook(Long id, Category category) {
        Book book = buildBook(category);
        book.setId(id);
        return book;
    }

    public Book buildBook2(Category category) {
        Book book = new Book();
        book.setTitle(BOOK_SECOND_TITLE);
        book.setAuthor(BOOK_SECOND_AUTHOR);
        book.setIsbn(BOOK_SECOND_ISBN);
        book.setPrice(BOOK_SECOND_PRICE);
        book.setCategories(Set.of(category));
        return book;
    }

    public CreateBookRequestDto buildCreateBookRequest(Long categoryId) {
        CreateBookRequestDto req = new CreateBookRequestDto();
        req.setTitle(BOOK_TITLE);
        req.setAuthor(BOOK_AUTHOR);
        req.setIsbn(BOOK_ISBN);
        req.setPrice(BOOK_PRICE);
        req.setCategoryIds(Set.of(categoryId));
        return req;
    }

    public BookDto buildBookDto(Long id, Long categoryId) {
        BookDto dto = new BookDto();
        dto.setId(id);
        dto.setTitle(BOOK_TITLE);
        dto.setAuthor(BOOK_AUTHOR);
        dto.setIsbn(BOOK_ISBN);
        dto.setPrice(BOOK_PRICE);
        dto.setCategoryIds(Set.of(categoryId));
        return dto;
    }

    public BookDtoWithoutCategoryIds buildBookDtoWithoutCategories(Long id) {
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(id);
        dto.setTitle(BOOK_TITLE);
        dto.setAuthor(BOOK_AUTHOR);
        dto.setIsbn(BOOK_ISBN);
        dto.setPrice(BOOK_PRICE);
        return dto;
    }

    public CategoryRequestDto buildCategoryRequest() {
        return new CategoryRequestDto(CATEGORY_NAME, CATEGORY_DESCRIPTION);
    }

    public CategoryDto buildCategoryDto(Long id) {
        return new CategoryDto(id, CATEGORY_NAME, CATEGORY_DESCRIPTION);
    }
}
