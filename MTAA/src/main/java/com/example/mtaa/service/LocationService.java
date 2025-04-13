package com.example.mtaa.service;

import com.example.mtaa.dto.LocationDTO;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.model.Location;
import com.example.mtaa.repository.LocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location addLocation(LocationDTO input){
        Location location = convertToLocation(input);
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id){
        return locationRepository.findById(id).orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND, "Location not found"));
    }

    public Location convertToLocation(LocationDTO input){
        Location location = new Location();
        location.setName(input.getName());
        location.setLatitude(input.getLatitude());
        location.setLongitude(input.getLongitude());

        return location;
    }
}
