package guru.sfg.brewery.web.controllers.api;


import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {


    @Test
    void deleteBeerBadCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-454e8e19c311")
                .header("Api-Key", "spring").header("Api-Secret", "spring12345Bad"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-454e8e19c311")
        .header("Api-Key", "spring").header("Api-Secret", "spring"))
                .andExpect(status().isOk());
    }
    @Test
    void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-454e8e19c311")
                .with(httpBasic("spring", "spring")))
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-454e8e19c311"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBers() throws Exception{
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/97df0c39-90c4-4ae0-b663-454e8e19c311"))
                .andExpect(status().isOk());
    }
    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }
    @Test
    void deleteBeerUrl() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-454e8e19c311")
                .param("apiKey", "spring").param("apiSecret", "spring"))  //?
                .andExpect(status().isOk());
    }
    @Test
    void deleteBeerCredsUrl() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-454e8e19c311")
                .param("apiKey", "spring").header("apiSecret", "guruXXX"))
                .andExpect(status().isUnauthorized());
    }
}
