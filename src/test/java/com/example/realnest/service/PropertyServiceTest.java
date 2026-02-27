package com.example.realnest.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import com.example.realnest.entity.RealNest;
import com.example.realnest.repository.PropertyDetailsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyDetailsRepo propertyDetailsRepo;

    @Mock
    private Cloudinary cloudinary;

    @InjectMocks
    private RealNestServices realNestServices;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Uploader mockUploader;

    private RealNest property;

    @BeforeEach
    void setUp() {
        property = new RealNest();
        property.setId(1L);
        property.setTitle("Villa");
        property.setLocation("Chennai");
        property.setPrice(1000);
        property.setType("Residential");
        property.setDescription("Nice property");
        property.setImageUrl("image-url");
    }

    @Test
    void testSaveDetails() {
        when(propertyDetailsRepo.save(property)).thenReturn(property);
        RealNest saved = realNestServices.savedetails(property);
        assertNotNull(saved);
        assertEquals("Villa", saved.getTitle());
        verify(propertyDetailsRepo, times(1)).save(property);
    }

    @Test
    void testGetAllProperties() {
        List<RealNest> list = Collections.singletonList(property);
        when(propertyDetailsRepo.findAll()).thenReturn(list);
        List<RealNest> result = realNestServices.getAllProperties();
        assertEquals(1, result.size());
        verify(propertyDetailsRepo, times(1)).findAll();
    }

    @Test
    void testGetPropertyById_Found() {
        when(propertyDetailsRepo.findById(1L)).thenReturn(Optional.of(property));
        RealNest result = realNestServices.getPropertyById(1L);
        assertNotNull(result);
        assertEquals("Villa", result.getTitle());
    }

    @Test
    void testGetPropertyById_NotFound() {
        when(propertyDetailsRepo.findById(1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> realNestServices.getPropertyById(1L));
        assertEquals("Property not found", exception.getMessage());
    }

    @Test
    void testGetPropertyByFilters() {
        when(propertyDetailsRepo.findByLocationAndPriceAndType("Chennai", 1000, "Residential"))
                .thenReturn(property);
        RealNest result = realNestServices.getPropertyByFilters("Chennai", 1000, "Residential");
        assertNotNull(result);
        assertEquals("Villa", result.getTitle());
    }

    @Test
    void testUpdateProperty_Success() {
        RealNest updated = new RealNest();
        updated.setTitle("Updated Villa");
        updated.setLocation("Mumbai");
        updated.setPrice(2000);
        updated.setType("Commercial");
        updated.setDescription("Updated Description");
        updated.setImageUrl("updated-image-url");

        when(propertyDetailsRepo.findById(1L)).thenReturn(Optional.of(property));
        when(propertyDetailsRepo.save(any(RealNest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RealNest result = realNestServices.updateProperty(1L, updated);

        assertEquals("Updated Villa", result.getTitle());
        assertEquals("Mumbai", result.getLocation());
        assertEquals(2000, result.getPrice());
        assertEquals("Commercial", result.getType());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("updated-image-url", result.getImageUrl());

        verify(propertyDetailsRepo, times(1)).findById(1L);
        verify(propertyDetailsRepo, times(1)).save(any(RealNest.class));
    }

    @Test
    void testUpdateProperty_NotFound() {
        when(propertyDetailsRepo.findById(1L)).thenReturn(Optional.empty());
        RealNest updated = new RealNest();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> realNestServices.updateProperty(1L, updated));

        // Use the exact substring from service, notice no space before "is"
        assertTrue(exception.getMessage().contains("Property with this id: 1is not found"));

        verify(propertyDetailsRepo, times(1)).findById(1L);
        verify(propertyDetailsRepo, never()).save(any());
    }

    @Test
    void testDeleteProperty_Success() {
        when(propertyDetailsRepo.findById(1L)).thenReturn(Optional.of(property));
        doNothing().when(propertyDetailsRepo).deleteById(1L);

        RealNest deleted = realNestServices.deleteProperty(1L);
        assertNotNull(deleted);
        assertEquals(1L, deleted.getId());

        verify(propertyDetailsRepo, times(1)).findById(1L);
        verify(propertyDetailsRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProperty_NotFound() {
        when(propertyDetailsRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> realNestServices.deleteProperty(1L));
        assertEquals("Property not found", exception.getMessage());
    }

    @Test
    void testUploadImage() throws Exception {
        // Prepare mock Cloudinary upload result
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/fake-image.jpg");

        // Inject mock uploader
        when(cloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.upload(any(byte[].class), eq(ObjectUtils.emptyMap()))).thenReturn(uploadResult);
        when(multipartFile.getBytes()).thenReturn("dummy-image".getBytes());

        String url = realNestServices.uploadImage(multipartFile);
        assertEquals("https://cloudinary.com/fake-image.jpg", url);

        verify(mockUploader, times(1)).upload(any(byte[].class), eq(ObjectUtils.emptyMap()));
    }
}