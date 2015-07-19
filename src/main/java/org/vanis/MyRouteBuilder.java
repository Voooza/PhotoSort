package org.vanis;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    public static final String[] PHOTO_EXTS = {"JPG", "JPEG"};

    private static final List<String> PHOTO_EXTS_LIST = Arrays.asList(PHOTO_EXTS);

    public static final String PHOTO_PROCESSOR_ENDPOINT = "direct:photoProcessor";

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {


        from("file:/home/voooza/tmp/photosort")
                .process(exchange -> {
                    File f = exchange.getIn().getBody(File.class);
                    exchange.getIn().setBody(f.getAbsolutePath());
                })
                .choice()
                    .when(exchange -> {
                        String filePath = exchange.getIn().getBody(String.class);
                        String ext = filePath.substring(filePath.lastIndexOf('.') + 1).toUpperCase();
                        return PHOTO_EXTS_LIST.contains(ext);
                    }).to(PHOTO_PROCESSOR_ENDPOINT)
                    .otherwise()
                        .log(LoggingLevel.INFO, "File ${body} is not a photo or has unknown format")
                        .end();

        from(PHOTO_PROCESSOR_ENDPOINT)
                .log(LoggingLevel.INFO, "Processing photo: ${body}");
    }

}
