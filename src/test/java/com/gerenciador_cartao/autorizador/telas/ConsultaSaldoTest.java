package com.gerenciador_cartao.autorizador.telas;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@DisplayName("Teste Automatizado da consulta de saldo de cartão")
public class ConsultaSaldoTest {

    WebDriver navegador;

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
        navegador.findElement(By.id("numeroCartao")).sendKeys("3452074415821093");
        navegador.findElement(By.cssSelector("input.p-password-input")).sendKeys("1234");
        navegador.findElement(By.id("submit")).click();
        navegador.get("http://localhost:5137/");
    }

    @Test
    @DisplayName("Consultar Saldo Cartão")
    public void testConsultaSaldoCartao() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoSaldo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("saldoCartao")
        ));
        botaoSaldo.click();
        navegador.findElement(By.id("numeroCartao")).sendKeys("3452074415821093");
        navegador.findElement(By.id("submit")).click();

        WebElement modalCartao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("pr_id_17")
        ));

        String numeroCartao = modalCartao.findElement(By.id("numeroCartao")).getAttribute("value");
        String saldoCartaoModal = modalCartao.findElement(By.id("saldo")).getAttribute("value");

        Assertions.assertTrue(numeroCartao.replace(" ", "").startsWith("345207441582"));
        Assertions.assertEquals(saldoCartaoModal.replace("\u00A0", " "), "R$ 500,00");
        Assertions.assertEquals(navegador.getCurrentUrl(), "http://localhost:5137/cartao/saldo");
        navegador.quit();
    }

    @Test
    @DisplayName("Não deve consultar saldo cartão inexistente")
    public void testConsultaSaldoCartaoInexistente() {

        WebDriverWait wait = new WebDriverWait(navegador, Duration.ofSeconds(10));
        WebElement botaoSaldo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("saldoCartao")
        ));
        botaoSaldo.click();
        navegador.findElement(By.id("numeroCartao")).sendKeys("3459374414921093");
        navegador.findElement(By.id("submit")).click();


        WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[@class='text-sm']")
        ));
        String msgErro = toastMsg.getText();
        Assertions.assertEquals(msgErro, "Cartão não encontrado");
        navegador.quit();
    }

}