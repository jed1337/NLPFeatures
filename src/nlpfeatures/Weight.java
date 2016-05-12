package nlpfeatures;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public final class Weight implements FormatString {
   private final char TAG;
   private HashMap<String, Integer> weights;

   public Weight(String path, char tag) throws IOException {
      this.TAG = tag;
      setWeights(path);
   }

   private void setWeights(String inputPath) throws IOException {
      this.weights = new HashMap<>();

      // Get the workbook instance for XLS file
      Iterator<Row> rowIterator = ExcelTools.getRowIterator(inputPath);
      while (rowIterator.hasNext()) {
         Row row = rowIterator.next();

         Iterator<Cell> cellIterator = row.cellIterator();

         try {
            String word = format(cellIterator.next().getStringCellValue());
            int value   = (int) cellIterator.next().getNumericCellValue();
            this.weights.put(word, value);
         } catch (IllegalStateException e) {
            System.err.println(e);
            rowIterator.next();
         }
      }
   }

   @Override
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
