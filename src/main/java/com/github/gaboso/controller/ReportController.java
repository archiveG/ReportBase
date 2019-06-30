package com.github.gaboso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report-base/")
public class ReportController {

    private final ReportService service;

    @Autowired
    public ReportController(ReportService service) {
        this.service = service;
    }


    @GetMapping(path = "download")
    public void download(@RequestParam("name") String name) {
        service.download(name);
    }

}