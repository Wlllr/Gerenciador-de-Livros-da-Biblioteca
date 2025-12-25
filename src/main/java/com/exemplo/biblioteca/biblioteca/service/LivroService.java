package com.exemplo.biblioteca.biblioteca.service;

import com.exemplo.biblioteca.biblioteca.model.Livro;
import com.exemplo.biblioteca.biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    @Transactional
    public String cadastrarLivro(Livro novoLivro) {
        try {
            // Se a validação do Bean passar, o JPA tenta salvar
            livroRepository.save(novoLivro);
            // CT1: Cadastro de livro realizado com sucesso
            return "Cadastro realizado com sucesso";
        } catch (DataIntegrityViolationException e) {
            // CT2: Captura o erro específico de ISBN ÚNICO (chave única)
            return "Erro: Livro já cadastrado (ISBN já existe)";
        }
    }


    public Optional<Livro> buscarLivroPorIsbn(String isbn) {
        return livroRepository.findByIsbn(isbn);
    }

    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    @Transactional
    public boolean editarLivro(Livro livroAtualizado) {
        // Usa o ID do Livro atualizado para o JPA saber qual registro atualizar
        if (livroAtualizado.getId() == null) {
            return false;
        }

        Optional<Livro> optionalLivro = livroRepository.findById(livroAtualizado.getId());

        if (optionalLivro.isPresent()) {
            Livro livroExistente = optionalLivro.get();

            // Atualiza os campos editáveis
            livroExistente.setTitulo(livroAtualizado.getTitulo());
            livroExistente.setAutor(livroAtualizado.getAutor());
            livroExistente.setAnoPublicacao(livroAtualizado.getAnoPublicacao());

            livroRepository.save(livroExistente);
            return true;
        }
        return false;
    }

    @Transactional
    public String excluirLivro(String isbn) {
        // Tenta encontrar o livro para verificar a existência
        Optional<Livro> livroOptional = livroRepository.findByIsbn(isbn);

        if (livroOptional.isPresent()) {
            // CT1-Exclusão: Exclusão bem-sucedida
            livroRepository.deleteByIsbn(isbn); // Remove pelo ISBN
            return "Livro excluído com sucesso";
        } else {
            // CT2-Exclusão: Livro não cadastrado
            return "Erro: Livro não encontrado";
        }
    }
}