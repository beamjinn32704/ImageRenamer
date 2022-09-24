
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class Copier {
    public static boolean copyOp(File file, File dest){
        InputStream is = null;
        OutputStream os = null;
        try {
            int bufferSize = 100000;
            is = new FileInputStream(file);
            os = new BufferedOutputStream(new FileOutputStream(dest), bufferSize);
            byte[] buffer = new byte[bufferSize];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            System.out.println("File Copied: " + dest.getAbsolutePath());
            is.close();
            os.close();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            try {
                is.close();
                os.close();
            } catch(Exception f) {
                f.printStackTrace();
            }
            return false;
        }
    }
}
