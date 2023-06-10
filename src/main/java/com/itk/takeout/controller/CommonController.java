package com.itk.takeout.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.itk.takeout.common.R;
import com.itk.takeout.service.S3BucketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {


    @Autowired
    private S3BucketService s3BucketService;

    private static final String COVER_IMAGE_PATH = "cover-image";

    /**
     * upload file
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        String name = s3BucketService.saveImage(file);
        return R.success(name);
    }

    /**
     * download files
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        log.info(name);
        // Set the content type based on the file extension
        String contentType = getContentTypeByFileName(name);
        response.setContentType(contentType);

        // Set the content disposition to attachment to force download
        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");

        // Get the S3 object from the specified path and filename
        S3Object s3Object = s3BucketService.download(name);

        // Get the input stream from the S3 object
        try (InputStream inputStream = s3Object.getObjectContent()) {
            // Get the output stream from the HTTP response
            try (OutputStream output = response.getOutputStream()) {
                // Read and write the data in chunks
                int bytesRead;
                byte[] buffer = new byte[1024];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private String getContentTypeByFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        switch (extension.toLowerCase()) {
            case "png":
                return "image/png";
            case "bmp":
                return "image/bmp";
            case "gif":
                return "image/gif";
            case "jpeg":
            case "jpg":
                return "image/jpeg";
            default:
                return "application/octet-stream";
        }
    }
//    @Value("${takeout.path}")
//    private String basePath;
//    /**
//     * upload file
//     * @param file
//     * @return
//     */
//    @PostMapping("/upload")
//    public R<String> upload(MultipartFile file) throws IOException {
//        log.info(file.toString());
//
//        //get original name
//        String orginalFile = file.getOriginalFilename();
//
//        // set unique name
//        String name = UUID.randomUUID().toString()+orginalFile;
//
//        //check dir exists
//        File dir = new File(basePath);
//        if (!dir.exists()){
//            dir.mkdirs();
//        }
//
//        file.transferTo(new File(basePath+name));
//        return R.success(name);
//    }
//
//    /**
//     * download files
//     */
//    @GetMapping("/download")
//    public void download(String name, HttpServletResponse response) throws IOException {
//        FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
//
//        ServletOutputStream output = response.getOutputStream();
//
//        response.setContentType("image/jpeg");
//
//        int len=0;
//        byte[] bytes = new byte[1024];
//        while((len=fileInputStream.read(bytes))!= -1){
//            output.write(bytes,0,len);
//            output.flush();
//        }
//
//        output.close();
//        fileInputStream.close();
//    }
}
