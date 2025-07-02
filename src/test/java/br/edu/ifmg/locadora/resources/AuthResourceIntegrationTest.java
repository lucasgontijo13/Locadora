package br.edu.ifmg.locadora.resources;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateRecoverToken() throws Exception {
        //verifica se o envio do token esta correto
        String json = "{\"email\": \"lucasgontijo111@gmail.com\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/recover-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testSaveNewPassword() throws Exception {
        //simula um token inválido para testar o endpoint
        String json = "{\"newPassword\": \"novaSenha123\", \"token\": \"token-invalido\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/new-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}