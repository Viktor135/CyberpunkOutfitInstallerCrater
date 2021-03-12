package de.vsc.coi;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

public class Application {

    public static void main(final String... args) {
        if (args == null || args.length != 1) {
            System.out.println("Please give the workspace as argument.");
            return;
        }
        final File workspace = new File(args[0]);
        if (!workspace.exists() || !workspace.isDirectory()) {
            System.out.println("The workspace has to be an existing directory.");
            return;
        }

        final FileCrawler crawler = new FileCrawler(workspace);
        try {
            final ModuleConfig moduleConfig = crawler.crawl();
            final File out = new File(workspace, "fomod");
            if (!out.exists()) {
                out.mkdirs();
            }
            moduleConfig.marshal(out.getPath());

        } catch (final JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

}
