
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.media.QuickTimeSoundDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class Main {
    public static void main(String[] args) {
        AlphaSuffixer suffixer = new AlphaSuffixer();
        File head = Util.getFile(Util.dirsOnly, "Choose Folder With Images");
        //GO THROUGH. FIND ALL IMAGES. GET DATE. RENAME ...
        if(head == null){
            Util.message(null, "Folder Chosen isn't a Folder.\nPress OK to Quit", "Quit");
            return;
        }
        File destHead = new File("Processed Files-" + CalendarHelper.nowFormat()).getAbsoluteFile();
        File errorLog = new File(destHead, "ErrorLog.txt");
        String errors = "";
        destHead.mkdirs();
        FileNameExtensionFilter vidFilter = new FileNameExtensionFilter("Videos", "avi", "mov", "qt", "mp4", "m4a", "m4p", "m4b", "m4r", "m4v");
        ArrayList<File> media = Util.getFilesOfType(head, Util.imgFilter, vidFilter);
//        ArrayList<File> all = Util.getFilesOfType(head, new FileFilter() {
//            @Override
//            public boolean accept(File f) {
//                return f.isFile();
//            }
//
//            @Override
//            public String getDescription() {
//                return "ALL FILES";
//            }
//        });
        Checker checker = new Checker() {
            @Override
            public boolean accept(Object obj) {
                return obj instanceof Date;
            }
            
            @Override
            public Object getter(Directory dir, int tagName) {
                return dir.getDate(tagName);
            }
        };
        int[] imgTagsWanted = new int[]{
            ExifIFD0Directory.TAG_DATETIME, ExifIFD0Directory.TAG_DATETIME_DIGITIZED,
            ExifIFD0Directory.TAG_DATETIME_ORIGINAL, ExifSubIFDDirectory.TAG_DATETIME,
            ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL
        };
        MetadataStorer imgStorer = new MetadataStorer(imgTagsWanted, checker, ExifIFD0Directory.class,
                ExifIFD0Directory.class, ExifIFD0Directory.class, ExifSubIFDDirectory.class,
                ExifSubIFDDirectory.class, ExifSubIFDDirectory.class);
        
        int[] vidTagsWanted = new int[]{QuickTimeDirectory.TAG_CREATION_TIME, QuickTimeSoundDirectory.TAG_CREATION_TIME,
            QuickTimeMetadataDirectory.TAG_CREATION_DATE, QuickTimeMetadataDirectory.TAG_CREATION_TIME};
        MetadataStorer vidStorer = new MetadataStorer(vidTagsWanted, checker, QuickTimeDirectory.class, QuickTimeSoundDirectory.class,
                QuickTimeMetadataDirectory.class, QuickTimeMetadataDirectory.class);
        Instant i;
        int count = 0;
        for(File m : media){
            try {
                Object date;
                String fileName = m.getName();
                String extension = "";
                int ind = fileName.lastIndexOf('.');
                if (ind > 0) {
                    extension = fileName.substring(ind+1);
                }
                extension = "." + extension;
                String suffix = Util.getNumSuffix(fileName.substring(0, ind));
                Metadata meta = ImageMetadataReader.readMetadata(m);
                if(vidFilter.accept(m)){
                    date = vidStorer.getTag(meta);
                } else {
                    date = imgStorer.getTag(meta);
                }
                if(date == null || !(date instanceof Date)){
                    BasicFileAttributes tribs = Files.readAttributes(m.toPath(), BasicFileAttributes.class);
                    FileTime ft = tribs.creationTime();
                    i = ft.toInstant();
                } else {
                    i = ((Date)date).toInstant();
                }
                if(Util.isBlank(suffix)){
                    suffix = suffixer.next();
                }
                String name = getName(i, suffix) + extension;
                File dest = new File(destHead, name);
                if(dest.isFile()){
                    suffix = suffixer.next();
                    name = getName(i, suffix) + extension;
                    dest = new File(destHead, name);
                    if(dest.isFile()){
                        errors += "Error! Original file " + fileName + " overrode existing processed file " + name + 
                            "!.\n";
                    }
                }
                if(!Copier.copyOp(m, dest)){
                    errors += "Error while copying " + m.getName() + "!\n";
                }
                count++;
            } catch(Exception e){
                e.printStackTrace();
                errors += "Error! " + m.getName() + " couldn't be processed!\n";
            }
        }
        if(Util.isBlank(errors)){
            try(PrintWriter writer = new PrintWriter(errorLog)){
                writer.println("No Errors!");
            } catch (Exception e){
                
            }
        } else {
            try (PrintWriter writer = new PrintWriter(errorLog)){
                writer.println(errors + "\nContanct Developers for Support!");
            } catch (Exception e){
                Util.message(null, "Unable to save error log!", "Error!");
            }
        }
        Util.openDir(destHead);
        if(errorLog.isFile()){
            Util.open(errorLog);
        }
        System.out.println(count);
    }
    
    private static String getName(Instant i, String suffix){
        ZonedDateTime zone = CalendarHelper.convert(i, ZoneOffset.UTC);
        return CalendarHelper.year(zone) + CalendarHelper.month(zone) + CalendarHelper.day(zone) + "_" + suffix;
    }
}