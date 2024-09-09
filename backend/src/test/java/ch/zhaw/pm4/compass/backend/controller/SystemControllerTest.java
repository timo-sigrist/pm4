package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.SystemStatusDto;
import ch.zhaw.pm4.compass.backend.service.SystemService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@TestPropertySource(locations = "classpath:git.properties")
public class SystemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext controller;
    @MockBean
    private SystemService systemService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    SystemStatusDto getSystemStatusDto() {
        return new SystemStatusDto("commitId", true, true, true);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testGetSystemStatus() throws Exception {
        when(systemService.isBackendReachable()).thenReturn(true);
        when(systemService.isDatabaseReachable()).thenReturn(true);
        when(systemService.isAuth0Reachable()).thenReturn(true);

        // Act
        mockMvc.perform(get("/system/status")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
                .andExpect(jsonPath("$.commitId").value(getSystemStatusDto().getCommitId()))
                .andExpect(jsonPath("$.backendIsReachable").value(getSystemStatusDto().isBackendIsReachable()))
                .andExpect(jsonPath("$.databaseIsReachable").value(getSystemStatusDto().isDatabaseIsReachable()))
                .andExpect(jsonPath("$.auth0IsReachable").value(getSystemStatusDto().isAuth0IsReachable()));
    }
}
