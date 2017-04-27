package it.redhat.demo.jms.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by fabio.ercoli@redhat.com on 19/04/17.
 */

@ApplicationScoped
public class FileContentProducer {

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private static final Logger LOG = LoggerFactory.getLogger(FileContentProducer.class);

    private String startProcess = null;
    private String sendSignal = null;

    @PostConstruct
    public void init() {

        startProcess = loadFile("startProcess.json");
        sendSignal = loadFile("sendSignal.json");

    }

    @Produces
    @StartProcess
    public String startProcess() {

        return startProcess;

    }

    @Produces
    @SendSignal
    public String sendSignal() {

        return sendSignal;

    }

    private String loadFile(String name) {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(name);

        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(is, UTF8_CHARSET));

            for(String e = reader.readLine(); e != null; e = reader.readLine()) {
                sb.append(e).append("\n");
            }
        } catch (IOException var11) {
            throw new RuntimeException(var11);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }

        }

        return sb.toString();
    }


}
