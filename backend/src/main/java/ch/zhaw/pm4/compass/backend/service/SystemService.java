package ch.zhaw.pm4.compass.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Setter;

/**
 * Service for checking the status of various system components such as the database and external authentication services.
 * This class provides functionalities to ensure that all parts of the system are operational and reachable.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Service
@Setter
public class SystemService {
  @Autowired
  private UserService userService;
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Checks if the backend service is operational. Always returns true as it's a simple check to demonstrate
   * the method could be extended to perform actual system checks.
   *
   * @return always true, indicating the backend is reachable.
   */
  public boolean isBackendReachable() {
    return true;
  }

  /**
   * Verifies that the database is accessible by performing a simple query.
   * Uses a native query that selects a minimal amount of data, designed to test connectivity and not load.
   *
   * @return true if the database query executes successfully, false if an
   *         exception occurs.
   */
  public boolean isDatabaseReachable() {
    try {
      Query query = entityManager.createNativeQuery("SELECT 1");
      query.setHint("javax.persistence.query.timeout", 3000);
      query.getSingleResult();

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Checks if the Auth0 service is reachable by attempting to retrieve a token.
   * This method assumes a valid configuration of the user service to communicate with Auth0.
   *
   * @return true if the token is successfully retrieved, indicating Auth0 is
   *         reachable, false if an exception occurs.
   */
  public boolean isAuth0Reachable() {
    try {
      userService.getToken();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
