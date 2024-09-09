package com.ecommerce.ecommerce.users.service;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.ecommerce.global.LoginDTO;
import com.ecommerce.ecommerce.global.Status;
import com.ecommerce.ecommerce.security.SecurityServices;
import com.ecommerce.ecommerce.users.Entity.User;
import com.ecommerce.ecommerce.users.Entity.UserDTO;
import com.ecommerce.ecommerce.users.Entity.UserFetchPojo;
import com.ecommerce.ecommerce.users.Repository.UserRepo;
import com.ecommerce.ecommerce.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    private String newJwt;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordEncoder PasswordEncoder;

    @Autowired
    SecurityServices securityServices;

    @Override
    public UserFetchPojo getById(Long id) {
        return userRepo.findById(id).map(UserFetchPojo::new)
                .orElseThrow(() -> new EntityNotFoundException("user not found with ID: " + id));
    }

    @Override
    public Status deleteById(Long id) {
        userRepo.findById(id).ifPresentOrElse(
                country -> {
                    userRepo.deleteById(id);
                },
                () -> {
                    throw new EntityNotFoundException("user not found with ID: " + id);
                });
        return new Status(true, "Deleted Successfully");
    }

    @Override
    public Status saveOrUpdate(UserDTO userDTO) {
        try {
            boolean nameExists = userRepo.existsByPhoneAndIdNot(userDTO.getPhone(),
                    userDTO.getId() != null ? userDTO.getId() : -1);
                   
            if (nameExists) {
                return new Status(false, "user already exists");
            }
            User user =new User();
            if(userDTO.getId()==null){
                user.setPhone(userDTO.getPhone());
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                user.setName(userDTO.getName());
                user.setRole(userDTO.getRole());
            }else{
                user=setUser(userDTO);
            }
            

            userRepo.save(user);
            return new Status(true, userDTO.getId() == null ?  "successfuly register" :  this.newJwt);
        } catch (Exception e) {
            log.error("Error saving or updating country: {}", e.getMessage(), e);
            return new Status(false, "An error occurred");
        }
    }
   

    private User setUser(UserDTO userDTO) {
    return userRepo.findById(userDTO.getId())
    .map(existingUser -> {
        existingUser.setName(userDTO.getName());
        existingUser.setAddress(userDTO.getAddress());

    if (!userDTO.getPhone().equals("") || userDTO.getPhone() != null) {
        existingUser.setPhone(userDTO.getPhone());
        this.newJwt = jwtUtil.generateToken(userDTO.getPhone());
        }
        if (!userDTO.getPassword().equals("") || userDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            this.newJwt = jwtUtil.generateToken(userDTO.getPhone());
            }

    return existingUser;
    }).orElseThrow(() -> new EntityNotFoundException("user not found with ID: " +
    userDTO.getId()));
    }

    @Override
    public List<UserFetchPojo> findAll() {
        return userRepo.findAll().stream().map(UserFetchPojo::new).toList();
    }

    @Override
    public Status userLogin(LoginDTO logDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logDto.getPhone(), logDto.getPassword()));
            UserDetails userDetails = securityServices.loadUserByUsername(authentication.getName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            return new Status(true,jwt);
        } catch (Exception e) {
            return new Status(false,"fail to login");
        }
    }

    @Override
    public Status updateProfile(MultipartFile file) {
try{
      Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
 User user=userRepo.findByPhone(authentication.getName());

String fileName=StringUtils.cleanPath(file.getOriginalFilename());
if(fileName.contains("..")){
    return new Status(false, "invalid file type");
}
user.setProfileimage(Base64.getEncoder().encodeToString(file.getBytes()));
userRepo.save(user);

    return new Status(true,"success upload");
}
catch(Exception e){
    return new Status(false,"fail to upload");
}
}

@Override
public UserFetchPojo getAuthUserInfo() {
    UserFetchPojo userFetchPojo=new UserFetchPojo();
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()){
            User user=userRepo.findByPhone(authentication.getName());
            userFetchPojo=new UserFetchPojo(user);
        }
       return userFetchPojo;
    }









    
    }



    

