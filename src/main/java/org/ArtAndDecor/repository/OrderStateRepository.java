package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OrderState Repository for database operations
 * Cleaned up - Contains only methods used by services
 */
@Repository
public interface OrderStateRepository extends JpaRepository<OrderState, Long> {

    /**
     * Find order state by name
     * @param orderStateName Order state name
     * @return Optional OrderState
     */
    Optional<OrderState> findByOrderStateName(String orderStateName);

    /**
     * Find enabled order states
     * @param enabled Enabled status
     * @return List of enabled order states
     */
    List<OrderState> findByOrderStateEnabledOrderByOrderStateName(Boolean enabled);
}