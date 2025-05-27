package com.example.demo.controller;

import com.example.demo.model.RentalContract;
import com.example.demo.service.RentalContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
@CrossOrigin(origins = "http://localhost:3000")
public class RentalContractController {
    private final RentalContractService contractService;

    public RentalContractController(RentalContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping("/create")
    public RentalContract createContract(@RequestBody RentalContract contract) {
        return contractService.createContract(contract);
    }

    @GetMapping
    public List<RentalContract> getAllContracts() {
        return contractService.getAllContracts();
    }

    @GetMapping("/{id}")
    public RentalContract getContractById(@PathVariable("id") Long id) {
        return contractService.getContractById(id);
    }

    @PutMapping("/{id}")
    public RentalContract updateContract(@PathVariable("id") Long id, @RequestBody RentalContract contract) {
        contract.setId(id);
        return contractService.updateContract(contract);
    }

    @DeleteMapping("/{id}")
    public void terminateContract(@PathVariable("id") Long id) {
        contractService.terminateContract(id);
    }

    @GetMapping("/tenant/{tenantId}")
    public List<RentalContract> getTenantContracts(@PathVariable("tenantId") Long tenantId) {
        return contractService.getContractsByTenant(tenantId);
    }

    @GetMapping("/owner/{ownerId}")
    public List<RentalContract> getOwnerContracts(@PathVariable("ownerId") Long ownerId) {
        return contractService.getContractsByOwner(ownerId);
    }

    @GetMapping("/space/{spaceId}")
    public List<RentalContract> getSpaceContracts(@PathVariable("spaceId") Long spaceId) {
        return contractService.getContractsBySpace(spaceId);
    }

    @GetMapping("/status/{status}")
    public List<RentalContract> getContractsByStatus(@PathVariable("status") String status) {
        return contractService.getContractsByStatus(status);
    }

    @PostMapping("/{id}/renew")
    public RentalContract renewContract(@PathVariable("id") Long id, @RequestBody RentalContract renewalDetails) {
        return contractService.renewContract(id, renewalDetails);
    }
}