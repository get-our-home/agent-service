package com.getourhome.agentservice;

import com.getourhome.agentservice.util.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AgentServiceApplicationTests {
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Test
    void contextLoads() {
    }

}
