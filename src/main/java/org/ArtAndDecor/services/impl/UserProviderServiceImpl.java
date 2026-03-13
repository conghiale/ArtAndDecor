package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.UserProviderDto;
import org.ArtAndDecor.model.UserProvider;
import org.ArtAndDecor.repository.UserProviderRepository;
import org.ArtAndDecor.services.UserProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserProvider Service Implementation
 * Handles all user provider-related business logic (Read-only operations)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProviderServiceImpl implements UserProviderService {

    private static final Logger logger = LoggerFactory.getLogger(UserProviderServiceImpl.class);

    private final UserProviderRepository userProviderRepository;

    @Override
    public List<UserProviderDto> findProvidersByCriteria(String userProviderName, String textSearch, Boolean userProviderEnabled) {
        logger.debug("Finding providers by criteria: providerName={}, textSearch={}, enabled={}",
                    userProviderName, textSearch, userProviderEnabled);
        
        List<UserProvider> providers = userProviderRepository.findProvidersByCriteria(
            userProviderName, textSearch, userProviderEnabled);
        
        return providers.stream()
                       .map(this::convertToDto)
                       .collect(Collectors.toList());
    }

    @Override
    public Optional<UserProviderDto> findProviderById(Long userProviderId) {
        logger.debug("Finding provider by ID: {}", userProviderId);
        
        return userProviderRepository.findById(userProviderId)
                                   .map(this::convertToDto);
    }

    @Override
    public Optional<UserProviderDto> findProviderByName(String providerName) {
        logger.debug("Finding provider by name: {}", providerName);
        
        return userProviderRepository.findByUserProviderName(providerName)
                                   .map(this::convertToDto);
    }

    @Override
    public List<UserProviderDto> getAllProviders() {
        logger.debug("Getting all providers");
        
        return userProviderRepository.findAll()
                                   .stream()
                                   .map(this::convertToDto)
                                   .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllProviderNames() {
        logger.debug("Getting all provider names");
        
        return userProviderRepository.findAllProviderNames();
    }

    @Override
    public UserProviderDto convertToDto(UserProvider userProvider) {
        if (userProvider == null) {
            return null;
        }

        UserProviderDto dto = UserProviderDto.builder()
                .userProviderId(userProvider.getUserProviderId())
                .userProviderName(userProvider.getUserProviderName())
                .userProviderDisplayName(userProvider.getUserProviderDisplayName())
                .userProviderRemark(userProvider.getUserProviderRemark())
                .userProviderEnabled(userProvider.getUserProviderEnabled())
                .build();

        // Add user count
        try {
            Long userCount = userProviderRepository.countUsersByProvider(userProvider.getUserProviderId());
            dto.setUserCount(userCount);
        } catch (Exception e) {
            logger.warn("Could not get user count for provider {}: {}", userProvider.getUserProviderId(), e.getMessage());
        }

        return dto;
    }

    @Override
    public UserProvider convertToEntity(UserProviderDto userProviderDto) {
        if (userProviderDto == null) {
            return null;
        }

        UserProvider provider = new UserProvider();
        provider.setUserProviderId(userProviderDto.getUserProviderId());
        provider.setUserProviderName(userProviderDto.getUserProviderName());
        provider.setUserProviderDisplayName(userProviderDto.getUserProviderDisplayName());
        provider.setUserProviderRemark(userProviderDto.getUserProviderRemark());
        provider.setUserProviderEnabled(userProviderDto.getUserProviderEnabled());

        return provider;
    }
}