package com.example.demo.controller;

import com.example.demo.dto.RentalContractDto;
import com.example.demo.dto.RentalContractCreateDto;
import com.example.demo.mapper.RentalContractMapper;
import com.example.demo.model.RentalContract;
import com.example.demo.service.RentalContractService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    @GetMapping
    public ResponseEntity<List<RentalContractDto>> getAllContracts() {
        log.info("Fetching all rental contracts");

        List<RentalContract> contracts = contractService.getAllContracts();
        List<RentalContractDto> contractDtos = contracts.stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} rental contracts", contractDtos.size());
        return ResponseEntity.ok(contractDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalContractDto> getContractById(@PathVariable Long id) {
        log.info("Fetching contract with ID: {}", id);

        RentalContract contract = contractService.getContractById(id);
        RentalContractDto contractDto = contractMapper.toDto(contract);

        log.info("Successfully retrieved contract: {}", contract.getContractNumber());
        return ResponseEntity.ok(contractDto);
    }

    @PostMapping("/create")
    public ResponseEntity<RentalContractDto> createContract(@Valid @RequestBody RentalContractCreateDto createDto) {
        log.info("Creating new rental contract for space ID: {} and tenant ID: {}",
                createDto.getSpaceId(), createDto.getTenantId());

        // Convert DTO to entity using mapper
        RentalContract contract = contractMapper.toEntity(createDto);

        RentalContract savedContract = contractService.createContract(contract);
        RentalContractDto responseDto = contractMapper.toDto(savedContract);

        log.info("Successfully created contract with ID: {}", savedContract.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalContractDto> updateContract(
            @PathVariable Long id,
            @Valid @RequestBody RentalContractDto contractDto) {

        log.info("Updating contract with ID: {}", id);

        contractDto.setId(id);
        RentalContract contract = contractMapper.toEntity(contractDto);
        RentalContract updatedContract = contractService.updateContract(contract);
        RentalContractDto responseDto = contractMapper.toDto(updatedContract);

        log.info("Successfully updated contract: {}", updatedContract.getContractNumber());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> terminateContract(@PathVariable Long id) {
        log.info("Terminating contract with ID: {}", id);

        contractService.terminateContract(id);

        log.info("Successfully terminated contract with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RentalContractDto>> getTenantContracts(@PathVariable Long tenantId) {
        log.info("Fetching contracts for tenant ID: {}", tenantId);

        List<RentalContract> contracts = contractService.getContractsByTenant(tenantId);
        List<RentalContractDto> contractDtos = contracts.stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} contracts for tenant {}", contractDtos.size(), tenantId);
        return ResponseEntity.ok(contractDtos);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RentalContractDto>> getOwnerContracts(@PathVariable Long ownerId) {
        log.info("Fetching contracts for owner ID: {}", ownerId);

        List<RentalContract> contracts = contractService.getContractsByOwner(ownerId);
        List<RentalContractDto> contractDtos = contracts.stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} contracts for owner {}", contractDtos.size(), ownerId);
        return ResponseEntity.ok(contractDtos);
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<RentalContractDto>> getSpaceContracts(@PathVariable Long spaceId) {
        log.info("Fetching contracts for space ID: {}", spaceId);

        List<RentalContract> contracts = contractService.getContractsBySpace(spaceId);
        List<RentalContractDto> contractDtos = contracts.stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} contracts for space {}", contractDtos.size(), spaceId);
        return ResponseEntity.ok(contractDtos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RentalContractDto>> getContractsByStatus(@PathVariable String status) {
        log.info("Fetching contracts with status: {}", status);

        List<RentalContract> contracts = contractService.getContractsByStatus(status);
        List<RentalContractDto> contractDtos = contracts.stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} contracts with status {}", contractDtos.size(), status);
        return ResponseEntity.ok(contractDtos);
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<RentalContractDto> renewContract(
            @PathVariable Long id,
            @Valid @RequestBody RentalContractDto renewalDetails) {

        log.info("Renewing contract with ID: {}", id);

        RentalContract renewalContract = contractMapper.toEntity(renewalDetails);
        RentalContract renewedContract = contractService.renewContract(id, renewalContract);
        RentalContractDto responseDto = contractMapper.toDto(renewedContract);

        log.info("Successfully renewed contract, new contract ID: {}", renewedContract.getId());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<RentalContractDto>> getExpiredContracts() {
        log.info("Fetching expired contracts");

        List<RentalContract> contracts = contractService.getExpiredContracts();
        List<RentalContractDto> contractDtos = contracts.stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} expired contracts", contractDtos.size());
        return ResponseEntity.ok(contractDtos);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<RentalContractDto>> getContractsExpiringInDays(
            @RequestParam(defaultValue = "30") int days) {

        log.info("Fetching contracts expiring in {} days", days);

        List<RentalContract> contracts = contractService.getContractsExpiringInDays(days);
        List<RentalContractDto> contractDtos = contracts.stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} contracts expiring in {} days", contractDtos.size(), days);
        return ResponseEntity.ok(contractDtos);
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<Double> getTotalActiveMonthlyRevenue() {
        log.info("Calculating total active monthly revenue");

        Double revenue = contractService.getTotalActiveMonthlyRevenue();

        log.info("Total active monthly revenue: {}", revenue);
        return ResponseEntity.ok(revenue);
    }
}
