package org.artanddecor.repository;

import org.artanddecor.model.CartState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartState entity
 * Handles CRUD operations for cart states
 */
@Repository
public interface CartStateRepository extends JpaRepository<CartState, Long> {

    /**
     * Find active cart state (ACTIVE)
     * @return Optional CartState
     */
    @Query("SELECT cs FROM CartState cs WHERE cs.cartStateName = 'ACTIVE' AND cs.cartStateEnabled = true")
    Optional<CartState> findActiveCartState();
}