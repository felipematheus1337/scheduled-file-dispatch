package scheduled_file_dispatch.v1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scheduled_file_dispatch.v1.application.port.out.*;
import scheduled_file_dispatch.v1.application.usecases.*;

@Configuration
public class UseCaseConfiguration {
    @Bean CadastrarUsuarioUseCase cadastrar(UsuarioRepositoryPort u, SenhaPort s) {
        return new CadastrarUsuarioUseCase(u, s);
    }
    @Bean AutenticarUsuarioUseCase autenticar(UsuarioRepositoryPort u, SenhaPort s, TokenPort t) {
        return new AutenticarUsuarioUseCase(u, s, t);
    }
    @Bean CriarRascunhoUseCase criar(EnvioRepositoryPort e) { return new CriarRascunhoUseCase(e); }
    @Bean ConsultarEnvioUseCase consultar(EnvioRepositoryPort e) { return new ConsultarEnvioUseCase(e); }
    @Bean EditarRascunhoUseCase editar(ConsultarEnvioUseCase c, EnvioRepositoryPort e) {
        return new EditarRascunhoUseCase(c, e);
    }
    @Bean ExcluirRascunhoUseCase excluir(ConsultarEnvioUseCase c, EnvioRepositoryPort e, ArquivoPort a) {
        return new ExcluirRascunhoUseCase(c, e, a);
    }
    @Bean AgendarEnvioUseCase agendar(ConsultarEnvioUseCase c, EnvioRepositoryPort e) {
        return new AgendarEnvioUseCase(c, e);
    }
    @Bean CancelarEnvioUseCase cancelar(ConsultarEnvioUseCase c, EnvioRepositoryPort e) {
        return new CancelarEnvioUseCase(c, e);
    }
    @Bean ReprocessarEnvioUseCase reprocessar(ConsultarEnvioUseCase c, EnvioRepositoryPort e) {
        return new ReprocessarEnvioUseCase(c, e);
    }
    @Bean AdicionarAnexoUseCase adicionar(ConsultarEnvioUseCase c, EnvioRepositoryPort e, ArquivoPort a) {
        return new AdicionarAnexoUseCase(c, e, a);
    }
    @Bean RemoverAnexoUseCase remover(ConsultarEnvioUseCase c, EnvioRepositoryPort e, ArquivoPort a) {
        return new RemoverAnexoUseCase(c, e, a);
    }
    @Bean BaixarAnexoUseCase baixar(ConsultarEnvioUseCase c, ArquivoPort a) {
        return new BaixarAnexoUseCase(c, a);
    }
    @Bean EnfileirarEnviosVencidosUseCase enfileirar(EnvioRepositoryPort e, MensagemPort m) {
        return new EnfileirarEnviosVencidosUseCase(e, m);
    }
    @Bean ProcessarEnvioUseCase processar(EnvioRepositoryPort e, ArquivoPort a, EmailPort m) {
        return new ProcessarEnvioUseCase(e, a, m);
    }
}
