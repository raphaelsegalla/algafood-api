package com.algaworks.algafood;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.service.CadastroCozinhaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CadastroCozinhaIntegrationTests {

    @Autowired
    private CadastroCozinhaService cadastroCozinhaService;

    @Test
    public void deveAtribuirId_QuandoCadastrarCozinhaComDadosCorretos() {
        // cenáio
        Cozinha novaCozinha =  new Cozinha();
        novaCozinha.setNome("Chinesa");

        // ação
        novaCozinha = cadastroCozinhaService.salvar(novaCozinha);

        // validação
        Assertions.assertNotNull(novaCozinha);
        Assertions.assertNotNull(novaCozinha.getId());
    }

    @Test
    public void deveFalhar_QuandoCadastrarCozinhaSemNome() {
        Cozinha novaCozinha = new Cozinha();
        novaCozinha.setNome(null);

        ConstraintViolationException erroEsperado =
                Assertions.assertThrows(ConstraintViolationException.class, () -> {
                    cadastroCozinhaService.salvar(novaCozinha);
                });

        Assertions.assertNotNull(erroEsperado);
    }

    @Test
    public void deveFalhar_QuandoExcluirCozinhaEmUso() {
        EntidadeEmUsoException erroEsperado =
                Assertions.assertThrows(EntidadeEmUsoException.class, () -> {
                    cadastroCozinhaService.excluir(1L);
        });

        Assertions.assertNotNull(erroEsperado);
    }

    @Test
    public void deveFalhar_QuandoExcluirCozinhaInixistente() {
        EntidadeNaoEncontradaException erroEsperado =
                Assertions.assertThrows(EntidadeNaoEncontradaException.class, () -> {
                    cadastroCozinhaService.excluir(100L);
                });

        Assertions.assertNotNull(erroEsperado);

    }

}
