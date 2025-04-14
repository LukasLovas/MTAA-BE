package com.example.mtaa.controller;

import com.example.mtaa.dto.CurrencyDTO;
import com.example.mtaa.dto.LoginDTO;
import com.example.mtaa.dto.TokenDTO;
import com.example.mtaa.dto.UserDTO;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.model.User;
import com.example.mtaa.service.UserService;
import com.example.mtaa.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;


    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }
    @Operation(summary = "Login to a user account", description = "Logs user in and generated a bearer token needed for authorization.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Wrong password", content = @Content),
            @ApiResponse(responseCode = "404", description = "User with this username was not found.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginRequest) {
        User user = userService.findUserByUsername(loginRequest.getUsername());
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        }catch(BadCredentialsException e){
            throw new CommonException(HttpStatus.BAD_REQUEST, "Credentials are incorrect.");
        }
        String token = JwtUtil.generateToken(loginRequest.getUsername(), user.getId());
        return ResponseEntity.ok(new TokenDTO(token));
    }

    @Operation(summary = "Register a new user", description = "Registers a new user with username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Username already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO userInput) {
        User registeredUser = userService.registerUser(userInput);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Logs out active user", description = "The active user is logged out of the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully.");
    }

    @Operation(summary = "Set default currency", description = "Default currency is set. All transactions in different currencies will be converted to this one.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency set successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid currency code", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/currency")
    public ResponseEntity<User> setCurrencyCode(@RequestBody CurrencyDTO currencyDTO) {
        return new ResponseEntity<>(userService.setCurrency(currencyDTO), HttpStatus.OK);
    }
}
