package com.example.demo_opencv_heroku.controllers;

import com.example.demo_opencv_heroku.services.OpenCVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class OpenCVController {
    @Autowired
    private OpenCVService openCVService;
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public String getImage() {
        return "Hello";
    }
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        File convertFile = new File("src/main/resources/static/images/" +file.getOriginalFilename());
        convertFile.createNewFile();
        FileOutputStream fileOut = new FileOutputStream(convertFile);
        fileOut.write(file.getBytes());
        fileOut.close();
        return new ResponseEntity<>("File is upload successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/snapshot", method = RequestMethod.POST)
    public  ResponseEntity<?> snapshot(){
        if (openCVService.snapShot()){
            return new ResponseEntity<>("File is upload successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("File is upload fail", HttpStatus.BAD_REQUEST);
    }
}
