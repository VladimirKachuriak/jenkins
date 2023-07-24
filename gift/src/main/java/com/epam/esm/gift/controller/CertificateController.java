package com.epam.esm.gift.controller;

import com.epam.esm.gift.model.Certificate;
import com.epam.esm.gift.model.exception.ResourceNotFoundException;
import com.epam.esm.gift.service.CertificateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("certificate")
public class CertificateController {
    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public List<Certificate> getAll(@RequestParam(name = "tag", required = false) String tagName,
                                    @RequestParam(name = "nameOrDescription", required = false) String description,
                                    @RequestParam(name = "sortByDate", required = false) String sortByDate,
                                    @RequestParam(name = "sortByName", required = false) String sortByName) throws Exception {
        List<Certificate> certificates = certificateService.getAll(tagName, description, sortByDate, sortByName);
        return certificates;
    }

    @GetMapping("/{id}")
    public Certificate getCertificateById(@PathVariable int id) {
        Certificate certificate = certificateService.getById(id);
        if (certificate == null) throw new ResourceNotFoundException(id);
        return certificate;
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody Certificate certificate, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        certificateService.create(certificate);
        return ResponseEntity.ok("Resource created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody Certificate certificate) throws Exception {
        certificate.setId(id);
        certificateService.update(certificate);

        return ResponseEntity.ok("Resource updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable int id) throws Exception {
        certificateService.delete(id);

        return ResponseEntity.ok("Resource deleted successfully");
    }


/*    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        System.out.println("fadsafs");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("fsad");
    }*/
}
