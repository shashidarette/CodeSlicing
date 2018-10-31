package fileComparison;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by neilwalkinshaw on 21/11/2016.
 */
public class DuplicateDetector {

    List<File> files;
    String suffix;

    public DuplicateDetector(File root, String suffix){
        files = new ArrayList<File>();
        this.suffix = suffix;
        populateFiles(root);

    }

    private void populateFiles(File root) {
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(root.listFiles()));
        for(File f: files) {
            if(f.isDirectory())
                populateFiles(f);
            else if(f.getName().endsWith(suffix)){
                this.files.add(f);
            }
        }
    }

    public double[][]fileComparison(boolean proportional){
        double[][] fileCompare = new double[files.size()][files.size()];
        for(int i = 0; i<files.size(); i++){
            for(int j = i+1; j<files.size(); j++){
                double score;
                if(i == j)
                    score = 0;
                else{
                    FileComparator fc = new FileComparator(files.get(i),files.get(j));
                    score = fc.coarseCompare(proportional);
                }
                fileCompare[i][j] = score;
                fileCompare[j][i] = score;
            }
        }
        return fileCompare;
    }

    public List<File> getFiles(){
        return files;
    }

}
