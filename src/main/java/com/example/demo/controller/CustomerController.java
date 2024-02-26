package com.example.demo.controller;

import com.example.demo.message.GlobalMessage;
import com.example.demo.model.DTO.CustomerDTO;
import com.example.demo.model.DTO.CustomerUpdateDTO;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.interfaces.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController // @RestController dùng cho API còn @Controller dùng cho return View HTML
@RequestMapping("/customers") // Endpoint gốc là /customers
@AllArgsConstructor
@Log4j2
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    // [GET] /customers
    @GetMapping({"", "/"})
    public ResponseEntity<Object> getCustomerList() {
        return ApiResponse.responseBuilder(HttpStatus.OK, GlobalMessage.SUCCESS, customerService.getAll());
    }

    // [GET] /customers/:id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomer(@PathVariable("id") String id) {
        return ApiResponse.responseBuilder(HttpStatus.OK, GlobalMessage.SUCCESS, customerService.getCustomerDTO(id));
    }

    // [POST] /customers/add
    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody CustomerDTO customerDTO) {
        return ApiResponse.responseBuilder(HttpStatus.CREATED, GlobalMessage.SUCCESS, customerService.create(customerDTO));
    }

    // [POST] /customers/update/:id
    @PostMapping("/update/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody CustomerUpdateDTO customerUpdate) {
        return ApiResponse.responseBuilder(HttpStatus.OK, GlobalMessage.SUCCESS, customerService.update(id, customerUpdate));
    }

    // [DELETE] /customers/delete/:id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        return ApiResponse.responseBuilder(HttpStatus.OK, GlobalMessage.SUCCESS, customerService.delete(id));
    }

    // [POST] /customers/import
    @PostMapping("/import")
    public ResponseEntity<Object> importCsvCustomer(@RequestParam("file") MultipartFile[] files) {
        return ApiResponse.responseBuilder(HttpStatus.OK, GlobalMessage.SUCCESS,
                customerService.loadCustomers(files));
    }

    // [GET] /customers/export
    @GetMapping("/export")
    public ResponseEntity<Object> exportCsv(@RequestParam(value = "getTemplate",
            defaultValue = "false") boolean getTemplate) {
        HttpHeaders responseHeader = new HttpHeaders();

        File file = null;
        try {
            file = customerService.exportCsv(getTemplate);
            byte[] data = FileUtils.readFileToByteArray(file);

            responseHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            responseHeader.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + file.getName());
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

            return new ResponseEntity<>(inputStreamResource, responseHeader, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (file != null)
                file.delete();
        }
    }
}
