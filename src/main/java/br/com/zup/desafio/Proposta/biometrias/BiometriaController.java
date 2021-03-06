package br.com.zup.desafio.Proposta.biometrias;

import java.net.URI;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/biometrias")
public class BiometriaController {

	@PersistenceContext
	private EntityManager em;

	@PostMapping("/{idCartao}")
	public ResponseEntity<BiometriaResponse> criar(@RequestBody @Valid BiometriaRequest form,
			@PathVariable("idCartao") Long id, UriComponentsBuilder uriBuilder) {

		// CRIANDO UMA BRIOMETRIA
		Biometria biometria = form.toModel(id, em);

		em.persist(biometria);

		URI uri = uriBuilder.path("/biometrias/{id}").buildAndExpand(biometria.getId()).toUri();

		return ResponseEntity.created(uri).body(new BiometriaResponse(biometria));

	}
}
