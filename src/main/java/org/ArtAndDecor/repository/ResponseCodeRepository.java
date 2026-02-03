package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.ResponseCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ResponseCode entity
 */
@Repository
public interface ResponseCodeRepository extends JpaRepository<ResponseCode, Long> {

    /**
     * Find ResponseCode by name
     * @param responseCodeName Response code name
     * @return Optional ResponseCode
     */
    Optional<ResponseCode> findByResponseCodeName(String responseCodeName);

    /**
     * Find ResponseCode by name and enabled status
     * @param responseCodeName Response code name
     * @param enabled Enabled status
     * @return Optional ResponseCode
     */
    Optional<ResponseCode> findByResponseCodeNameAndResponseCodeEnabled(String responseCodeName, Boolean enabled);

    /**
     * Find response code with custom query
     * @param codeName Response code name
     * @return Optional ResponseCode
     */
    @Query("SELECT rc FROM ResponseCode rc WHERE rc.responseCodeName = :codeName AND rc.responseCodeEnabled = true")
    Optional<ResponseCode> findEnabledResponseCodeByName(@Param("codeName") String codeName);
}