package com.itk.takeout.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import com.itk.takeout.common.CustomException;
import com.itk.takeout.config.AmazonConfig;
import com.itk.takeout.service.S3BucketService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.multipart.MultipartFile;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.apache.http.entity.ContentType.*;

@Service
@Configuration
public class S3BucketSeriviceImpl implements S3BucketService {

    @Autowired
    private AmazonConfig amazonConfig;

    @Value("${amazon.s3bucket.bucketName}")
    private String bucketName;

    private static final String COVER_IMAGE_PATH = "cover-image";

    @Override
    public String saveImage(MultipartFile file) {
        //check if the file is empty
        if (file.isEmpty()) {
            throw new CustomException("Cannot upload empty file");
        }
        //Check if the file is an image
        List<String> allowedContentTypes = Arrays.asList(
                IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType()
        );

        if (!allowedContentTypes.contains(file.getContentType())) {
            throw new CustomException("File uploaded is not an image");
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        //Save Image in S3 and return file path
        String orginalFile = file.getOriginalFilename();
        String s3BucketFilePath = String.format("%s/%s", COVER_IMAGE_PATH, UUID.randomUUID()+orginalFile);
        PutObjectResult putObjectResult = null;
        try {
            putObjectResult=amazonConfig.s3().putObject(
                    bucketName, s3BucketFilePath, file.getInputStream(), objectMetadata
            );
        } catch (AmazonServiceException e) {
            throw new CustomException("Failed to upload the file");
        } catch (IOException e) {
            throw new CustomException("Failed to upload file");
        }
        return s3BucketFilePath;
    }

    @Override
    public S3Object download(String fileName) {

        return amazonConfig.s3().getObject(bucketName, fileName);
    }
}
