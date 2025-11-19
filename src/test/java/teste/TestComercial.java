package teste;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lessa.pages.ComercialPage;

public class TestComercial {

    private static final Logger log = LoggerFactory.getLogger(TestComercial.class);

    private static final String URL_DEFAULT = "http://www.fluxis.com.br:8083/fluxis/login.do";
    private static final String USUARIO_DEFAULT = "alexandre.lessa@celer.matriz";
    private static final String SENHA_DEFAULT = "0";

    private WebDriver driver;
    private ComercialPage comercialPage;

    @BeforeEach
    public void setUp() {
        log.info("Iniciando setup do teste");
        FirefoxOptions options = new FirefoxOptions();
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        comercialPage = new ComercialPage(driver);
        log.info("Setup concluído");
    }

    @Test
    public void acessarTelaCadastroProposta() {
        log.info("=== Iniciando teste: acessarTelaCadastroProposta ===");

        try {
            // ETAPA 1: LOGIN
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 1: Realizando LOGIN           ║");
            log.info("╚═══════════════════════════════════════╝");

            boolean loginSucesso = comercialPage.realizarLoginCompleto(
                    URL_DEFAULT, USUARIO_DEFAULT, SENHA_DEFAULT
            );

            if (!loginSucesso) {
                throw new RuntimeException("❌ Login falhou");
            }
            log.info("✓ Login OK\n");

            // ETAPA 2: NAVEGAÇÃO
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 2: Acessando cadastro         ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.acessarTelaCadastro();
            log.info("✓ Tela acessada\n");

            // ETAPA 3: PREENCHIMENTO (SEM DEBUG NO MEIO!)
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 3: Preenchendo prazo          ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.preencherPrazo("15");
            log.info("✓ Prazo OK\n");

            // ETAPA 4: CLIENTE
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 4: Selecionando cliente       ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.selecionarCliente("DESTOM", "20.746.370/0001-80 - DESTOM");
            log.info("✓ Cliente OK\n");

            // ETAPA 5: VENDEDOR
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 5: Selecionando vendedor      ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.selecionarVendedor("Alex", "Alexandre Lessa (Fluxis)");
            log.info("✓ Vendedor OK\n");

            // ETAPA 6: UTILIZAÇÃO
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 6: Selecionando utilização    ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.selecionarUtilizacao("Venda de produto/mercadoria");
            log.info("✓ Utilização OK\n");

            // ETAPA 7: OPERAÇÃO
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 7: Selecionando operação      ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.selecionarOperacao("Consumidor final");
            log.info("✓ Operação OK\n");

            // ETAPA 8: TABELA PREÇO
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 8: Selecionando tabela      ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.selecionarTabelaPreco("TABELA PADRAO CF - 2025");
            log.info("✓ Tabela OK\n");

            // ETAPA 9: OBSERVAÇÃO
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 9: Preenchendo observação     ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.preencherObservacaoInterna("Teste automação - "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            log.info("✓ Observação OK\n");

            // ETAPA 10: OBSERVAÇÃO EXTERNA
            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ETAPA 10: Preenchendo observação     ║");
            log.info("╚═══════════════════════════════════════╝");
            comercialPage.preencherObservacaoExterna("Testando automação");
            log.info("✓ Observação OK\n");

            log.info("╔═══════════════════════════════════════╗");
            log.info("║  ✅✅✅ TESTE CONCLUÍDO ✅✅✅         ║");
            log.info("╚═══════════════════════════════════════╝");

            // Aguardar visualização
            comercialPage.aguardarSegundos(5, "Visualizando resultado");

        } catch (Exception e) {
            log.error("❌ TESTE FALHOU: {}", e.getMessage());
            comercialPage.aguardarSegundos(60, "Análise manual do erro");
            throw new RuntimeException("Teste falhou", e);
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                log.info("Aguardando antes de fechar o browser (para visualização)");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.warn("Interrupção durante aguardo final");
                Thread.currentThread().interrupt();
            }

            log.info("Fechando browser");
            driver.quit();
            log.info("Teardown concluído");
        }
    }
}
