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
 */
public class Main {

    public static void main(String args[]) {
        Sardine sardine = SardineFactory.begin(args[1], args[2]);

        File root = new File(args[0]);

        try {
            uploadDir(sardine, root, args[3]);
        } catch (IOException e) {
            e.printStackTrace();
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
