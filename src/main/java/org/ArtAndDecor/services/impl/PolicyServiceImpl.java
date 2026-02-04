package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.PolicyDto;
import org.ArtAndDecor.model.Policy;
import org.ArtAndDecor.repository.PolicyRepository;
import org.ArtAndDecor.services.PolicyService;
import org.ArtAndDecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Policy Service Implementation
 * Implements business logic for Policy management (system configuration)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PolicyServiceImpl implements PolicyService {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceImpl.class);
    private final PolicyRepository policyRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PolicyDto> findPolicyByName(String policyName) {
        logger.debug("Finding policy by name: {}", policyName);
        return policyRepository.findByPolicyName(policyName)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PolicyDto> findPolicyById(Long policyId) {
        logger.debug("Finding policy by ID: {}", policyId);
        return policyRepository.findById(policyId)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PolicyDto> findPolicyBySlug(String policySlug) {
        logger.debug("Finding policy by slug: {}", policySlug);
        return policyRepository.findByPolicySlug(policySlug)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PolicyDto> findAllEnabledPolicies() {
        logger.debug("Finding all enabled policies");
        return policyRepository.findByPolicyEnabledTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PolicyDto> searchPoliciesByName(String namePattern) {
        logger.debug("Searching policies by name pattern: {}", namePattern);
        if (namePattern == null || namePattern.isBlank()) {
            return List.of();
        }
        return policyRepository.findByPolicyNameContainingIgnoreCase(namePattern)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PolicyDto> searchPoliciesByValue(String valuePattern) {
        logger.debug("Searching policies by value pattern: {}", valuePattern);
        if (valuePattern == null || valuePattern.isBlank()) {
            return List.of();
        }
        return policyRepository.findByPolicyValueContaining(valuePattern)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public PolicyDto createPolicy(PolicyDto policyDto) {
        logger.info("Creating new policy: {}", policyDto.getPolicyName());
        
        // Check if policy name already exists
        if (policyRepository.existsByPolicyName(policyDto.getPolicyName())) {
            logger.warn("Policy name already exists: {}", policyDto.getPolicyName());
            throw new IllegalArgumentException("Policy name already exists: " + policyDto.getPolicyName());
        }
        
        // Auto-generate slug if not provided
        String slug = policyDto.getPolicySlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(policyDto.getPolicyName());
            logger.info("Auto-generated slug for policy: {}", slug);
        }
        
        // Create entity
        Policy policy = new Policy();
        policy.setPolicyName(policyDto.getPolicyName());
        policy.setPolicySlug(slug);
        policy.setPolicyValue(policyDto.getPolicyValue());
        policy.setPolicyRemarkEn(policyDto.getPolicyRemarkEn());
        policy.setPolicyRemark(policyDto.getPolicyRemark());
        policy.setPolicyEnabled(policyDto.getPolicyEnabled() != null ? policyDto.getPolicyEnabled() : true);
        
        Policy saved = policyRepository.save(policy);
        logger.info("Policy created successfully with ID: {}", saved.getPolicyId());
        
        return mapToDto(saved);
    }
    
    @Override
    public PolicyDto updatePolicy(Long policyId, PolicyDto policyDto) {
        logger.info("Updating policy with ID: {}", policyId);
        
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> {
                    logger.error("Policy not found with ID: {}", policyId);
                    return new IllegalArgumentException("Policy not found with ID: " + policyId);
                });
        
        // Check if policy name is being changed and if new name already exists
        if (!policy.getPolicyName().equals(policyDto.getPolicyName())) {
            if (policyRepository.existsByPolicyName(policyDto.getPolicyName())) {
                logger.warn("New policy name already exists: {}", policyDto.getPolicyName());
                throw new IllegalArgumentException("Policy name already exists: " + policyDto.getPolicyName());
            }
        }
        
        // Auto-generate slug if not provided
        String slug = policyDto.getPolicySlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(policyDto.getPolicyName());
            logger.info("Auto-generated slug for policy: {}", slug);
        }
        
        // Update entity
        policy.setPolicyName(policyDto.getPolicyName());
        policy.setPolicySlug(slug);
        policy.setPolicyValue(policyDto.getPolicyValue());
        policy.setPolicyRemarkEn(policyDto.getPolicyRemarkEn());
        policy.setPolicyRemark(policyDto.getPolicyRemark());
        if (policyDto.getPolicyEnabled() != null) {
            policy.setPolicyEnabled(policyDto.getPolicyEnabled());
        }
        
        Policy updated = policyRepository.save(policy);
        logger.info("Policy updated successfully with ID: {}", updated.getPolicyId());
        
        return mapToDto(updated);
    }
    
    @Override
    public PolicyDto updatePolicyStatus(Long policyId, Boolean enabled) {
        logger.info("Updating policy status - ID: {}, Enabled: {}", policyId, enabled);
        
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> {
                    logger.error("Policy not found with ID: {}", policyId);
                    return new IllegalArgumentException("Policy not found with ID: " + policyId);
                });
        
        policy.setPolicyEnabled(enabled);
        Policy updated = policyRepository.save(policy);
        
        logger.info("Policy status updated successfully with ID: {}", policyId);
        return mapToDto(updated);
    }
    
    @Override
    public PolicyDto updatePolicyValue(Long policyId, String newValue) {
        logger.info("Updating policy value - ID: {}", policyId);
        
        if (newValue == null || newValue.isBlank()) {
            throw new IllegalArgumentException("Policy value cannot be empty");
        }
        
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> {
                    logger.error("Policy not found with ID: {}", policyId);
                    return new IllegalArgumentException("Policy not found with ID: " + policyId);
                });
        
        policy.setPolicyValue(newValue);
        Policy updated = policyRepository.save(policy);
        
        logger.info("Policy value updated successfully for policy: {}", policy.getPolicyName());
        return mapToDto(updated);
    }
    
    @Override
    public void deletePolicy(Long policyId) {
        logger.info("Deleting policy with ID: {}", policyId);
        
        if (!policyRepository.existsById(policyId)) {
            logger.error("Policy not found with ID: {}", policyId);
            throw new IllegalArgumentException("Policy not found with ID: " + policyId);
        }
        
        policyRepository.deleteById(policyId);
        logger.info("Policy deleted successfully with ID: {}", policyId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalPolicyCount() {
        return policyRepository.count();
    }
    
    // =============================================
    // HELPER METHODS
    // =============================================
    
    /**
     * Map Policy entity to PolicyDto
     * @param policy the policy entity
     * @return PolicyDto
     */
    private PolicyDto mapToDto(Policy policy) {
        if (policy == null) {
            return null;
        }
        
        return PolicyDto.builder()
                .policyId(policy.getPolicyId())
                .policyName(policy.getPolicyName())
                .policySlug(policy.getPolicySlug())
                .policyValue(policy.getPolicyValue())
                .policyRemarkEn(policy.getPolicyRemarkEn())
                .policyRemark(policy.getPolicyRemark())
                .policyEnabled(policy.getPolicyEnabled())
                .createdDt(policy.getCreatedDt())
                .modifiedDt(policy.getModifiedDt())
                .build();
    }
}
