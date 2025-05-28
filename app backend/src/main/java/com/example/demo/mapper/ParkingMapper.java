package com.example.demo.mapper;

import com.example.demo.dto.ParkingDTO;
import com.example.demo.model.Parking;
import org.springframework.stereotype.Component;

@Component
public class ParkingMapper {

    public ParkingDTO toDTO(Parking parking) {
        if (parking == null) {
            return null;
        }

        return ParkingDTO.builder()
                .id(parking.getId())
                .numberOfSpots(parking.getNumberOfSpots())
                .pricePerSpot(parking.getPricePerSpot())
                .covered(parking.getCovered())
                .parkingType(parking.getParkingType())
                .build();
    }

    public Parking fromDTO(ParkingDTO dto) {
        if (dto == null) {
            return null;
        }

        return Parking.builder()
                .id(dto.getId())
                .numberOfSpots(dto.getNumberOfSpots())
                .pricePerSpot(dto.getPricePerSpot())
                .covered(dto.getCovered())
                .parkingType(dto.getParkingType())
                .build();
    }
}