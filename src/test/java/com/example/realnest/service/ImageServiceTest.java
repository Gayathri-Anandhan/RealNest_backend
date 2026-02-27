package com.example.realnest.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setup() throws Exception {
        // Mock cloudinary uploader
        when(cloudinary.uploader()).thenReturn(uploader);

        // Set private @Value fields using reflection
        setPrivateField("useUnsigned", true);
        setPrivateField("unsignedPreset", "preset123");
        setPrivateField("folder", "test_folder");
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = ImageService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(imageService, value);
    }

    @Test
    void testUploadImage_Unsigned() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "dummy-image".getBytes()
        );

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("secure_url", "http://mock-image-url.com/test.jpg");

        when(uploader.upload(eq(file.getBytes()), any(Map.class))).thenReturn(mockResult);

        String url = imageService.uploadImage(file);

        assertEquals("http://mock-image-url.com/test.jpg", url);
        verify(uploader, times(1)).upload(eq(file.getBytes()), any(Map.class));
    }

    @Test
    void testUploadImage_Signed() throws Exception {
        // Change useUnsigned to false
        setPrivateField("useUnsigned", false);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "dummy-image".getBytes()
        );

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("secure_url", "http://mock-image-signed.com/test.jpg");

        when(uploader.upload(eq(file.getBytes()), any(Map.class))).thenReturn(mockResult);

        String url = imageService.uploadImage(file);

        assertEquals("http://mock-image-signed.com/test.jpg", url);
        verify(uploader, times(1)).upload(eq(file.getBytes()), any(Map.class));
    }
}