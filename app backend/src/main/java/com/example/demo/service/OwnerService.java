package com.example.demo.service;

import com.example.demo.model.Owner;
import com.example.demo.model.ComercialSpace;
import java.util.List;

public interface OwnerService {
    // Operații CRUD de bază pentru Owner
    Owner addOwner(Owner owner);
    List<Owner> getAllOwners();
    Owner getOwnerById(Long id);
    Owner updateOwner(Owner owner);
    void deleteOwner(Long id);

    // Operații pentru managementul spațiilor
    List<ComercialSpace> getOwnerSpaces(Long ownerId);
    List<ComercialSpace> getOwnerAvailableSpaces(Long ownerId);
    long getOwnerSpacesCount(Long ownerId);
    Double getOwnerMonthlyRevenue(Long ownerId);
    List<Owner> getOwnersWithAvailableSpaces();
}