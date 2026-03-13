package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.UserRoleDto;
import org.ArtAndDecor.model.UserRole;
import org.ArtAndDecor.repository.UserRoleRepository;
import org.ArtAndDecor.services.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserRole Service Implementation
 * Handles all user role-related business logic (Read-only operations)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRoleServiceImpl implements UserRoleService {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleServiceImpl.class);

    private final UserRoleRepository userRoleRepository;

    @Override
    public List<UserRoleDto> findRolesByCriteria(String userRoleName, String textSearch, Boolean userRoleEnabled) {
        logger.debug("Finding roles by criteria: roleName={}, textSearch={}, enabled={}",
                    userRoleName, textSearch, userRoleEnabled);
        
        List<UserRole> roles = userRoleRepository.findRolesByCriteria(
            userRoleName, textSearch, userRoleEnabled);
        
        return roles.stream()
                   .map(this::convertToDto)
                   .collect(Collectors.toList());
    }

    @Override
    public Optional<UserRoleDto> findRoleById(Long userRoleId) {
        logger.debug("Finding role by ID: {}", userRoleId);
        
        return userRoleRepository.findById(userRoleId)
                                .map(this::convertToDto);
    }

    @Override
    public Optional<UserRoleDto> findRoleByName(String roleName) {
        logger.debug("Finding role by name: {}", roleName);
        
        return userRoleRepository.findByUserRoleName(roleName)
                                .map(this::convertToDto);
    }

    @Override
    public List<UserRoleDto> getAllRoles() {
        logger.debug("Getting all roles");
        
        return userRoleRepository.findAll()
                                .stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllRoleNames() {
        logger.debug("Getting all role names");
        
        return userRoleRepository.findAllRoleNames();
    }

    @Override
    public UserRoleDto convertToDto(UserRole userRole) {
        if (userRole == null) {
            return null;
        }

        UserRoleDto dto = UserRoleDto.builder()
                .userRoleId(userRole.getUserRoleId())
                .userRoleName(userRole.getUserRoleName())
                .userRoleDisplayName(userRole.getUserRoleDisplayName())
                .userRoleRemark(userRole.getUserRoleRemark())
                .userRoleEnabled(userRole.getUserRoleEnabled())
                .build();

        // Add user count
        try {
            Long userCount = userRoleRepository.countUsersByRole(userRole.getUserRoleId());
            dto.setUserCount(userCount);
        } catch (Exception e) {
            logger.warn("Could not get user count for role {}: {}", userRole.getUserRoleId(), e.getMessage());
        }

        return dto;
    }

    @Override
    public UserRole convertToEntity(UserRoleDto userRoleDto) {
        if (userRoleDto == null) {
            return null;
        }

        UserRole role = new UserRole();
        role.setUserRoleId(userRoleDto.getUserRoleId());
        role.setUserRoleName(userRoleDto.getUserRoleName());
        role.setUserRoleDisplayName(userRoleDto.getUserRoleDisplayName());
        role.setUserRoleRemark(userRoleDto.getUserRoleRemark());
        role.setUserRoleEnabled(userRoleDto.getUserRoleEnabled());

        return role;
    }
}