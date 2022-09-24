
import com.drew.metadata.Directory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public interface Checker {
    boolean accept(Object obj);
    
    Object getter(Directory dir, int tagName);
}
