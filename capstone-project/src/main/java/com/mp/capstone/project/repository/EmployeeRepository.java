package com.mp.capstone.project.repository;

import com.mp.capstone.project.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    /**
     * Looks up an employee by their Auth0 user_id (e.g. {@code auth0|64f1...}).
     * Used by {@link com.mp.capstone.project.service.Auth0LoginService} after login
     * to resolve the local employee from the Auth0 subject claim in the JWT.
     */
    Optional<Employee> findByAuth0UserId(String auth0UserId);

    /**
     * Returns true if the given medical record is assigned to the given employee.
     * Used to gate access before allowing an employee to read or update a record.
     */
    @Query("""
            SELECT COUNT(e) > 0
            FROM Employee e
            JOIN e.records r
            WHERE e.id = :empId
              AND r.id = :recordId
            """)
    boolean isRecordAssignedToEmployee(@Param("empId") String empId,
                                       @Param("recordId") String recordId);

    /**
     * Checks whether an Auth0 user ID is already registered to an employee.
     * Prevents duplicate Auth0 registrations on retry scenarios.
     */
    boolean existsByAuth0UserId(String auth0UserId);
}