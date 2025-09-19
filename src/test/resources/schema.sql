-- Tabela cartao
CREATE TABLE cartao (
  id VARBINARY(16) NOT NULL,
  numero_cartao VARCHAR(255) NOT NULL,
  saldo DECIMAL(38,2) DEFAULT NULL,
  senha VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT UK_numero_cartao UNIQUE (numero_cartao)
);

-- Tabela transacao
CREATE TABLE transacao (
  id VARBINARY(16) NOT NULL,
  data_hora TIMESTAMP DEFAULT NULL,
  status VARCHAR(255) DEFAULT NULL,
  valor DECIMAL(38,2) DEFAULT NULL,
  cartao_id VARBINARY(16) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FK_cartao FOREIGN KEY (cartao_id) REFERENCES cartao(id)
);