package Socal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import nlpfeatures.ExcelOutput;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Contains the weights of the words within a particular part of speech
 * @author Jed Caychingco
 */
public final class Weight{
   /**
    * The tag of the part of speech of the class
    */
   private final char TAG;
   
   /**
    * The weights of the words falling under that part of speech
    */
   private HashMap<String, Integer> weights;

   /**
    * Creates an instance of this class with the specified tag
    * @param inputPath The path to an Excel file containing words
    * and their respective weight
    * @param tag The tag of the weight's part of speech
    * @throws IOException If the input path is invalid or cannot be found.
    */
   public Weight(String inputPath, char tag) throws IOException {
      this.TAG = tag;
      setWeights(inputPath);
   }

   /**
    * Sets the weights.
    * @param inputPath The path to an Excel file containing words
    * and their respective weight
    * @throws IOException If the input path is invalid or cannot be found.
    */
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

   /**
    * @param word The String to be formatted
    * @return A trimmed lowercase version of the word
    */
   public String format(String word) {
      return word.toLowerCase().trim();
   }

   /**
    * Returns the weight of the word.
    * @param key The word to be whose weight needs to be retrieved
    * @return The weight of the word. 0, if it is not present
    */
   public int getWordValue(String key) {
      return weights.getOrDefault(key, 0);
   }

   /**
    * Returns the Tag
    * @return 
    */
   public char getTag() {
      return TAG;
   }
}
