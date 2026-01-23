package lessa.pages;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lessa.utils.AutocompleteHelper;
import lessa.utils.Select2Helper;

/**
 * Page Object para funcionalidades do módulo Comercial Gerencia navegação e
 * interação com telas de Proposta
 *
 * @author Alexandre Lessa
 * @version 2.0
 */
public class PropostaPage extends BasePages {

    private static final Logger log = LoggerFactory.getLogger(PropostaPage.class);

    // ========== DEPENDÊNCIAS ==========
    private final Actions actions;
    private final AutocompleteHelper autocompleteHelper;
    private final Select2Helper select2Helper;

    // ========== LOCATORS – LOGIN ==========
    private final By campoUsuario = By.id("username");
    private final By campoSenha = By.id("password");
    private final By botaoEntrar = By.cssSelector("button[type='submit'], input[type='submit']");

    // ========== LOCATORS – MENU HIERÁRQUICO ==========
    private final By menuComercial = By.id("ThemeOffice_Comercial");
    private final By menuProposta = By.id("cmSubMenuID19_Proposta");
    private final By submenuPropostaContainer = By.id("cmSubMenuID35");
    private final By menuCadastro = By.id("cmSubMenuID35_Cadastro");

    // ========== LOCATORS – IFRAME (DINÂMICO) ==========
    /**
     * Iframe com ID/Name dinâmico seguindo o padrão: window_TIMESTAMP_content
     * Exemplo: window_1763396377552_content
     *
     * Estratégia: Localizar por src (proposta.do) que é fixo
     */
    private By obterLocatorIframeDinamico() {
        // ESTRATÉGIA PRINCIPAL: Por src (mais confiável)
        return By.xpath("//iframe[contains(@src, 'proposta.do')]");
    }

    // Estratégia alternativa (caso src mude)
    private By obterLocatorIframePorPattern() {
        return By.xpath("//iframe[contains(@id, 'window_') and contains(@id, '_content')]");
    }

    // ========== LOCATORS – IFRAME ASSINATURA DIGITAL (DINÂMICO) ==========
    /**
     * Iframe de assinatura com ID/Name dinâmico: window_TIMESTAMP_content
     * Exemplo: window_1764175950552_content
     * Estratégia: Localizar por src (assinatura.do)
     */
    private By obterLocatorIframeAssinatura() {
        return By.xpath("//iframe[contains(@src, 'assinatura.do')]");
    }

    // ========== LOCATORS - IFRAME DE CONDIÇÕES (NOVO) ==========
    private final By iframeCondicoesProposta = By.xpath("//iframe[contains(@src, 'showCondicoesProposta=true')]");
    // Locator mais robusto para o botão OK (não depende do ID específico)
    private final By botaoOkCondicao = By.xpath("//input[@type='button' and @value='OK' and @onclick='closeWindow()']");

    // ========== LOCATORS – SWEETALERT2 (POPUP) ==========
    private final By swalPopup = By.cssSelector(".swal2-popup.swal2-show");
    private final By swalBotaoOk = By.cssSelector("button.swal2-confirm");
    private final By swalTitulo = By.id("swal2-title");
    private final By swalMensagem = By.id("swal2-html-container");

    // ========== LOCATORS - SELECT2 CONDIÇÃO (NOVO) ==========
    // ID do select original que o Select2 transforma
    private final By campoCondicaoPropostaSelect = By.id("idCondicao"); // Ajuste se o ID for diferente
    private final By campoCondicaoPropostaSpan = By.id("select2-chosen-1"); // Span que exibe o texto selecionado

    // ========== LOCATORS – CADASTRO DE PROPOSTA ==========
    private final By campoPrazo = By.id("prazoDias");
    private final By campoCliente = By.id("cliente");
    private final By campoVendedor = By.id("representante");
    private final By campoUtilizacao = By.id("utilizacao");
    private final By campoObservacaoInterna = By.id("observacaoInterna");
    private final By campoObservacaoExterna = By.id("observacaoExterna");
    private final By campoFrete = By.id("freight");
    private final By botaoSalvar = By.xpath("//input[@type='submit' and @value='Salvar']");
    // private final By campoIdProposta = By.id("id");
    private final By campoIdProposta = By.cssSelector("input[name='id']");
    private final By campoComponente = By.id("componente");
    private final By campoTabelaPrecoComponente = By.id("idTabelaPrecoComponente");
    private final By campoQuantidade = By.id("componentAmount");
    private final By botaoAdicionarComponente = By.id("pAddComponent");
    private final By campoServico = By.id("servico");

    // ========== LOCATORS – ABAS (TABS) ==========
    private final By abaComponente = By.xpath("//div[contains(text(), 'Componente')]");
    private final By abaServico = By.xpath("//div[contains(text(), 'Serviço')]");

    // ========== LOCATORS – CADASTRO DE SERVIÇO ==========
    private final By campoServicoQuantidade = By.id("serviceAmount");
    private final By campoServicoPreco = By.id("servicePrice");
    private final By botaoAdicionarServico = By.id("addService");

    // Campos Select2 (clique para abrir dropdown)
    private final By campoTipoPagamentoContainer = By.id("s2id_tipoPagamento");
    private final By campoTipoPagamentoSpan = By.id("select2-chosen-62");
    private final By campoTipoPagamentoSelect = By.id("tipoPagamento");

    private final By campoContaCorrenteContainer = By.id("s2id_contaCorrente");
    private final By campoContaCorrenteSpan = By.id("select2-chosen-63");
    private final By campoContaCorrenteSelect = By.id("contaCorrente");

    private final By campoCondicaoRecebimentoContainer = By.id("s2id_condicao");
    private final By campoCondicaoRecebimentoSpan = By.id("select2-chosen-64");
    private final By campoCondicaoRecebimentoSelect = By.id("condicao");

    // ========================================================================
    // SEÇÃO ADICIONAL: LOCATORS - ABA CONDIÇÕES E AUTORIZAÇÃO
    // ========================================================================

    // ========== LOCATORS – ABA CONDIÇÕES (Complemento) ==========
    private final By abaCondicoes = By.xpath("//div[contains(text(), 'Condições')]");

    // ========== LOCATORS – AUTORIZAÇÃO ==========
    private final By botaoAutorizar = By.xpath("//input[@type='submit' and @value='Autorizar']");

    private final By botaoAdicionarCondicao = By.id("addCondicao");

    // Locator dinâmico para resultados do Select2
    private final String select2ResultXpath = "//div[contains(@class, 'select2-result-label') and text()='%s']";

    // ========== LOCATORS – FORMULÁRIO ASSINATURA ==========
    private final By campoLoginAssinatura = By.id("login");
    private final By campoSenhaAssinatura = By.id("password");
    private final By botaoOkAssinatura = By.id("btnOk");

    // ========== LOCATORS – POPUP DECISÃO (GERAR PEDIDO) ==========
    private final By popupDecisaoTitulo = By.id("swal2-title");
    private final By popupDecisaoMensagem = By.id("swal2-html-container");
    private final By botaoOkDecisao = By.cssSelector("button.swal2-confirm");
    private final By botaoCancelarDecisao = By.cssSelector("button.swal2-cancel");

    // ========== LOCATORS – JANELA CONDIÇÕES DA PROPOSTA ==========
    private final By janelaCondicoesTitulo = By.id("window_1764176229657_top");
    private final By campoCondicaoProposta = By.id("idCondicao");
    private final By campoCondicaoPropostaContainer = By.id("s2id_idCondicao");
    private final By botaoOkCondicoesProposta = By.id("xinput834");

    private final By popupMensagem = By.id("swal2-html-container");

    // ========== CONSTRUTOR ==========
    public PropostaPage(WebDriver driver) {
        super(driver);
        this.actions = new Actions(driver);
        this.autocompleteHelper = new AutocompleteHelper(driver);
        this.select2Helper = new Select2Helper(driver);
        log.info("ComercialPage inicializada");

    }

    // ========================================================================
    // SEÇÃO 1: AUTENTICAÇÃO
    // ========================================================================
    /**
     * Realiza o login completo no sistema
     *
     * @param url     URL do sistema
     * @param usuario Nome de usuário
     * @param senha   Senha do usuário
     * @return true se login bem-sucedido, false caso contrário
     */
    public boolean realizarLoginCompleto(String url, String usuario, String senha) {
        try {
            log.info("Acessando URL: {}", url);
            driver.get(url);

            aguardarSegundos(2, "Aguardando página de login carregar");

            log.info("Preenchendo usuário: {}", usuario);
            WebElement campoUser = wait.until(ExpectedConditions.presenceOfElementLocated(campoUsuario));
            campoUser.clear();
            campoUser.sendKeys(usuario);

            log.info("Preenchendo senha");
            WebElement campoPass = driver.findElement(campoSenha);
            campoPass.clear();
            campoPass.sendKeys(senha);

            log.info("Clicando em Entrar");
            WebElement btnEntrar = driver.findElement(botaoEntrar);
            btnEntrar.click();

            aguardarSegundos(3, "Aguardando login ser processado");

            String urlAtual = driver.getCurrentUrl();
            log.info("URL após login: {}", urlAtual);

            boolean loginSucesso = !urlAtual.contains("login.do");

            if (loginSucesso) {
                log.info("✓ Login realizado com sucesso!");
            } else {
                log.error("✗ Login falhou - ainda na página de login");
            }

            return loginSucesso;

        } catch (Exception e) {
            log.error("Erro ao realizar login: {}", e.getMessage(), e);
            return false;
        }
    }

    // ========================================================================
    // SEÇÃO 2: NAVEGAÇÃO NO MENU
    // ========================================================================
    /**
     * Acessa a tela de cadastro de proposta navegando pelo menu hierárquico
     * Sequência: Comercial > Proposta > Cadastro
     */
    public void acessarTelaCadastro() {
        try {
            log.info("=== Iniciando navegação no menu hierárquico ===");

            // PASSO 1: Clicar em "Comercial" para abrir o primeiro nível
            abrirMenuComercial();

            // PASSO 2: Passar o mouse em "Proposta" para abrir o submenu
            abrirSubmenuProposta();

            // PASSO 3: Clicar em "Cadastro" no submenu
            clicarCadastro();

            // PASSO 4: Aguardar a tela de cadastro carregar
            aguardarTelaCadastroCarregar();

            log.info("✓ Tela de cadastro acessada com sucesso");

        } catch (Exception e) {
            log.error("Erro ao acessar tela de cadastro: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao acessar tela de cadastro", e);
        }
    }

    /**
     * PASSO 1: Abre o menu Comercial
     */
    private void abrirMenuComercial() throws InterruptedException {
        log.info("PASSO 1: Abrindo menu Comercial");

        // ✅ VERIFICAÇÃO DEFENSIVA - URL válida?
        String urlAtual = driver.getCurrentUrl();
        if (urlAtual.equals("about:blank") || urlAtual.contains("login.do")) {
            throw new RuntimeException(
                    "❌ ERRO PRÉ-CONDIÇÃO: Login não foi realizado! URL atual: " + urlAtual);
        }

        WebElement menuCom = wait.until(ExpectedConditions.presenceOfElementLocated(menuComercial));
        wait.until(ExpectedConditions.visibilityOf(menuCom));

        destacarElemento(menuCom);

        // ESTRATÉGIA 1: Tentar click direto primeiro
        boolean sucesso = false;
        try {
            log.debug("Tentando click direto no menu Comercial");
            menuCom.click();
            Thread.sleep(1000);
            sucesso = true;
        } catch (Exception e) {
            log.debug("Click direto falhou: {}", e.getMessage());
        }

        // ESTRATÉGIA 2: Click via JavaScript
        if (!sucesso) {
            try {
                log.debug("Tentando click via JavaScript");
                js.executeScript("arguments[0].click();", menuCom);
                Thread.sleep(1000);
                sucesso = true;
            } catch (Exception e) {
                log.debug("Click JS falhou: {}", e.getMessage());
            }
        }

        // ESTRATÉGIA 3: Simular o evento MouseUp manualmente
        if (!sucesso) {
            try {
                log.debug("Tentando simular evento MouseUp");
                String onmouseup = menuCom.getAttribute("onmouseup");
                log.debug("Atributo onmouseup: {}", onmouseup);

                if (onmouseup != null && onmouseup.contains("cmItemMouseUp")) {
                    js.executeScript(
                            "var menuItem = arguments[0].parentElement;"
                                    + "var submenuId = 'cmSubMenuID83';"
                                    + "var submenu = document.getElementById(submenuId);"
                                    + "if (submenu) {"
                                    + "  submenu.style.visibility = 'visible';"
                                    + "  submenu.style.display = 'block';"
                                    + "  submenu.style.left = menuItem.offsetLeft + 'px';"
                                    + "  submenu.style.top = (menuItem.offsetTop + menuItem.offsetHeight) + 'px';"
                                    + "}",
                            menuCom);
                    Thread.sleep(1000);
                    sucesso = true;
                }
            } catch (Exception e) {
                log.debug("Simulação de MouseUp falhou: {}", e.getMessage());
            }
        }

        // ESTRATÉGIA 4: Hover usando Actions
        if (!sucesso) {
            try {
                log.debug("Tentando hover com Actions");
                actions.moveToElement(menuCom).click().perform();
                Thread.sleep(1000);
                sucesso = true;
            } catch (Exception e) {
                log.debug("Hover falhou: {}", e.getMessage());
            }
        }

        if (!sucesso) {
            log.error("Todas as estratégias para abrir o menu Comercial falharam");
            throw new RuntimeException("Não foi possível abrir o menu Comercial");
        }

        log.info("✓ Menu Comercial aberto");
    }

