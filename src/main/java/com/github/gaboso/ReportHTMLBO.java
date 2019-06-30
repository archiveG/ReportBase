package com.github.gaboso;


import com.github.gaboso.config.ITextPDFUserAgent;
import com.github.gaboso.config.ReportConfig;
import com.github.gaboso.exception.ReportException;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
public class ReportHTMLBO {

    private static final String UTF_8 = StandardCharsets.UTF_8.toString();
    private SimpleDateFormat formatterDateHourDownload = new SimpleDateFormat("yyyyMMddHHmmss");

    private final HttpServletResponse httpServletResponse;
    private final ResourceLoader resourceLoader;

    @Value("classpath:report/misc/logo.png")
    private Resource logo;

    public ReportHTMLBO(HttpServletResponse httpServletResponse, ResourceLoader resourceLoader) {
        this.httpServletResponse = httpServletResponse;
        this.resourceLoader = resourceLoader;
    }


    public void download(ReportConfig config) {
        try {
            String html = processHTMLTemplate(config);
            String xhtml = parseToXHTML(html);
            byte[] pdfBytes = generatePDFBytes(xhtml);

            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.setHeader("content-disposition", "attachment; filename=\"" + generateReportName(config) + ".pdf\"");

            httpServletResponse.getOutputStream().write(pdfBytes);
            httpServletResponse.flushBuffer();
        } catch (IOException e) {
            throw new ReportException("Error while downloading report: " + config.getReportName(), e);
        }
    }

    public String printSavedReport(ReportConfig config) {
        String html = processHTMLTemplate(config);
        String xhtml = parseToXHTML(html);
        byte[] pdfBytes = generatePDFBytes(xhtml);

        return DatatypeConverter.printBase64Binary(pdfBytes);
    }

    private static String processHTMLTemplate(ReportConfig config) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(UTF_8);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context thymeleafContext = buildThymeleafContext(config);
        return templateEngine.process(config.getTemplateBase(), thymeleafContext);
    }

    private static Context buildThymeleafContext(ReportConfig config) {
        Context context = new Context();
        context.setVariables(config.getParams());
        context.setVariable("templateHeader", config.getTemplateHeader());
        context.setVariable("templateFooter", config.getTemplateFooter());
        context.setVariable("templateHeaderDefault", config.getTemplateHeaderDefault());
        context.setVariable("templateFooterDefault", config.getTemplateFooterDefault());

        return context;
    }

    private byte[] generatePDFBytes(String xHtml) {
        try (ByteArrayOutputStream pdfWriter = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFontDirectory("report/font/roboto", true);
            (new ITextPDFUserAgent(renderer, this.resourceLoader)).apply();

            renderer.setDocumentFromString(xHtml);
            renderer.layout();
            renderer.createPDF(pdfWriter);

            return pdfWriter.toByteArray();
        } catch (Exception e) {
            throw new ReportException("Erro ao gerar PDF.", e);
        }
    }


    private static String parseToXHTML(String html) {
        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {

            Tidy tidy = new Tidy();
            tidy.setInputEncoding(UTF_8);
            tidy.setOutputEncoding(UTF_8);
            tidy.setXHTML(true);
            tidy.parseDOM(inputStream, outputStream);
            return outputStream.toString(UTF_8);
        } catch (IOException e) {
            throw new ReportException("Erro ao converter p/ XHTML.", e);
        }
    }

    private String generateReportName(ReportConfig config) {
        return config.getReportName() + formatterDateHourDownload.format(new Date());
    }


    private void loadLogo(HashMap<String, Object> parametros) throws IOException {
        InputStream inputStream = logo.getInputStream();
        loaderImageBase(parametros, inputStream, "logo");
    }


    private static void loaderImageBase(HashMap<String, Object> params, InputStream inputStream, String paramKey) throws IOException {
        byte[] bytesImagem = new byte[inputStream.available()];

        if (inputStream.read(bytesImagem) > -1) {
            params.put(paramKey, Base64.encodeBase64String(bytesImagem));
        } else {
            throw new ReportException("Erro ao carregar '" + paramKey + "' p/ o relatório.", null);
        }
    }

    public ReportConfig buildDefaultConfig(String reportName) {
        try {
            HashMap<String, Object> params = new HashMap<>();
            loadLogo(params);
            return new ReportConfig(reportName, params);
        } catch (IOException e) {
            throw new ReportException("Não foi possível carregar as configurações básicas do relatório.", e);
        }
    }


    public String removeImgTag(String html) {
        return html.replaceAll("<img([\\w\\W]+?)/>", "");
    }

}