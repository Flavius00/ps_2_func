package com.example.demo.controller;

import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Building;
import com.example.demo.service.ComercialSpaceService;
import com.example.demo.service.BuildingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spaces")
@CrossOrigin(origins = "http://localhost:3000")
public class ComercialSpaceController {
    private final ComercialSpaceService spaceService;
    private final BuildingService buildingService;

    public ComercialSpaceController(ComercialSpaceService spaceService, BuildingService buildingService) {
        this.spaceService = spaceService;
        this.buildingService = buildingService;
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

    @GetMapping("/edit/{id}")
    public String editSpace(@PathVariable Long id, Model model) {
        ComercialSpace space = spaceService.getSpaceById(id);
        if (space == null) {
            return "redirect:/spaces";
        }
        List<Building> buildings = buildingService.getAllBuildings();
        model.addAttribute("space", space);
        model.addAttribute("buildings", buildings);
        return "owners/edit-space";
    }

    @PostMapping("/update")
    public String updateSpace(@RequestBody ComercialSpace space) {
        spaceService.updateSpace(space);
        return "redirect:/spaces";
    }

    @GetMapping("/create")
    public String showCreateSpaceForm(Model model) {
        model.addAttribute("space", new ComercialSpace());
        model.addAttribute("buildings", buildingService.getAllBuildings());
        return "owners/create-space";
    }

    @PostMapping("/create")
    public ComercialSpace createSpace(@RequestBody ComercialSpace space) {
        return spaceService.addSpace(space);
    }

    @GetMapping("getAll")
    public List<ComercialSpace> getAllSpaces() {
        return spaceService.getAllSpaces();
    }

    @GetMapping("/available")
    public List<ComercialSpace> getAvailableSpaces() {
        return spaceService.getAvailableSpaces();
    }

    @GetMapping("/type/{spaceType}")
    public List<ComercialSpace> getSpacesByType(@PathVariable String spaceType) {
        return spaceService.getSpacesByType(spaceType);
    }

    @GetMapping("/owner/{ownerId}")
    public List<ComercialSpace> getSpacesByOwner(@PathVariable Long ownerId) {
        return spaceService.getSpacesByOwner(ownerId);
    }

    @GetMapping("/building/{buildingId}")
    public List<ComercialSpace> getSpacesByBuilding(@PathVariable Long buildingId) {
        return spaceService.getSpacesByBuilding(buildingId);
    }
}