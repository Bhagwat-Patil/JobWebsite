package com.jobwebsite.Controller;

//form controller
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobwebsite.Entity.Form;
import com.jobwebsite.Service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin("*")
public class FormController {

    private static final Logger logger= LoggerFactory.getLogger(FormController.class);

    @Autowired
    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping("/form/saveForm")
    public ResponseEntity<String> saveUser(@RequestPart("formData") String formData,
                                           @RequestPart(value = "cv",required = false ) MultipartFile multipartFile) throws JsonProcessingException {
        logger.info("Received request to save user");
        ObjectMapper objectMapper = new ObjectMapper();
        Form user = objectMapper.readValue(formData, Form.class);

        // handle cv
        if(!multipartFile.isEmpty() && multipartFile!=null){
            String fileType = multipartFile.getContentType();
            if ("application/pdf".equals(fileType) || "image/jpeg".equals(fileType) || "image/jpg".equals(fileType)) {
                try {
                    user.setCv(multipartFile.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Only PDF and JPEG/JPG files are allowed.");
            }
        }
        else{
            user.setCv(null);
        }
        String message=formService.saveUser(user);
        return  ResponseEntity.status(HttpStatus.OK).body("User successfully save");
    }

}

