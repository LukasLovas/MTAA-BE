package com.example.mtaa.controller;

import com.example.mtaa.dto.LocationDTO;
import com.example.mtaa.model.Location;
import com.example.mtaa.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService){
        this.locationService = locationService;
    }

    @Operation(summary = "Creates a location", description = "Creates and saves a new Location object into the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("")
    public ResponseEntity<Location> addLocation(@RequestBody @Validated LocationDTO input){
        return ResponseEntity.ok(locationService.addLocation(input));
    }

    @Operation(summary = "Retrieves location by ID", description = "Queries the database for a Location object based on ID from the user input.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Location with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id){
        return ResponseEntity.ok(locationService.getLocationById(id));
    }
}