    /**
     * PASSO 2: Passa o mouse em "Proposta" para abrir o submenu
     */
    private void abrirSubmenuProposta() throws InterruptedException {
        log.info("PASSO 2: Abrindo submenu Proposta");

        WebElement menuProp = wait.until(ExpectedConditions.presenceOfElementLocated(menuProposta));

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", menuProp);
        Thread.sleep(300);

        destacarElemento(menuProp);

        WebElement trPai = menuProp.findElement(By.xpath("./ancestor::tr[@class='ThemeOfficeMenuItem']"));

        String onmouseoverAttr = trPai.getAttribute("onmouseover");
        log.debug("onmouseover do Proposta: {}", onmouseoverAttr);

        try {
            log.debug("Tentando executar onmouseover via JS");
            js.executeScript(onmouseoverAttr);
            Thread.sleep(1000);
        } catch (Exception e) {
            log.debug("Execução direta falhou: {}", e.getMessage());
        }

        log.debug("Simulando hover com Actions");
        actions.moveToElement(trPai).perform();
        Thread.sleep(1000);

        log.debug("Forçando visibilidade do submenu");
        js.executeScript(
                "var submenu = document.getElementById('cmSubMenuID35');"
                        + "if (submenu) {"
                        + "  submenu.style.visibility = 'visible';"
                        + "  submenu.style.display = 'block';"
                        + "}");
        Thread.sleep(500);

        WebElement submenu = wait.until(ExpectedConditions.presenceOfElementLocated(submenuPropostaContainer));
        String visibility = submenu.getCssValue("visibility");
        String display = submenu.getCssValue("display");

        log.debug("Submenu visibility: {}, display: {}", visibility, display);

        if ("hidden".equals(visibility)) {
            log.warn("Submenu ainda oculto, forçando novamente");
            js.executeScript("arguments[0].style.visibility = 'visible';", submenu);
        }

        Thread.sleep(500);
        log.info("✓ Submenu Proposta aberto");
    }

    /**
     * PASSO 3: Clica em "Cadastro" no submenu
     */
    private void clicarCadastro() throws InterruptedException {
        log.info("PASSO 3: Clicando em Cadastro");

        WebElement menuCad = wait.until(ExpectedConditions.presenceOfElementLocated(menuCadastro));

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", menuCad);
        Thread.sleep(500);

        destacarElemento(menuCad);

        // ESTRATÉGIA 1: Click direto
        boolean sucesso = false;
        try {
            log.debug("Tentando click direto em Cadastro");
            wait.until(ExpectedConditions.elementToBeClickable(menuCad));
            menuCad.click();
            Thread.sleep(1500);
            sucesso = true;
        } catch (Exception e) {
            log.debug("Click direto falhou: {}", e.getMessage());
        }

        // ESTRATÉGIA 2: Click via JavaScript
        if (!sucesso) {
            try {
                log.debug("Tentando click via JavaScript");
                js.executeScript("arguments[0].click();", menuCad);
                Thread.sleep(1500);
                sucesso = true;
            } catch (Exception e) {
                log.debug("Click JS falhou: {}", e.getMessage());
            }
        }

        // ESTRATÉGIA 3: Actions click
        if (!sucesso) {
            try {
                log.debug("Tentando click com Actions");
                actions.moveToElement(menuCad).click().perform();
                Thread.sleep(1500);
                sucesso = true;
            } catch (Exception e) {
                log.debug("Actions click falhou: {}", e.getMessage());
            }
        }

        if (!sucesso) {
            log.warn("Todas as tentativas de click falharam");
            throw new RuntimeException("Não foi possível clicar em Cadastro");
        }

        log.info("✓ Cadastro clicado");
    }

    // ========================================================================
    // SEÇÃO 3: GESTÃO DE CONTEXTOS (POPUP/IFRAME) - REFATORADO ✨
    // ========================================================================
    /**
     * PASSO 4: Aguarda a tela de cadastro carregar ESTRATÉGIA: Detecta
     * automaticamente se é popup ou iframe
     */
    private void aguardarTelaCadastroCarregar() {
        log.info("PASSO 4: Aguardando tela de cadastro carregar");

        try {
            // Aguardar um pouco para o sistema processar o click
            Thread.sleep(2000);

            // ESTRATÉGIA 1: Verificar se abriu uma nova janela (popup)
            if (detectarNovaJanela()) {
                log.info("✓ Detectado: Sistema abriu POPUP");
                trocarParaNovaJanela();

                // Aguardar página carregar no popup
                aguardarPaginaCarregar();

                // Dentro do popup, verificar se tem iframe
                log.info("Verificando se há iframe dentro do popup...");
                if (detectarIframeDinamico()) {
                    log.info("✓ Detectado: Iframe dentro do popup");
                    trocarParaIframeProposta();
                }
            } // ESTRATÉGIA 2: Verificar se carregou em iframe na mesma janela
            else if (detectarIframeDinamico()) {
                log.info("✓ Detectado: Sistema carregou em IFRAME dinâmico");
                trocarParaIframeProposta();
            } // ESTRATÉGIA 3: Carregou na mesma página (sem popup/iframe)
            else {
                log.info("✓ Detectado: Sistema carregou na mesma página");
            }

            // VALIDAÇÃO CRÍTICA: Aguardar o campo estar presente
            log.info("Aguardando campo 'prazoDias' estar disponível...");

            // Aumentar timeout para 30 segundos (ERP pode ser lento)
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

            WebElement campoPrazoElement = longWait.until(
                    ExpectedConditions.presenceOfElementLocated(campoPrazo));

            log.debug("✓ Campo 'prazoDias' encontrado no DOM");

            // Aguardar estar visível
            longWait.until(ExpectedConditions.visibilityOf(campoPrazoElement));
            log.debug("✓ Campo 'prazoDias' visível");

            // Aguardar estar habilitado
            longWait.until(ExpectedConditions.elementToBeClickable(campoPrazoElement));
            log.debug("✓ Campo 'prazoDias' habilitado");

            log.info("✓ Tela de cadastro carregada com sucesso");

        } catch (Exception e) {
            log.error("❌ Erro ao aguardar tela de cadastro: {}", e.getMessage());

            // DEBUG INTENSIVO
            log.error("========== DEBUG DETALHADO ==========");
            imprimirContextoAtual();
            listarIframesNaPagina();

            // Tentar voltar ao contexto principal e listar novamente
            try {
                log.debug("Tentando voltar ao contexto principal...");
                driver.switchTo().defaultContent();
                log.debug("✓ Voltou para contexto principal");

                log.debug("Listando iframes após voltar ao contexto principal:");
                listarIframesNaPagina();

                // Tentar localizar o iframe novamente com informações extras
                try {
                    By locator = obterLocatorIframeDinamico();
                    WebElement iframe = driver.findElement(locator);
                    log.info("⚠ O IFRAME EXISTE mas não foi detectado automaticamente!");
                    log.info("  Forçando troca para o iframe...");
                    driver.switchTo().frame(iframe);
                    aguardarPaginaCarregar();
                    Thread.sleep(1000);
                    log.info("✓ Troca forçada concluída");

                    // Tentar localizar o campo novamente
                    WebElement campo = driver.findElement(campoPrazo);
                    log.info("✓✓✓ CAMPO ENCONTRADO após troca forçada!");

                } catch (Exception e3) {
                    log.error("Campo não encontrado mesmo após troca forçada");
                }

            } catch (Exception e2) {
                log.error("Não foi possível executar debug avançado: {}", e2.getMessage());
            }

            log.error("====================================");

            throw new RuntimeException("Falha ao carregar tela de cadastro", e);
        }
    }

    /**
     * Detecta se uma nova janela foi aberta
     *
     * @return true se uma nova janela foi aberta, false caso contrário
     */
    private boolean detectarNovaJanela() {
        try {
            log.debug("Verificando se abriu nova janela...");

            // Aguardar até 5 segundos para ver se abre nova janela
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            Boolean novaJanelaAbriu = shortWait.until(wd -> wd.getWindowHandles().size() > 1);

            log.debug("Número de janelas abertas: {}", driver.getWindowHandles().size());

            return novaJanelaAbriu != null && novaJanelaAbriu;

        } catch (Exception e) {
            log.debug("Nenhuma nova janela detectada");
            return false;
        }
    }

    /**
     * Detecta se um iframe dinâmico existe e está disponível
     *
     * @return true se o iframe existe, false caso contrário
     */
    private boolean detectarIframeDinamico() {
        try {
            log.debug("Verificando se existe iframe dinâmico (proposta.do)...");

            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Tentar localizar por src primeiro (mais confiável)
            By locatorDinamico = obterLocatorIframeDinamico();

            WebElement iframe = shortWait.until(
                    ExpectedConditions.presenceOfElementLocated(locatorDinamico));

            String iframeId = iframe.getAttribute("id");
            String iframeName = iframe.getAttribute("name");
            String iframeSrc = iframe.getAttribute("src");

            log.debug("✓ Iframe dinâmico encontrado:");
            log.debug("  - ID: {}", iframeId);
            log.debug("  - Name: {}", iframeName);
            log.debug("  - Src: {}", iframeSrc);

            return true;

        } catch (Exception e) {
            log.debug("Iframe dinâmico não encontrado: {}", e.getMessage());

            // Tentar estratégia alternativa por pattern do ID
            try {
                log.debug("Tentando localizar por pattern do ID...");
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

                By locatorPorPattern = obterLocatorIframePorPattern();
                WebElement iframe = shortWait.until(
                        ExpectedConditions.presenceOfElementLocated(locatorPorPattern));

                log.debug("✓ Iframe encontrado por pattern: {}", iframe.getAttribute("id"));
                return true;

            } catch (Exception e2) {
                log.debug("Nenhum iframe detectado por nenhuma estratégia");
                return false;
            }
        }
    }

    /**
     * Troca para a nova janela/popup que foi aberta
     *
     * @param timeoutSegundos Tempo máximo para aguardar a janela abrir
     */
    private void trocarParaNovaJanela(int timeoutSegundos) {
        try {
            log.info("Aguardando nova janela ser aberta (timeout: {}s)...", timeoutSegundos);

            // Guarda a janela original
            String janelaOriginal = driver.getWindowHandle();
            log.debug("Janela original: {}", janelaOriginal);

            // Aguarda até que existam pelo menos 2 janelas
            WebDriverWait windowWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSegundos));
            windowWait.until(wd -> wd.getWindowHandles().size() > 1);

            // Pega todas as janelas abertas
            Set<String> todasJanelas = driver.getWindowHandles();
            log.debug("Total de janelas abertas: {}", todasJanelas.size());

