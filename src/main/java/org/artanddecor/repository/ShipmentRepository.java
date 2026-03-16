package org.artanddecor.repository;

import org.artanddecor.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByShipmentCode(String shipmentCode);

    List<Shipment> findByOrderOrderIdOrderByCreatedDtDesc(Long orderId);

    Page<Shipment> findByShipmentStateShipmentStateId(Long shipmentStateId, Pageable pageable);

    boolean existsByShipmentCodeAndShipmentIdNot(String shipmentCode, Long shipmentId);

    boolean existsByShipmentCode(String shipmentCode);

    @Query("SELECT s FROM Shipment s WHERE " +
           "(:shipmentId IS NULL OR s.shipmentId = :shipmentId) AND " +
           "(:orderId IS NULL OR s.order.orderId = :orderId) AND " +
           "(:shipmentCode IS NULL OR LOWER(s.shipmentCode) LIKE LOWER(CONCAT('%', :shipmentCode, '%'))) AND " +
           "(:shipmentStateId IS NULL OR s.shipmentState.shipmentStateId = :shipmentStateId) AND " +
           "(:receiverName IS NULL OR LOWER(s.receiverName) LIKE LOWER(CONCAT('%', :receiverName, '%'))) AND " +
           "(:receiverPhone IS NULL OR s.receiverPhone LIKE CONCAT('%', :receiverPhone, '%')) AND " +
           "(:city IS NULL OR LOWER(s.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:country IS NULL OR LOWER(s.country) LIKE LOWER(CONCAT('%', :country, '%'))) AND " +
           "(:minShippingFee IS NULL OR s.shippingFeeAmount >= :minShippingFee) AND " +
           "(:maxShippingFee IS NULL OR s.shippingFeeAmount <= :maxShippingFee) AND " +
           "(:shippedAfter IS NULL OR s.shippedAt >= :shippedAfter) AND " +
           "(:shippedBefore IS NULL OR s.shippedAt <= :shippedBefore) AND " +
           "(:deliveredAfter IS NULL OR s.deliveredAt >= :deliveredAfter) AND " +
           "(:deliveredBefore IS NULL OR s.deliveredAt <= :deliveredBefore) AND " +
           "(:textSearch IS NULL OR " +
           "  LOWER(s.shipmentCode) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(s.receiverName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(s.receiverPhone) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(s.city) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(s.shipmentRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<Shipment> findByCriteria(
            @Param("shipmentId") Long shipmentId,
            @Param("orderId") Long orderId,
            @Param("shipmentCode") String shipmentCode,
            @Param("shipmentStateId") Long shipmentStateId,
            @Param("receiverName") String receiverName,
            @Param("receiverPhone") String receiverPhone,
            @Param("city") String city,
            @Param("country") String country,
            @Param("minShippingFee") BigDecimal minShippingFee,
            @Param("maxShippingFee") BigDecimal maxShippingFee,
            @Param("shippedAfter") LocalDateTime shippedAfter,
            @Param("shippedBefore") LocalDateTime shippedBefore,
            @Param("deliveredAfter") LocalDateTime deliveredAfter,
            @Param("deliveredBefore") LocalDateTime deliveredBefore,
            @Param("textSearch") String textSearch,
            Pageable pageable);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.shippedAt IS NOT NULL")
    long countShippedShipments();

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.deliveredAt IS NOT NULL")
    long countDeliveredShipments();

    @Query("SELECT AVG(DATEDIFF(s.deliveredAt, s.shippedAt)) FROM Shipment s WHERE s.shippedAt IS NOT NULL AND s.deliveredAt IS NOT NULL")
    Double getAverageDeliveryTimeInDays();

    // Additional methods for statistics
    long countByShipmentStateShipmentStateId(Long shipmentStateId);

    @Query("SELECT SUM(s.shippingFeeAmount) FROM Shipment s")
    BigDecimal sumAllShippingFees();

    long countByCreatedDtAfter(LocalDateTime dateTime);
}