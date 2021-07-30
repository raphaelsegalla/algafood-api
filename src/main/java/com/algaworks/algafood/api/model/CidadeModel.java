package com.algaworks.algafood.api.model;

import com.algaworks.algafood.api.model.input.EstadoIdInput;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CidadeModel {

    private Long id;
    private String nome;
    private EstadoIdInput estado;

}

