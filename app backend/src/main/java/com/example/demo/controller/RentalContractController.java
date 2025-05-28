package com.example.demo.controller;

import com.example.demo.dto.RentalContractDto;
import com.example.demo.dto.RentalContractCreateDto;
import com.example.demo.mapper.RentalContractMapper;
import com.example.demo.model.RentalContract;
import com.example.demo.service.RentalContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contracts")
@CrossOrigin(origins = "http://localhost:3000")
public class RentalContractController {
    private final RentalContractService contractService;
    private final RentalContractMapper contractMapper;

    public RentalContractController(RentalContractService contractService,
                                    RentalContractMapper contractMapper) {
        this.contractService = contractService;
        this.contractMapper = contractMapper;
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalContractDto> updateContract(@PathVariable("id") Long id, @RequestBody RentalContractDto contractDto) {
        try {
            contractDto.setId(id);
            RentalContract contract = contractMapper.toEntity(contractDto);
            RentalContract updatedContract = contractService.updateContract(contract);
            RentalContractDto responseDto = contractMapper.toDto(updatedContract);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("Error updating contract: " + e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> terminateContract(@PathVariable("id") Long id) {
        try {
            contractService.terminateContract(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error terminating contract: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RentalContractDto>> getTenantContracts(@PathVariable("tenantId") Long tenantId) {
        try {
            List<RentalContract> contracts = contractService.getContractsByTenant(tenantId);
            List<RentalContractDto> contractDtos = contracts.stream()
                    .map(contractMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contractDtos);
        } catch (Exception e) {
            System.err.println("Error getting tenant contracts: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RentalContractDto>> getOwnerContracts(@PathVariable("ownerId") Long ownerId) {
        try {
            List<RentalContract> contracts = contractService.getContractsByOwner(ownerId);
            List<RentalContractDto> contractDtos = contracts.stream()
                    .map(contractMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contractDtos);
        } catch (Exception e) {
            System.err.println("Error getting owner contracts: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<RentalContractDto>> getSpaceContracts(@PathVariable("spaceId") Long spaceId) {
        try {
            List<RentalContract> contracts = contractService.getContractsBySpace(spaceId);
            List<RentalContractDto> contractDtos = contracts.stream()
                    .map(contractMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contractDtos);
        } catch (Exception e) {
            System.err.println("Error getting space contracts: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RentalContractDto>> getContractsByStatus(@PathVariable("status") String status) {
        try {
            List<RentalContract> contracts = contractService.getContractsByStatus(status);
            List<RentalContractDto> contractDtos = contracts.stream()
                    .map(contractMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contractDtos);
        } catch (Exception e) {
            System.err.println("Error getting contracts by status: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<RentalContractDto> renewContract(@PathVariable("id") Long id, @RequestBody RentalContractDto renewalDetails) {
        try {
            RentalContract renewalContract = contractMapper.toEntity(renewalDetails);
            RentalContract renewedContract = contractService.renewContract(id, renewalContract);
            RentalContractDto responseDto = contractMapper.toDto(renewedContract);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("Error renewing contract: " + e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<RentalContractDto> createContract(@RequestBody RentalContractCreateDto createDto) {
        try {
            System.out.println("Received contract create DTO: " + createDto);

            if (createDto.getSpaceId() == null || createDto.getTenantId() == null) {
                return ResponseEntity.badRequest().body(null);
            }

            // Convert DTO to entity using mapper
            RentalContract contract = contractMapper.toEntity(createDto);

            RentalContract savedContract = contractService.createContract(contract);

            // Convert back to DTO for response
            RentalContractDto responseDto = contractMapper.toDto(savedContract);
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            System.err.println("Error creating contract: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<RentalContractDto>> getAllContracts() {
        try {
            List<RentalContract> contracts = contractService.getAllContracts();
            List<RentalContractDto> contractDtos = contracts.stream()
                    .map(contractMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contractDtos);
        } catch (Exception e) {
            System.err.println("Error getting all contracts: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalContractDto> getContractById(@PathVariable("id") Long id) {
        try {
            RentalContract contract = contractService.getContractById(id);
            RentalContractDto contractDto = contractMapper.toDto(contract);
            return ResponseEntity.ok(contractDto);
        } catch (Exception e) {
            System.err.println("Error getting contract by id: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}