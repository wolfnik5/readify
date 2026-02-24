package store.book.bookstore.service.impl;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.book.bookstore.dto.BookDto;
import store.book.bookstore.dto.BookDtoWithoutCategoryIds;
import store.book.bookstore.dto.BookSearchParametersDto;
import store.book.bookstore.dto.CreateBookRequestDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.mapper.BookMapper;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.Category;
import store.book.bookstore.repository.BookRepository;
import store.book.bookstore.repository.BookSpecificationBuilder;
import store.book.bookstore.repository.CategoryRepository;
import store.book.bookstore.service.BookService;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        setCategories(book, requestDto.getCategoryIds());
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find Book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find book by id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id: " + id));
        bookMapper.updateBookFromDto(requestDto, book);
        setCategories(book, requestDto.getCategoryIds());
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public Page<BookDto> search(BookSearchParametersDto params, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification, pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId,
                                                               Pageable pageable) {
        return bookRepository.findAllByCategoriesId(categoryId, pageable)
                .map(bookMapper::toDtoWithoutCategories);
    }

    private void setCategories(Book book, Set<Long> categoryIds) {
        Set<Category> categories = categoryIds.stream()
                .map(id -> categoryRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Category not found with id: " + id)
                ))
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }
}
