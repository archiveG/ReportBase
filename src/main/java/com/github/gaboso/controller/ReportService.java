package com.github.gaboso.controller;

import com.github.gaboso.ReportHTMLBO;
import com.github.gaboso.config.ReportConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    private final ReportHTMLBO reportHTMLBO;

    public ReportService(ReportHTMLBO reportHTMLBO) {
        this.reportHTMLBO = reportHTMLBO;
    }

    public void download(String name) {
        reportHTMLBO.download(configurar(name));
    }

    private ReportConfig configurar(String name) {
        ReportConfig config = reportHTMLBO.buildDefaultConfig("sells")
                .setTemplateHeaderName("header-logo")
                .setTemplateFooterName("footer-page");

        inserirParametros(config, name);

        return config;
    }


    private void inserirParametros(ReportConfig config, String name) {
        config.addParam("NAME", name);
    }

}