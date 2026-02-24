package store.book.bookstore.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.book.bookstore.dto.CategoryDto;
import store.book.bookstore.dto.CategoryRequestDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.mapper.CategoryMapper;
import store.book.bookstore.model.Category;
import store.book.bookstore.repository.CategoryRepository;
import store.book.bookstore.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto save(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toEntity(categoryRequestDto);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
        categoryMapper.updateCategoryFromDto(categoryRequestDto, category);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
