package com.gerenciador_cartao.autorizador.telas;

import com.gerenciador_cartao.autorizador.utils.GeraNumeroCartao;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@DisplayName("Teste Automatizado da realização de transações")
@Tag("ui")
public class TransacaoTest {

    private WebDriver navegador;

    private static final String NUMERO_CARTAO = GeraNumeroCartao.gerarNumero16Digitos();

    @BeforeEach
    public void inicializaLogin() {
        WebDriverManager.chromedriver().setup();
        navegador = new ChromeDriver();
        navegador.get("http://localhost:5137/");

        navegador.findElement(By.name("email")).sendKeys("teste@teste.com");
        navegador.findElement(By.name("password")).sendKeys("5963@sfrt$");
        navegador.findElement(By.tagName("button")).click();

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoCadastro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("cadastroCartao")
        ));
        botaoCadastro.click();
        navegador.findElement(By.id("numeroCartao")).sendKeys(NUMERO_CARTAO);
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1234");
        navegador.findElement(By.id("submit")).click();
        navegador.get("http://localhost:5137/");
    }

    @Test
    @DisplayName("Realizar nova transação")
    public void testTransacao() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoTransacao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("transacao")
        ));
        botaoTransacao.click();
        navegador.findElement(By.cssSelector("#valor input")).sendKeys("57.32");
        navegador.findElement(By.id("numeroCartao")).sendKeys(NUMERO_CARTAO);
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1234");
        navegador.findElement(By.id("submit")).click();

        WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[@class='text-sm']")
        ));
        String msgSucesso = toastMsg.getText();
        Assertions.assertEquals(msgSucesso, "Transação processada com Sucesso!");


        WebElement modalCartao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("pr_id_17")
        ));

        String numeroCartao = modalCartao.findElement(By.id("numeroCartao")).getAttribute("value");
        String saldoCartaoModal = modalCartao.findElement(By.id("saldo")).getAttribute("value");

        Assertions.assertTrue(numeroCartao.replace(" ", "").startsWith(NUMERO_CARTAO.substring(0, 12)));
        Assertions.assertTrue(!saldoCartaoModal.isEmpty());

        Assertions.assertEquals(navegador.getCurrentUrl(), "http://localhost:5137/transacao");
        navegador.quit();
    }

    @Test
    @DisplayName("Não deve realizar uma transação quando saldo insuficiente")
    public void testTransacaoSaldoInsuficiente() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoTransacao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("transacao")
        ));
        botaoTransacao.click();
        navegador.findElement(By.cssSelector("#valor input")).sendKeys("750.32");
        navegador.findElement(By.id("numeroCartao")).sendKeys(NUMERO_CARTAO);
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1234");
        navegador.findElement(By.id("submit")).click();

        WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[@class='text-sm']")
        ));
        String msgErro = toastMsg.getText();
        Assertions.assertEquals(msgErro, "SALDO_INSUFICIENTE");

        navegador.quit();
    }

    @Test
    @DisplayName("Não deve realizar uma transação quando senha incorreta")
    public void testTransacaoSenhaIncorreta() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoTransacao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("transacao")
        ));
        botaoTransacao.click();
        navegador.findElement(By.cssSelector("#valor input")).sendKeys("120.42");
        navegador.findElement(By.id("numeroCartao")).sendKeys(NUMERO_CARTAO);
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("4321");
        navegador.findElement(By.id("submit")).click();

        WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[@class='text-sm']")
        ));
        String msgErro = toastMsg.getText();
        Assertions.assertEquals(msgErro, "SENHA_INVALIDA");

        navegador.quit();
    }

    @Test
    @DisplayName("Não deve realizar uma transação quando cartão não encontrado")
    public void testTransacaoCartaoInexistente() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoTransacao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("transacao")
        ));
        botaoTransacao.click();

        String numeroCartaoInvertido = new StringBuilder(NUMERO_CARTAO).reverse().toString();
        navegador.findElement(By.cssSelector("#valor input")).sendKeys("750.32");
        navegador.findElement(By.id("numeroCartao")).sendKeys(numeroCartaoInvertido);
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1234");
        navegador.findElement(By.id("submit")).click();

        WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[@class='text-sm']")
        ));
        String msgErro = toastMsg.getText();
        Assertions.assertEquals(msgErro, "Cartão não encontrado");

        navegador.quit();
    }
}
