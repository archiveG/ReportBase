package com.github.gaboso.config;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class ReportConfig {

    private static final String REPORT_PREFIX_HTML = "report/html";
    private static final String TEMPLATE_PREFIX_HEADER = "header";
    private static final String TEMPLATE_PREFIX_FOOTER = "footer";
    private static final String TEMPLATE_SUFFIX = "template";
    private static final String TEMPLATE_HEADER_NAME_DEFAULT = "header-logo";
    private static final String TEMPLATE_FOOTER_NAME_DEFAULT = "footer-page";

    private String templateHeaderName;
    private String templateFooterName;
    private String reportName;
    private Map<String, Object> params;
    private Collection fields;

    public ReportConfig(String reportName, Map<String, Object> params) {
        this.reportName = reportName;
        this.params = params;
    }


    public String getReportName() {
        return this.reportName;
    }


    public Collection getFields() {
        return this.fields;
    }


    public ReportConfig setFields(Collection fields) {
        this.fields = fields;
        return this;
    }

    public ReportConfig addParam(String name, Object value) {
        this.params.put(name, value);
        return this;
    }


    public Map<String, Object> getParams() {
        return this.params;
    }


    public String getTemplateBase() {
        return String.join(File.separator, REPORT_PREFIX_HTML, this.reportName, TEMPLATE_SUFFIX);
    }


    public String getTemplateHeader() {
        return String.join(File.separator, REPORT_PREFIX_HTML, TEMPLATE_PREFIX_HEADER,
                Objects.isNull(this.templateHeaderName) ? TEMPLATE_HEADER_NAME_DEFAULT : this.templateHeaderName);
    }

    public ReportConfig setTemplateHeaderName(String templateHeaderName) {
        this.templateHeaderName = templateHeaderName;
        return this;
    }

    public String getTemplateFooter() {
        return String.join(File.separator, REPORT_PREFIX_HTML, TEMPLATE_PREFIX_FOOTER,
                Objects.isNull(this.templateFooterName) ? TEMPLATE_FOOTER_NAME_DEFAULT : this.templateFooterName);
    }

    public ReportConfig setTemplateFooterName(String templateFooterName) {
        this.templateFooterName = templateFooterName;
        return this;
    }


    public String getTemplateHeaderDefault() {
        return String.join(File.separator, REPORT_PREFIX_HTML, TEMPLATE_PREFIX_HEADER, TEMPLATE_HEADER_NAME_DEFAULT);
    }


    public String getTemplateFooterDefault() {
        return String.join(File.separator, REPORT_PREFIX_HTML, TEMPLATE_PREFIX_FOOTER, TEMPLATE_FOOTER_NAME_DEFAULT);
    }

}