package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.MockDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class MockDataServiceImpl implements MockDataService {
    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final ComercialSpaceRepository spaceRepository;
    private final RentalContractRepository contractRepository;

    public MockDataServiceImpl(UserRepository userRepository,
                               BuildingRepository buildingRepository,
                               ComercialSpaceRepository spaceRepository,
                               RentalContractRepository contractRepository) {
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
        this.spaceRepository = spaceRepository;
        this.contractRepository = contractRepository;
    }

    @PostConstruct
    public void generateMockData() {
        // Verifică dacă datele au fost deja generate
        if (userRepository.count() > 0) {
            System.out.println("Mock data already exists, skipping generation.");
            return;
        }

        System.out.println("Generating mock data...");

        try {
            // Create buildings
            Building building1 = buildingRepository.save(Building.builder()
                    .name("Business Tower Plaza")
                    .address("Strada Republicii 15, Cluj-Napoca")
                    .totalFloors(12)
                    .yearBuilt(2010)
                    .latitude(46.770439)
                    .longitude(23.591423)
                    .build());

            Building building2 = buildingRepository.save(Building.builder()
                    .name("City Center Office Park")
                    .address("Bulevardul 21 Decembrie 1989 77, Cluj-Napoca")
                    .totalFloors(8)
                    .yearBuilt(2015)
                    .latitude(46.768224)
                    .longitude(23.583485)
                    .build());

            Building building3 = buildingRepository.save(Building.builder()
                    .name("Liberty Mall")
                    .address("Calea Victoriei 60, București")
                    .totalFloors(5)
                    .yearBuilt(2012)
                    .latitude(44.439663)
                    .longitude(26.096306)
                    .build());

            Building building4 = buildingRepository.save(Building.builder()
                    .name("Logistics Center Iași")
                    .address("Strada Bucium 34, Iași")
                    .totalFloors(3)
                    .yearBuilt(2018)
                    .latitude(47.132813)
                    .longitude(27.604859)
                    .build());

            System.out.println("✅ Buildings created successfully");

            // Create owners
            Owner owner1 = (Owner) userRepository.save(Owner.builder()
                    .name("Adrian Popescu")
                    .email("adrian.popescu@example.com")
                    .username("adrianp")
                    .password("owner123")
                    .phone("0745123456")
                    .address("Strada Alexandru Vlahuță 3, Cluj-Napoca")
                    .profilePictureUrl("/assets/profile-adrian.jpg")
                    .role(User.UserRole.OWNER)
                    .companyName("Real Estate Investments SRL")
                    .taxId("RO12345678")
                    .build());

            Owner owner2 = (Owner) userRepository.save(Owner.builder()
                    .name("Maria Ionescu")
                    .email("maria.ionescu@example.com")
                    .username("mariai")
                    .password("owner456")
                    .phone("0756789123")
                    .address("Bulevardul Decebal 14, București")
                    .profilePictureUrl("/assets/profile-maria.jpg")
                    .role(User.UserRole.OWNER)
                    .companyName("Urban Property Management SA")
                    .taxId("RO87654321")
                    .build());

            // Create tenants
            Tenant tenant1 = (Tenant) userRepository.save(Tenant.builder()
                    .name("Elena Dumitrescu")
                    .email("elena.dumitrescu@example.com")
                    .username("elenad")
                    .password("tenant123")
                    .phone("0723456789")
                    .address("Strada Avram Iancu 18, Cluj-Napoca")
                    .profilePictureUrl("/assets/profile-elena.jpg")
                    .role(User.UserRole.TENANT)
                    .companyName("Tech Innovation Labs SRL")
                    .businessType("Software Development")
                    .taxId("RO23456789")
                    .build());

            Tenant tenant2 = (Tenant) userRepository.save(Tenant.builder()
                    .name("Mihai Radu")
                    .email("mihai.radu@example.com")
                    .username("mihair")
                    .password("tenant456")
                    .phone("0734567891")
                    .address("Strada Iuliu Maniu 22, București")
                    .profilePictureUrl("/assets/profile-mihai.jpg")
                    .role(User.UserRole.TENANT)
                    .companyName("Fashion Boutique SRL")
                    .businessType("Retail - Clothing")
                    .taxId("RO34567891")
                    .build());

            User admin = userRepository.save(User.builder()
                    .name("Admin User")
                    .email("admin@example.com")
                    .username("admin")
                    .password("admin123")
                    .phone("0712345678")
                    .address("Strada Administrației 1, București")
                    .profilePictureUrl("/assets/profile-admin.jpg")
                    .role(User.UserRole.ADMIN)
                    .build());

            System.out.println("✅ Users created successfully");

            // Create Commercial spaces cu parking individual pentru fiecare
            List<String> officeAmenities = Arrays.asList("Air Conditioning", "High-Speed Internet", "24/7 Access", "Security", "Meeting Rooms");
            List<String> retailAmenities = Arrays.asList("Store Front", "Air Conditioning", "Security System", "Storage Room");
            List<String> warehouseAmenities = Arrays.asList("Loading Dock", "24/7 Access", "Security System", "High Ceilings");

            // Office Space 1 - cu propriul său parking
            ComercialSpace office1 = spaceRepository.save(ComercialSpace.builder()
                    .name("Premium Office Suite 101")
                    .description("Modern office space with panoramic city views")
                    .area(120.0)
                    .pricePerMonth(2000.0)
                    .address(building1.getAddress())
                    .latitude(building1.getLatitude())
                    .longitude(building1.getLongitude())
                    .amenities(officeAmenities)
                    .available(true)
                    .owner(owner1)
                    .building(building1)
                    .parking(Parking.builder()
                            .numberOfSpots(50)
                            .pricePerSpot(150.0)
                            .covered(true)
                            .parkingType(Parking.ParkingType.UNDERGROUND)
                            .build())
                    .spaceType(ComercialSpace.SpaceType.OFFICE)
                    .floors(1)
                    .numberOfRooms(4)
                    .hasReception(true)
                    .build());

            // Office Space 2 - cu propriul său parking
            ComercialSpace office2 = spaceRepository.save(ComercialSpace.builder()
                    .name("Executive Office 305")
                    .description("High-end office space in the heart of the business district")
                    .area(85.0)
                    .pricePerMonth(1500.0)
                    .address(building2.getAddress())
                    .latitude(building2.getLatitude())
                    .longitude(building2.getLongitude())
                    .amenities(officeAmenities)
                    .available(true)
                    .owner(owner1)
                    .building(building2)
                    .parking(Parking.builder()
                            .numberOfSpots(30)
                            .pricePerSpot(100.0)
                            .covered(false)
                            .parkingType(Parking.ParkingType.SURFACE)
                            .build())
                    .spaceType(ComercialSpace.SpaceType.OFFICE)
                    .floors(1)
                    .numberOfRooms(3)
                    .hasReception(false)
                    .build());

            // Retail Space 1 - cu propriul său parking
            ComercialSpace retail1 = spaceRepository.save(ComercialSpace.builder()
                    .name("Liberty Mall Shop 15")
                    .description("Prime retail location with high foot traffic")
                    .area(75.0)
                    .pricePerMonth(3000.0)
                    .address(building3.getAddress())
                    .latitude(building3.getLatitude())
                    .longitude(building3.getLongitude())
                    .amenities(retailAmenities)
                    .available(false)
                    .owner(owner2)
                    .building(building3)
                    .parking(Parking.builder()
                            .numberOfSpots(25)
                            .pricePerSpot(120.0)
                            .covered(false)
                            .parkingType(Parking.ParkingType.SURFACE)
                            .build())
                    .spaceType(ComercialSpace.SpaceType.RETAIL)
                    .shopWindowSize(8.0)
                    .hasCustomerEntrance(true)
                    .maxOccupancy(30)
                    .build());

            // Retail Space 2 - cu propriul său parking
            ComercialSpace retail2 = spaceRepository.save(ComercialSpace.builder()
                    .name("Corner Boutique 22")
                    .description("Stylish boutique space perfect for fashion retail")
                    .area(60.0)
                    .pricePerMonth(2500.0)
                    .address(building3.getAddress())
                    .latitude(building3.getLatitude())
                    .longitude(building3.getLongitude())
                    .amenities(retailAmenities)
                    .available(true)
                    .owner(owner2)
                    .building(building3)
                    .parking(Parking.builder()
                            .numberOfSpots(20)
                            .pricePerSpot(110.0)
                            .covered(false)
                            .parkingType(Parking.ParkingType.SURFACE)
                            .build())
                    .spaceType(ComercialSpace.SpaceType.RETAIL)
                    .shopWindowSize(6.0)
                    .hasCustomerEntrance(true)
                    .maxOccupancy(25)
                    .build());

            // Warehouse Space 1 - fără parking
            ComercialSpace warehouse1 = spaceRepository.save(ComercialSpace.builder()
                    .name("Logistics Center Unit A")
                    .description("Spacious warehouse with excellent transportation access")
                    .area(500.0)
                    .pricePerMonth(3500.0)
                    .address(building4.getAddress())
                    .latitude(building4.getLatitude())
                    .longitude(building4.getLongitude())
                    .amenities(warehouseAmenities)
                    .available(true)
                    .owner(owner2)
                    .building(building4)
                    .spaceType(ComercialSpace.SpaceType.WAREHOUSE)
                    .ceilingHeight(6.0)
                    .hasLoadingDock(true)
                    .securityLevel(ComercialSpace.SecurityLevel.HIGH)
                    .build());

            // Warehouse Space 2 - fără parking
            ComercialSpace warehouse2 = spaceRepository.save(ComercialSpace.builder()
                    .name("Industrial Storage B3")
                    .description("Climate-controlled storage space suitable for various goods")
                    .area(320.0)
                    .pricePerMonth(2800.0)
                    .address(building4.getAddress())
                    .latitude(building4.getLatitude())
                    .longitude(building4.getLongitude())
                    .amenities(warehouseAmenities)
                    .available(true)
                    .owner(owner1)
                    .building(building4)
                    .spaceType(ComercialSpace.SpaceType.WAREHOUSE)
                    .ceilingHeight(5.0)
                    .hasLoadingDock(true)
                    .securityLevel(ComercialSpace.SecurityLevel.MEDIUM)
                    .build());

            System.out.println("✅ Commercial spaces created successfully");

            // Create Rental Contracts
            RentalContract contract1 = contractRepository.save(RentalContract.builder()
                    .space(retail1)
                    .tenant(tenant2)
                    .startDate(LocalDate.of(2024, 3, 1))
                    .endDate(LocalDate.of(2025, 2, 28))
                    .monthlyRent(retail1.getPricePerMonth())
                    .securityDeposit(retail1.getPricePerMonth() * 2)
                    .status(RentalContract.ContractStatus.ACTIVE)
                    .isPaid(true)
                    .dateCreated(LocalDate.of(2024, 2, 15))
                    .contractNumber("RENT-2024-001")
                    .notes("Tenant has requested signage installation approval")
                    .build());

            RentalContract contract2 = contractRepository.save(RentalContract.builder()
                    .space(office1)
                    .tenant(tenant1)
                    .startDate(LocalDate.of(2023, 12, 1))
                    .endDate(LocalDate.of(2024, 11, 30))
                    .monthlyRent(office1.getPricePerMonth())
                    .securityDeposit(office1.getPricePerMonth() * 2)
                    .status(RentalContract.ContractStatus.PENDING)
                    .isPaid(false)
                    .dateCreated(LocalDate.now())
                    .contractNumber("RENT-2024-002")
                    .notes("Tenant plans to use space as software development office")
                    .build());

            System.out.println("✅ Rental contracts created successfully");

            // Update the space availability based on contracts
            retail1.setAvailable(false);
            office1.setAvailable(false);
            spaceRepository.save(retail1);
            spaceRepository.save(office1);

            System.out.println("Mock data generation completed successfully!");
            System.out.println("Generated:");
            System.out.println("- " + userRepository.count() + " users");
            System.out.println("- " + buildingRepository.count() + " buildings");
            System.out.println("- " + spaceRepository.count() + " commercial spaces");
            System.out.println("- " + contractRepository.count() + " rental contracts");

        } catch (Exception e) {
            System.err.println("❌ Error generating mock data: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ComercialSpace> getSpaces() {
        return spaceRepository.findAll();
    }
}