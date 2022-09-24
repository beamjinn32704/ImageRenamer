
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import java.util.Collection;
import java.util.Iterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class MetadataStorer {
    private Class[] directories;
    private int[] tagsWanted;
    private Checker checker;

    public MetadataStorer(int[] tagsWanted, Checker checker, Class... directories) {
        this.checker = checker;
        this.directories = directories;
        this.tagsWanted = tagsWanted;
    }

    public Class<? extends Directory>[] getDirectories() {
        return directories;
    }

    public int[] getTagsWanted() {
        return tagsWanted;
    }
    
    public Object getTag(Metadata meta){
        Object hold = null;
        for(int i = 0; i < directories.length; i++){
            int tagCode = tagsWanted[i];
            Class<? extends Directory> c = directories[i];
            Collection ds = meta.getDirectoriesOfType(c);
            Iterator it = ds.iterator();
            while(it.hasNext()){
                Directory dir = (Directory) it.next();
                Object res = checker.getter(dir, tagCode);
                if(res != null){
                    if(checker.accept(res)){
                        return res;
                    } else {
                        if(hold == null){
                            hold = res;
                        }
                    }
                }
            }
        }
        return hold;
    }
}
