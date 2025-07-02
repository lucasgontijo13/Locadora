package br.edu.ifmg.locadora.resources;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class RentalResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //Simula a busca de todos os aluguéis por um administrador

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testFindAllRentals() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rentals"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    //Simula a busca de um aluguel especifico pelo seu ID por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testFindRentalById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rentals/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    // Simula a inserção de um novo aluguel por um cliente
    @Test
    @WithMockUser(authorities = "ROLE_CLIENT")
    public void testInsertRental() throws Exception {
        String json = "{\"rentalDate\": \"2025-12-20T14:00:00Z\", \"returnDate\": \"2025-12-25T12:00:00Z\", \"user\": {\"id\": 1}, \"vehicle\": {\"id\": 1}}";

        mockMvc.perform(MockMvcRequestBuilders.post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    //Simula a atualização de um aluguel existente por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testUpdateRental() throws Exception {
        String json = "{\"rentalDate\": \"2025-12-21T14:00:00Z\", \"returnDate\": \"2025-12-26T12:00:00Z\", \"user\": {\"id\": 1}, \"vehicle\": {\"id\": 1}}";

        mockMvc.perform(MockMvcRequestBuilders.put("/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    //Simula a exclusão de um aluguel por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testDeleteRental() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rentals/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Simula a busca de todos os aluguéis de um usuário especifico por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testFindByUserId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rentals/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    //Simula a obtenção do valor total de aluguéis de um usuário
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_CLIENT"})
    public void testGetTotalValueByUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rentals/user/1/total"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    //Simula a busca pelo aluguel de maior valor de um usuário
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_CLIENT"})
    public void testGetHighestValueRentalByUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rentals/user/1/highest"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    //Simula a busca pelo aluguel de menor valor de um usuário
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_CLIENT"})
    public void testGetLowestValueRentalByUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rentals/user/1/lowest"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}