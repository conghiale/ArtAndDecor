package org.artanddecor.repository;

import org.artanddecor.model.ShipmentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentStateRepository extends JpaRepository<ShipmentState, Long> {

    Optional<ShipmentState> findByShipmentStateName(String shipmentStateName);

    List<ShipmentState> findByShipmentStateEnabledTrueOrderByShipmentStateName();

    boolean existsByShipmentStateNameAndShipmentStateIdNot(String shipmentStateName, Long shipmentStateId);

    boolean existsByShipmentStateName(String shipmentStateName);

    @Query("SELECT ss FROM ShipmentState ss WHERE " +
           "(:shipmentStateId IS NULL OR ss.shipmentStateId = :shipmentStateId) AND " +
           "(:shipmentStateName IS NULL OR LOWER(ss.shipmentStateName) LIKE LOWER(CONCAT('%', :shipmentStateName, '%'))) AND " +
           "(:shipmentStateEnabled IS NULL OR ss.shipmentStateEnabled = :shipmentStateEnabled) AND " +
           "(:textSearch IS NULL OR " +
           "  LOWER(ss.shipmentStateName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(ss.shipmentStateDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(ss.shipmentStateRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<ShipmentState> findByCriteria(
            @Param("shipmentStateId") Long shipmentStateId,
            @Param("shipmentStateName") String shipmentStateName,
            @Param("shipmentStateEnabled") Boolean shipmentStateEnabled,
            @Param("textSearch") String textSearch,
            Pageable pageable);

    @Query("SELECT ss.shipmentStateName FROM ShipmentState ss WHERE ss.shipmentStateEnabled = true ORDER BY ss.shipmentStateName")
    List<String> findAllEnabledShipmentStateNames();
}