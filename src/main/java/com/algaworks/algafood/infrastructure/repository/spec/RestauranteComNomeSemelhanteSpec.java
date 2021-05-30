package com.algaworks.algafood.infrastructure.repository.spec;

import com.algaworks.algafood.domain.model.Restaurante;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;

@AllArgsConstructor
public class RestauranteComNomeSemelhanteSpec implements Specification<Restaurante> {

    private static final long serialVersionUID = -3513411355430092921L;

    private String nome;

    @Override
    public Predicate toPredicate(Root<Restaurante> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(root.get("nome"), "%" + nome + "%");
    }
}
