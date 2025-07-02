package br.edu.ifmg.locadora.resource;

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
public class VehicleResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Simula a busca de todos os veículos
    @Test
    public void testFindAllVehicles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Simula a busca de um veículo específico pelo seu ID
    @Test
    public void testFindVehicleById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    // Simula a inserção de um novo veículo por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testInsertVehicle() throws Exception {
        String json = "{\"brand\": \"Nova Marca\", \"model\": \"Novo Modelo\", \"licensePlate\": \"XYZ-5678\", \"dailyRate\": 150.0}";

        mockMvc.perform(MockMvcRequestBuilders.post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    // Simula a atualização de um veículo existente por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testUpdateVehicle() throws Exception {
        String json = "{\"brand\": \"Marca Atualizada\", \"model\": \"Modelo Atualizado\", \"licensePlate\": \"ABC-1234\", \"dailyRate\": 160.0}";

        mockMvc.perform(MockMvcRequestBuilders.put("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Simula a exclusão de um veículo por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testDeleteVehicle() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/vehicles/4"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Simula a busca por veículos disponíveis em um determinado período
    @Test
    public void testFindAvailableVehicles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles/available/2025-12-01/2025-12-10"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}