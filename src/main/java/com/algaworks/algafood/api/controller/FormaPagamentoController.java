package com.algaworks.algafood.api.controller;

import com.algaworks.algafood.api.assembler.FormaPagamentoInputDisassembler;
import com.algaworks.algafood.api.assembler.FormaPagamentoModelAssembler;
import com.algaworks.algafood.api.model.FormaPagamentoModel;
import com.algaworks.algafood.api.model.input.FormaPagamentoInput;
import com.algaworks.algafood.api.openapi.controller.FormaPagamentoControllerOpenApi;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.repository.FormaPagamentoRepository;
import com.algaworks.algafood.domain.service.CadastroFormaPagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/formas-pagamento")
public class FormaPagamentoController implements FormaPagamentoControllerOpenApi {

    @Autowired
    private FormaPagamentoRepository formaPagamentoRepository;

    @Autowired
    private CadastroFormaPagamentoService cadastroFormaPagamentoService;

    @Autowired
    private FormaPagamentoModelAssembler formaPagamentoModelAssembler;

    @Autowired
    private FormaPagamentoInputDisassembler formaPagamentoInputDisassembler;

    @GetMapping
    public ResponseEntity<CollectionModel<FormaPagamentoModel>> listar(ServletWebRequest request) {
        ShallowEtagHeaderFilter.disableContentCaching(request.getRequest());

        String eTag = "0";

        OffsetDateTime dataUltimaAtualizacao = formaPagamentoRepository.getDataUltimaAtualizacao();

        if (dataUltimaAtualizacao != null) {
            eTag = String.valueOf(dataUltimaAtualizacao.toEpochSecond());
        }

        if (request.checkNotModified(eTag)) {
            return null;
        }

        List<FormaPagamento> todasFormasPagamentos = formaPagamentoRepository.findAll();

        CollectionModel<FormaPagamentoModel> formasPagamentosModel = formaPagamentoModelAssembler.toCollectionModel(todasFormasPagamentos);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePublic())
                .eTag(eTag)
                .body(formasPagamentosModel);
    }

//    @GetMapping
//    public ResponseEntity<List<FormaPagamentoModel>> listar() {
//        List<FormaPagamento> todasFormasPagamentos = formaPagamentoRepository.findAll();
//
//        List<FormaPagamentoModel> formasPagamentosModel = formaPagamentoModelAssembler.toCollectionModel(todasFormasPagamentos);
//
//        return ResponseEntity.ok()
//                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePublic())
//                .body(formasPagamentosModel);
//    }

//    @GetMapping
//    public List<FormaPagamentoModel> listar() {
//        return formaPagamentoModelAssembler.toCollectionModel(formaPagamentoRepository.findAll());
//    }

    @GetMapping("/{formaPagamentoId}")
    public ResponseEntity<FormaPagamentoModel> buscar(@PathVariable Long formaPagamentoId, ServletWebRequest request) {

        ShallowEtagHeaderFilter.disableContentCaching(request.getRequest());

        String eTag = "0";

        OffsetDateTime dataAtualizacao = formaPagamentoRepository
                .getDataAtualizacaoById(formaPagamentoId);

        if (dataAtualizacao != null) {
            eTag = String.valueOf(dataAtualizacao.toEpochSecond());
        }

        if (request.checkNotModified(eTag)) {
            return null;
        }

        FormaPagamento formaPagamento = cadastroFormaPagamentoService.buscarOuFalhar(formaPagamentoId);

        FormaPagamentoModel formaPagamentoModel = formaPagamentoModelAssembler.toModel(formaPagamento);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .eTag(eTag)
                .body(formaPagamentoModel);
    }

//    @GetMapping("/{formaPagamentoId}")
//    public ResponseEntity<FormaPagamentoModel> buscar(@PathVariable Long formaPagamentoId) {
//        FormaPagamento formaPagamento = cadastroFormaPagamentoService.buscarOuFalhar(formaPagamentoId);
//
//        FormaPagamentoModel formaPagamentoModel =  formaPagamentoModelAssembler.toModel(formaPagamento);
//
//        return ResponseEntity.ok()
////                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
////                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePrivate())
//                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePublic())
////                .cacheControl(CacheControl.noCache())
////                .cacheControl(CacheControl.noStore())
//                .body(formaPagamentoModel);
//    }

//    @GetMapping("/{formaPagamentoId}")
//    public FormaPagamentoModel buscar(@PathVariable long formaPagamentoId) {
//        return formaPagamentoModelAssembler.toModel(cadastroFormaPagamentoService.buscarOuFalhar(formaPagamentoId));
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormaPagamentoModel adicionar(@RequestBody @Valid FormaPagamentoInput formaPagamentoInput) {
        FormaPagamento formaPagamento = formaPagamentoInputDisassembler.toDomainObject(formaPagamentoInput);
        return formaPagamentoModelAssembler.toModel(cadastroFormaPagamentoService.salvar(formaPagamento));
    }

    @PutMapping("/{formaPagamentoId}")
    public FormaPagamentoModel atualizar(@PathVariable Long formaPagamentoId, @RequestBody @Valid FormaPagamentoInput formaPagamentoInput) {
        FormaPagamento formaPagamentoAtual = cadastroFormaPagamentoService.buscarOuFalhar(formaPagamentoId);

        formaPagamentoInputDisassembler.copyToDomainObject(formaPagamentoInput, formaPagamentoAtual);

        return formaPagamentoModelAssembler.toModel(cadastroFormaPagamentoService.salvar(formaPagamentoAtual));
    }

    @DeleteMapping("/{formaPagamentoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long formaPagamentoId) {
        cadastroFormaPagamentoService.excluir(formaPagamentoId);
    }

}
