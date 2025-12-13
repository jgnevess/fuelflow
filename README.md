# FuelFlow

🔥 **FuelFlow** é um backend em **Java** voltado para o gerenciamento e análise de **preços e histórico de combustíveis** em postos de gasolina no Brasil.

O projeto organiza e padroniza **dados públicos disponibilizados pelo governo brasileiro**, facilitando a consulta e a análise das informações por pessoas e sistemas.

A API está disponível publicamente em:

🔗 **Link futuro**

A aplicação foi estruturada com **Maven** e preparada para execução via **Docker**, garantindo um ambiente simples, reproduzível e fácil de evoluir.

Foco em clareza, desempenho e extensibilidade.

---

## 🚀 Objetivo

Disponibilizar uma **API pública** para consulta e análise de **preços e histórico de combustíveis** no Brasil, com foco em organização, padronização e fácil consumo dos dados.

A aplicação é executada em ambiente controlado e mantida como **código fechado**, sendo disponibilizado publicamente apenas o acesso à API.

O serviço foi projetado para operar de forma estável em produção, com possibilidade de evolução contínua e integração com sistemas externos.


## 🧱 Stack

- **Java**
- **Maven**
- **Postgres**
- **Docker / Docker Compose**
- Arquitetura backend organizada por camadas

---

## 🌐 API Endpoints

Base path:
```
/api/v1/fuel
```

---

### 🔍 Listar postos por município
```
GET /api/v1/fuel/municipality
```

**Query params**

| Nome | Tipo | Descrição |
|-----|------|-----------|
| municipality | string | Nome do município |
| state | string | UF |
| pageNumber | int | Número da página |

**Resposta**
- `Page<FuelStationResponse>`
---

### 🏘️ Listar postos por bairro
```
GET /api/v1/fuel/neighborhood
```

**Query params**

| Nome | Tipo | Descrição |
|-----|------|-----------|
| municipality | string | Nome do município |
| state | string | UF |
| neighborhood | string | Nome do bairro |
| pageNumber | int | Número da página |

**Resposta**
- `Page<FuelStationResponse>`

---

### ⛽ Buscar posto por CNPJ

```
GET /api/v1/fuel/station/{cnpj}
```

**Path param**

| Nome | Tipo | Descrição |
|-----|------|-----------|
| cnpj | string | CNPJ do posto |

**Resposta**

- `FuelStationResponse`

---

### 📊 Média de preços por município
```
GET /api/v1/fuel/{state}/{municipality}/avg
```

**Path params**

| Nome | Tipo | Descrição |
|-----|------|-----------|
| state | string | UF |
| municipality | string | Município |

**Resposta**

- Média de preços dos combustíveis

---

### 📊 Média de preços por bairro

```
GET /api/v1/fuel/{state}/{municipality}/{neighborhood}/avg
```

**Path params**

| Nome | Tipo | Descrição |
|-----|------|-----------|
| state | string | UF |
| municipality | string | Município |
| neighborhood | string | Bairro |

**Resposta**

- Média de preços dos combustíveis no bairro

---

### 💰 Posto mais barato por combustível
```
GET /api/v1/fuel/{state}/{municipality}/cheapest
```

**Query params**

| Nome | Tipo | Descrição |
|-----|------|-----------|
| product | string | Tipo de combustível (ex: GASOLINA, ETANOL) |

**Resposta**

- Posto com menor preço para o combustível informado

---

### 📈 Top maiores preços por combustível
```
GET /api/v1/fuel/{state}/{municipality}/top-prices
```

**Query params**

| Nome | Tipo | Descrição |
|-----|------|-----------|
| product | string | Tipo de combustível |

**Resposta**
- Lista de postos com os maiores preços

---

### 📉 Piores preços por combustível
```
GET /api/v1/fuel/{state}/{municipality}/worst-prices
```
--- 

**Query params**

| Nome | Tipo | Descrição |
|-----|------|-----------|
| product | string | Tipo de combustível |

**Resposta**

- Lista de postos com os piores preços

---

> Todos os endpoints retornam `200 OK` em caso de sucesso.  
> Parâmetros inválidos podem resultar em `400 Bad Request`.
