package com.example.demo.controller;

import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Building;
import com.example.demo.model.Owner;
import com.example.demo.service.ComercialSpaceService;
import com.example.demo.service.BuildingService;
import com.example.demo.service.OwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spaces")
@CrossOrigin(origins = "http://localhost:3000")
public class ComercialSpaceController {
    private final ComercialSpaceService spaceService;
    private final BuildingService buildingService;
    private final OwnerService ownerService;

    public ComercialSpaceController(ComercialSpaceService spaceService,
                                    BuildingService buildingService,
                                    OwnerService ownerService) {
        this.spaceService = spaceService;
        this.buildingService = buildingService;
        this.ownerService = ownerService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ComercialSpace>> getAllSpaces() {
        try {
            List<ComercialSpace> spaces = spaceService.getAllSpaces();
            if (spaces == null) {
                spaces = List.of();
            }

            System.out.println("=== DEBUGGING SPACES ===");
            System.out.println("Returning " + spaces.size() + " spaces to frontend");

            for (ComercialSpace space : spaces) {
                System.out.println("Space ID: " + space.getId() +
                        ", Name: " + space.getName() +
                        ", Owner ID: " + space.getOwnerId() +
                        ", Owner Name: " + space.getOwnerName() +
                        ", Building ID: " + space.getBuildingId() +
                        ", Building Name: " + space.getBuildingName());
            }
            System.out.println("=== END DEBUGGING ===");

            return ResponseEntity.ok(spaces);
        } catch (Exception e) {
            System.err.println("Error in getAllSpaces: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteSpace(@PathVariable Long id) {
        spaceService.deleteSpace(id);
        return "redirect:/spaces";
    }

    @GetMapping("/details/{id}")
    public ComercialSpace spaceDetails(@PathVariable Long id) {
        ComercialSpace space = spaceService.getSpaceById(id);
        if (space == null) {
            throw new RuntimeException("Space not found");
        }
        return space;
    }

    // CORECTARE CRITICĂ: Update endpoint care păstrează owner-ul și building-ul
    @PostMapping("/update")
    public ResponseEntity<?> updateSpace(@RequestBody Map<String, Object> updateData) {
        try {
            System.out.println("Received update data: " + updateData);

            // Extract space ID
            Long spaceId = ((Number) updateData.get("id")).longValue();

            // Get existing space to preserve relationships
            ComercialSpace existingSpace = spaceService.getSpaceById(spaceId);
            if (existingSpace == null) {
                return ResponseEntity.notFound().build();
            }

            System.out.println("Existing space - Owner ID: " + existingSpace.getOwnerId() +
                    ", Building ID: " + existingSpace.getBuildingId());

            // Update only the fields that should be editable
            if (updateData.containsKey("name")) {
                existingSpace.setName((String) updateData.get("name"));
            }
            if (updateData.containsKey("description")) {
                existingSpace.setDescription((String) updateData.get("description"));
            }
            if (updateData.containsKey("area")) {
                existingSpace.setArea(((Number) updateData.get("area")).doubleValue());
            }
            if (updateData.containsKey("pricePerMonth")) {
                existingSpace.setPricePerMonth(((Number) updateData.get("pricePerMonth")).doubleValue());
            }
            if (updateData.containsKey("address")) {
                existingSpace.setAddress((String) updateData.get("address"));
            }
            if (updateData.containsKey("available")) {
                existingSpace.setAvailable((Boolean) updateData.get("available"));
            }
            if (updateData.containsKey("latitude")) {
                existingSpace.setLatitude(((Number) updateData.get("latitude")).doubleValue());
            }
            if (updateData.containsKey("longitude")) {
                existingSpace.setLongitude(((Number) updateData.get("longitude")).doubleValue());
            }

            // Handle type-specific fields
            if (updateData.containsKey("floors")) {
                existingSpace.setFloors(((Number) updateData.get("floors")).intValue());
            }
            if (updateData.containsKey("numberOfRooms")) {
                existingSpace.setNumberOfRooms(((Number) updateData.get("numberOfRooms")).intValue());
            }
            if (updateData.containsKey("hasReception")) {
                existingSpace.setHasReception((Boolean) updateData.get("hasReception"));
            }
            if (updateData.containsKey("shopWindowSize")) {
                existingSpace.setShopWindowSize(((Number) updateData.get("shopWindowSize")).doubleValue());
            }
            if (updateData.containsKey("hasCustomerEntrance")) {
                existingSpace.setHasCustomerEntrance((Boolean) updateData.get("hasCustomerEntrance"));
            }
            if (updateData.containsKey("maxOccupancy")) {
                existingSpace.setMaxOccupancy(((Number) updateData.get("maxOccupancy")).intValue());
            }
            if (updateData.containsKey("ceilingHeight")) {
                existingSpace.setCeilingHeight(((Number) updateData.get("ceilingHeight")).doubleValue());
            }
            if (updateData.containsKey("hasLoadingDock")) {
                existingSpace.setHasLoadingDock((Boolean) updateData.get("hasLoadingDock"));
            }
            if (updateData.containsKey("securityLevel")) {
                existingSpace.setSecurityLevel(
                        ComercialSpace.SecurityLevel.valueOf((String) updateData.get("securityLevel")));
            }

            // IMPORTANT: NU schimba owner-ul sau building-ul!
            // Acestea rămân neschimbate pentru a păstra integritatea datelor

            System.out.println("Before update - Owner ID: " + existingSpace.getOwnerId() +
                    ", Building ID: " + existingSpace.getBuildingId());

            ComercialSpace updatedSpace = spaceService.updateSpace(existingSpace);

            System.out.println("After update - Owner ID: " + updatedSpace.getOwnerId() +
                    ", Building ID: " + updatedSpace.getBuildingId());

            return ResponseEntity.ok(updatedSpace);

        } catch (Exception e) {
            System.err.println("Error updating space: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Failed to update space: " + e.getMessage());
        }
    }

    @GetMapping("/create")
    public String showCreateSpaceForm(Model model) {
        model.addAttribute("space", new ComercialSpace());
        model.addAttribute("buildings", buildingService.getAllBuildings());
        return "owners/create-space";
    }

    // Replace the createSpace method in ComercialSpaceController.java with this:

    @PostMapping("/create")
    public ResponseEntity<ComercialSpace> createSpace(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("=== CREATE SPACE DEBUG ===");
            System.out.println("Raw request data: " + requestData);

            // Extract owner ID
            Long ownerId = null;
            if (requestData.get("owner") instanceof Map) {
                Map<String, Object> ownerData = (Map<String, Object>) requestData.get("owner");
                if (ownerData.get("id") != null) {
                    ownerId = ((Number) ownerData.get("id")).longValue();
                }
            }

            // Extract building ID
            Long buildingId = null;
            if (requestData.get("building") instanceof Map) {
                Map<String, Object> buildingData = (Map<String, Object>) requestData.get("building");
                if (buildingData.get("id") != null) {
                    buildingId = ((Number) buildingData.get("id")).longValue();
                }
            }

            System.out.println("Extracted Owner ID: " + ownerId);
            System.out.println("Extracted Building ID: " + buildingId);

            if (ownerId == null) {
                System.err.println("ERROR: Owner ID is missing from request");
                return ResponseEntity.badRequest().body(null);
            }

            if (buildingId == null) {
                System.err.println("ERROR: Building ID is missing from request");
                return ResponseEntity.badRequest().body(null);
            }

            // Create ComercialSpace object
            ComercialSpace space = ComercialSpace.builder()
                    .name((String) requestData.get("name"))
                    .description((String) requestData.get("description"))
                    .area(requestData.get("area") != null ? ((Number) requestData.get("area")).doubleValue() : 0.0)
                    .pricePerMonth(requestData.get("pricePerMonth") != null ? ((Number) requestData.get("pricePerMonth")).doubleValue() : 0.0)
                    .address((String) requestData.get("address"))
                    .latitude(requestData.get("latitude") != null ? ((Number) requestData.get("latitude")).doubleValue() : 0.0)
                    .longitude(requestData.get("longitude") != null ? ((Number) requestData.get("longitude")).doubleValue() : 0.0)
                    .available((Boolean) requestData.getOrDefault("available", true))
                    .spaceType(requestData.get("spaceType") != null ?
                            ComercialSpace.SpaceType.valueOf((String) requestData.get("spaceType")) :
                            ComercialSpace.SpaceType.OFFICE)
                    // Create Owner and Building objects with IDs
                    .owner(Owner.builder().id(ownerId).build())
                    .building(Building.builder().id(buildingId).build())
                    // Type-specific fields
                    .floors(requestData.get("floors") != null ? ((Number) requestData.get("floors")).intValue() : null)
                    .numberOfRooms(requestData.get("numberOfRooms") != null ? ((Number) requestData.get("numberOfRooms")).intValue() : null)
                    .hasReception((Boolean) requestData.getOrDefault("hasReception", false))
                    .shopWindowSize(requestData.get("shopWindowSize") != null ? ((Number) requestData.get("shopWindowSize")).doubleValue() : null)
                    .hasCustomerEntrance((Boolean) requestData.getOrDefault("hasCustomerEntrance", true))
                    .maxOccupancy(requestData.get("maxOccupancy") != null ? ((Number) requestData.get("maxOccupancy")).intValue() : null)
                    .ceilingHeight(requestData.get("ceilingHeight") != null ? ((Number) requestData.get("ceilingHeight")).doubleValue() : null)
                    .hasLoadingDock((Boolean) requestData.getOrDefault("hasLoadingDock", false))
                    .securityLevel(requestData.get("securityLevel") != null ?
                            ComercialSpace.SecurityLevel.valueOf((String) requestData.get("securityLevel")) :
                            ComercialSpace.SecurityLevel.MEDIUM)
                    .build();

            // Handle amenities
            if (requestData.get("amenities") instanceof List) {
                space.setAmenities((List<String>) requestData.get("amenities"));
            }

            System.out.println("Created space object with Owner ID: " + space.getOwner().getId() +
                    " and Building ID: " + space.getBuilding().getId());

            ComercialSpace createdSpace = spaceService.addSpace(space);

            System.out.println("Space created successfully - ID: " + createdSpace.getId() +
                    ", Owner ID: " + createdSpace.getOwnerId() +
                    ", Owner Name: " + createdSpace.getOwnerName());
            System.out.println("=== END CREATE SPACE DEBUG ===");

            return ResponseEntity.ok(createdSpace);
        } catch (Exception e) {
            System.err.println("Error creating space: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<ComercialSpace>> getAvailableSpaces() {
        try {
            List<ComercialSpace> spaces = spaceService.getAvailableSpaces();
            return ResponseEntity.ok(spaces != null ? spaces : List.of());
        } catch (Exception e) {
            System.err.println("Error getting available spaces: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/type/{spaceType}")
    public ResponseEntity<List<ComercialSpace>> getSpacesByType(@PathVariable String spaceType) {
        try {
            List<ComercialSpace> spaces = spaceService.getSpacesByType(spaceType);
            return ResponseEntity.ok(spaces != null ? spaces : List.of());
        } catch (Exception e) {
            System.err.println("Error getting spaces by type: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ComercialSpace>> getSpacesByOwner(@PathVariable Long ownerId) {
        try {
            System.out.println("Getting spaces for owner ID: " + ownerId);
            List<ComercialSpace> spaces = spaceService.getSpacesByOwner(ownerId);
            System.out.println("Found " + (spaces != null ? spaces.size() : 0) + " spaces for owner " + ownerId);
            return ResponseEntity.ok(spaces != null ? spaces : List.of());
        } catch (Exception e) {
            System.err.println("Error getting spaces by owner: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<ComercialSpace>> getSpacesByBuilding(@PathVariable Long buildingId) {
        try {
            List<ComercialSpace> spaces = spaceService.getSpacesByBuilding(buildingId);
            return ResponseEntity.ok(spaces != null ? spaces : List.of());
        } catch (Exception e) {
            System.err.println("Error getting spaces by building: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }
}