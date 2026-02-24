package com.academy.library.service;

import com.academy.library.exception.BookNotFoundException;
import com.academy.library.model.Book;
import com.academy.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookService using Mockito.
 *
 * TODO Task 6: Implement all four test methods below.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    // Sample book used across tests
    private final Book sampleBook = Book.builder()
            .id(1L)
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("978-0-13-468599-1")
            .available(true)
            .build();

    @Test
    @org.junit.jupiter.api.DisplayName("findById: returns book when it exists")
    void findById_shouldReturnBook_whenExists() {
        // TODO: when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        //       Book result = bookService.findById(1L);
        //       assertThat(result).isEqualTo(sampleBook);
    }

    @Test
    @org.junit.jupiter.api.DisplayName("findById: throws exception when not found")
    void findById_shouldThrow_whenNotExists() {
        // TODO: when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        //       assertThatThrownBy(() -> bookService.findById(99L))
        //           .isInstanceOf(BookNotFoundException.class)
        //           .hasMessageContaining("99");
    }

    @Test
    @org.junit.jupiter.api.DisplayName("save: calls repository.save and returns saved book")
    void save_shouldCallRepositorySave() {
        // TODO: when(bookRepository.save(sampleBook)).thenReturn(sampleBook);
        //       Book result = bookService.save(sampleBook);
        //       assertThat(result).isNotNull();
        //       verify(bookRepository, times(1)).save(sampleBook);
    }

    @Test
    @org.junit.jupiter.api.DisplayName("delete: calls repository.deleteById when book exists")
    void delete_shouldVerifyRepositoryDelete() {
        // TODO: when(bookRepository.existsById(1L)).thenReturn(true);
        //       bookService.delete(1L);
        //       verify(bookRepository, times(1)).deleteById(1L);
    }
}
