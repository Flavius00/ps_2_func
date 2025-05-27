package com.example.demo.repository;

import com.example.demo.model.RentalContract;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RentalContractRepository {
    private final List<RentalContract> contracts = new ArrayList<>();

    public RentalContract save(RentalContract contract) {
        if (contract.getId() == null) {
            contract.setId((long) (contracts.size() + 1));
        }

        RentalContract existingContract = findById(contract.getId());
        if (existingContract == null) {
            contracts.add(contract);
        } else {
            // Update existing contract
            update(contract);
        }
        return contract;
    }

    public List<RentalContract> findAll() {
        return contracts;
    }

    public RentalContract findById(Long id) {
        return contracts.stream()
                .filter(contract -> contract.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public RentalContract update(RentalContract updatedContract) {
        for (int i = 0; i < contracts.size(); i++) {
            if (contracts.get(i).getId().equals(updatedContract.getId())) {
                contracts.set(i, updatedContract);
                return updatedContract;
            }
        }
        return null;
    }

    public ArrayList<RentalContract> findByUserId(Long userId) {
        ArrayList<RentalContract> contracts = new ArrayList<>();
        for (RentalContract contract : this.contracts) {
            if(contract.getTenant().getId().equals(userId)) {
                contracts.add(contract);
            }
        }
        return contracts;
    }

    public boolean deleteById(Long id) {
        return contracts.removeIf(contract -> contract.getId().equals(id));
    }
}