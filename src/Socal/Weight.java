package Socal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import nlpfeatures.ExcelOutput;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public final class Weight{
   private final char TAG;
   private HashMap<String, Integer> weights;

   public Weight(String path, char tag) throws IOException {
      this.TAG = tag;
      setWeights(path);
   }

   private void setWeights(String inputPath) throws IOException {
      this.weights = new HashMap<>();

      for(Row row : ExcelOutput.getSheet(inputPath)){
         Iterator<Cell> cellIterator = row.cellIterator();
         try {
            String word = format(cellIterator.next().getStringCellValue());
            int value   = (int) cellIterator.next().getNumericCellValue();
            this.weights.put(word, value);
         } catch (IllegalStateException e) {
            System.err.println(e);
         }
      }
   }

   public String format(String word) {
      return word.toLowerCase().trim();
   }

   public int getWordValue(String key) {
      return weights.getOrDefault(key, 0);
   }

   public char getTag() {
      return TAG;
   }
}
