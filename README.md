# Mini Autorizador (Back-End)
Case Técnico - Mini autorizador VR

Projeto de um mini autorizador de transações, com cadastro de cartões, consulta de saldo e realização de transações.

### Estrutura Pastas
```
src/
autorizador/
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── gerenciador_cartao
│   │   │           └── autorizador
│   │   │               ├── configuration
│   │   │               │   └── OpenAPIConfig.java   <-- Configuração Swagger
│   │   │               │   └── SecurityConfig.java   <-- Configuração Segurança
│   │   │               ├── controller
│   │   │               │   └── CartaoController.java
│   │   │               │   └── TransacaoController.java
│   │   │               │   └── GlobalExceptionHandler.java <-- Interceptador exceções
│   │   │               ├── dto
│   │   │               │   └── CartaoDto.java
│   │   │               │   └── TransacaoDto.java
│   │   │               └── exception <-- Exceções customizadas
│   │   │               │   └── CartaoExistenteException.java  
│   │   │               │   └── CartaoNaoEncontradoException.java
│   │   │               │   └── TransacaoException.java
│   │   │               ├── model
│   │   │               │   └── Cartao.java
│   │   │               │   └── Status.java
│   │   │               │   └── Transacao.java
│   │   │               ├── repository
│   │   │               │   └── CartaoRepository.java
│   │   │               │   └── TransacaoRepository.java
│   │   │               ├── AutorizadorApplication.java 
│   │   │               ├── service
│   │   │               │   └── CartaoService.java
│   │   │               │   └── TransacaoService.java

│   ├── resources
│   │   ├── application.properties  <-- Configurações

│   └── test
│       └── java
│           └── com
│               └── gerenciador_cartao
│                   └── autorizador
│                       └── controller <-- Testes de endpoints
│                          └── CartaoControllerTest.java  
│                          └── TransacaoControllerTest.java  
│                       └── services <-- Testes de serviço
│                          └── CartaoServiceTest.java  
│                          └── TransacaoServiceTest.java
│       └── resources
│           └── application-test.properties  <-- Configuração para o perfil de teste
│           └── schema.sql <-- Arquivo SQL para criar tabelas no banco de dados de teste

```


### Tecnologias
```
 * Java 21
 * Maven
 * Spring Boot
 * Spring Data JPA 
 * Spring Validation
 * Spring Web 
 * Spring Security 
 * MySQL (banco de dados relacional)
 * H2 (banco de dados em memória para testes)
 * Swagger / OpenAPI 
 * DevTools
 * JUnit & Test Libraries 
```

## Executando o Projeto:
* mvn clean install -> Instale as dependências necessárias
* mvn spring-boot:run -> Rodar o projeto
* mvn test -DexcludedGroups=ui -> Executar testes excluindo testes de interfaces


## Endpoints [Swagger](http://localhost:8080/swagger-ui/index.html#)


### Criar novo cartão
```
Method: POST
URL: http://localhost:8080/cartoes
Body (json):
{
    "numeroCartao": "6549873025634501",
    "senha": "1234"
}
Autenticação: BASIC, com login = username e senha = password
```
#### Respostas:
```
Criação com sucesso:
   Status Code: 201
   Body (json):
{
    "numeroCartao": "1076038107488810",
    "senha": null, (*Não achei correto devolver a senha no body*)
    "saldo": 500
}
-----------------------------------------
Caso o cartão já exista:
   Status Code: 422
   Body (json):
{
    "error": "Cartão ja existente",
    "message": "Já existe um Cartão cadastrado com esse número",
    "timestamp": "2025-09-19T16:03:53.1609858",
    "status": 422
}
-----------------------------------------
Erro de autenticação: 401
```

### Obter saldo do Cartão
```
Method: GET
URL: http://localhost:8080/cartoes/{numeroCartao} , onde {numeroCartao} é o número do cartão que se deseja consultar
Autenticação: BASIC, com login = username e senha = password
```

#### Respostas:
```
Obtenção com sucesso:
   Status Code: 200
   Body: 500.00
-----------------------------------------
Caso o cartão não exista:
   Status Code: 404
{
    "error": "Cartão não encontrado",
    "message": "Cartão não encontrado",
    "timestamp": "2025-09-19T16:04:57.1124388",
    "status": 404
}
-----------------------------------------
Erro de autenticação: 401
```

### Realizar uma Transação
```
Method: POST
URL: http://localhost:8080/transacoes
Body (json):
{
  "numeroCartao": "5637734552302650",
  "senha": "1234",
  "valor": 1
}
Autenticação: BASIC, com login = username e senha = password
```

#### Respostas:
```
Transação realizada com sucesso:
   Status Code: 201
   Body: 
{
    "numeroCartao": "1076038107488810",
    "valor": 1,
    "dataTransacao": "2025-09-19T16:08:21.294747",
    "status": "SUCESSO"
}
-----------------------------------------
Caso alguma regra de autorização tenha barrado a mesma:
   Status Code: 422

* Body Cartão não encontrado * 
{
    "error": "Cartão não encontrado",
    "message": "Cartão não encontrado",
    "timestamp": "2025-09-19T16:07:47.4515996",
    "status": 404
}

* Body Senha incorreta * 
{
    "error": "Falha na transação",
    "message": "SENHA_INVALIDA",
    "timestamp": "2025-09-19T16:09:37.0310726",
    "status": 422
}
* Body Saldo Insuficiente * 
{
    "error": "Falha na transação",
    "message": "SALDO_INSUFICIENTE",
    "timestamp": "2025-09-19T16:09:49.9337462",
    "status": 422
}
-----------------------------------------
Erro de autenticação: 401
```

## Observações:
* As senhas dos cartões são salvas utilizando criptografia
* Os Atributos dos Dtos foram anotados com validações do Jakarta Validation para garantir que os dados atendam às restrições de formato e valor.
* Foi Criado o GlobalExceptionHandler centralizando as exceções. 
* Foi Adicionado testes **automatizados de interface** utilizando Selenium WebDriver e JUnit. Esses testes simulam o comportamento de um usuário real no navegador e garantem o funcionamento correto das principais telas do sistema de cadastro de cartão, consulta de saldo e de realização de transações.
* Para exceção dos testes automatizados, é necessário que os projetos frontend e backend estejam rodando, e que o banco de dados esteja disponível
