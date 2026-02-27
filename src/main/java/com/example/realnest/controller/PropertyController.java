package com.example.realnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.example.realnest.entity.RealNest;
import com.example.realnest.service.ImageService;
import com.example.realnest.service.RealNestServices;
import com.fasterxml.jackson.databind.ObjectMapper;

// @Controller  -- for thymeleaf
//this annotation is used to mark a class as web controller
//to handle http requests
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/properties")

public class PropertyController {
    @Autowired
    private RealNestServices realNestServices;

    ///// thymeleaf/////
    // @PostMapping("/save/property")
    // public String saveProperty(@ModelAttribute RealNest r){
    // //@ModelAttribute tells to take the incoming request parameter and map them
    ///// to the fields of this object
    // realNestServices.savedetails(r);
    // return "/save/property";
    // }
    ////// React////////
    // @PostMapping("/saveProperty")
    // public RealNest saveProperty(@RequestBody RealNest r) {
    // realNestServices.savedetails(r);
    // return r;
    // }

    // Model is a interface from spring to pass the data from controller to view
    // m is the name given to the variable of model
    // REalNest is the name of the modelattribute before we put that data into model
    ///// thymeleaf////
    // @GetMapping("/display/allproperty")
    // public String displayAll(Model m){
    // m.addAttribute("REalNest",realNestServices.getAllProperties());
    // return "display/allproperty";
    // }
    ///// React/////////
    @GetMapping("/allproperties")
    public List<RealNest> getAllProperties() {
        return realNestServices.getAllProperties();
    }

    ////////// thymeleaf//////
    // @GetMapping("/add/property")
    // public String showAddPropertyform(Model m){
    // m.addAttribute("REalNest",new RealNest());
    // return "/add/product";
    // }
    // @GetMapping("/viewproperties")
    // public RealNest viewProperty(@RequestParam("id") Long id){
    // return realNestServices.getPropertyByType(id);
    // }
    @GetMapping("/viewproperties")
    public RealNest viewProperty(@RequestParam("id") Long id) {
        return realNestServices.getPropertyById(id);
    }

    @GetMapping("/filterproperties")
    public RealNest filterProperty(@RequestParam("location") String location, @RequestParam("price") Integer price,
            @RequestParam("type") String type) {
        return realNestServices.getPropertyByFilters(location, price, type);
    }

    // @PutMapping("/updateProperty")
    // public RealNest updateProperty(@RequestParam("id") Long id, @RequestBody
    // RealNest r) {
    // RealNest updateProperty = realNestServices.updateProperty(id, r);
    // return updateProperty;
    // }

    @PutMapping(value = "/updateProperty", consumes = "multipart/form-data")
    public RealNest updateProperty(
            @RequestParam("property") String propertyJson,
            @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        RealNest r = mapper.readValue(propertyJson, RealNest.class);

        if (file != null && !file.isEmpty()) {
            String imageUrl = imageService.uploadImage(file);
            r.setImageUrl(imageUrl);
        }

        return realNestServices.updateProperty(r.getId(), r);
    }

    @DeleteMapping("/deleteProperty")
    public String deleteProperty(@RequestParam("id") Long id) {
        RealNest delprops = realNestServices.deleteProperty(id);
        return "Property deleted successfully with id: " + id;
    }

    @Autowired
    private ImageService imageService;

    // @PostMapping("/upload-image")
    // public ResponseEntity<String> uploadImage(
    // @RequestParam("file") MultipartFile file) throws Exception {

    // String imageUrl = imageService.uploadImage(file);
    // return ResponseEntity.ok(imageUrl);
    // }

    // @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    // public ResponseEntity<String> uploadImage(
    // @RequestParam("file") MultipartFile file) {

    // return ResponseEntity.ok("File received: " + file.getOriginalFilename());
    // }
    @PostMapping(value = "/saveProperty", consumes = "multipart/form-data")
    public RealNest saveProperty(
            @RequestParam("property") String propertyJson,
            @RequestParam("file") MultipartFile file) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        RealNest r = mapper.readValue(propertyJson, RealNest.class);

        String imageUrl = imageService.uploadImage(file);
        r.setImageUrl(imageUrl);

        return realNestServices.savedetails(r);
    }
}
