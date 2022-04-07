package com.algaworks.algafood.api.v1.controller;

import com.algaworks.algafood.api.v1.model.RestauranteApenasNomeModel;
import com.algaworks.algafood.api.v1.model.RestauranteBasicoModel;
import com.algaworks.algafood.api.v1.model.RestauranteModel;
import com.algaworks.algafood.api.v1.model.input.RestauranteInput;
import com.algaworks.algafood.api.v1.openapi.controller.RestauranteControllerOpenApi;
import com.algaworks.algafood.api.v1.assembler.*;
import com.algaworks.algafood.core.security.CheckSecurity;
import com.algaworks.algafood.core.validation.ValidacaoException;
import com.algaworks.algafood.domain.exception.CidadeNaoEncontradoException;
import com.algaworks.algafood.domain.exception.CozinhaNaoEncontradoException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.exception.RestauranteNaoEncontradoException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/restaurantes")
public class RestauranteController implements RestauranteControllerOpenApi {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private CadastroRestauranteService cadastroRestauranteService;

    @Autowired
    private SmartValidator validator;

    @Autowired
    private RestauranteModelAssembler restauranteModelAssembler;

    @Autowired
    private RestauranteInputDisassembler restauranteInputDisassembler;

    @Autowired
    private RestauranteInputAssembler restauranteInputAssembler;

    @Autowired
    private RestauranteBasicoModelAssembler restauranteBasicoModelAssembler;

    @Autowired
    private RestauranteApenasNomeModelAssembler restauranteApenasNomeModelAssembler;

    @CheckSecurity.Restaurantes.PodeConsultar
    @GetMapping
    public CollectionModel<RestauranteBasicoModel> listar() {
        return restauranteBasicoModelAssembler.toCollectionModel(restauranteRepository.findAll());
    }

    @CheckSecurity.Restaurantes.PodeConsultar
    @GetMapping(params = "projecao=apenas-nome")
    public CollectionModel<RestauranteApenasNomeModel> listarApenasNomes() {
        return restauranteApenasNomeModelAssembler.toCollectionModel(restauranteRepository.findAll());
    }

    @CheckSecurity.Restaurantes.PodeConsultar
    @GetMapping("/{restauranteId}")
    public RestauranteModel buscar(@PathVariable Long restauranteId) {
        Restaurante restaurante = cadastroRestauranteService.buscarOuFalhar(restauranteId);
        return restauranteModelAssembler.toModel(restaurante);
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestauranteModel adicionar(@RequestBody @Valid RestauranteInput restauranteInput) {
        try {
            Restaurante restaurante = restauranteInputDisassembler.toDomainObject(restauranteInput);
            return restauranteModelAssembler.toModel(cadastroRestauranteService.salvar(restaurante));
        } catch (CozinhaNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @PutMapping("/{restauranteId}")
    public RestauranteModel atualizar(@PathVariable Long restauranteId, @RequestBody @Valid RestauranteInput restauranteInput) {

        try {
            Restaurante restauranteAtual = cadastroRestauranteService.buscarOuFalhar(restauranteId);

            restauranteInputDisassembler.copyToDomainObject(restauranteInput, restauranteAtual);

            return restauranteModelAssembler.toModel(cadastroRestauranteService.salvar(restauranteAtual));
        } catch (CozinhaNaoEncontradoException | CidadeNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @PutMapping("/{restauranteId}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> ativar(@PathVariable Long restauranteId) {
        cadastroRestauranteService.ativar(restauranteId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @DeleteMapping("/{restauranteId}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> inativar(@PathVariable Long restauranteId) {
        cadastroRestauranteService.inativar(restauranteId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @PutMapping("/ativacoes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativarMultiplos(@RequestBody List<Long> restauranteIds) {
        try {
            cadastroRestauranteService.ativar(restauranteIds);
        } catch (RestauranteNaoEncontradoException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @DeleteMapping("/ativacoes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativarMultiplos(@RequestBody List<Long> restauranteIds) {
        try {
            cadastroRestauranteService.inativar(restauranteIds);
        } catch (RestauranteNaoEncontradoException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @PutMapping("/{restauranteId}/abertura")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> abrir(@PathVariable Long restauranteId) {
        cadastroRestauranteService.abrir(restauranteId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @PutMapping("/{restauranteId}/fechamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> fechar(@PathVariable Long restauranteId) {
        cadastroRestauranteService.fechar(restauranteId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeEditar
    @PatchMapping("/{restauranteId}")
    public RestauranteModel atualizarParcial(@PathVariable Long restauranteId, @RequestBody Map<String, Object> campos, HttpServletRequest request) {
        Restaurante restauranteAtual = cadastroRestauranteService.buscarOuFalhar(restauranteId);

        merge(campos, restauranteAtual, request);
        validate(restauranteAtual, "restaurante");

        return atualizar(restauranteId, restauranteInputAssembler.toModelInput(restauranteAtual));
    }

    private void validate(Restaurante restaurante, String objetcName) {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(restaurante, objetcName);

        validator.validate(restaurante, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new ValidacaoException(bindingResult);
        }

    }

    private void merge(Map<String, Object> dadosOrigem, Restaurante restauranteDestino, HttpServletRequest request) {

        ServletServerHttpRequest servletServerHttpRequest = new ServletServerHttpRequest(request);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

            Restaurante restauranteOrigem = objectMapper.convertValue(dadosOrigem, Restaurante.class);

            dadosOrigem.forEach((nomePropriedade, valorPropriedade) -> {
                Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
                field.setAccessible(true);

                System.out.println(restauranteOrigem);

                Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);

                System.out.println(nomePropriedade + " = " + valorPropriedade + " = " + novoValor);

                ReflectionUtils.setField(field, restauranteDestino, novoValor);

                System.out.println("----------------------------------------------------------");
            });
        } catch (IllegalArgumentException e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            throw new HttpMessageNotReadableException(e.getMessage(), rootCause, servletServerHttpRequest);
        }
    }
}
