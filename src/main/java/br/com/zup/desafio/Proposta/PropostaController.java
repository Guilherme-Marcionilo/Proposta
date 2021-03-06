package br.com.zup.desafio.Proposta;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zup.desafio.Proposta.cartoes.CartaoResponseRouter;
import br.com.zup.desafio.Proposta.cartoes.CartaoRouter;
import br.com.zup.desafio.Proposta.status.StatusGateway;
import br.com.zup.desafio.Proposta.status.StatusProposta;
import br.com.zup.desafio.Proposta.status.StatusRequest;
import br.com.zup.desafio.Proposta.status.StatusResponse;
import feign.FeignException;

@RestController
@RequestMapping("/proposta")
public class PropostaController extends RuntimeException {

	@PersistenceContext
	private EntityManager em;
	
	
	@Autowired
	private StatusGateway statusGateway;

	@Autowired
	private PropostaRepository propostaRepository;

	@Autowired
	private CartaoRouter cartaoRouter;

	private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

	@PostMapping
	@Transactional
	ResponseEntity<?> cadastrar(@Valid @RequestBody NovaPropostaRequest request,
			UriComponentsBuilder uriComponentsBuilder) {

		logger.info("Início: criação proposta");

		if (propostaRepository.existsByDocumento(request.getDocumento())) {

			logger.warn("Proposta não foi criada");

			Assert.assertTrue(propostaRepository.existsByDocumento(request.getDocumento()));

			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.body("Ops! Isto não pode ser processado! Pois, o documento já existe!");

		}

		Proposta proposta = request.toModel();

		StatusRequest req = new StatusRequest(proposta);

		// StatusProposta status = response.toModel();
		
		 try {
	            StatusResponse response = statusGateway.status(proposta.toStatus());
		
	            proposta.setStatus(response.getResultadoSolicitacao());

	            System.out.println(proposta);

	            logger.info("Proposta Criada com Sucesso!", proposta.getDocumento());
	            
	            propostaRepository.save(proposta);

		 }		 catch (FeignException e) {

	            proposta.setStatus(StatusProposta.COM_RESTRICAO);
	        }

		return ResponseEntity
				.created(uriComponentsBuilder.path("/propostas/{id}").buildAndExpand(proposta.getId()).toUri()).build();

	}
	
	//NOSSO AGENDAMENTO = SCHEDULED
	@Transactional
	@Scheduled(fixedDelay = 5000)
	public void criaCartao() {
		
		Query query = em.createNativeQuery("SELECT * FROM propostas WHERE status = :status AND" +
                " cartao_id is null limit 10 for update", Proposta.class);

        query.setParameter("status", PropostaStatus.ELEGIVEL.toString());

        List<Proposta> propostas = query.getResultList();

		System.out.println("Entrou no Scheduled");

		while (propostas.size() > 0) {
			Proposta proposta = propostas.get(0);
			System.out.println("Cadastrando cartão da proposta: " + proposta.getId());
			CartaoResponseRouter  cartaoResponse = cartaoRouter.criaCartao(proposta.toCartaoRequest());
			proposta.toCartaoResponse(cartaoResponse.toModel(proposta));

			em.merge(proposta);

			propostas.remove(0);

			System.out.println("Quantidade de propostas -> " + propostas.size());
		}

		System.out.println("Saiu do Scheduled");

	}
	
	@GetMapping
	public ResponseEntity<?> listarPropostas() {
		
		List<Proposta> listarProposta = propostaRepository.findAll();
		
		return ResponseEntity.ok(listarProposta);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> consultar(@PathVariable("id") Long id) {
		logger.info("Consultando proposta");

		Optional<Proposta> proposta = propostaRepository.findById(id);

		if (proposta.isPresent()) {
			Assert.assertNotNull(proposta);
			logger.info("Proposta encontrada");
			return ResponseEntity.ok(proposta);
		}

		logger.warn("Proposta não encontrada");
		return ResponseEntity.notFound().build();
	}

}
