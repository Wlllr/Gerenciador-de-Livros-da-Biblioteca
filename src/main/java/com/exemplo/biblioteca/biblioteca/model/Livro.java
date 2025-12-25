package com.exemplo.biblioteca.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Garante a validação para ISBN (CT3 - Cadastro e CT3 - Exclusão)
    // Nao permitindo nunca que fique vazio
    @NotBlank(message = "O preenchimento do campo ISBN é obrigatório.")
    @Pattern(regexp = "\\d{13}", message = "ISBN inválido, deve conter 13 dígitos numéricos.")
    @Size(min = 13, max = 13, message = "O ISBN deve conter exatamente 13 dígitos.")
    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    @NotBlank(message = "O Título é obrigatório.")
    private String titulo;

    private String autor;

    @NotNull(message = "O Ano de Publicação é obrigatório.")
    @Min(value = 1000, message = "Ano de publicação inválido. Deve ser no mínimo 1000.")
    private Integer anoPublicacao;

    public Livro(String titulo, String autor, Integer anoPublicacao, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.isbn = isbn;
    }

    public Livro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }
}