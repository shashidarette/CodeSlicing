package fileComparison;

import java.io.*;
import java.util.*;

/**
 * Created by neilwalkinshaw on 21/11/2016.
 */
public class FileComparator {

    File from,to;

    public FileComparator(File from, File to){
        this.from = from;
        this.to = to;
    }

    public double coarseCompare(boolean proportional){
        List<String> fromFile = new ArrayList<String>();
        List<String> toFile = new ArrayList<String>();
        extractStrings(fromFile, from);
        extractStrings(toFile, to);
        int total =fromFile.size() + toFile.size();
        Set<String> toRemove = new HashSet<String>();
        toRemove.add("}");
        toRemove.add("");
        toRemove.add("{");
        fromFile.removeAll(toRemove);
        fromFile.retainAll(toFile);
        double returnVal = fromFile.size();
        if(proportional)
            returnVal = returnVal / total;
        return returnVal;
    }

    private void extractStrings(Collection<String> fromFile, File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while(line != null) {
                line.trim();
                fromFile.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean[][] detailedCompare(){
        List<String> fromFile = new ArrayList<String>();
        List<String> toFile = new ArrayList<String>();
        extractStrings(fromFile, from);
        extractStrings(toFile, to);
        boolean[][] matrix = new boolean[fromFile.size()][toFile.size()];
        for(int i = 0; i< fromFile.size(); i++){
            for(int j = 0; j<toFile.size(); j++){
                boolean match = fromFile.get(i).equals(toFile.get(j));
                if(fromFile.get(i).length()==0 || fromFile.get(i).equals("{")||fromFile.get(i).equals("}"))
                   match = false;
                matrix[i][j]=match;
            }
        }
        return matrix;
    }

}
