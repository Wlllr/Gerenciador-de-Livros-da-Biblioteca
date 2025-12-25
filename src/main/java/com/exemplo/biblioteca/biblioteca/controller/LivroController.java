package com.exemplo.biblioteca.biblioteca.controller;

import com.exemplo.biblioteca.biblioteca.model.Livro;
import com.exemplo.biblioteca.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping("/")
    public String listar(Model model, @RequestParam(required = false) String isbnBusca) {
        List<Livro> livros;

        if (isbnBusca != null && isbnBusca.length() == 13 && isbnBusca.matches("\\d+")) {
            // Busca específica por ISBN
            Optional<Livro> livro = livroService.buscarLivroPorIsbn(isbnBusca);
            livros = livro.map(List::of).orElseGet(List::of);
            model.addAttribute("isbnBusca", isbnBusca);
        } else {
            // Lista todos
            livros = livroService.listarTodos();
        }

        model.addAttribute("livros", livros);

        // Renderização da pagina através da view
        if (!model.containsAttribute("livro")) {
            model.addAttribute("livro", new Livro());
        }
        return "lista-livros";
    }

    // --- Cadastro e Edição ---

    @GetMapping("/adicionar")
    public String exibirFormularioAdicionar(Model model) {
        if (!model.containsAttribute("livro")) {
            model.addAttribute("livro", new Livro());
        }
        model.addAttribute("acao", "Adicionar");
        return "form-livro";
    }

    @PostMapping("/salvar")
    public String salvarLivro(@Valid @ModelAttribute("livro") Livro livro,
                              BindingResult result,
                              RedirectAttributes ra) {

        // Se houver erros de Bean Validation (CT3 - ISBN vazio/inválido)
        if (result.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.livro", result);
            ra.addFlashAttribute("livro", livro);
            ra.addFlashAttribute("mensagemErro", "Erro de Validação: Preencha todos os campos corretamente.");
            return "redirect:/adicionar";
        }

        // Tenta salvar (CT1 e CT2)
        String resultado = livroService.cadastrarLivro(livro);

        if (resultado.startsWith("Erro")) {
            ra.addFlashAttribute("mensagemErro", resultado); // CT2: Livro já cadastrado
            ra.addFlashAttribute("livro", livro);
            return "redirect:/adicionar"; // Volta ao formulário de adição para corrigir
        } else {
            ra.addFlashAttribute("mensagemSucesso", resultado); // CT1: Sucesso
        }
        return "redirect:/";
    }

    @GetMapping("/editar")
    public String exibirFormularioEditar(@RequestParam String isbn, Model model, RedirectAttributes ra) {
        Optional<Livro> optionalLivro = livroService.buscarLivroPorIsbn(isbn);

        if (optionalLivro.isPresent()) {
            model.addAttribute("livro", optionalLivro.get());
            model.addAttribute("acao", "Editar");
            return "form-livro";
        } else {
            ra.addFlashAttribute("mensagemErro", "Erro: Livro não encontrado para edição.");
            return "redirect:/";
        }
    }

    @PostMapping("/atualizar")
    public String atualizarLivro(@Valid @ModelAttribute("livro") Livro livroAtualizado,
                                 BindingResult result,
                                 RedirectAttributes ra) {

        if (result.hasErrors()) {
            ra.addFlashAttribute("mensagemErro", "Erro de Validação. Verifique Título e Ano.");
            return "redirect:/editar?isbn=" + livroAtualizado.getIsbn(); // Volta para edição
        }

        boolean sucesso = livroService.editarLivro(livroAtualizado);

        if (sucesso) {
            ra.addFlashAttribute("mensagemSucesso", "Livro atualizado com sucesso!");
        } else {
            ra.addFlashAttribute("mensagemErro", "Erro: Falha ao atualizar livro. Livro não encontrado.");
        }
        return "redirect:/";
    }


    // --- Exclusão (Delete) ---

    @GetMapping("/excluir")
    public String excluirLivro(@RequestParam String isbn, RedirectAttributes ra) {

        // CT3: ISBN inválido, deve conter 13 dígitos
        if (isbn == null || !isbn.matches("\\d{13}")) {
            ra.addFlashAttribute("mensagemErro", "Erro: ISBN inválido, deve conter 13 dígitos");
            return "redirect:/";
        }

        // Usa o Service para excluir (CT1 e CT2 de exclusão)
        String resultado = livroService.excluirLivro(isbn);

        if (resultado.startsWith("Erro")) {
            ra.addFlashAttribute("mensagemErro", resultado); // CT2: Livro não encontrado
        } else {
            ra.addFlashAttribute("mensagemSucesso", resultado); // CT1: Sucesso
        }
        return "redirect:/";
    }
}