            // Troca para a nova janela (que não é a original)
            for (String janela : todasJanelas) {
                if (!janela.equals(janelaOriginal)) {
                    driver.switchTo().window(janela);
                    log.info("✓ Foco trocado para nova janela: {}", janela);

                    // Aguardar a janela carregar
                    aguardarPaginaCarregar();

                    break;
                }
            }

        } catch (Exception e) {
            log.error("Erro ao trocar para nova janela: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao trocar para nova janela", e);
        }
    }

    /**
     * Sobrecarga: Troca para nova janela com timeout padrão de 10s
     */
    private void trocarParaNovaJanela() {
        trocarParaNovaJanela(10);
    }

    /**
     * Troca para o iframe correto onde estão os campos do cadastro Lida com
     * iframes aninhados (iframe dentro de iframe)
     */
    private void trocarParaIframeProposta() {
        try {
            log.info("========================================");
            log.info("Iniciando busca por iframes aninhados...");
            log.info("========================================");

            // 1. Voltar para o contexto principal
            driver.switchTo().defaultContent();
            log.info("✓ Voltou para contexto principal (defaultContent)");

            // 2. Identificar todos os iframes disponíveis no nível raiz
            List<WebElement> iframesRaiz = driver.findElements(By.tagName("iframe"));
            log.info("Total de iframes no nível raiz: {}", iframesRaiz.size());

            // 3. Procurar pelo iframe que contém 'proposta.do'
            WebElement iframeProposta = null;
            boolean encontrouEmNivelRaiz = false;

            // Tentar encontrar no nível raiz primeiro
            for (int i = 0; i < iframesRaiz.size(); i++) {
                WebElement iframe = iframesRaiz.get(i);
                String src = iframe.getAttribute("src");
                String id = iframe.getAttribute("id");
                String name = iframe.getAttribute("name");

                log.info("Iframe [{}] - src: {}, id: {}, name: {}", i, src, id, name);

                if (src != null && src.contains("proposta.do")) {
                    log.info("✓ Iframe 'proposta.do' encontrado no nível raiz!");
                    iframeProposta = iframe;
                    encontrouEmNivelRaiz = true;
                    break;
                }
            }

            // 4. Se não encontrou no nível raiz, procurar dentro de cada iframe (aninhado)
            if (!encontrouEmNivelRaiz) {
                log.warn("Iframe 'proposta.do' NÃO encontrado no nível raiz");
                log.info("Procurando em iframes aninhados...");

                for (int i = 0; i < iframesRaiz.size(); i++) {
                    try {
                        // Entrar no iframe pai
                        driver.switchTo().defaultContent();
                        driver.switchTo().frame(iframesRaiz.get(i));

                        log.info("Dentro do iframe [{}], procurando iframes aninhados...", i);

                        // Aguardar um pouco para o conteúdo carregar
                        Thread.sleep(1000);

                        // Procurar iframes dentro deste iframe
                        List<WebElement> iframesAninhados = driver.findElements(By.tagName("iframe"));
                        log.info("  → Iframes aninhados encontrados: {}", iframesAninhados.size());

                        for (int j = 0; j < iframesAninhados.size(); j++) {
                            WebElement iframeAninhado = iframesAninhados.get(j);
                            String src = iframeAninhado.getAttribute("src");
                            String id = iframeAninhado.getAttribute("id");
                            String name = iframeAninhado.getAttribute("name");

                            log.info("  → Iframe aninhado [{}] - src: {}, id: {}, name: {}", j, src, id, name);

                            if (src != null && src.contains("proposta.do")) {
                                log.info("✓✓✓ IFRAME 'proposta.do' ENCONTRADO COMO ANINHADO! ✓✓✓");
                                iframeProposta = iframeAninhado;
                                break;
                            }
                        }

                        if (iframeProposta != null) {
                            break; // Encontrou, sair do loop
                        }

                    } catch (Exception e) {
                        log.debug("Erro ao explorar iframe [{}]: {}", i, e.getMessage());
                        driver.switchTo().defaultContent(); // Voltar ao contexto principal
                    }
                }
            }

            // 5. Validar se encontrou o iframe
            if (iframeProposta == null) {
                log.error("❌ IFRAME 'proposta.do' NÃO ENCONTRADO!");
                imprimirContextoAtual();
                throw new RuntimeException("Não foi possível localizar o iframe da proposta");
            }

            // 6. Entrar no iframe correto
            log.info("Entrando no iframe 'proposta.do'...");
            driver.switchTo().frame(iframeProposta);
            log.info("✓ Dentro do iframe 'proposta.do'");

            // 7. Aguardar conteúdo carregar
            aguardarIframeCarregarComConteudo();

            // 8. VALIDAÇÃO FINAL: Verificar se o campo prazoDias está presente
            log.info("Validando presença do campo 'prazoDias'...");
            try {
                WebDriverWait validacao = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement campoPrazoValidacao = validacao.until(
                        ExpectedConditions.presenceOfElementLocated(By.id("prazoDias")));
                log.info("✓✓✓ CAMPO 'prazoDias' ENCONTRADO E PRONTO! ✓✓✓");

                // Log de debug
                log.debug("Campo - Visível: {}, Habilitado: {}",
                        campoPrazoValidacao.isDisplayed(),
                        campoPrazoValidacao.isEnabled());

            } catch (Exception e) {
                log.error("❌ Campo 'prazoDias' NÃO encontrado mesmo após entrar no iframe!");
                log.error("URL atual: {}", driver.getCurrentUrl());

                // Listar todos os inputs disponíveis
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                log.error("Inputs disponíveis no iframe (total: {}):", inputs.size());
                for (int i = 0; i < Math.min(inputs.size(), 10); i++) {
                    WebElement input = inputs.get(i);
                    log.error("  Input [{}] - id: {}, name: {}, type: {}",
                            i,
                            input.getAttribute("id"),
                            input.getAttribute("name"),
                            input.getAttribute("type"));
                }

                throw new RuntimeException("Campo 'prazoDias' não encontrado após trocar para iframe", e);
            }

            log.info("========================================");
            log.info("✓ Iframe configurado com sucesso!");
            log.info("========================================");

        } catch (Exception e) {
            log.error("Erro fatal ao trocar para iframe: {}", e.getMessage(), e);
            driver.switchTo().defaultContent();
            throw new RuntimeException("Falha ao configurar iframe da proposta", e);
        }
    }

    /**
     * Aguarda o iframe carregar completamente com conteúdo validável
     */
    private void aguardarIframeCarregarComConteudo() throws InterruptedException {
        log.info("Aguardando conteúdo do iframe carregar...");

        // 1. Aguardar document.readyState === 'complete'
        try {
            wait.until((ExpectedCondition<Boolean>) wd -> js.executeScript("return document.readyState")
                    .equals("complete"));
            log.debug("✓ document.readyState = complete");
        } catch (Exception e) {
            log.warn("Não foi possível verificar document.readyState");
        }

        // 2. Aguardar jQuery (se existir)
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(wd -> {
                Boolean jQueryDefined = (Boolean) js.executeScript("return typeof jQuery != 'undefined'");
                if (jQueryDefined) {
                    Long activeConnections = (Long) js.executeScript("return jQuery.active");
                    return activeConnections == 0;
                }
                return true;
            });
            log.debug("✓ jQuery.active = 0");
        } catch (Exception e) {
            log.debug("jQuery não disponível");
        }

        // 3. Aguardar estabilização (importante para iframes)
        Thread.sleep(2000);

        // 4. Validar presença de elementos
        try {
            List<WebElement> inputs = driver.findElements(By.tagName("input"));
            log.info("✓ Total de campos <input> no iframe: {}", inputs.size());

            if (inputs.isEmpty()) {
                log.warn("⚠ AVISO: Nenhum input encontrado! Iframe pode não ter carregado");
                Thread.sleep(3000); // Aguardar mais
            }

        } catch (Exception e) {
            log.error("Erro ao validar conteúdo do iframe: {}", e.getMessage());
        }

        log.info("✓ Conteúdo do iframe validado");
    }

    /**
     * Volta para o contexto principal (fora dos iframes)
     */
    public void voltarDoIframe() {
        driver.switchTo().defaultContent();
        log.debug("Voltou para contexto principal");
    }

    private void aguardarTelaAssinaturaCarregar() {
        log.info("PASSO 4: Aguardando tela de assinatura carregar");

        try {
            // Aguardar um pouco para o sistema processar o click
            Thread.sleep(2000);

            // ESTRATÉGIA 1: Verificar se abriu uma nova janela (popup)
            if (detectarNovaJanela()) {
                log.info("✓ Detectado: Sistema abriu POPUP");
                trocarParaNovaJanela();

                // Aguardar página carregar no popup
                aguardarPaginaCarregar();

                // Dentro do popup, verificar se tem iframe
                log.info("Verificando se há iframe dentro do popup...");
                if (detectarIframeDinamico()) {
                    log.info("✓ Detectado: Iframe dentro do popup");
                    trocarParaIframeAssinatura();
                }
            } // ESTRATÉGIA 2: Verificar se carregou em iframe na mesma janela
            else if (detectarIframeDinamico()) {
                log.info("✓ Detectado: Sistema carregou em IFRAME dinâmico");
                trocarParaIframeAssinatura();
            } // ESTRATÉGIA 3: Carregou na mesma página (sem popup/iframe)
            else {
                log.info("✓ Detectado: Sistema carregou na mesma página");
            }

            // VALIDAÇÃO CRÍTICA: Aguardar o campo estar presente
            log.info("Aguardando campo 'senha' estar disponível...");

            // Aumentar timeout para 30 segundos (ERP pode ser lento)
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

            WebElement campoSenhaElement = longWait.until(
                    ExpectedConditions.presenceOfElementLocated(campoSenha));

            log.debug("✓ Campo 'senha' encontrado no DOM");

            // Aguardar estar visível
            longWait.until(ExpectedConditions.visibilityOf(campoSenhaElement));
            log.debug("✓ Campo 'senha' visível");

            // Aguardar estar habilitado
            longWait.until(ExpectedConditions.elementToBeClickable(campoSenhaElement));
            log.debug("✓ Campo 'senha' habilitado");

            log.info("✓ Tela de assinatura carregada com sucesso");

        } catch (Exception e) {
            log.error("❌ Erro ao aguardar tela de assinatura: {}", e.getMessage());

            // DEBUG INTENSIVO
            log.error("========== DEBUG DETALHADO ==========");
            imprimirContextoAtual();
            listarIframesNaPagina();

            // Tentar voltar ao contexto principal e listar novamente
            try {
                log.debug("Tentando voltar ao contexto principal...");
                driver.switchTo().defaultContent();
                log.debug("✓ Voltou para contexto principal");

                log.debug("Listando iframes após voltar ao contexto principal:");
                listarIframesNaPagina();

                // Tentar localizar o iframe novamente com informações extras
                try {
                    By locator = obterLocatorIframeDinamico();
                    WebElement iframe = driver.findElement(locator);
                    log.info("⚠ O IFRAME EXISTE mas não foi detectado automaticamente!");
                    log.info("  Forçando troca para o iframe...");
                    driver.switchTo().frame(iframe);
                    aguardarPaginaCarregar();
                    Thread.sleep(1000);
                    log.info("✓ Troca forçada concluída");

                    // Tentar localizar o campo novamente
                    WebElement campo = driver.findElement(campoSenha);
                    log.info("✓✓✓ CAMPO ENCONTRADO após troca forçada!");

                } catch (Exception e3) {
                    log.error("Campo não encontrado mesmo após troca forçada");
                }

            } catch (Exception e2) {
                log.error("Não foi possível executar debug avançado: {}", e2.getMessage());
            }

            log.error("====================================");

            throw new RuntimeException("Falha ao carregar tela de assinatura", e);
        }
    }

    /**
     * Troca para o iframe correto onde estão os campos do cadastro Lida com
     * iframes aninhados (iframe dentro de iframe)
     */
    private void trocarParaIframeAssinatura() {
        try {
            log.info("========================================");
            log.info("Iniciando busca por iframes aninhados...");
            log.info("========================================");

            // 1. Voltar para o contexto principal
            driver.switchTo().defaultContent();
            log.info("✓ Voltou para contexto principal (defaultContent)");

            // 2. Identificar todos os iframes disponíveis no nível raiz
            List<WebElement> iframesRaiz = driver.findElements(By.tagName("iframe"));
            log.info("Total de iframes no nível raiz: {}", iframesRaiz.size());

            // 3. Procurar pelo iframe que contém 'proposta.do'
            WebElement iframeAssinatura = null;
            boolean encontrouEmNivelRaiz = false;

            // Tentar encontrar no nível raiz primeiro
            for (int i = 0; i < iframesRaiz.size(); i++) {
                WebElement iframe = iframesRaiz.get(i);
                String src = iframe.getAttribute("src");
                String id = iframe.getAttribute("id");
                String name = iframe.getAttribute("name");

                log.info("Iframe [{}] - src: {}, id: {}, name: {}", i, src, id, name);

                if (src != null && src.contains("assinatura.do")) {
                    log.info("✓ Iframe 'assinatura.do' encontrado no nível raiz!");
                    iframeAssinatura = iframe;
                    encontrouEmNivelRaiz = true;
                    break;
                }
            }

            // 4. Se não encontrou no nível raiz, procurar dentro de cada iframe (aninhado)
            if (!encontrouEmNivelRaiz) {
                log.warn("Iframe 'assinatura.do' NÃO encontrado no nível raiz");
                log.info("Procurando em iframes aninhados...");

                for (int i = 0; i < iframesRaiz.size(); i++) {
                    try {
                        // Entrar no iframe pai
                        driver.switchTo().defaultContent();
                        driver.switchTo().frame(iframesRaiz.get(i));

                        log.info("Dentro do iframe [{}], procurando iframes aninhados...", i);

                        // Aguardar um pouco para o conteúdo carregar
                        Thread.sleep(1000);

                        // Procurar iframes dentro deste iframe
                        List<WebElement> iframesAninhados = driver.findElements(By.tagName("iframe"));
                        log.info("  → Iframes aninhados encontrados: {}", iframesAninhados.size());

                        for (int j = 0; j < iframesAninhados.size(); j++) {
                            WebElement iframeAninhado = iframesAninhados.get(j);
                            String src = iframeAninhado.getAttribute("src");
                            String id = iframeAninhado.getAttribute("id");
                            String name = iframeAninhado.getAttribute("name");

                            log.info("  → Iframe aninhado [{}] - src: {}, id: {}, name: {}", j, src, id, name);

                            if (src != null && src.contains("assinatura.do")) {
                                log.info("✓✓✓ IFRAME 'assinatura.do' ENCONTRADO COMO ANINHADO! ✓✓✓");
                                iframeAssinatura = iframeAninhado;
                                break;
                            }
                        }

                        if (iframeAssinatura != null) {
                            break; // Encontrou, sair do loop
                        }

                    } catch (Exception e) {
                        log.debug("Erro ao explorar iframe [{}]: {}", i, e.getMessage());
                        driver.switchTo().defaultContent(); // Voltar ao contexto principal
                    }
                }
            }

            // 5. Validar se encontrou o iframe
            if (iframeAssinatura == null) {
                log.error("❌ IFRAME 'assinatura.do' NÃO ENCONTRADO!");
                imprimirContextoAtual();
                throw new RuntimeException("Não foi possível localizar o iframe da assinatura");
            }

            // 6. Entrar no iframe correto
            log.info("Entrando no iframe 'assinatura.do'...");
            driver.switchTo().frame(iframeAssinatura);
            log.info("✓ Dentro do iframe 'assinatura.do'");

            // 7. Aguardar conteúdo carregar
            aguardarIframeCarregarComConteudo();

            // 8. VALIDAÇÃO FINAL: Verificar se o campo prazoDias está presente
            log.info("Validando presença do campo 'senha'...");
            try {
                WebDriverWait validacao = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement campoSenhaValidacao = validacao.until(
                        ExpectedConditions.presenceOfElementLocated(By.id("password")));
                log.info("✓✓✓ CAMPO 'senha' ENCONTRADO E PRONTO! ✓✓✓");

                // Log de debug
                log.debug("Campo - Visível: {}, Habilitado: {}",
                        campoSenhaValidacao.isDisplayed(),
                        campoSenhaValidacao.isEnabled());

            } catch (Exception e) {
                log.error("❌ Campo 'senha' NÃO encontrado mesmo após entrar no iframe!");
                log.error("URL atual: {}", driver.getCurrentUrl());

                // Listar todos os inputs disponíveis
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                log.error("Inputs disponíveis no iframe (total: {}):", inputs.size());
                for (int i = 0; i < Math.min(inputs.size(), 10); i++) {
                    WebElement input = inputs.get(i);
                    log.error("  Input [{}] - id: {}, name: {}, type: {}",
                            i,
                            input.getAttribute("id"),
                            input.getAttribute("name"),
                            input.getAttribute("type"));
                }

                throw new RuntimeException("Campo 'senha' não encontrado após trocar para iframe", e);
            }

            log.info("========================================");
            log.info("✓ Iframe configurado com sucesso!");
            log.info("========================================");

        } catch (Exception e) {
            log.error("Erro fatal ao trocar para iframe: {}", e.getMessage(), e);
            driver.switchTo().defaultContent();
            throw new RuntimeException("Falha ao configurar iframe da proposta", e);
        }
    }

    // ========================================================================
    // SEÇÃO 4: DEBUG E DIAGNÓSTICO ✨
    // ========================================================================
    /**
     * MÉTODO DE DEBUG: Imprime informações do contexto atual Útil para
     * diagnosticar problemas de frame/window
     */
    private void imprimirContextoAtual() {
        try {
            log.debug("========== DEBUG: CONTEXTO ATUAL ==========");
            log.debug("URL atual: {}", driver.getCurrentUrl());
            log.debug("Título da página: {}", driver.getTitle());
            log.debug("Número de janelas: {}", driver.getWindowHandles().size());

            // Tentar encontrar iframes
            try {
                int totalIframes = driver.findElements(By.tagName("iframe")).size();
                log.debug("Total de iframes na página: {}", totalIframes);

                if (totalIframes > 0) {
                    log.debug("IDs/Names dos iframes:");
                    driver.findElements(By.tagName("iframe")).forEach(iframe -> {
                        String id = iframe.getAttribute("id");
                        String name = iframe.getAttribute("name");
                        log.debug("  - ID: '{}', Name: '{}'", id, name);
                    });
                }
            } catch (Exception e) {
                log.debug("Não foi possível listar iframes");
            }

            log.debug("==========================================");

        } catch (Exception e) {
            log.debug("Erro ao imprimir contexto: {}", e.getMessage());
        }
    }

    /**
     * MÉTODO DE DEBUG: Lista todos os iframes da página Use temporariamente
     * para descobrir o locator correto do iframe
     */
    public void listarIframesNaPagina() {
        try {
            log.info("========== LISTANDO IFRAMES ==========");

            // Salvar se estava dentro de um iframe
            boolean estavaEmIframe = false;
            String iframeAtual = null;

            try {
                // Tentar obter URL do contexto atual
                String urlContexto = driver.getCurrentUrl();
                if (urlContexto.contains("assinatura.do")) {
                    estavaEmIframe = true;
                    log.debug("⚠ Estava dentro do iframe assinatura.do");
                }
            } catch (Exception e) {
                // Ignorar
            }

            // Voltar para contexto principal para listar
            driver.switchTo().defaultContent();
            log.debug("✓ Voltou para contexto principal para listar");

            java.util.List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            log.info("Total de iframes encontrados: {}", iframes.size());

            if (iframes.isEmpty()) {
                log.warn("⚠ NENHUM IFRAME encontrado");
            } else {
                for (int i = 0; i < iframes.size(); i++) {
                    WebElement iframe = iframes.get(i);
                    String id = iframe.getAttribute("id");
                    String name = iframe.getAttribute("name");
                    String src = iframe.getAttribute("src");

                    log.info("═══════════════════════════════════════");
                    log.info("Iframe #{}: ", i);
                    log.info("  - ID   : '{}'", id != null ? id : "SEM ID");
                    log.info("  - Name : '{}'", name != null ? name : "SEM NAME");
                    log.info("  - Src  : '{}'", src != null ? src : "SEM SRC");
                }
            }

            log.info("======================================");

            // ✅ RETORNAR AO IFRAME SE ESTAVA DENTRO
            if (estavaEmIframe) {
                log.info("⚠ Retornando ao iframe 'proposta.do'...");
                trocarParaIframeProposta();
                log.info("✓ Contexto restaurado para iframe 'proposta.do'");
            }

        } catch (Exception e) {
            log.error("Erro ao listar iframes: {}", e.getMessage());
        }
    }

    // ========================================================================
    // SEÇÃO 5: PREENCHIMENTO DE FORMULÁRIOS
    // ========================================================================
    /**
     * Preenche o campo de prazo (VERSÃO SIMPLIFICADA)
     *
     * @param prazo Número de dias (ex: "15")
     */
    public void preencherPrazo(String prazo) {
        try {
            log.info("Preenchendo prazo: {} dias", prazo);

            // Aguardar campo estar presente E visível
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(campoPrazo));

            // Limpar e preencher
            campo.clear();
            campo.sendKeys(prazo);

            log.info("✓ Prazo preenchido com sucesso");

        } catch (Exception e) {
            log.error("Erro ao preencher prazo: {}", e.getMessage());

            // DEBUG simplificado
            log.error("DEBUG - URL: {}", driver.getCurrentUrl());
            log.error("DEBUG - Total inputs: {}", driver.findElements(By.tagName("input")).size());
            log.error("DEBUG - Total iframes: {}", driver.findElements(By.tagName("iframe")).size());

            throw new RuntimeException("Falha ao preencher prazo", e);
        }
    }

    /**
     * Seleciona um cliente usando autocomplete
     *
     * @param textoDigitar  Texto inicial para filtrar (ex: "DESTOM")
     * @param opcaoCompleta Texto completo da opção (ex: "20.746.370/0001-80 -
     *                      DESTOM INDUSTRIA...")
     */
    public void selecionarCliente(String textoDigitar, String opcaoCompleta) {
        try {
            log.info("Selecionando cliente");
            autocompleteHelper.selecionarOpcao(campoCliente, textoDigitar, opcaoCompleta);
            log.info("✓ Cliente selecionado");
        } catch (Exception e) {
            log.error("Erro ao selecionar cliente: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao selecionar cliente", e);
        }
    }

    /**
     * Seleciona um vendedor/representante usando autocomplete
     *
     * @param textoDigitar  Texto inicial para filtrar (ex: "Alex")
     * @param opcaoCompleta Texto completo da opção (ex: "Alexandre Lessa
     *                      (Fluxis)")
     */
    public void selecionarVendedor(String textoDigitar, String opcaoCompleta) {
        try {
            log.info("Selecionando vendedor");
            autocompleteHelper.selecionarOpcao(campoVendedor, textoDigitar, opcaoCompleta);
            log.info("✓ Vendedor selecionado");
        } catch (Exception e) {
            log.error("Erro ao selecionar vendedor: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao selecionar vendedor", e);
        }
    }

    /**
     * Seleciona utilização usando Select2 (por texto visível)
     *
     * @param textoOpcao Texto da opção (ex: "Venda de produto/mercadoria")
     */
    public void selecionarUtilizacao(String textoOpcao) {
        try {
            log.info("Selecionando utilização: {}", textoOpcao);
            select2Helper.selecionarOpcao("utilizacao", textoOpcao);
            log.info("✓ Utilização selecionada");
        } catch (Exception e) {
            log.error("Erro ao selecionar utilização: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao selecionar utilização", e);
        }
    }

    /**
     * Seleciona utilização usando Select2 (por valor do option)
     *
     * @param valor Valor do option (ex: "1", "2", "3")
     */
    public void selecionarUtilizacaoPorValor(String valor) {
        try {
            log.info("Selecionando utilização por valor: {}", valor);
            select2Helper.selecionarPorValor("utilizacao", valor);
            log.info("✓ Utilização selecionada por valor");
        } catch (Exception e) {
            log.error("Erro ao selecionar utilização por valor: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao selecionar utilização por valor", e);
        }
    }

    /**
     * Seleciona operação usando Select2 (por texto visível)
     *
     * @param textoOpcao Texto da opção (ex: "Consumidor final")
     */
    public void selecionarOperacao(String textoOpcao) {
        try {
            log.info("Selecionando operação: {}", textoOpcao);
            select2Helper.selecionarOpcao("indicadorOperacao", textoOpcao);
            log.info("✓ Operação selecionada");
        } catch (Exception e) {
            log.error("Erro ao selecionar operação: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao selecionar operação", e);
        }
    }

    /**
     * Seleciona operação usando Select2 (por valor do option)
     *
     * @param valor Valor do option (ex: "0" = Normal, "1" = Consumidor Final)
     */
    public void selecionarOperacaoPorValor(String valor) {
        try {
            log.info("Selecionando operação por valor: {}", valor);
            select2Helper.selecionarPorValor("indicadorOperacao", valor);
            log.info("✓ Operação selecionada por valor");
        } catch (Exception e) {
            log.error("Erro ao selecionar operação por valor: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao selecionar operação por valor", e);
        }
    }

    /**
     * Seleciona tabela preço usando Select2 (por texto visível)
     *
     * @param textoOpcao Texto da opção (ex: "TABELA PADRAO CF - 2025")
     */
    public void selecionarTabelaPreco(String textoOpcao) {
        try {
            log.info("Selecionando tabela preco: {}", textoOpcao);
            select2Helper.selecionarOpcao("tabelaPreco", textoOpcao);
            log.info("✓ Tabela selecionada");
        } catch (Exception e) {
            log.error("Erro ao selecionar tabela: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao selecionar tabela", e);
        }
    }

    /**
     * Seleciona tabela preço usando Select2 (por valor do option)
     *
     * @param valor Valor do option (ex: "0" = Normal, "170" = TABELA PADRAO CF
     *              - 2025)
     */
    public void selecionarTabelaPrecoPorValor(String valor) {
        try {
            log.info("Selecionando tabela por valor: {}", valor);
            select2Helper.selecionarPorValor("tabelaPreco", valor);
            log.info("✓ tabela selecionada por valor");
        } catch (Exception e) {
            log.error("Erro ao selecionar tabela por valor: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao selecionar tabela por valor", e);
        }
    }

    /**
     * Preenche o campo de observação (VERSÃO SIMPLIFICADA)
     *
     * @param observacao Texto (ex: "Teste automação")
     */
    public void preencherObservacaoInterna(String observacao) {
        try {

            // Captura data atual formatada
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String textoCompleto = observacao + " - " + dataHora;

            log.info("Preenchendo observacao: {} texto", observacao);

            // Aguardar campo estar presente E visível
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(campoObservacaoInterna));

            // Limpar e preencher
            campo.clear();
            campo.sendKeys(observacao);

            log.info("✓ Observação preenchida com sucesso");

        } catch (Exception e) {
            log.error("Erro ao preencher observação: {}", e.getMessage());

            // DEBUG simplificado
            log.error("DEBUG - URL: {}", driver.getCurrentUrl());
            log.error("DEBUG - Total inputs: {}", driver.findElements(By.tagName("input")).size());
            log.error("DEBUG - Total iframes: {}", driver.findElements(By.tagName("iframe")).size());

            throw new RuntimeException("Falha ao preencher observação", e);
        }
    }

    /**
     * Preenche o campo de observação (VERSÃO SIMPLIFICADA)
     *
     * @param observacaoExterna Texto (ex: "Testando automação")
     */
    public void preencherObservacaoExterna(String observacaoExterna) {
        try {

            log.info("Preenchendo observacao: {} texto", observacaoExterna);

            // Aguardar campo estar presente E visível
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(campoObservacaoExterna));

            // Limpar e preencher
            campo.clear();
            campo.sendKeys(observacaoExterna);

            log.info("✓ Observação preenchida com sucesso");

        } catch (Exception e) {
            log.error("Erro ao preencher observação: {}", e.getMessage());

            // DEBUG simplificado
            log.error("DEBUG - URL: {}", driver.getCurrentUrl());
            log.error("DEBUG - Total inputs: {}", driver.findElements(By.tagName("input")).size());
            log.error("DEBUG - Total iframes: {}", driver.findElements(By.tagName("iframe")).size());

            throw new RuntimeException("Falha ao preencher observação", e);
        }
    }

    /**
     * Preenche o campo de frete (VERSÃO SIMPLIFICADA)
     *
     * @param frete Valor (ex: "150")
     */
    public void preencherFrete(String freight) {
        try {

            log.info("Preenchendo frete: {} valor", freight);

            // Aguardar campo estar presente E visível
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(campoFrete));

            // Limpar e preencher
            campo.clear();
            campo.sendKeys(freight);

            log.info("✓ Frete preenchido com sucesso");

        } catch (Exception e) {
            log.error("Erro ao preencher frete: {}", e.getMessage());

            // DEBUG simplificado
            log.error("DEBUG - URL: {}", driver.getCurrentUrl());
            log.error("DEBUG - Total inputs: {}", driver.findElements(By.tagName("input")).size());
            log.error("DEBUG - Total iframes: {}", driver.findElements(By.tagName("iframe")).size());

            throw new RuntimeException("Falha ao preencher frete", e);
        }
    }

    // ========================================================================
    // SEÇÃO 6: AÇÕES DE SALVAMENTO E CAPTURA
    // ========================================================================
    /**
     * Clica no botão Salvar e aguarda o sistema gerar o código da proposta
     *
     * @return Código da proposta gerado pelo sistema
     */
    public String salvarProposta() {
        try {
            log.info("Clicando em Salvar...");

            // Aguardar botão estar clicável
            WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(botaoSalvar));
            botao.click();

            log.info("✓ Botão Salvar clicado");

            // Aguardar o campo ID ser preenchido pelo sistema (não vazio)
            wait.until(driver -> {
                WebElement campoId = driver.findElement(campoIdProposta);
                String valor = campoId.getAttribute("value");
                return valor != null && !valor.trim().isEmpty();
            });

            // Capturar o código gerado
            String codigoProposta = driver.findElement(campoIdProposta).getAttribute("value");

            log.info("✓ Proposta salva com sucesso! Código: {}", codigoProposta);

            return codigoProposta;

        } catch (Exception e) {
            log.error("Erro ao salvar proposta: {}", e.getMessage());

            // DEBUG simplificado
            log.error("DEBUG - URL: {}", driver.getCurrentUrl());
            log.error("DEBUG - Botão Salvar presente: {}",
                    driver.findElements(botaoSalvar).size() > 0);
            throw new RuntimeException("Falha ao salvar proposta", e);
        }

    }

    // ========================================================================
    // SEÇÃO 7: PREENCHIMENTO DE ITENS
    // ========================================================================
    /**
     * Clica na aba Componente
     */
    public void clicarAbaComponente() {
        try {
            log.info("Clicando na aba Componente...");

            WebElement aba = wait.until(ExpectedConditions.elementToBeClickable(abaComponente));

            // Destacar para debug
            destacarElemento(aba);

            // Estratégia 1: Click direto
            boolean sucesso = false;
            try {
                aba.click();
                Thread.sleep(1000);
                sucesso = true;
            } catch (Exception e) {
                log.debug("Click direto falhou: {}", e.getMessage());
            }

            // Estratégia 2: Click via JavaScript
            if (!sucesso) {
                try {
                    js.executeScript("arguments[0].click();", aba);
                    Thread.sleep(1000);
                    sucesso = true;
                } catch (Exception e) {
                    log.debug("Click JS falhou: {}", e.getMessage());
                }
            }

            if (!sucesso) {
                throw new RuntimeException("Não foi possível clicar na aba Componente");
            }

            // Aguardar campos da aba Componente ficarem visíveis
            wait.until(ExpectedConditions.visibilityOfElementLocated(campoComponente));

            log.info("✓ Aba Componente ativada");

        } catch (Exception e) {
            log.error("Erro ao clicar na aba Componente: {}", e.getMessage());
            throw new RuntimeException("Falha ao acessar aba Componente", e);
        }
    }

    /**
     * Clica na aba Serviço
     */
    public void clicarAbaServico() {
        try {
            log.info("Clicando na aba Serviço...");

            WebElement aba = wait.until(ExpectedConditions.elementToBeClickable(abaServico));

            // Destacar para debug
            destacarElemento(aba);

            // Estratégia 1: Click direto
            boolean sucesso = false;
            try {
                aba.click();
                Thread.sleep(1000);
                sucesso = true;
            } catch (Exception e) {
                log.debug("Click direto falhou: {}", e.getMessage());
            }

            // Estratégia 2: Click via JavaScript
            if (!sucesso) {
                try {
                    js.executeScript("arguments[0].click();", aba);
                    Thread.sleep(1000);
                    sucesso = true;
                } catch (Exception e) {
                    log.debug("Click JS falhou: {}", e.getMessage());
                }
            }

            if (!sucesso) {
                throw new RuntimeException("Não foi possível clicar na aba Serviço");
            }

            // Aguardar campos da aba Serviço ficarem visíveis
            wait.until(ExpectedConditions.visibilityOfElementLocated(campoServico));

            log.info("✓ Aba Serviço ativada");

        } catch (Exception e) {
            log.error("Erro ao clicar na aba Serviço: {}", e.getMessage());
            throw new RuntimeException("Falha ao acessar aba Serviço", e);
        }
    }

    /**
     * Verifica se está na aba Componente
     *
     * @return true se o campo componente está visível
     */
    private boolean estaAbaComponente() {
        try {
            WebElement campo = driver.findElement(campoComponente);
            return campo.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se está na aba Serviço
     *
     * @return true se o campo serviço está visível
     */
    private boolean estaAbaServico() {
        try {
            WebElement campo = driver.findElement(campoServico);
            return campo.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adiciona um item completo à proposta (componente + quantidade)
     *
     * @param textoDigitar  Texto inicial para filtrar (ex: "CON00032")
     * @param opcaoCompleta Texto completo da opção (ex: "CON00032_01 -
     *                      NX-102...")
     * @param quantidade    Quantidade do item (ex: "2")
     */
    public void adicionarItem(String textoDigitar, String opcaoCompleta, String quantidade) {
        try {
            log.info("Adicionando item: {} | Qtd: {}", textoDigitar, quantidade);

            // 0. **NOVO**: Garantir que está na aba Componente
            if (!estaAbaComponente()) {
                log.info("  ⚠ Não está na aba Componente, trocando...");
                clicarAbaComponente();
            }

            // 1. Selecionar componente
            log.info("  → Selecionando componente...");
            selecionarComponenteManual(textoDigitar);
            log.info("  ✓ Componente selecionado");

            // 2. Aguardar sistema processar (onblur + AJAX)
            log.info("  ⏳ Aguardando sistema processar...");
            Thread.sleep(2000);

            // 3. Forçar carregamento clicando no campo quantidade
            log.info("  → Clicando no campo quantidade para forçar carregamento...");
            WebElement campoQtd = wait.until(ExpectedConditions.elementToBeClickable(campoQuantidade));
            campoQtd.click();
            log.info("  ✓ Campo quantidade clicado");

            // 4. Aguardar tabela de preço carregar
            log.info("  ⏳ Aguardando tabela de preços...");
            aguardarTabelaPrecoCarregar();
            log.info("  ✓ Tabela carregada");

            // 5. Preencher quantidade
            log.info("  → Preenchendo quantidade...");
            campoQtd.clear();
            campoQtd.sendKeys(quantidade);
            log.info("  ✓ Quantidade preenchida");

            // 6. Clicar em Adicionar
            log.info("  → Clicando em Adicionar...");
            WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(botaoAdicionarComponente));
            botao.click();
            log.info("  ✓ Botão clicado");

            // 7. Detectar e fechar popup "Pronto!"
            log.info("  ⏳ Verificando popup de confirmação...");
            Thread.sleep(1000); // Aguardar popup aparecer

            if (detectarEFecharPopup()) {
                log.info("  ✓ Popup confirmação fechado");
            } else {
                log.debug("  ℹ Nenhum popup detectado (comportamento normal)");
            }

            // 8. Aguardar confirmação (campos limparem)
            aguardarItemSerAdicionado();

            log.info("✓ Item adicionado com sucesso\n");

        } catch (Exception e) {
            log.error("Erro ao adicionar item: {}", e.getMessage());
            log.error("DEBUG - URL: {}", driver.getCurrentUrl());

            // Tentar fechar popup se estiver travado
            try {
                if (detectarPopupSweetAlert(2)) {
                    log.warn("⚠ Popup ainda aberto após erro, tentando fechar...");
                    fecharPopupSweetAlert();
                }
            } catch (Exception e2) {
                log.debug("Não foi possível fechar popup no tratamento de erro");
            }

            throw new RuntimeException("Falha ao adicionar item", e);
        }
    }

    /**
     * Seleciona componente manualmente (SEM autocompleteHelper) Uso: Se
     * autocompleteHelper causar postback que limpa campos
     */
    private void selecionarComponenteManual(String textoDigitar) {
        try {
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(campoComponente));
            campo.clear();
            campo.sendKeys(textoDigitar);

            Thread.sleep(1000); // Aguardar lista aparecer

            campo.sendKeys(Keys.DOWN); // Selecionar primeira opção
            campo.sendKeys(Keys.ENTER); // Confirmar

        } catch (Exception e) {
            throw new RuntimeException("Erro ao selecionar componente manualmente", e);
        }
    }

    /**
     * Seleciona componente usando autocomplete
     */
    private void selecionarComponente(String textoDigitar, String opcaoCompleta) {
        try {
            autocompleteHelper.selecionarOpcao(campoComponente, textoDigitar, opcaoCompleta);
        } catch (Exception e) {
            log.error("Erro ao selecionar componente via autocompleteHelper: {}", e.getMessage());
            throw new RuntimeException("Falha ao selecionar componente", e);
        }
    }

    /**
     * Aguarda a tabela de preço do componente carregar
     */
    private void aguardarTabelaPrecoCarregar() {
        try {
            wait.until(driver -> {
                try {
                    WebElement select = driver.findElement(campoTabelaPrecoComponente);
                    List<WebElement> opcoes = select.findElements(By.tagName("option"));

                    return opcoes.stream().anyMatch(o -> {
                        String value = o.getAttribute("value");
                        return value != null && !value.trim().isEmpty();
                    });
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            log.error("Timeout ao aguardar tabela de preço carregar");
            throw new RuntimeException("Tabela de preço não carregou", e);
        }
    }

    /**
     * Aguarda o item ser adicionado à lista (campos limparem)
     */
    private void aguardarItemSerAdicionado() {
        try {
            wait.until(driver -> {
                WebElement campo = driver.findElement(campoQuantidade);
                String valor = campo.getAttribute("value");
                return valor == null || valor.trim().isEmpty();
            });
        } catch (Exception e) {
            log.warn("Timeout ao aguardar confirmação de item adicionado");
        }
    }

    /**
     * Adiciona um serviço à proposta (serviço + quantidade) ATUALIZADO: Agora
     * trata popup SweetAlert2 "Pronto!" e garante estar na aba correta
     *
     * @param textoDigitar  Texto inicial para filtrar (ex: "manut")
     * @param opcaoCompleta Texto completo da opção (ex: "Serviços de
     *                      manutenção")
     * @param quantidade    Quantidade do item (ex: "1")
     */
    public void adicionarServico(String textoDigitar, String opcaoCompleta, String quantidade) {
        try {
            log.info("Adicionando serviço: {} | Qtd: {}", textoDigitar, quantidade);

            // 0. **CRÍTICO**: Garantir que está na aba Serviço
            if (!estaAbaServico()) {
                log.info("  ⚠ Não está na aba Serviço, trocando...");
                clicarAbaServico();
            } else {
                log.info("  ✓ Já está na aba Serviço");
            }

            // 1. Selecionar serviço
            log.info("  → Selecionando serviço...");
            selecionarServicoManual(textoDigitar);
            log.info("  ✓ Serviço selecionado");

            // 2. Aguardar sistema processar (onblur + AJAX)
            log.info("  ⏳ Aguardando sistema processar...");
            Thread.sleep(2000);

            // 3. Preencher quantidade (usando o campo correto: serviceAmount)
            log.info("  → Preenchendo quantidade...");
            WebElement campoQtd = wait.until(ExpectedConditions.elementToBeClickable(campoServicoQuantidade));
            campoQtd.click(); // Forçar foco
            campoQtd.clear();
            campoQtd.sendKeys(quantidade);
            log.info("  ✓ Quantidade preenchida");

            // 4. (OPCIONAL) Preencher preço se necessário
            // Se o sistema preenche automaticamente, pular esta etapa
            // Se precisar preencher manualmente, descomentar:
            log.info("  → Preenchendo preço...");
            WebElement campoPreco = wait.until(ExpectedConditions.elementToBeClickable(campoServicoPreco));
            campoPreco.click();
            campoPreco.clear();
            campoPreco.sendKeys("100.00"); // Substituir pelo valor correto
            log.info("  ✓ Preço preenchido");

            // 5. Clicar em Adicionar (botão específico de serviço)
            log.info("  → Clicando em Adicionar...");
            WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(botaoAdicionarServico));
            botao.click();
            log.info("  ✓ Botão clicado");

            // 6. Detectar e fechar popup "Pronto!"
            log.info("  ⏳ Verificando popup de confirmação...");
            Thread.sleep(1000); // Aguardar popup aparecer

            if (detectarEFecharPopup()) {
                log.info("  ✓ Popup confirmação fechado");
            } else {
                log.debug("  ℹ Nenhum popup detectado (comportamento normal)");
            }

            // 7. Aguardar confirmação (campo quantidade limpar)
            aguardarServicoSerAdicionado();

            log.info("✓ Serviço adicionado com sucesso\n");

        } catch (Exception e) {
            log.error("Erro ao adicionar serviço: {}", e.getMessage());
            log.error("DEBUG - URL: {}", driver.getCurrentUrl());
            log.error("DEBUG - Está na aba Serviço? {}", estaAbaServico());

            // Tentar fechar popup se estiver travado
            try {
                if (detectarPopupSweetAlert(2)) {
                    log.warn("⚠ Popup ainda aberto após erro, tentando fechar...");
                    fecharPopupSweetAlert();
                }
            } catch (Exception e2) {
                log.debug("Não foi possível fechar popup no tratamento de erro");
            }

            throw new RuntimeException("Falha ao adicionar serviço", e);
        }
    }

    /**
     * Seleciona serviço manualmente (SEM autocompleteHelper) Uso: Se
     * autocompleteHelper causar postback que limpa campos
     */
    private void selecionarServicoManual(String textoDigitar) {
        try {
            log.debug("Aguardando campo serviço estar visível...");
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(campoServico));

            log.debug("Campo encontrado, limpando...");
            campo.clear();

            log.debug("Digitando: '{}'", textoDigitar);
            campo.sendKeys(textoDigitar);

            log.debug("Aguardando autocomplete aparecer...");
            Thread.sleep(1500); // Aguardar lista aparecer

            log.debug("Enviando tecla DOWN...");
            campo.sendKeys(Keys.DOWN); // Selecionar primeira opção

            Thread.sleep(300);

            log.debug("Enviando tecla ENTER...");
            campo.sendKeys(Keys.ENTER); // Confirmar

            Thread.sleep(500);

            log.debug("✓ Serviço selecionado via teclado");

        } catch (Exception e) {
            log.error("Erro ao selecionar serviço manualmente: {}", e.getMessage());
            log.error("Campo serviço presente? {}", driver.findElements(campoServico).size() > 0);
            log.error("Campo serviço visível? {}",
                    driver.findElements(campoServico).stream()
                            .findFirst()
                            .map(WebElement::isDisplayed)
                            .orElse(false));
            throw new RuntimeException("Erro ao selecionar serviço manualmente", e);
        }
    }

    /**
     * Aguarda o serviço ser adicionado à lista (campo quantidade limpar)
     */
    private void aguardarServicoSerAdicionado() {
        try {
            log.debug("Aguardando campo quantidade (serviceAmount) limpar...");
            wait.until(driver -> {
                try {
                    WebElement campo = driver.findElement(campoServicoQuantidade);
                    String valor = campo.getAttribute("value");
                    return valor == null || valor.trim().isEmpty();
                } catch (Exception e) {
                    return false;
                }
            });
            log.debug("✓ Campo quantidade limpo (serviço adicionado)");
        } catch (Exception e) {
            log.warn("Timeout ao aguardar confirmação de serviço adicionado");
        }
    }

    // ========================================================================
    // SEÇÃO 9: GESTÃO DA ABA CONDIÇÕES (SELECT2) ✨ REFATORADO
    // ========================================================================

    /**
     * Clica na aba Condições
     */
    public void clicarAbaCondicoes() {
        try {
            log.info("Clicando na aba Condições...");

            WebElement aba = wait.until(ExpectedConditions.elementToBeClickable(abaCondicoes));

            // Destacar para debug
            destacarElemento(aba);

            // Estratégia 1: Click direto
            boolean sucesso = false;
            try {
                aba.click();
                Thread.sleep(1000);
                sucesso = true;
            } catch (Exception e) {
                log.debug("Click direto falhou: {}", e.getMessage());
            }

            // Estratégia 2: Click via JavaScript
            if (!sucesso) {
                try {
                    js.executeScript("arguments[0].click();", aba);
                    Thread.sleep(1000);
                    sucesso = true;
                } catch (Exception e) {
                    log.debug("Click JS falhou: {}", e.getMessage());
                }
            }

            if (!sucesso) {
                throw new RuntimeException("Não foi possível clicar na aba Condições");
            }

            // Aguardar campos da aba Condições ficarem visíveis
            wait.until(ExpectedConditions.visibilityOfElementLocated(campoTipoPagamentoSelect));

            log.info("✓ Aba Condições ativada");

        } catch (Exception e) {
            log.error("Erro ao clicar na aba Condições: {}", e.getMessage());
            throw new RuntimeException("Falha ao acessar aba Condições", e);
        }
    }

    /**
     * Verifica se está na aba Condições
     *
     * @return true se o campo tipo pagamento está visível
     */
    private boolean estaAbaCondicoes() {
        try {
            WebElement campo = driver.findElement(campoTipoPagamentoSelect);
            return campo.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adiciona uma condição de recebimento à proposta
     * 
     * @param tipoPagamento       Texto da opção (ex: "Boleto", "Dinheiro", etc)
     * @param contaCorrente       Texto da opção (ex: "BANCO SANTANDER", "CAIXA
     *                            ECONÔMICA FEDERAL", etc)
     * @param condicaoRecebimento Texto da opção (ex: "28 dias", "02x - 30/45", etc)
     */
    public void adicionarCondicaoRecebimento(String tipoPagamento, String contaCorrente, String condicaoRecebimento) {
        try {
            log.info("Adicionando condição de recebimento:");
            log.info("  • Tipo: {}", tipoPagamento);
            log.info("  • Conta: {}", contaCorrente);
            log.info("  • Condição: {}", condicaoRecebimento);

            // 0. Garantir que está na aba Condições
            if (!estaAbaCondicoes()) {
                log.info("  ⚠ Não está na aba Condições, trocando...");
                clicarAbaCondicoes();
            } else {
                log.info("  ✓ Já está na aba Condições");
            }

            // 1. Selecionar Tipo de Pagamento
            log.info("  → Selecionando tipo de pagamento...");
            selecionarCampoSelect2PorTexto("tipoPagamento", tipoPagamento);
            log.info("  ✓ Tipo de pagamento selecionado");

            // Aguardar sistema processar (pode haver AJAX)
            Thread.sleep(1500);

            // 2. Selecionar Conta Corrente
            log.info("  → Selecionando conta corrente...");
            selecionarCampoSelect2PorTexto("contaCorrente", contaCorrente);
            log.info("  ✓ Conta corrente selecionada");

            // Aguardar sistema processar
            Thread.sleep(1500);

            // 3. Selecionar Condição de Recebimento
            log.info("  → Selecionando condição de recebimento...");
            selecionarCampoSelect2PorTexto("condicao", condicaoRecebimento);
            log.info("  ✓ Condição de recebimento selecionada");

            // Aguardar sistema processar
            Thread.sleep(1500);

            // 4. Clicar em Adicionar
            log.info("  → Clicando em Adicionar...");
            WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(botaoAdicionarCondicao));
            destacarElemento(botao);
            botao.click();
            log.info("  ✓ Botão clicado");

            // 5. Verificar popup de confirmação
            log.info("  ⏳ Verificando popup de confirmação...");
            Thread.sleep(1000);

            if (detectarEFecharPopup()) {
                log.info("  ✓ Popup confirmação fechado");
            } else {
                log.debug("  ℹ Nenhum popup detectado (comportamento normal)");
            }

            // 6. Aguardar confirmação (campos limparem)
            aguardarCondicaoSerAdicionada();

            log.info("✓ Condição de recebimento adicionada com sucesso\n");

        } catch (Exception e) {
            log.error("Erro ao adicionar condição de recebimento: {}", e.getMessage());
            log.error("DEBUG - URL: {}", driver.getCurrentUrl());
            log.error("DEBUG - Está na aba Condições? {}", estaAbaCondicoes());

            // Tentar fechar popup se estiver travado
            try {
                if (detectarPopupSweetAlert(2)) {
                    log.warn("⚠ Popup ainda aberto após erro, tentando fechar...");
                    fecharPopupSweetAlert();
                }
            } catch (Exception e2) {
                log.debug("Não foi possível fechar popup no tratamento de erro");
            }

            throw new RuntimeException("Falha ao adicionar condição de recebimento", e);
        }
    }

    /**
     * Seleciona uma opção em um campo Select2 pelo texto (ESTRATÉGIA ROBUSTA)
     * Método agnóstico ao jQuery - funciona mesmo se jQuery não estiver disponível
     * 
     * @param selectId   ID do select original (ex: "tipoPagamento")
     * @param textoOpcao Texto visível da opção (ex: "Boleto")
     */
    private void selecionarCampoSelect2PorTexto(String selectId, String textoOpcao) {
        try {
            log.debug("Iniciando seleção Select2 para '{}': '{}'", selectId, textoOpcao);

            // 1. Aguardar o select estar presente
            By selectLocator = By.id(selectId);
            WebElement selectElement = wait.until(ExpectedConditions.presenceOfElementLocated(selectLocator));

            // 2. ESTRATÉGIA PRINCIPAL: Selecionar via JavaScript puro (SEM jQuery)
            log.debug("Tentando seleção via JavaScript puro...");

            String scriptSelecao = String.format(
                    "var select = arguments[0];" +
                            "var optionText = '%s';" +
                            "var found = false;" +
                            "for (var i = 0; i < select.options.length; i++) {" +
                            "  var currentText = select.options[i].text.trim();" +
                            "  if (currentText === optionText || currentText.indexOf(optionText) !== -1) {" +
                            "    select.selectedIndex = i;" +
                            "    found = true;" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!found) { return 'OPTION_NOT_FOUND'; }" +
                            "return select.value;",
                    textoOpcao.replace("'", "\\'"));

            Object resultado = js.executeScript(scriptSelecao, selectElement);

            if ("OPTION_NOT_FOUND".equals(resultado)) {
                log.error("Opção '{}' não encontrada no select '{}'", textoOpcao, selectId);

                // DEBUG: Listar opções disponíveis
                String scriptListarOpcoes = "var select = arguments[0];" +
                        "var opcoes = [];" +
                        "for (var i = 0; i < select.options.length; i++) {" +
                        "  opcoes.push(select.options[i].text);" +
                        "}" +
                        "return opcoes.join(' | ');";

                String opcoesDisponiveis = (String) js.executeScript(scriptListarOpcoes, selectElement);
                log.error("Opções disponíveis: {}", opcoesDisponiveis);

                throw new RuntimeException("Opção não encontrada: " + textoOpcao);
            }

            log.debug("✓ Opção selecionada via JS puro. Valor: {}", resultado);

            // 3. Disparar evento 'change' nativo (sem jQuery)
            log.debug("Disparando evento 'change' nativo...");
            String scriptChange = "var select = arguments[0];" +
                    "var event = new Event('change', { bubbles: true });" +
                    "select.dispatchEvent(event);";

            js.executeScript(scriptChange, selectElement);

            // 4. Tentar disparar eventos Select2 (se disponível, mas sem falhar se não
            // estiver)
            try {
                log.debug("Tentando disparar eventos Select2...");
                String scriptSelect2Events = "var select = arguments[0];" +
                        "if (typeof jQuery !== 'undefined' && jQuery(select).data('select2')) {" +
                        "  jQuery(select).trigger('change');" +
                        "  return 'JQUERY_OK';" +
                        "}" +
                        "return 'JQUERY_NOT_AVAILABLE';";

                String statusJQuery = (String) js.executeScript(scriptSelect2Events, selectElement);
                log.debug("Status jQuery: {}", statusJQuery);

            } catch (Exception e) {
                log.debug("jQuery não disponível (normal): {}", e.getMessage());
            }

            // 5. Aguardar eventos processarem
            Thread.sleep(500);

            // 6. VALIDAÇÃO: Verificar se o texto foi selecionado no container visual do
            // Select2
            try {
                String containerId = "s2id_" + selectId;
                By spanChosenLocator = By.cssSelector("#" + containerId + " .select2-chosen");

                WebElement spanChosen = driver.findElement(spanChosenLocator);
                String textoExibido = spanChosen.getText();

                log.debug("Texto exibido no Select2: '{}'", textoExibido);

                if (textoExibido != null && textoExibido.contains(textoOpcao)) {
                    log.debug("✓ Validação OK: Select2 exibindo '{}'", textoExibido);
                } else if (textoExibido == null || textoExibido.trim().isEmpty()) {
                    log.warn("⚠ AVISO: Span do Select2 está vazio, mas valor foi setado no select");
                } else {
                    log.warn("⚠ AVISO: Texto difere: esperado '{}', exibido '{}'", textoOpcao, textoExibido);
                }

            } catch (Exception e) {
                log.debug("Não foi possível validar span do Select2 (normal para alguns casos)");
            }

            log.debug("✓ Seleção concluída para '{}'", selectId);

        } catch (Exception e) {
            log.error("Erro ao selecionar campo Select2 '{}': {}", selectId, e.getMessage());
            throw new RuntimeException("Falha ao selecionar Select2: " + selectId, e);
        }
    }

    /**
     * Aguarda a condição ser adicionada à lista (campos limparem)
     */
    private void aguardarCondicaoSerAdicionada() {
        try {
            log.debug("Aguardando campo tipo pagamento limpar...");
            wait.until(driver -> {
                try {
                    WebElement select = driver.findElement(campoTipoPagamentoSelect);
                    String valor = select.getAttribute("value");
                    return valor == null || valor.trim().isEmpty();
                } catch (Exception e) {
                    return false;
                }
            });
            log.debug("✓ Campo tipo pagamento limpo (condição adicionada)");
        } catch (Exception e) {
            log.warn("Timeout ao aguardar confirmação de condição adicionada");
        }
    }

    // ========================================================================
    // SEÇÃO 10: AUTORIZAR PROPOSTA
    // ========================================================================
    /**
     * Clica no botão Autorizar o sistema mudar o status da proposta
     *
     * @return Status Autorizado
     */

    public boolean autorizarProposta() {
        try {
            log.info("Preparando autorização da proposta...");

            // PASSO 1: Garantir que está no iframe da proposta
            try {
                driver.switchTo().defaultContent();
                trocarParaIframeProposta();
                log.info("✓ Contexto configurado para iframe da proposta");
            } catch (Exception e) {
                log.warn("Não foi necessário trocar de iframe");
            }

            // PASSO 2: Localizar o botão Autorizar
            WebElement botaoAutorizar = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//input[@onclick='return authorize();' and @value='Autorizar']")));
            log.info("✓ Botão Autorizar localizado");

            // Scroll até o botão
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", botaoAutorizar);
            Thread.sleep(500);

            // PASSO 3: Clicar no botão usando JavaScript
            js.executeScript("arguments[0].click();", botaoAutorizar);
            log.info("✓ Clique executado no botão Autorizar");

            // PASSO 4: Aguardar o iframe de assinatura carregar
            log.info("Aguardando abertura da janela de assinatura...");
            Thread.sleep(2000);

            // PASSO 5: Tentar detectar o iframe de assinatura
            driver.switchTo().defaultContent();

            WebDriverWait iframeWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement iframeAssinatura = iframeWait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//iframe[contains(@src, 'assinatura.do')]")));

            log.info("✓ Iframe de assinatura detectado!");
            log.info("  - ID: {}", iframeAssinatura.getAttribute("id"));
            log.info("  - Name: {}", iframeAssinatura.getAttribute("name"));
            log.info("  - Src: {}", iframeAssinatura.getAttribute("src"));

            return true;

        } catch (Exception e) {
            log.error("❌ Erro ao autorizar proposta: {}", e.getMessage());

            // DEBUG
            log.error("DEBUG - URL atual: {}", driver.getCurrentUrl());
            driver.switchTo().defaultContent();
            listarIframesNaPagina();

            throw new RuntimeException("Falha ao autorizar proposta", e);
        }
    }

    public void preencherSenha(String senha) {
        try {
            log.info("Preparando para preencher senha...");

            // PASSO 1: Voltar ao contexto principal
            driver.switchTo().defaultContent();
            log.info("✓ Voltou para contexto principal");

            // PASSO 2: Localizar o iframe de assinatura
            WebDriverWait iframeWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement iframeAssinatura = iframeWait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//iframe[contains(@src, 'assinatura.do')]")));

            log.info("✓ Iframe de assinatura localizado");
            log.info("  - ID: {}", iframeAssinatura.getAttribute("id"));

            // PASSO 3: Entrar no iframe
            driver.switchTo().frame(iframeAssinatura);
            log.info("✓ Foco trocado para iframe de assinatura");

            // Aguardar conteúdo carregar
            Thread.sleep(1500);

            // PASSO 4: Aguardar e localizar campo de senha
            WebElement campoDeSenha = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//input[@id='password' and @type='password']")));

            log.info("✓ Campo de senha localizado");

            // PASSO 5: Preencher senha
            campoDeSenha.clear();
            campoDeSenha.sendKeys(senha);
            log.info("✓ Senha preenchida");

            // Aguardar um pouco para estabilizar
            Thread.sleep(500);

            // PASSO 6: Localizar e clicar no botão OK
            WebElement botaoOk = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//input[@id='btnOk' and @onclick='return signup();']")));

            log.info("✓ Botão OK localizado");

            // Clicar usando JavaScript para garantir
            js.executeScript("arguments[0].click();", botaoOk);
            log.info("✓ Botão OK clicado");

            // PASSO 7: Aguardar processamento
            Thread.sleep(2000);

            // Voltar ao contexto principal
            driver.switchTo().defaultContent();
            log.info("✓ Voltou ao contexto principal após assinatura");

        } catch (Exception e) {
            log.error("❌ Erro ao preencher senha: {}", e.getMessage());

            // DEBUG
            driver.switchTo().defaultContent();
            log.error("DEBUG - Total de iframes: {}",
                    driver.findElements(By.tagName("iframe")).size());
            listarIframesNaPagina();

            throw new RuntimeException("Falha ao preencher senha", e);
        }
    }

    /**
     * Confirma a geração do Pedido de Venda no popup SweetAlert
     * que aparece após autorizar a proposta
     * O popup aparece dentro do iframe principal da proposta (src="proposta.do")
     */
    public void confirmarGeracaoPedido() {
        try {
            log.info("Aguardando popup de decisão aparecer...");

            // PASSO 1: Voltar ao contexto principal
            driver.switchTo().defaultContent();
            log.info("✓ Voltou para contexto principal");

            // Aguardar processamento da assinatura
            Thread.sleep(3000);

            // PASSO 2: Localizar o iframe principal da proposta (onde foi preenchida)
            log.debug("Localizando iframe principal da proposta...");

            // O iframe tem src="proposta.do" e width="1300"
            By iframePropostaLocator = By.xpath("//iframe[contains(@src, 'proposta.do') and @width='1300']");

            WebDriverWait iframeWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement iframeProposta = iframeWait.until(
                    ExpectedConditions.presenceOfElementLocated(iframePropostaLocator));

            String iframeId = iframeProposta.getAttribute("id");
            String iframeSrc = iframeProposta.getAttribute("src");
            log.info("✓ Iframe principal localizado");
            log.debug("  - ID: {}", iframeId);
            log.debug("  - Src: {}", iframeSrc);

            // PASSO 3: Entrar no iframe da proposta
            driver.switchTo().frame(iframeProposta);
            log.info("✓ Foco trocado para iframe da proposta");

            // Aguardar um pouco para o popup aparecer
            Thread.sleep(1500);

            // PASSO 4: Localizar o popup dentro do iframe
            log.debug("Procurando popup dentro do iframe...");

            WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement popup = popupWait.until(
                    ExpectedConditions.visibilityOfElementLocated(swalPopup));

            log.info("✓ Popup de decisão detectado no iframe!");

            // PASSO 5: Ler informações do popup
            try {
                WebElement titulo = popup.findElement(swalTitulo);
                WebElement mensagem = popup.findElement(swalMensagem);
                log.info("  - Título: '{}'", titulo.getText());
                log.info("  - Mensagem: '{}'", mensagem.getText());
            } catch (Exception e) {
                log.debug("Não foi possível ler textos do popup");
            }

            // PASSO 6: Localizar e clicar no botão OK
            WebElement botaoOk = popup.findElement(swalBotaoOk);

            // Aguardar estar clicável
            popupWait.until(ExpectedConditions.elementToBeClickable(botaoOk));
            log.info("✓ Botão OK localizado e clicável");

            // Scroll até o botão (garantia)
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", botaoOk);
            Thread.sleep(500);

            // Clicar no botão OK
            try {
                log.debug("Tentando click normal...");
                botaoOk.click();
                log.info("✓ Click normal executado no botão OK");
            } catch (Exception e) {
                log.warn("Click normal falhou, usando JavaScript...");
                js.executeScript("arguments[0].click();", botaoOk);
                log.info("✓ Click via JavaScript executado no botão OK");
            }

            // PASSO 7: Aguardar o popup desaparecer
            log.debug("Aguardando popup fechar...");
            Thread.sleep(1500);

            try {
                popupWait.until(ExpectedConditions.invisibilityOfElementLocated(swalPopup));
                log.info("✓ Popup fechado com sucesso");
            } catch (Exception e) {
                log.debug("Timeout ao aguardar popup sumir (pode ter fechado)");
            }

            // PASSO 8: IMPORTANTE - Permanecer no iframe da proposta
            // NÃO voltar ao contexto principal ainda, pois o próximo passo
            // (selecionar condição) também acontece em um iframe
            log.info("✓ Geração de pedido confirmada");
            log.debug("Permanecendo no contexto do iframe da proposta");

            // Aguardar processamento
            Thread.sleep(1500);

        } catch (Exception e) {
            log.error("❌ Erro ao confirmar geração de pedido: {}", e.getMessage());

            // DEBUG
            try {
                driver.switchTo().defaultContent();
                log.error("DEBUG - URL atual: {}", driver.getCurrentUrl());
                log.error("DEBUG - Total de iframes: {}", driver.findElements(By.tagName("iframe")).size());

                // Verificar se existe iframe com proposta.do
                List<WebElement> iframesProposta = driver.findElements(
                        By.xpath("//iframe[contains(@src, 'proposta.do')]"));
                log.error("DEBUG - Iframes com 'proposta.do': {}", iframesProposta.size());

            } catch (Exception ex) {
                log.debug("Erro durante debug");
            }

            // Garantir que voltou ao contexto principal
            driver.switchTo().defaultContent();

            throw new RuntimeException("Falha ao confirmar geração de pedido", e);
        }
    }

    /**
     * Seleciona a condição da proposta no iframe que aparece
     * após confirmar a geração do pedido
     * 
     * @param opcaoCondicao Texto da opção a ser selecionada (ex: "Boleto - 28
     *                      dias")
     *                      Se null ou vazio, seleciona a primeira opção disponível
     */
    public void selecionarCondicaoProposta(String opcaoCondicao) {
        try {
            log.info("Preparando para selecionar condição da proposta...");

            // PASSO 1: Voltar ao contexto principal
            driver.switchTo().defaultContent();
            log.info("✓ Voltou para contexto principal");

            // PASSO 2: Aguardar e localizar o iframe de condições
            log.info("Aguardando iframe de condições aparecer...");
            WebDriverWait iframeWait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // O iframe de condições tem src contendo "showCondicoesProposta=true"
            WebElement iframe = iframeWait.until(
                    ExpectedConditions.presenceOfElementLocated(iframeCondicoesProposta));

            String iframeId = iframe.getAttribute("id");
            String iframeSrc = iframe.getAttribute("src");
            log.info("✓ Iframe de condições localizado");
            log.debug("  - ID: {}", iframeId);
            log.debug("  - Src: {}", iframeSrc);

            // PASSO 3: Entrar no iframe
            driver.switchTo().frame(iframe);
            log.info("✓ Foco trocado para iframe de condições");

            // Aguardar conteúdo carregar
            aguardarPaginaCarregar();
            Thread.sleep(1500);

            // PASSO 4: Verificar se o select existe e tem opções
            WebElement selectElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(campoCondicaoPropostaSelect));

            log.debug("✓ Select localizado (ID: idCondicao)");

            // Verificar opções disponíveis
            List<WebElement> options = selectElement.findElements(By.tagName("option"));
            log.info("Opções disponíveis no select: {}", options.size());

            // Listar opções
            for (int i = 0; i < options.size(); i++) {
                WebElement opt = options.get(i);
                String valor = opt.getAttribute("value");
                String texto = opt.getText();
                log.debug("  Opção [{}]: valor='{}', texto='{}'", i, valor, texto);
            }

            // PASSO 5: Usar Select2Helper para selecionar a condição
            Select2Helper select2 = new Select2Helper(driver);

            if (opcaoCondicao != null && !opcaoCondicao.trim().isEmpty()) {
                log.info("Selecionando condição: '{}'", opcaoCondicao);

                try {
                    // Usar o ID correto: "idCondicao"
                    select2.selecionarOpcao("idCondicao", opcaoCondicao);
                    log.info("✓ Condição selecionada via Select2Helper");

                } catch (Exception e) {
                    log.warn("Select2Helper falhou, tentando método alternativo: {}", e.getMessage());
                    selecionarCondicaoAlternativo(opcaoCondicao);
                }

            } else {
                log.info("Nenhuma condição específica informada, selecionando primeira disponível");
                selecionarPrimeiraCondicao();
            }

            log.info("✓ Condição selecionada com sucesso");

            // PASSO 6: Aguardar um pouco para processar
            Thread.sleep(1000);

            // PASSO 7: Localizar e clicar no botão OK
            log.info("Localizando botão OK...");

            WebElement botaoOk = wait.until(
                    ExpectedConditions.elementToBeClickable(botaoOkCondicao));

            log.info("✓ Botão OK localizado");
            log.debug("  - ID: {}", botaoOk.getAttribute("id"));
            log.debug("  - Value: {}", botaoOk.getAttribute("value"));
            log.debug("  - OnClick: {}", botaoOk.getAttribute("onclick"));

            // Scroll até o botão
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", botaoOk);
            Thread.sleep(500);

            // Garantir que está visível e habilitado
            if (!botaoOk.isDisplayed()) {
                log.error("❌ Botão OK não está visível!");
                throw new RuntimeException("Botão OK não visível");
            }

            if (!botaoOk.isEnabled()) {
                log.error("❌ Botão OK não está habilitado!");
                throw new RuntimeException("Botão OK não habilitado");
            }

            log.debug("Botão OK está visível e habilitado");

            // ESTRATÉGIA 1: Click normal
            boolean cliqueSucesso = false;
            try {
                log.debug("Tentativa 1: Click normal no botão OK");
                botaoOk.click();
                Thread.sleep(500);
                cliqueSucesso = true;
                log.info("✓ Botão OK clicado (click normal)");
            } catch (Exception e) {
                log.debug("Click normal falhou: {}", e.getMessage());
            }

            // ESTRATÉGIA 2: Click via JavaScript
            if (!cliqueSucesso) {
                try {
                    log.debug("Tentativa 2: Click via JavaScript");
                    js.executeScript("arguments[0].click();", botaoOk);
                    Thread.sleep(500);
                    cliqueSucesso = true;
                    log.info("✓ Botão OK clicado (JavaScript)");
                } catch (Exception e) {
                    log.debug("Click JavaScript falhou: {}", e.getMessage());
                }
            }

            // ESTRATÉGIA 3: Executar a função closeWindow() diretamente
            if (!cliqueSucesso) {
                try {
                    log.debug("Tentativa 3: Executar closeWindow() via JavaScript");
                    js.executeScript("closeWindow();");
                    Thread.sleep(500);
                    cliqueSucesso = true;
                    log.info("✓ Função closeWindow() executada via JavaScript");
                } catch (Exception e) {
                    log.debug("Execução de closeWindow() falhou: {}", e.getMessage());
                }
            }

            // ESTRATÉGIA 4: Simular tecla ENTER
            if (!cliqueSucesso) {
                try {
                    log.debug("Tentativa 4: Simular tecla ENTER");
                    botaoOk.sendKeys(Keys.ENTER);
                    Thread.sleep(500);
                    cliqueSucesso = true;
                    log.info("✓ Tecla ENTER enviada ao botão OK");
                } catch (Exception e) {
                    log.debug("ENTER falhou: {}", e.getMessage());
                }
            }

            if (!cliqueSucesso) {
                log.error("❌ Nenhuma estratégia de clique funcionou!");
                throw new RuntimeException("Não foi possível clicar no botão OK");
            }

            log.info("✓ Confirmação enviada com sucesso");

            // PASSO 8: Aguardar o iframe fechar
            Thread.sleep(1500);

            // Voltar ao contexto principal
            driver.switchTo().defaultContent();
            log.info("✓ Condição selecionada e confirmada com sucesso");

        } catch (Exception e) {
            log.error("❌ Erro ao selecionar condição da proposta: {}", e.getMessage());

            // DEBUG
            driver.switchTo().defaultContent();
            log.error("DEBUG - Total de iframes: {}",
                    driver.findElements(By.tagName("iframe")).size());
            listarIframesNaPagina();

            throw new RuntimeException("Falha ao selecionar condição da proposta", e);
        }
    }

    /**
     * Sobrecarga: Seleciona a primeira condição disponível
     */
    public void selecionarCondicaoProposta() {
        selecionarCondicaoProposta(null);
    }

    /**
     * Método auxiliar: Seleciona condição usando método alternativo
     * (quando Select2Helper falha)
     */
    private void selecionarCondicaoAlternativo(String textoOpcao) throws InterruptedException {
        log.info("Usando método alternativo para selecionar: '{}'", textoOpcao);

        try {
            // PASSO 1: Clicar no container do Select2 para abrir
            WebElement container = wait.until(
                    ExpectedConditions.elementToBeClickable(campoCondicaoPropostaContainer));

            log.debug("Container localizado: {}", container.getAttribute("id"));

            // Scroll e click
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", container);
            Thread.sleep(300);

            container.click();
            log.debug("✓ Container clicado - dropdown deve abrir");
            Thread.sleep(1000);

            // PASSO 2: Localizar a opção pelo texto
            // O Select2 cria divs com classe "select2-result-label"
            String xpathOpcao = String.format(
                    "//div[@class='select2-result-label' and contains(text(), '%s')]",
                    textoOpcao);

            log.debug("Procurando opção com XPath: {}", xpathOpcao);

            WebElement opcao = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpathOpcao)));

            log.debug("✓ Opção localizada: '{}'", opcao.getText());

            // Scroll até a opção
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", opcao);
            Thread.sleep(300);

            // PASSO 3: Clicar na opção
            try {
                opcao.click();
                log.info("✓ Opção '{}' selecionada (click normal)", textoOpcao);
            } catch (Exception e) {
                log.debug("Click normal falhou, usando JavaScript");
                js.executeScript("arguments[0].click();", opcao);
                log.info("✓ Opção '{}' selecionada (JavaScript)", textoOpcao);
            }

            Thread.sleep(800);

            // PASSO 4: Validar seleção
            WebElement spanSelecionado = driver.findElement(campoCondicaoPropostaSpan);
            String textoSelecionado = spanSelecionado.getText();
            log.debug("Texto exibido no Select2: '{}'", textoSelecionado);

            if (textoSelecionado.contains(textoOpcao)) {
                log.info("✓ Validação OK: condição selecionada corretamente");
            } else {
                log.warn("⚠ AVISO: Texto selecionado difere do esperado");
            }

        } catch (Exception e) {
            log.error("Erro no método alternativo: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Método auxiliar: Seleciona a primeira condição quando nenhuma foi
     * especificada
     */
    private void selecionarPrimeiraCondicao() throws InterruptedException {
        log.info("Selecionando primeira condição disponível...");

        try {
            // Abrir o dropdown clicando no container
            WebElement container = wait.until(
                    ExpectedConditions.elementToBeClickable(campoCondicaoPropostaContainer));

            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", container);
            Thread.sleep(300);

            container.click();
            log.debug("✓ Dropdown aberto");
            Thread.sleep(800);

            // Localizar primeira opção disponível
            WebElement primeiraOpcao = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".select2-result-label")));

            String textoOpcao = primeiraOpcao.getText();
            log.info("Primeira opção encontrada: '{}'", textoOpcao);

            // Clicar na primeira opção
            primeiraOpcao.click();
            log.info("✓ Primeira opção selecionada: '{}'", textoOpcao);

            Thread.sleep(1000);

        } catch (Exception e) {
            log.error("Erro ao selecionar primeira condição", e);
            throw e;
        }
    }

    // ========================================================================
    // SEÇÃO 11: GESTÃO DE POPUPS SWEETALERT2
    // ========================================================================
    /**
     * Detecta se um popup SweetAlert2 está visível na tela
     *
     * @param timeoutSegundos Tempo máximo para aguardar o popup aparecer
     * @return true se o popup está visível, false caso contrário
     */
    private boolean detectarPopupSweetAlert(int timeoutSegundos) {
        try {
            log.debug("Verificando se popup SweetAlert2 está presente (timeout: {} segundos)...", timeoutSegundos);

            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSegundos));
            // Aguardar o popup estar presente E visível
            WebElement popup = shortWait.until(ExpectedConditions.visibilityOfElementLocated(swalPopup));

            // Verificar se realmente está visível (display != none)
            String display = popup.getCssValue("display");
            boolean isVisible = !"none".equals(display) && popup.isDisplayed();

            if (isVisible) {
                // Capturar título e mensagem para log
                try {
                    String titulo = driver.findElement(swalTitulo).getText();
                    String mensagem = driver.findElement(swalMensagem).getText();
                    log.info("✓ Popup SweetAlert2 detectado:");
                    log.info("  - Título: '{}'", titulo);
                    log.info("  - Mensagem: '{}'", mensagem);
                } catch (Exception e) {
                    log.debug("Não foi possível capturar textos do popup");

                    // Fallback: tentar pegar texto completo
                    try {
                        String textoCompleto = popup.getText();
                        if (textoCompleto != null && !textoCompleto.trim().isEmpty()) {
                            log.info("  - Conteúdo: '{}'", textoCompleto.replaceAll("\\s+", " ").trim());
                        }
                    } catch (Exception e2) {
                        log.debug("Também não conseguiu ler texto completo");
                    }
                }
            }

            return isVisible;

        } catch (Exception e) {
            log.debug("Nenhum popup SweetAlert2 detectado");
            return false;
        }
    }

    /**
     * Sobrecarga: detecta popup com timeout padrão de 3 segundos
     */
    private boolean detectarPopupSweetAlert() {
        return detectarPopupSweetAlert(3);
    }

    /**
     * Fecha o popup SweetAlert2 clicando no botão OK
     *
     * @throws RuntimeException se não conseguir fechar o popup
     */
    private void fecharPopupSweetAlert() {
        try {
            log.info("Fechando popup SweetAlert2...");

            // Aguardar botão OK estar clicável
            WebElement botaoOk = wait.until(ExpectedConditions.elementToBeClickable(swalBotaoOk));

            // Estratégia 1: Click direto
            boolean sucesso = false;
            try {
                botaoOk.click();
                Thread.sleep(500);
                sucesso = true;
                log.debug("✓ Click direto no botão OK");
            } catch (Exception e) {
                log.debug("Click direto falhou: {}", e.getMessage());
            }

            // Estratégia 2: Click via JavaScript
            if (!sucesso) {
                try {
                    js.executeScript("arguments[0].click();", botaoOk);
                    Thread.sleep(500);
                    sucesso = true;
                    log.debug("✓ Click via JS no botão OK");
                } catch (Exception e) {
                    log.debug("Click JS falhou: {}", e.getMessage());
                }
            }

            // Estratégia 3: Simular tecla ENTER
            if (!sucesso) {
                try {
                    botaoOk.sendKeys(Keys.ENTER);
                    Thread.sleep(500);
                    sucesso = true;
                    log.debug("✓ ENTER no botão OK");
                } catch (Exception e) {
                    log.debug("ENTER falhou: {}", e.getMessage());
                }
            }

            if (!sucesso) {
                throw new RuntimeException("Não foi possível clicar no botão OK do popup");
            }

            // Aguardar popup desaparecer
            wait.until(ExpectedConditions.invisibilityOfElementLocated(swalPopup));

            log.info("✓ Popup fechado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao fechar popup SweetAlert2: {}", e.getMessage());
            throw new RuntimeException("Falha ao fechar popup", e);
        }
    }

    /**
     * Detecta e fecha popup SweetAlert2 (se presente) Método conveniente que
     * combina detecção + fechamento
     *
     * @return true se popup foi detectado e fechado, false se não havia popup
     */
    private boolean detectarEFecharPopup() {
        if (detectarPopupSweetAlert()) {
            fecharPopupSweetAlert();
            return true;
        }
        return false;
    }

    // ========================================================================
    // SEÇÃO 12: UTILITÁRIOS
    // ========================================================================
    /**
     * Aguarda um número específico de segundos
     *
     * @param segundos Número de segundos a aguardar
     * @param mensagem Mensagem opcional para log (pode ser null)
     */
    public void aguardarSegundos(int segundos, String mensagem) {
        try {
            if (mensagem != null && !mensagem.isEmpty()) {
                log.info(mensagem);
            }
            Thread.sleep(segundos * 1000L);
        } catch (InterruptedException e) {
            log.warn("Aguardo interrompido");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Destaca um elemento na tela (útil para debug)
     *
     * @param element Elemento a destacar
     */
    private void destacarElemento(WebElement element) {
        try {
            String originalStyle = element.getAttribute("style");
            js.executeScript(
                    "arguments[0].setAttribute('style', 'border: 3px solid red; background: yellow;');",
                    element);
            Thread.sleep(300);
            js.executeScript(
                    "arguments[0].setAttribute('style', '" + (originalStyle != null ? originalStyle : "") + "');",
                    element);
        } catch (Exception e) {
            log.trace("Não foi possível destacar elemento");
        }
    }

}
