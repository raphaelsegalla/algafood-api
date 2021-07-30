package com.algaworks.algafood.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cidade {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank
    @Column(nullable = false)
    private String nome;

//    @JsonIgnoreProperties(value = "nome", allowGetters = true)
//    @Valid
//    @ConvertGroup(from = Default.class, to = Groups.EstadoId.class)
//    @NotNull
    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;


}
