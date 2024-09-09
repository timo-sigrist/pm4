package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class SystemServiceTest {
  @Mock
  private UserService userService;
  @Mock
  private EntityManager entityManager;

  private SystemService systemService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    systemService = new SystemService();
    systemService.setUserService(userService);
    systemService.setEntityManager(entityManager);
  }

  @Test
  public void testIsBackendReachable() {
    SystemService systemService = new SystemService();
    boolean result = systemService.isBackendReachable();

    assertTrue(result);
  }

  @Test
  public void testIsDatabaseReachable() {
    Query query = mock(Query.class);

    doReturn(1).when(query).getSingleResult();
    doReturn(query).when(entityManager).createNativeQuery("SELECT 1");
    assertTrue(systemService.isDatabaseReachable());

    doThrow(RuntimeException.class).when(entityManager).createNativeQuery("SELECT 1");
    assertFalse(systemService.isDatabaseReachable());
  }

  @Test
  public void testIsAuth0Reachable() {
    doReturn("token").when(userService).getToken();
    assertTrue(systemService.isAuth0Reachable());

    doThrow(RuntimeException.class).when(userService).getToken();
    assertFalse(systemService.isAuth0Reachable());
  }
}
