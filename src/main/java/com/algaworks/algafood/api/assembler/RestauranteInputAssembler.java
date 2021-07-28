package com.algaworks.algafood.api.assembler;

import com.algaworks.algafood.api.model.input.CozinhaIdInput;
import com.algaworks.algafood.api.model.input.RestauranteInput;
import com.algaworks.algafood.domain.model.Restaurante;
import org.springframework.stereotype.Component;

@Component
public class RestauranteInputAssembler {

    public RestauranteInput toModelInput(Restaurante restaurante) {
        RestauranteInput restauranteInput = new RestauranteInput();
        restauranteInput.setNome(restaurante.getNome());
        restauranteInput.setTaxaFrete(restaurante.getTaxaFrete());

        CozinhaIdInput cozinhaIdInput = new CozinhaIdInput();
        cozinhaIdInput.setId(restaurante.getId());

        restauranteInput.setCozinha(cozinhaIdInput);

        return restauranteInput;
    }

}
