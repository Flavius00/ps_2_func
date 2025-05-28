package com.example.demo.mapper;

import com.example.demo.dto.BuildingDto;
import com.example.demo.dto.BuildingCreateDto;
import com.example.demo.model.Building;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    // Entity to DTO mapping
    BuildingDto toDto(Building entity);

    // Create DTO to Entity mapping
    Building toEntity(BuildingCreateDto createDto);

    // Full DTO to Entity mapping (for updates)
    Building toEntity(BuildingDto dto);
}