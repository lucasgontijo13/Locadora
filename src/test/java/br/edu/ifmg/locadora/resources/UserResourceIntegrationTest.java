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
public class UserResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //Simula a busca de todos os usarios por um administrador
     
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testFindAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    
    //Simula a busca de um usario específico pelo ID por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testFindUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    
    //Simula a inserção de um novo usario por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testInsertUser() throws Exception {
        String json = "{\"name\": \"Novo Usuario\", \"email\": \"novousuario@email.com\", \"phone\": \"123456789\", \"username\": \"novousuario\", \"password\": \"senha123\", \"roles\": [{\"authority\": \"ROLE_CLIENT\"}]}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    //Simula a atualização de um usuario existente por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testUpdateUser() throws Exception {
        String json = "{\"name\": \"Usuario Atualizado\", \"email\": \"usuario@email.com\", \"phone\": \"987654321\", \"username\": \"usuario\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    //Simula a exclusão de um usario por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/2"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    //Simula a busca de todos os usarios com perfil de cliente por um administrador

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testFindAllClients() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/clients"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    //Simula a busca de um cliente específico pelo ID por um administrador
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testFindClientById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/clients/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2));
    }

    //Simula o registro de um novo cliente através do endpoint público de cadastro
    @Test
    public void testSignUp() throws Exception {
        String json = "{\"name\": \"Novo Cliente\", \"email\": \"novocliente@email.com\", \"phone\": \"1122334455\", \"username\": \"novocliente\", \"password\": \"senhaNova\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}