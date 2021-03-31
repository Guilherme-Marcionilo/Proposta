package br.com.zup.desafio.Proposta.cartoes;

import java.time.LocalDateTime;

public class AvisoResponse {

	private String validoAte;

	private String destino;

	private Cartao cartao;

	@Deprecated
	public AvisoResponse() {
	}
	
	//MÉTODO QUE ENVIA PARA A MODEL E PEGA OS NOVOS ATRIBUTOS
	public Aviso toModel() {
        return new Aviso(LocalDateTime.parse(validoAte), destino, cartao);
    }

	public String getValidoAte() {
		return validoAte;
	}

	public String getDestino() {
		return destino;
	}

	public Cartao getCartao() {
		return cartao;
	}

}
