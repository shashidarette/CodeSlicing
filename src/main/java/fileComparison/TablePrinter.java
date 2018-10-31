package fileComparison;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neilwalkinshaw on 21/11/2016.
 */
public class TablePrinter {



    public static void printRelations(double[][] scores, File target, List<File> files) throws IOException {
        FileWriter fw = new FileWriter(target);
        CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.EXCEL);
        List<String> record = new ArrayList<String>();
        record.add("From");
        record.add("To");
        record.add("Overlap");
        csvPrinter.printRecord(record);
        for(int i = 0; i<scores.length; i++){

            double[] toScores = scores[i];
            for(int j = i; j<toScores.length;j++){
                record = new ArrayList<String>();
                record.add(files.get(i).getName());
                record.add(files.get(j).getName());
                record.add(Double.toString(toScores[j]));
                csvPrinter.printRecord(record);
                record = new ArrayList<String>();
                record.add(files.get(j).getName());
                record.add(files.get(i).getName());
                record.add(Double.toString(toScores[j]));
                csvPrinter.printRecord(record);
            }

        }
        csvPrinter.close();
    }

    public static void printRelations(boolean[][] scores, File target) throws IOException {
        FileWriter fw = new FileWriter(target);
        CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.EXCEL);
        List<String> record = new ArrayList<String>();
        record.add("FromLine");
        record.add("ToLine");
        record.add("Match");
        csvPrinter.printRecord(record);
        for(int i = 0; i<scores.length; i++){

            boolean[] toScores = scores[i];
            for(int j = i; j<toScores.length;j++){
                record = new ArrayList<String>();
                record.add(Integer.toString(i));
                record.add(Integer.toString(j));
                if(toScores[j])
                    record.add(Integer.toString(1));
                else
                    record.add(Integer.toString(0));
                csvPrinter.printRecord(record);
                record = new ArrayList<String>();
                record.add(Integer.toString(j));
                record.add(Integer.toString(i));
                if(toScores[j])
                    record.add(Integer.toString(1));
                else
                    record.add(Integer.toString(0));
                csvPrinter.printRecord(record);
            }

        }
        csvPrinter.close();
    }

}
