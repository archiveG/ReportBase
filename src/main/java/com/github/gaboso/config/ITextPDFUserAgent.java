package com.github.gaboso.config;

import com.github.gaboso.exception.ReportException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ResourceLoader;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ITextPDFUserAgent extends ITextUserAgent {

    private static final String RESOURCE_PATH_CSS = "classpath:report/html/css/";
    private static final String RESOURCE_PATH_IMAGE = "classpath:report/misc/";
    private static final List SUPPORTED_IMAGE_EXTENSIONS = Arrays.asList("png", "jpg", "svg");
    private static final String CSS_FILE_EXTENSION = "css";

    private ITextRenderer defaultRenderer;
    private ResourceLoader resourceLoader;

    public ITextPDFUserAgent(ITextRenderer defaultRenderer, ResourceLoader resourceLoader) {
        super(new ITextOutputDevice(defaultRenderer.getDotsPerPoint()));
        this.defaultRenderer = defaultRenderer;
        this.resourceLoader = resourceLoader;
    }

    public void apply() {
        SharedContext sharedContext = defaultRenderer.getSharedContext();
        setSharedContext(sharedContext);
        sharedContext.setUserAgentCallback(this);
    }

    @Override
    protected InputStream openStream(String uri) {
        try {
            return this.resourceLoader.getResource(redirect(uri)).getInputStream();
        } catch (Exception e) {
            throw new ReportException("Error while loading resource.", e);
        }
    }

    private static String redirect(String uri) {
        String resourceName = FilenameUtils.getName(uri);
        String extension = FilenameUtils.getExtension(resourceName);

        if (CSS_FILE_EXTENSION.equals(extension)) {
            return RESOURCE_PATH_CSS + resourceName;
        }
        if (SUPPORTED_IMAGE_EXTENSIONS.contains(extension)) {
            return RESOURCE_PATH_IMAGE + resourceName;
        }

        throw new ReportException("Resource " + extension + " not supported", null);
    }

}