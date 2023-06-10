package com.itk.takeout.service;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

public interface S3BucketService {

    String saveImage(MultipartFile file);

    S3Object download(String fileName);
}
