package com.example.demo.mapper;

import com.example.demo.dto.BuildingDto;
import com.example.demo.dto.BuildingCreateDto;
import com.example.demo.model.Building;
import org.springframework.stereotype.Component;

@Component
public class BuildingMapper {

    public BuildingDto toDto(Building entity) {
        if (entity == null) {
            return null;
        }

        BuildingDto dto = new BuildingDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setTotalFloors(entity.getTotalFloors());
        dto.setYearBuilt(entity.getYearBuilt());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());

        return dto;
    }

    public Building toEntity(BuildingCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        Building entity = new Building();
        entity.setName(createDto.getName());
        entity.setAddress(createDto.getAddress());
        entity.setTotalFloors(createDto.getTotalFloors());
        entity.setYearBuilt(createDto.getYearBuilt());
        entity.setLatitude(createDto.getLatitude());
        entity.setLongitude(createDto.getLongitude());

        return entity;
    }

    public Building toEntity(BuildingDto dto) {
        if (dto == null) {
            return null;
        }

        Building entity = new Building();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setTotalFloors(dto.getTotalFloors());
        entity.setYearBuilt(dto.getYearBuilt());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());

        return entity;
    }
}