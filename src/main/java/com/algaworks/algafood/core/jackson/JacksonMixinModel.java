package com.algaworks.algafood.core.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Component;

@Component
public class JacksonMixinModel extends SimpleModule {

    private static final long serialVersionUID = -5055764959530681484L;

    public JacksonMixinModel() {
//        setMixInAnnotation(Restaurante.class, RestauranteMixin.class);
//        setMixInAnnotation(Cidade.class, CidadeMixin.class);
//        setMixInAnnotation(Cozinha.class, CozinhaMixin.class);
    }
}
