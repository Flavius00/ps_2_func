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

        // Verifică dacă proprietarul are spații comerciale asociate
        long spacesCount = spaceRepository.countByOwnerId(id);
        if (spacesCount > 0) {
            throw new IllegalStateException("Cannot delete owner with associated commercial spaces. " +
                    "Please remove all spaces first.");
        }

        ownerRepository.deleteById(id);
    }

    // Metodele pentru managementul spațiilor
    /**
     * Obține toate spațiile comerciale ale unui proprietar
     * NOTA: Nu mai folosim owner.getSpaces() deoarece am eliminat lista din Owner
     * În schimb, folosim query invers prin ComercialSpaceRepository
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
     * NOTA: Calculăm pe baza spațiilor obținute prin query, nu prin owner.getSpaces()
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