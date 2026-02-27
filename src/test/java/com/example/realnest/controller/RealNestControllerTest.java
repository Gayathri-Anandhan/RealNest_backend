package com.example.realnest.controller;

import com.example.realnest.config.AuthFilter;
import com.example.realnest.config.JwtUtil;
import com.example.realnest.entity.RealNest;
import com.example.realnest.service.ImageService;
import com.example.realnest.service.RealNestServices;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyController.class)
@AutoConfigureMockMvc(addFilters = false)
class RealNestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RealNestServices realNestServices;

    @MockBean
    private ImageService imageService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthFilter authFilter;

    //  TEST 1: GET all properties
    @Test
    void shouldReturnAllProperties() throws Exception {

        RealNest property = new RealNest();
        property.setId(1L);
        property.setTitle("Villa");

        when(realNestServices.getAllProperties())
                .thenReturn(Arrays.asList(property));

        mockMvc.perform(get("/api/properties/allproperties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Villa"));
    }

    // TEST 2: GET property by ID
    @Test
    void shouldReturnPropertyById() throws Exception {

        RealNest property = new RealNest();
        property.setId(1L);
        property.setTitle("Apartment");

        when(realNestServices.getPropertyById(1L))
                .thenReturn(property);

        mockMvc.perform(get("/api/properties/viewproperties")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Apartment"));
    }

    //  TEST 3: DELETE property
    @Test
    void shouldDeleteProperty() throws Exception {

        RealNest property = new RealNest();
        property.setId(1L);

        when(realNestServices.deleteProperty(1L))
                .thenReturn(property);

        mockMvc.perform(delete("/api/properties/deleteProperty")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Property deleted successfully with id: 1"));
    }

    //  TEST 4: SAVE property (Multipart)
    @Test
    void shouldSaveProperty() throws Exception {

        String propertyJson = "{\"title\":\"Villa\"}";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy-image".getBytes());

        RealNest savedProperty = new RealNest();
        savedProperty.setTitle("Villa");

        when(imageService.uploadImage(Mockito.any()))
                .thenReturn("image-url");

        when(realNestServices.savedetails(Mockito.any(RealNest.class)))
                .thenReturn(savedProperty);

        mockMvc.perform(
                multipart("/api/properties/saveProperty")
                        .file(file)
                        .param("property", propertyJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Villa"));
    }
}