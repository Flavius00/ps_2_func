package com.example.demo.service.impl;

import com.example.demo.model.Owner;
import com.example.demo.model.ComercialSpace;
import com.example.demo.repository.OwnerRepository;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.service.OwnerService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final ComercialSpaceRepository spaceRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository,
                            ComercialSpaceRepository spaceRepository) {
        this.ownerRepository = ownerRepository;
        this.spaceRepository = spaceRepository;
    }

    // Metodele de bază pentru Owner
    @Override
    public Owner addOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
    }

    @Override
    public Owner updateOwner(Owner owner) {
        if (!ownerRepository.existsById(owner.getId())) {
            throw new ResourceNotFoundException("Owner not found with id: " + owner.getId());
        }
        return ownerRepository.save(owner);
    }

    @Override
    public void deleteOwner(Long id) {
        if (!ownerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Owner not found with id: " + id);
        }
        ownerRepository.deleteById(id);
    }

    // Metodele pentru managementul spațiilor
    /**
     * Obține toate spațiile comerciale ale unui proprietar
     */
    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getOwnerSpaces(Long ownerId) {
        return spaceRepository.findByOwnerId(ownerId);
    }

    /**
     * Obține doar spațiile disponibile ale unui proprietar
     */
    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getOwnerAvailableSpaces(Long ownerId) {
        return spaceRepository.findAvailableSpacesByOwnerId(ownerId);
    }

    /**
     * Obține numărul total de spații ale unui proprietar
     */
    @Override
    @Transactional(readOnly = true)
    public long getOwnerSpacesCount(Long ownerId) {
        return spaceRepository.countByOwnerId(ownerId);
    }

    /**
     * Obține veniturile totale lunare ale unui proprietar din spațiile închiriate
     */
    @Override
    @Transactional(readOnly = true)
    public Double getOwnerMonthlyRevenue(Long ownerId) {
        return spaceRepository.findByOwnerId(ownerId)
                .stream()
                .filter(space -> !space.getAvailable()) // doar spațiile închiriate
                .mapToDouble(ComercialSpace::getPricePerMonth)
                .sum();
    }

    /**
     * Obține proprietarii care au spații disponibile
     */
    @Override
    @Transactional(readOnly = true)
    public List<Owner> getOwnersWithAvailableSpaces() {
        return ownerRepository.findOwnersWithAvailableSpaces();
    }
}