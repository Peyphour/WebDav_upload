package fr.bnancy.webdav_upload;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by bertrand on 01/03/17.
 *
 * args[0] => Root directory to upload
 * args[1] => WebDav username
 * args[2] => WebDav password
 * args[3] => WebDav URL
 *
 * task Gradle fatJar to create a runnable Jar file
 */
public class Main {

    public static void main(String args[]) {
        Sardine sardine = SardineFactory.begin(args[1], args[2]);

        File root = new File(args[0]);

        sanitizeNames(root);

        try {
            uploadDir(sardine, root, args[3]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sanitizeNames(File root) {
        for(File f : root.listFiles()) {
            if(f.isDirectory())
                sanitizeNames(f);
            System.out.println("renaming " + f.getName() + " to " + f.getName().replaceAll("[^a-zA-Z0-9.-/]", "_"));
            f.renameTo(new File(f.getParent()  + "/" + f.getName().replaceAll("[^a-zA-Z0-9.-/]", "_")));
        }
    }

    private static void uploadDir(Sardine sardine, File root, String url) throws IOException {
        for(File f : root.listFiles()) {
            String relativePath = root.toURI().relativize(f.toURI()).getPath();
            if(f.isDirectory()) {
                System.out.println("Creating dir " + root.getPath() + relativePath);
                sardine.createDirectory(url + relativePath);
                uploadDir(sardine, f, url + relativePath);
                f.delete();
            } else if(f.isFile()) {
                System.out.println("Uploading file " + root.getPath() + relativePath);
                sardine.put(url + relativePath, new FileInputStream(f));
                f.delete();
            }
        }
    }
}
