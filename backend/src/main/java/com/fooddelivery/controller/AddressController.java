package com.fooddelivery.controller;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.AddressRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private Customer getCustomer(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return (user instanceof Customer) ? (Customer) user : null;
    }

    @GetMapping
    public ResponseEntity<?> listAddresses(@RequestParam String username) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        
        List<Address> addresses = addressRepository.findByCustomer(customer);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Address addr : addresses) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", addr.getId());
            map.put("streetAddress", addr.getStreetAddress());
            map.put("city", addr.getCity());
            map.put("zipCode", addr.getZipCode());
            map.put("latitude", addr.getLatitude() != null ? addr.getLatitude() : 6.9271);
            map.put("longitude", addr.getLongitude() != null ? addr.getLongitude() : 79.8612);
            map.put("addressType", addr.getAddressType());
            map.put("specificDetail", addr.getSpecificDetail());
            map.put("deliveryInstructions", addr.getDeliveryInstructions());
            map.put("customerName", customer.getFullName());
            map.put("phoneNumber", customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "");
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAddress(@RequestParam String username,
                                        @RequestParam String streetAddress,
                                        @RequestParam String city,
                                        @RequestParam String zipCode,
                                        @RequestParam String addressType,
                                        @RequestParam(required = false) String specificDetails,
                                        @RequestParam(required = false) Double latitude,
                                        @RequestParam(required = false) Double longitude,
                                        @RequestParam(required = false) String customerName,
                                        @RequestParam(required = false) String phoneNumber) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        if (customerName != null && !customerName.trim().isEmpty()) {
            customer.setFullName(customerName.trim());
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            customer.setPhoneNumber(phoneNumber.trim());
        }
        userRepository.save(customer);

        // OOP: Polymorphism - Instantiate the specific Address type
        Address newAddress;
        if ("HOME".equals(addressType)) {
            HomeAddress home = new HomeAddress();
            home.setGateCode(specificDetails);
            newAddress = home;
        } else if ("OFFICE".equals(addressType)) {
            OfficeAddress office = new OfficeAddress();
            office.setFloorAndCompany(specificDetails);
            newAddress = office;
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid address type"));
        }

        newAddress.setStreetAddress(streetAddress);
        newAddress.setCity(city);
        newAddress.setZipCode(zipCode);
        newAddress.setLatitude(latitude);
        newAddress.setLongitude(longitude);
        newAddress.setCustomer(customer);

        addressRepository.save(newAddress);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id, @RequestParam String username) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Address address = addressRepository.findById(id).orElse(null);
        if (address != null && address.getCustomer().getId().equals(customer.getId())) {
            addressRepository.delete(address);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getAddress(@PathVariable Long id, @RequestParam String username) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Address address = addressRepository.findById(id).orElse(null);
        if (address == null || !address.getCustomer().getId().equals(customer.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Not found"));
        }
        return ResponseEntity.ok(address);
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> editAddress(@PathVariable Long id,
                                         @RequestParam String username,
                                         @RequestParam String streetAddress,
                                         @RequestParam String city,
                                         @RequestParam String zipCode,
                                         @RequestParam(required = false) String specificDetails,
                                         @RequestParam(required = false) Double latitude,
                                         @RequestParam(required = false) Double longitude,
                                         @RequestParam(required = false) String customerName,
                                         @RequestParam(required = false) String phoneNumber) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        if (customerName != null && !customerName.trim().isEmpty()) {
            customer.setFullName(customerName.trim());
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            customer.setPhoneNumber(phoneNumber.trim());
        }
        userRepository.save(customer);

        Address address = addressRepository.findById(id).orElse(null);
        if (address != null && address.getCustomer().getId().equals(customer.getId())) {
            address.setStreetAddress(streetAddress);
            address.setCity(city);
            address.setZipCode(zipCode);
            if (latitude != null) address.setLatitude(latitude);
            if (longitude != null) address.setLongitude(longitude);
            
            if (address instanceof HomeAddress) {
                ((HomeAddress) address).setGateCode(specificDetails);
            } else if (address instanceof OfficeAddress) {
                ((OfficeAddress) address).setFloorAndCompany(specificDetails);
            }
            addressRepository.save(address);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
