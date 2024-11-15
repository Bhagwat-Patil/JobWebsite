package com.jobwebsite.ServiceImpl;

//form serviceimpl

import com.jobwebsite.Entity.Form;
import com.jobwebsite.Repository.FormRepository;
import com.jobwebsite.Service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class FormServiceImpl implements FormService {

    private final static Logger logger= LoggerFactory.getLogger(FormServiceImpl.class);

    @Autowired
    private final FormRepository formRepository;

    public FormServiceImpl(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    @Override
    public String saveUser(Form user) {
        try {
            logger.info("Attempting to save new user: {}", user);
            formRepository.save(user);
            return "User data save successfully";
        } catch (DataAccessException ex) {
            logger.error("Error saving user: {}", ex.getMessage());
            throw new RuntimeException("Unable to save user. Please try again.");
        }
    }
}

