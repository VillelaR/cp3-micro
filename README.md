# Study Apir

API Java para estudo de Spring Boot

## Criando .jar

```
mvn clean package
```

- Localizar o .jar em /target

```
java -Dspring.profiles.active=dev -jar target/app.jar
```

## Variáveis de ambiente

```
export DB_USER=root
export DB_PASSWORD=root_pwd
export DB_SERVER=localhost
export DB_PORT=3306
export DB_DATABASE=api
```

* Usando arquivo .env e carregando em ambiente bash (Linux/Mac)

```
export $(cat .env | xargs)
env | grep DB_
```

## MER

![](assets/images/mer.png)

## Instalação

* Limpar e criar a pasta */target*

```
mvn clean package
```

* Configuração do Swagger

    - https://springdoc.org/properties.html

- application.properties

```
springdoc.swagger-ui.path=/
springdoc.swagger-ui.disable-swagger-default-url=true
```


## Navegação

### Executar a API

-  *Executando* **Maven**

```
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

-  *Instruções* **Docker Compose**

    -  https://devhints.io/docker-compose

```
docker compose up
docker compose up db
docker compose stop
```

### Documentação da API (Swagger)
- http://localhost:8080/swagger-ui.html


## Referencias

- https://springdoc.org/


#Teste integrado de produtos (CRUD)

-- Criar um produto
curl -X 'POST' \
  'http://localhost:9000/api/v2/produtos' \
  -o produto.json \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "nome": "Uva"
}'

-- Pegar o ID do produto criado
PRODUTO_ID=$(jq '.id' produto.json)
echo $PRODUTO_ID

-- Listar o produto pelo ID
curl -X 'GET' \
  http://localhost:9000/api/v2/produtos/$PRODUTO_ID \
  -H 'accept: */*'
  
-- Alterar o valor do produto
curl -X 'PUT' \
  http://localhost:9000/api/v2/produtos/$PRODUTO_ID \
  -o product_updated.json \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "valor": 500
}'

-- Excluir o produto
curl -X 'DELETE' \
  http://localhost:9000/api/v2/produtos/$PRODUTO_ID \
  -H 'accept: */*'

-- Listar produtos
curl -X 'GET' \
  'http://localhost:9000/api/v2/produtos' \
  -H 'accept: */*'



  name: Teste Integrado de API - Produtos

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  run-tests:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout do código
        uses: actions/checkout@v2

      - name: Configuração do ambiente
        run: |
          sudo apt-get update
          sudo apt-get install -y curl jq

      - name: Teste GET /api/v2/produtos
        run: |
          echo "Testando GET /api/v2/produtos"
          response=$(curl -s -o response.json -w "%{http_code}" http://localhost:9000/api/v2/produtos)
          if [ "$response" -ne 200 ]; then
            echo "Erro ao acessar GET /api/v2/produtos. Código de resposta: $response"
            cat response.json
            exit 1
          fi
          echo "GET /api/v2/produtos - Sucesso"

      - name: Teste POST /api/v2/produtos
        run: |
          echo "Testando POST /api/v2/produtos"
          response=$(curl -s -o response.json -w "%{http_code}" -X POST http://localhost:9000/api/v2/produtos -H "Content-Type: application/json" -d '{"nome": "Produto Teste"}')
          if [ "$response" -ne 200 ]; then
            echo "Erro ao acessar POST /api/v2/produtos. Código de resposta: $response"
            cat response.json
            exit 1
          fi
          echo "POST /api/v2/produtos - Sucesso"

      - name: Teste PUT /api/v2/produtos/{id}
        run: |
          echo "Testando PUT /api/v2/produtos/{id}"
          # Primeiro criamos um produto para atualizar
          create_response=$(curl -s -o create_response.json -w "%{http_code}" -X POST http://localhost:9000/api/v2/produtos -H "Content-Type: application/json" -d '{"nome": "Produto Teste Update"}')
          product_id=$(jq '.id' create_response.json)
          
          # Agora atualizamos o produto
          response=$(curl -s -o response.json -w "%{http_code}" -X PUT http://localhost:9000/api/v2/produtos/$product_id -H "Content-Type: application/json" -d '{"valor": 20.5}')
          if [ "$response" -ne 200 ]; then
            echo "Erro ao acessar PUT /api/v2/produtos/$product_id. Código de resposta: $response"
            cat response.json
            exit 1
          fi
          echo "PUT /api/v2/produtos/$product_id - Sucesso"

      - name: Teste DELETE /api/v2/produtos/{id}
        run: |
          echo "Testando DELETE /api/v2/produtos/{id}"
          # Primeiro criamos um produto para excluir
          create_response=$(curl -s -o create_response.json -w "%{http_code}" -X POST http://localhost:9000/api/v2/produtos -H "Content-Type: application/json" -d '{"nome": "Produto Teste Delete"}')
          product_id=$(jq '.id' create_response.json)
          
          # Agora deletamos o produto
          response=$(curl -s -o response.json -w "%{http_code}" -X DELETE http://localhost:9000/api/v2/produtos/$product_id)
          if [ "$response" -ne 200 ]; then
            echo "Erro ao acessar DELETE /api/v2/produtos/$product_id. Código de resposta: $response"
            cat response.json
            exit 1
          fi
          echo "DELETE /api/v2/produtos/$product_id - Sucesso"

