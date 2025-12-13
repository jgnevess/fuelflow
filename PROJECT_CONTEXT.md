# FuelFlow API – Project Context

## Objetivo do Projeto
Este projeto é uma API REST focada em análise de preços de combustíveis por cidade, bairro e posto, com ênfase em:
- desempenho
- cache
- modelagem de dados
- clareza de contrato (API bem definida)

O projeto é voltado para portfólio profissional, com foco em backend engineering.

---

## Stack Tecnológica
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Redis (cache)
- Swagger / OpenAPI
- Docker (ambiente local)
- HTML + CSS + JS (frontend simples para demonstração)
- Tailwind ou Bootstrap (no máximo)

NÃO utilizar frameworks frontend como React, Angular ou Vue.

---

## Princípios do Projeto
- Backend-first
- API pública (sem autenticação por enquanto)
- Frontend apenas para visualização e demonstração
- Código organizado, sem overengineering
- Cache aplicado apenas onde faz sentido
- Decisões técnicas devem ser justificáveis

---

## Domínio do Negócio
A API trabalha com:
- Combustíveis (Gasolina, Etanol, Diesel, etc.)
- Postos de combustível (identificados por CNPJ)
- Localização (Estado, Cidade, Bairro)
- Histórico de preços

---

## Endpoints Existentes (exemplos)
- Média de preços por cidade
- Média por bairro
- Ranking de melhores preços
- Posto mais barato
- Estatísticas (min, max, média, desvio padrão)
- Últimos preços registrados

---

## Estrutura de Resposta
As respostas da API seguem estes princípios:
- Contexto explícito (cidade, estado, tipo de cálculo)
- Dados prontos para consumo frontend
- Sem lógica implícita no cliente
- Campos com nomes claros

Exemplo de contexto:
- MEDIA_POR_CIDADE
- TOP_3_MELHORES_PREÇOS_POR_CIDADE
- CHEAPEST_STATION

---

## Cache (Redis)
Regras para cache:
- Cachear DTOs, não entidades JPA
- Cache aplicado apenas em endpoints de leitura pesada
- TTL padrão: 15 a 30 minutos
- Chaves de cache devem incluir:
    - estado
    - cidade
    - bairro (quando aplicável)
    - produto

Não cachear:
- endpoints de escrita
- dados muito voláteis

---

## Serialização
- Usar JSON como formato de cache
- Evitar Serializable/JDK serialization
- Jackson configurado com suporte a datas (JavaTimeModule)

---

## Frontend (Demonstração)
O frontend deve:
- Consumir a API real
- Exibir dados como:
    - tabelas
    - rankings
    - indicadores simples
- Não conter lógica de negócio
- Servir apenas como vitrine técnica

---

## Segurança
- API pública
- CORS liberado apenas para o frontend do projeto
- Rate limit opcional
- Sem autenticação por enquanto

---

## O que NÃO fazer
- Não transformar em sistema comercial
- Não criar painel administrativo
- Não adicionar login
- Não usar frameworks frontend complexos
- Não fazer overengineering

---

## Como o ChatGPT deve ajudar
Ao ajudar neste projeto, o ChatGPT deve:
- Priorizar decisões simples e maduras
- Evitar sugestões desnecessárias
- Explicar trade-offs técnicos
- Pensar como um backend engineer experiente
- Não sugerir tecnologias fora da stack definida

