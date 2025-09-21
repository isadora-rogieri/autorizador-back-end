package com.gerenciador_cartao.autorizador.telas;

import com.gerenciador_cartao.autorizador.utils.GeraNumeroCartao;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@DisplayName("Teste Automatizado do cadastro de cartão")
@TestMethodOrder(OrderAnnotation.class)
@Tag("ui")
public class CadastroCartaoTest {

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
    }

    @Test
    @Order(1)
    @DisplayName("Cadastrar novo cartão")
    public void testCadastroCartao() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoCadastro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("cadastroCartao")
        ));
        botaoCadastro.click();
        navegador.findElement(By.id("numeroCartao")).sendKeys(NUMERO_CARTAO);
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1234");
        navegador.findElement(By.id("submit")).click();

        WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[@class='text-sm']")
        ));
        String msgSucesso = toastMsg.getText();
        Assertions.assertEquals(msgSucesso, "Cartão cadastrado com Sucesso!");


        WebElement modalCartao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("pr_id_17")
        ));

        String numeroCartao = modalCartao.findElement(By.id("numeroCartao")).getAttribute("value");
        String saldoCartaoModal = modalCartao.findElement(By.id("saldo")).getAttribute("value");

        Assertions.assertTrue(numeroCartao.replace(" ", "").startsWith(NUMERO_CARTAO.substring(0, 12)));
        Assertions.assertEquals(saldoCartaoModal.replace("\u00A0", " "), "R$ 500,00");


        Assertions.assertEquals(navegador.getCurrentUrl(), "http://localhost:5137/cartao/cadastro");
        navegador.quit();
    }

    @Test
    @DisplayName("Não deve cadastrar cartão com número ja existente")
    public void testCadastroCartaoExistente() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoCadastro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("cadastroCartao")
        ));
        botaoCadastro.click();
        navegador.findElement(By.id("numeroCartao")).sendKeys(NUMERO_CARTAO);
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1234");
        navegador.findElement(By.id("submit")).click();

        WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[@class='text-sm']")
        ));
        String msgErro = toastMsg.getText();
        Assertions.assertEquals(msgErro, "Já existe um Cartão cadastrado com esse número");
        navegador.quit();
    }

    @Test
    @DisplayName("Não deve cadastrar cartão com número menor que 16 digitos")
    public void testCadastroCartaoNumeroInvalido() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoCadastro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("cadastroCartao")
        ));
        botaoCadastro.click();
        navegador.findElement(By.id("numeroCartao")).sendKeys(NUMERO_CARTAO.substring(0, 14));
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1234");
        navegador.findElement(By.id("submit")).click();

        String msgErro = navegador.findElement(
                By.xpath("//span[@class='text-red-500 text-sm']")).getText();
        Assertions.assertEquals(msgErro, "Número Cartão deve conter 16 caracteres");

        navegador.quit();

    }

    @Test
    @DisplayName("Não deve cadastrar cartão com senha inválida")
    public void testCadastroCartaoSenhaInvalido() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoCadastro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("cadastroCartao")
        ));
        botaoCadastro.click();
        navegador.findElement(By.id("numeroCartao")).sendKeys(NUMERO_CARTAO);
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1a34");
        navegador.findElement(By.id("submit")).click();

        String msgErroSenha = navegador.findElement(
                By.xpath("//span[@class='text-red-500 text-sm']")).getText();
        Assertions.assertEquals(msgErroSenha, "A senha só pode conter números");
        navegador.quit();

    }

}