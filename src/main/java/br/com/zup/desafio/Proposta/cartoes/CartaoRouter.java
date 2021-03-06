package br.com.zup.desafio.Proposta.cartoes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.zup.desafio.Proposta.avisos.AvisoRequest;
import br.com.zup.desafio.Proposta.avisos.AvisoResponse;
import br.com.zup.desafio.Proposta.bloqueios.BloqueioRequest;
import br.com.zup.desafio.Proposta.bloqueios.BloqueioResponse;
import br.com.zup.desafio.Proposta.carteiras.CarteiraRequest;
import br.com.zup.desafio.Proposta.carteiras.CarteiraResponse;

@FeignClient(name = "cartoes", url = "${cartao.api}/api/cartoes")
public interface CartaoRouter {

	@PostMapping
	public CartaoResponseRouter criaCartao(@RequestBody CartaoRequestRouter form);

	@PostMapping("/{id}/bloqueios")
	public BloqueioResponse bloqueio(@PathVariable String id, BloqueioRequest request);

	@GetMapping("/{id}")
	public CartaoResponseRouter buscaCartaoPorId(@PathVariable String id);

	@PostMapping("/{id}/avisos")
	AvisoResponse notificaAviso(@PathVariable String id, AvisoRequest request);
	
	@PostMapping("/{id}/carteiras")
	public CarteiraResponse criaCarteira(@PathVariable String id, CarteiraRequest request);

}
