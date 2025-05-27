package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.MockDataService;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
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
        // Create buildings
        Building building1 = Building.builder()
                .id(1L)
                .name("Business Tower Plaza")
                .address("Strada Republicii 15, Cluj-Napoca")
                .totalFloors(12)
                .yearBuilt(2010)
                .latitude(46.770439)
                .longitude(23.591423)
                .build();

        Building building2 = Building.builder()
                .id(2L)
                .name("City Center Office Park")
                .address("Bulevardul 21 Decembrie 1989 77, Cluj-Napoca")
                .totalFloors(8)
                .yearBuilt(2015)
                .latitude(46.768224)
                .longitude(23.583485)
                .build();

        Building building3 = Building.builder()
                .id(3L)
                .name("Liberty Mall")
                .address("Calea Victoriei 60, București")
                .totalFloors(5)
                .yearBuilt(2012)
                .latitude(44.439663)
                .longitude(26.096306)
                .build();

        Building building4 = Building.builder()
                .id(4L)
                .name("Logistics Center Iași")
                .address("Strada Bucium 34, Iași")
                .totalFloors(3)
                .yearBuilt(2018)
                .latitude(47.132813)
                .longitude(27.604859)
                .build();

        buildingRepository.save(building1);
        buildingRepository.save(building2);
        buildingRepository.save(building3);
        buildingRepository.save(building4);

        // Create owners
        Owner owner1 = new Owner();
        owner1.setId(1L);
        owner1.setName("Adrian Popescu");
        owner1.setEmail("adrian.popescu@example.com");
        owner1.setUsername("adrianp");
        owner1.setPassword("owner123");
        owner1.setPhone("0745123456");
        owner1.setAddress("Strada Alexandru Vlahuță 3, Cluj-Napoca");
        owner1.setProfilePictureUrl("/assets/profile-adrian.jpg");
        owner1.setRole(User.UserRole.OWNER);
        owner1.setCompanyName("Real Estate Investments SRL");
        owner1.setTaxId("RO12345678");

        Owner owner2 = new Owner();
        owner2.setId(2L);
        owner2.setName("Maria Ionescu");
        owner2.setEmail("maria.ionescu@example.com");
        owner2.setUsername("mariai");
        owner2.setPassword("owner456");
        owner2.setPhone("0756789123");
        owner2.setAddress("Bulevardul Decebal 14, București");
        owner2.setProfilePictureUrl("/assets/profile-maria.jpg");
        owner2.setRole(User.UserRole.OWNER);
        owner2.setCompanyName("Urban Property Management SA");
        owner2.setTaxId("RO87654321");

        userRepository.save(owner1);
        userRepository.save(owner2);

        // Create tenants
        Tenant tenant1 = new Tenant();
        tenant1.setId(3L);
        tenant1.setName("Elena Dumitrescu");
        tenant1.setEmail("elena.dumitrescu@example.com");
        tenant1.setUsername("elenad");
        tenant1.setPassword("tenant123");
        tenant1.setPhone("0723456789");
        tenant1.setAddress("Strada Avram Iancu 18, Cluj-Napoca");
        tenant1.setProfilePictureUrl("/assets/profile-elena.jpg");
        tenant1.setRole(User.UserRole.TENANT);
        tenant1.setCompanyName("Tech Innovation Labs SRL");
        tenant1.setBusinessType("Software Development");
        tenant1.setTaxId("RO23456789");

        Tenant tenant2 = new Tenant();
        tenant2.setId(4L);
        tenant2.setName("Mihai Radu");
        tenant2.setEmail("mihai.radu@example.com");
        tenant2.setUsername("mihair");
        tenant2.setPassword("tenant456");
        tenant2.setPhone("0734567891");
        tenant2.setAddress("Strada Iuliu Maniu 22, București");
        tenant2.setProfilePictureUrl("/assets/profile-mihai.jpg");
        tenant2.setRole(User.UserRole.TENANT);
        tenant2.setCompanyName("Fashion Boutique SRL");
        tenant2.setBusinessType("Retail - Clothing");
        tenant2.setTaxId("RO34567891");

        User admin = User.builder()
                .id(5L)
                .name("Admin User")
                .email("admin@example.com")
                .username("admin")
                .password("admin123")
                .phone("0712345678")
                .address("Strada Administrației 1, București")
                .profilePictureUrl("/assets/profile-admin.jpg")
                .role(User.UserRole.ADMIN)
                .build();

        userRepository.save(tenant1);
        userRepository.save(tenant2);
        userRepository.save(admin);

        // Create parking facilities
        Parking parking1 = Parking.builder()
                .id(1L)
                .numberOfSpots(50)
                .pricePerSpot(150.0)
                .covered(true)
                .parkingType("UNDERGROUND")
                .build();

        Parking parking2 = Parking.builder()
                .id(2L)
                .numberOfSpots(30)
                .pricePerSpot(100.0)
                .covered(false)
                .parkingType("SURFACE")
                .build();

        // Create Comercial spaces
        List<String> officeAmenities = Arrays.asList("Air Conditioning", "High-Speed Internet", "24/7 Access", "Security", "Meeting Rooms");
        List<String> retailAmenities = Arrays.asList("Store Front", "Air Conditioning", "Security System", "Storage Room");
        List<String> warehouseAmenities = Arrays.asList("Loading Dock", "24/7 Access", "Security System", "High Ceilings");

        // Office Spaces
        ComercialSpace office1 = new ComercialSpace();
        office1.setId(1L);
        office1.setName("Premium Office Suite 101");
        office1.setDescription("Modern office space with panoramic city views");
        office1.setArea(120.0);
        office1.setPricePerMonth(2000.0);
        office1.setAddress(building1.getAddress());
        office1.setLatitude(building1.getLatitude());
        office1.setLongitude(building1.getLongitude());
        office1.setAmenities(officeAmenities);
        office1.setAvailable(true);
        office1.setOwner(owner1);
        office1.setBuilding(building1);
        office1.setParking(parking1);
        office1.setSpaceType("OFFICE");
        office1.setFloors(1);
        office1.setNumberOfRooms(4);
        office1.setHasReception(true);

        ComercialSpace office2 = new ComercialSpace();
        office2.setId(2L);
        office2.setName("Executive Office 305");
        office2.setDescription("High-end office space in the heart of the business district");
        office2.setArea(85.0);
        office2.setPricePerMonth(1500.0);
        office2.setAddress(building2.getAddress());
        office2.setLatitude(building2.getLatitude());
        office2.setLongitude(building2.getLongitude());
        office2.setAmenities(officeAmenities);
        office2.setAvailable(true);
        office2.setOwner(owner1);
        office2.setBuilding(building2);
        office2.setParking(parking2);
        office2.setSpaceType("OFFICE");
        office2.setFloors(1);
        office2.setNumberOfRooms(3);
        office2.setHasReception(false);

        // Retail Spaces
        ComercialSpace retail1 = new ComercialSpace();
        retail1.setId(3L);
        retail1.setName("Liberty Mall Shop 15");
        retail1.setDescription("Prime retail location with high foot traffic");
        retail1.setArea(75.0);
        retail1.setPricePerMonth(3000.0);
        retail1.setAddress(building3.getAddress());
        retail1.setLatitude(building3.getLatitude());
        retail1.setLongitude(building3.getLongitude());
        retail1.setAmenities(retailAmenities);
        retail1.setAvailable(false);
        retail1.setOwner(owner2);
        retail1.setBuilding(building3);
        retail1.setParking(parking2);
        retail1.setSpaceType("RETAIL");
        retail1.setShopWindowSize(8.0);
        retail1.setHasCustomerEntrance(true);
        retail1.setMaxOccupancy(30);

        ComercialSpace retail2 = new ComercialSpace();
        retail2.setId(4L);
        retail2.setName("Corner Boutique 22");
        retail2.setDescription("Stylish boutique space perfect for fashion retail");
        retail2.setArea(60.0);
        retail2.setPricePerMonth(2500.0);
        retail2.setAddress(building3.getAddress());
        retail2.setLatitude(building3.getLatitude());
        retail2.setLongitude(building3.getLongitude());
        retail2.setAmenities(retailAmenities);
        retail2.setAvailable(true);
        retail2.setOwner(owner2);
        retail2.setBuilding(building3);
        retail2.setParking(parking2);
        retail2.setSpaceType("RETAIL");
        retail2.setShopWindowSize(6.0);
        retail2.setHasCustomerEntrance(true);
        retail2.setMaxOccupancy(25);

        // Warehouse Spaces
        ComercialSpace warehouse1 = new ComercialSpace();
        warehouse1.setId(5L);
        warehouse1.setName("Logistics Center Unit A");
        warehouse1.setDescription("Spacious warehouse with excellent transportation access");
        warehouse1.setArea(500.0);
        warehouse1.setPricePerMonth(3500.0);
        warehouse1.setAddress(building4.getAddress());
        warehouse1.setLatitude(building4.getLatitude());
        warehouse1.setLongitude(building4.getLongitude());
        warehouse1.setAmenities(warehouseAmenities);
        warehouse1.setAvailable(true);
        warehouse1.setOwner(owner2);
        warehouse1.setBuilding(building4);
        warehouse1.setParking(null);
        warehouse1.setSpaceType("WAREHOUSE");
        warehouse1.setCeilingHeight(6.0);
        warehouse1.setHasLoadingDock(true);
        warehouse1.setSecurityLevel("HIGH");

        ComercialSpace warehouse2 = new ComercialSpace();
        warehouse2.setId(6L);
        warehouse2.setName("Industrial Storage B3");
        warehouse2.setDescription("Climate-controlled storage space suitable for various goods");
        warehouse2.setArea(320.0);
        warehouse2.setPricePerMonth(2800.0);
        warehouse2.setAddress(building4.getAddress());
        warehouse2.setLatitude(building4.getLatitude());
        warehouse2.setLongitude(building4.getLongitude());
        warehouse2.setAmenities(warehouseAmenities);
        warehouse2.setAvailable(true);
        warehouse2.setOwner(owner1);
        warehouse2.setBuilding(building4);
        warehouse2.setParking(null);
        warehouse2.setSpaceType("WAREHOUSE");
        warehouse2.setCeilingHeight(5.0);
        warehouse2.setHasLoadingDock(true);
        warehouse2.setSecurityLevel("MEDIUM");

        // Save all spaces
        spaceRepository.save(office1);
        spaceRepository.save(office2);
        spaceRepository.save(retail1);
        spaceRepository.save(retail2);
        spaceRepository.save(warehouse1);
        spaceRepository.save(warehouse2);


        // Create Rental Contracts
        RentalContract contract1 = RentalContract.builder()
                .id(1L)
                .space(retail1)
                .tenant(tenant2)
                .startDate(LocalDate.of(2024, 3, 1))
                .endDate(LocalDate.of(2025, 2, 28))
                .monthlyRent(retail1.getPricePerMonth())
                .securityDeposit(retail1.getPricePerMonth() * 2)
                .status("ACTIVE")
                .isPaid(true)
                .dateCreated(LocalDate.of(2024, 2, 15))
                .contractNumber("RENT-2024-001")
                .notes("Tenant has requested signage installation approval")
                .build();

        RentalContract contract2 = RentalContract.builder()
                .id(2L)
                .space(office1)
                .tenant(tenant1)
                .startDate(LocalDate.of(2023, 12, 1))
                .endDate(LocalDate.of(2024, 11, 30))
                .monthlyRent(office1.getPricePerMonth())
                .securityDeposit(office1.getPricePerMonth() * 2)
                .status("PENDING")
                .isPaid(false)
                .dateCreated(LocalDate.now())
                .contractNumber("RENT-2024-002")
                .notes("Tenant plans to use space as software development office")
                .build();

        contractRepository.save(contract1);
        contractRepository.save(contract2);

        // Update the space availability based on contracts
        retail1.setAvailable(false);
        office1.setAvailable(false);
        spaceRepository.update(retail1);
        spaceRepository.update(office1);
    }

    @Override
    public List<ComercialSpace> getSpaces() {
        return spaceRepository.findAll();
    }
}