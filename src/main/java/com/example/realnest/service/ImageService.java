package com.example.realnest.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ImageService {

    @Autowired
    private Cloudinary cloudinary;

    @Value("${cloudinary.use_unsigned}")
    private boolean useUnsigned;

    @Value("${cloudinary.unsigned_preset}")
    private String unsignedPreset;

    @Value("${cloudinary.folder}")
    private String folder;

    public String uploadImage(MultipartFile file) throws Exception {

        Map uploadResult;

        if (useUnsigned) {
            // Unsigned upload
            uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "upload_preset", unsignedPreset,
                            "folder", folder
                    )
            );
        } else {
            // Signed upload
            uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );
        }

        return uploadResult.get("secure_url").toString();
    }
}