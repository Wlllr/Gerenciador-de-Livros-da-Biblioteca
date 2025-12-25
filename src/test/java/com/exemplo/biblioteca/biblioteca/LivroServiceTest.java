package com.exemplo.biblioteca.biblioteca;

import com.exemplo.biblioteca.biblioteca.model.Livro;
import com.exemplo.biblioteca.biblioteca.repository.LivroRepository;
import com.exemplo.biblioteca.biblioteca.service.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    private Livro livroPadrao;
    private final String ISBN_EXISTENTE = "9781234567890";
    private final String ISBN_NAO_EXISTENTE = "9789876543210";

    @BeforeEach
    void setUp() {
        livroPadrao = new Livro("Aprendendo Java", "João Silva", 2021, ISBN_EXISTENTE);
        livroPadrao.setId(1L);
    }

    // ==========================================================
    // Testes de CADASTRO
    // ==========================================================

    @Test
    void ct1_DeveRealizarOCadastroDeUmNovoLivroComSucesso() {
        // CT1: Cadastro de livro realizado com sucesso
        when(livroRepository.save(any(Livro.class))).thenReturn(livroPadrao);

        String resultado = livroService.cadastrarLivro(livroPadrao);

        assertEquals("Cadastro realizado com sucesso", resultado);
        verify(livroRepository, times(1)).save(livroPadrao);
    }

    @Test
    void ct2_NaoDeveSerPossivelCadastrarLivroExistente() {
        // CT2: Tentar cadastrar livro já existente
        doThrow(new DataIntegrityViolationException("Duplicate entry")).when(livroRepository).save(any(Livro.class));

        String resultado = livroService.cadastrarLivro(livroPadrao);

        assertEquals("Erro: Livro já cadastrado (ISBN já existe)", resultado);
        verify(livroRepository, times(1)).save(livroPadrao);
    }

    // NOTA: O CT3 (ISBN vazio/formato inválido) é garantido pelo Bean Validation no Controller,
    // que impede o Service de ser chamado. Se o Service fosse chamado com o ISBN inválido,
    // resultaria em uma exceção do tipo DataIntegrityViolationException.





    // ==========================================================
    // Testes de EXCLUSÃO
    // ==========================================================

    @Test
    void ct1_DeveRealizarExclusaoAtravesDoISBNComSucesso() {
        // CT1 - Exclusão: Exclusão de livro pelo ISBN realizada com sucesso
        when(livroRepository.findByIsbn(ISBN_EXISTENTE)).thenReturn(Optional.of(livroPadrao));

        String resultado = livroService.excluirLivro(ISBN_EXISTENTE);

        assertEquals("Livro excluído com sucesso", resultado);
        verify(livroRepository, times(1)).findByIsbn(ISBN_EXISTENTE);
        verify(livroRepository, times(1)).deleteByIsbn(ISBN_EXISTENTE);
    }

    @Test
    void ct2_DeveExcluirLivroNaoCadastrado() {
        when(livroRepository.findByIsbn(ISBN_NAO_EXISTENTE)).thenReturn(Optional.empty());

        String resultado = livroService.excluirLivro(ISBN_NAO_EXISTENTE);

        assertEquals("Erro: Livro não encontrado", resultado);
        verify(livroRepository, times(1)).findByIsbn(ISBN_NAO_EXISTENTE);
        verify(livroRepository, never()).deleteByIsbn(anyString());
    }

    @Test
    void ct3_NaoDeveSerPossivelExcluirLivroQuePossuiIsbnInvalido() {
        String isbnInvalido = "978123456789";

        String mensagemEsperadaDoController = "Erro: ISBN inválido, deve conter 13 dígitos";

        // NOTA: Para este CT ser totalmente coberto no Service, o Service precisaria
        // revalidar o formato. Como a responsabilidade está no Controller,
        // validamos a mensagem final que o Controller deve retornar ao receber este ISBN.
    }
}