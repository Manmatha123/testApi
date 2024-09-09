package com.ecommerce.ecommerce.products.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.ecommerce.global.Status;
import com.ecommerce.ecommerce.products.entity.ProductDTO;
import com.ecommerce.ecommerce.products.service.ProductService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/product")
public class ProductController {
   
    

       
@Autowired
private ProductService pService;

// @GetMapping(value="/list/user/id/{id}")
// public List<ProductDTO> listByUserId(@PathVariable Long id){
//     return pService.listByUserId(id);
// }

    // @GetMapping(value="/list/place/{place}")
    // public List<ProductDTO> findAllByPlace(@PathVariable String place){
    //     return pService.findAllByStorePlace(place);
    // }
    
    @GetMapping(value="/list/category/{category}")
    public List<ProductDTO> findAllByCategory(@PathVariable String category){
        return pService.findAllByCategory(category);
    }
    @GetMapping(value="/id/{id}")
    public ProductDTO getById(@PathVariable Long id ){
        return pService.findByProductId(id);
    }
    @RequestMapping(value = "/saveorupdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public Status saveOrUpdateProduct(@RequestPart("product") ProductDTO productDTO,@RequestPart("file")MultipartFile file){
        return pService.saveOrUpdate(productDTO,file);
    }

    @DeleteMapping(value="/delete/id/{id}")
    public Status deleteById(@PathVariable Long id ){
        return pService.deleteProductById(id);
    }


}
