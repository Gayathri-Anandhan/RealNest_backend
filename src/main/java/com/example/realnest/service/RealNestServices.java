package com.example.realnest.service;
import com.cloudinary.utils.ObjectUtils;
import com.example.realnest.entity.RealNest;
import com.example.realnest.repository.PropertyDetailsRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

import com.cloudinary.Cloudinary;

import java.util.List;

@Service
// the service annotation tells the framework that it is business logic
public class RealNestServices {
    @Autowired
    private PropertyDetailsRepo propertyDetailsRepo;

    // public void savedetails(RealNest r){
    // propertyDetailsRepo.save(r);
    // }
    public RealNest savedetails(RealNest r) {
        return propertyDetailsRepo.save(r);
    }

    public List<RealNest> getAllProperties() {
        return propertyDetailsRepo.findAll();
    }

    // public List<RealNest> getPropertyByType(Long id) {
    // return propertyDetailsRepo.findByType(id);
    // }
    public RealNest getPropertyById(Long id) {
        return propertyDetailsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public RealNest getPropertyByFilters(String location,Integer price,String type) {
        return propertyDetailsRepo.findByLocationAndPriceAndType(location,price,type);
    }

    public RealNest updateProperty(Long id, RealNest updateProps) {
        RealNest exisProperty = propertyDetailsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Property with this id: " + id + "is not found"));
        exisProperty.setTitle(updateProps.getTitle());
        exisProperty.setDescription(updateProps.getDescription());
        exisProperty.setPrice(updateProps.getPrice());
        exisProperty.setType(updateProps.getType());
        exisProperty.setLocation(updateProps.getLocation());
        exisProperty.setImageUrl(updateProps.getImageUrl());
        return propertyDetailsRepo.save(exisProperty);
    }

    public RealNest deleteProperty(Long id) {
        RealNest exisProperty = propertyDetailsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        propertyDetailsRepo.deleteById(id);
        return exisProperty;
    }

    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws Exception {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.emptyMap()
        );

        return uploadResult.get("secure_url").toString();
    }
    
}
