package com.example.demo.controller;

import com.example.demo.model.RentalContract;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Tenant;
import com.example.demo.service.RentalContractService;
import com.example.demo.service.ComercialSpaceService;
import com.example.demo.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contracts")
@CrossOrigin(origins = "http://localhost:3000")
public class RentalContractController {
    private final RentalContractService contractService;
    private final ComercialSpaceService spaceService;
    private final TenantService tenantService;

    public RentalContractController(RentalContractService contractService,
                                    ComercialSpaceService spaceService,
                                    TenantService tenantService) {
        this.contractService = contractService;
        this.spaceService = spaceService;
        this.tenantService = tenantService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createContract(@RequestBody Map<String, Object> contractData) {
        try {
            System.out.println("Received contract data: " + contractData);

            // Extract space and tenant IDs from the request
            Long spaceId = null;
            Long tenantId = null;

            // Handle space data
            if (contractData.get("space") instanceof Map) {
                Map<String, Object> spaceData = (Map<String, Object>) contractData.get("space");
                spaceId = ((Number) spaceData.get("id")).longValue();
            } else if (contractData.get("spaceId") != null) {
                spaceId = ((Number) contractData.get("spaceId")).longValue();
            }

            // Handle tenant data
            if (contractData.get("tenant") instanceof Map) {
                Map<String, Object> tenantData = (Map<String, Object>) contractData.get("tenant");
                tenantId = ((Number) tenantData.get("id")).longValue();
            } else if (contractData.get("tenantId") != null) {
                tenantId = ((Number) contractData.get("tenantId")).longValue();
            }

            if (spaceId == null || tenantId == null) {
                return ResponseEntity.badRequest().body("Space ID and Tenant ID are required");
            }

            // Fetch the actual entities
            ComercialSpace space = spaceService.getSpaceById(spaceId);
            Tenant tenant = tenantService.getTenantById(tenantId);

            // Build the contract with the fetched entities
            RentalContract contract = RentalContract.builder()
                    .space(space)
                    .tenant(tenant)
                    .startDate(java.time.LocalDate.parse((String) contractData.get("startDate")))
                    .endDate(java.time.LocalDate.parse((String) contractData.get("endDate")))
                    .monthlyRent(((Number) contractData.get("monthlyRent")).doubleValue())
                    .securityDeposit(contractData.get("securityDeposit") != null ?
                            ((Number) contractData.get("securityDeposit")).doubleValue() : null)
                    .status(RentalContract.ContractStatus.valueOf(
                            (String) contractData.getOrDefault("status", "ACTIVE")))
                    .isPaid((Boolean) contractData.getOrDefault("isPaid", false))
                    .dateCreated(contractData.get("dateCreated") != null ?
                            java.time.LocalDate.parse((String) contractData.get("dateCreated")) :
                            java.time.LocalDate.now())
                    .contractNumber((String) contractData.get("contractNumber"))
                    .notes((String) contractData.get("notes"))
                    .build();

            RentalContract savedContract = contractService.createContract(contract);
            return ResponseEntity.ok(savedContract);

        } catch (Exception e) {
            System.err.println("Error creating contract: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Failed to create contract: " + e.getMessage());
        }
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