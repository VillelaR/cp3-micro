package com.github.acnaweb.study_apir.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.acnaweb.study_apir.dto.produto.ProdutoRequestCreate;
import com.github.acnaweb.study_apir.dto.produto.ProdutoRequestUpdate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerProdutoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveExecutarFluxoCompletoCrudProduto() throws Exception {
        ProdutoRequestCreate createRequest = new ProdutoRequestCreate();
        createRequest.setNome("Notebook Gamer");

        String createContent = objectMapper.writeValueAsString(createRequest);

        var createResult = mockMvc.perform(post("/api/v2/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createContent))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nome").value("Notebook Gamer"))
            .andExpect(jsonPath("$.valor").value(2000))
            .andReturn();

        JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long produtoId = createJson.get("id").asLong();

        mockMvc.perform(get("/api/v2/produtos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(produtoId))
            .andExpect(jsonPath("$[0].nome").value("Notebook Gamer"))
            .andExpect(jsonPath("$[0].valor").value(2000));

        mockMvc.perform(get("/api/v2/produtos/{id}", produtoId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(produtoId))
            .andExpect(jsonPath("$.nome").value("Notebook Gamer"))
            .andExpect(jsonPath("$.valor").value(2000));

        ProdutoRequestUpdate updateRequest = new ProdutoRequestUpdate();
        updateRequest.setValor(new BigDecimal("3499.90"));

        mockMvc.perform(put("/api/v2/produtos/{id}", produtoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valor").value(3499.90));

        mockMvc.perform(delete("/api/v2/produtos/{id}", produtoId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v2/produtos/{id}", produtoId))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v2/produtos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}

