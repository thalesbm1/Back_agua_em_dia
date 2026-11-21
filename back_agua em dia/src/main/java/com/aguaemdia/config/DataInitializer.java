package com.aguaemdia.config;

import com.aguaemdia.dto.RelatoDTO;
import com.aguaemdia.dto.UsuarioDTO;
import com.aguaemdia.dto.UsuarioResponseDTO;
import com.aguaemdia.dto.VazamentoDTO;
import com.aguaemdia.service.RelatoService;
import com.aguaemdia.service.UsuarioService;
import com.aguaemdia.service.VazamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga de dados inicial para testes acadêmicos e demonstração imediata do Água em Dia.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final VazamentoService vazamentoService;
    private final RelatoService relatoService;

    @Autowired
    public DataInitializer(UsuarioService usuarioService,
                           VazamentoService vazamentoService,
                           RelatoService relatoService) {
        this.usuarioService = usuarioService;
        this.vazamentoService = vazamentoService;
        this.relatoService = relatoService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Criação de Usuários de Teste
        UsuarioResponseDTO u1 = usuarioService.cadastrar(new UsuarioDTO(
                "Carlos Silva",
                "carlos@email.com",
                "(81) 98765-4321",
                "123456",
                "ATIVO"
        ));

        UsuarioResponseDTO u2 = usuarioService.cadastrar(new UsuarioDTO(
                "Maria Souza",
                "maria@email.com",
                "(81) 99123-8877",
                "123456",
                "ATIVO"
        ));

        // 2. Criação de Vazamentos Iniciais
        VazamentoDTO v1 = new VazamentoDTO();
        v1.setStatus("ABERTO");
        v1.setBairro("Santo Amaro");
        v1.setReferencia("Rua da Aurora, próximo à ponte");
        v1.setTipo("ADUTORA");
        v1.setPorte("FORTE");
        v1.setLatitude(-8.0430);
        v1.setLongitude(-34.8860);
        v1.setVazaoEstimada(7200);
        VazamentoDTO v1Salvo = vazamentoService.criar(v1);

        VazamentoDTO v2 = new VazamentoDTO();
        v2.setStatus("EM_MANUTENCAO");
        v2.setBairro("Boa Vista");
        v2.setReferencia("Av. Conde da Boa Vista, parada 3");
        v2.setTipo("REDE");
        v2.setPorte("MEDIO");
        v2.setLatitude(-8.0570);
        v2.setLongitude(-34.8880);
        v2.setVazaoEstimada(960);
        VazamentoDTO v2Salvo = vazamentoService.criar(v2);

        // 3. Criação de Relatos Iniciais
        RelatoDTO r1 = new RelatoDTO();
        r1.setUsuarioId(u1.getId());
        r1.setVazamentoId(v1Salvo.getId());
        r1.setBairro("Santo Amaro");
        r1.setReferencia("Rua da Aurora, próximo à ponte");
        r1.setTipo("ADUTORA");
        r1.setPorte("FORTE");
        r1.setDescricao("Jorro alto saindo de cano principal sob a calçada. Muita água limpa escorrendo.");
        relatoService.registrarRelato(r1);

        RelatoDTO r2 = new RelatoDTO();
        r2.setUsuarioId(u2.getId());
        r2.setVazamentoId(v2Salvo.getId());
        r2.setBairro("Boa Vista");
        r2.setReferencia("Av. Conde da Boa Vista, parada 3");
        r2.setTipo("REDE");
        r2.setPorte("MEDIO");
        r2.setDescricao("Água brotando do asfalto, criando poça e atrapalhando a passagem dos ônibus.");
        relatoService.registrarRelato(r2);

        // 4. Criação de um terceiro relato livre que criará automaticamente um novo vazamento
        RelatoDTO r3 = new RelatoDTO();
        r3.setUsuarioId(u1.getId());
        r3.setBairro("Recife Antigo");
        r3.setReferencia("Rua do Bom Jesus, em frente à Sinagoga");
        r3.setTipo("RAMAL");
        r3.setPorte("FIO");
        r3.setDescricao("Cavalete com vazamento contínuo na parede externa.");
        relatoService.registrarRelato(r3);
    }
}
