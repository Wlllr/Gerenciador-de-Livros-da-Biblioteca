package com.exemplo.biblioteca.biblioteca.repository;

import com.exemplo.biblioteca.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Método customizado para buscar o livro pelo ISBN
    Optional<Livro> findByIsbn(String isbn);

    // Método para exclusão (usado no service)
    void deleteByIsbn(String isbn);
}