package nlpfeatures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public abstract class Preprocess implements FormatString{
   private final String REGEX_WHITE_LIST     = "[^((a-zA-Z'Ññ\"’\\s-)|([\\r\\n\\t]))]+";
   private final ArrayList<String> stopwords = new ArrayList<>();
   
   protected String outputPath;
   protected String[][] data;

   protected Preprocess(String inputPath, String outputPath, String stopwordsPath, boolean preprocessArticle) {
      try {
         this.outputPath = outputPath;
         setData(inputPath, preprocessArticle);
         setStopWords(stopwordsPath);
         
      } catch (IOException ex) {
         Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
//<editor-fold defaultstate="collapsed" desc="Getters">

   public String[] getDataAtIndex(int index) {
      return this.data[index];
   }

   public String[][] getAllData() {
      return this.data;
   }

   public String[] getUniqueWords(int index) {
      return new HashSet<>(Arrays.asList(data[index])).toArray(new String[0]);
   }

   public Set<String> getUniqueWords(){
      Set<String> uniqueWords = new HashSet<>();
      
      for(String[] articles: data){
         for(String articleWord: articles){
            uniqueWords.add(articleWord);
         }
      }
      
      return uniqueWords;
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Setters">
   private void setStopWords(String stopwordsPath) throws FileNotFoundException, IOException{
      BufferedReader br = new BufferedReader(new FileReader(stopwordsPath));
      String line;
      while ((line = br.readLine()) != null) {
         stopwords.add(line.trim());
      }
   }

   /**
    * Sets the data of String[][] this.data
    * this.data[i] = article i
    * this.data[i][j] = word j in article i
    * 
    * @param inputPath Path of the excel file containing articles
    * @throws FileNotFoundException If the file indicated by inputPath is not found
    * @throws IOException 
    */
   private void setData(String inputPath, boolean preprocessArticle) throws FileNotFoundException, IOException {
      List<String[]> tempList = new ArrayList<>();
      
      //Iterate through each rows from first sheet
      Iterator<Row> rowIterator = ExcelTools.getRowIterator(inputPath);
      
//      Iterator<Row> rowIterator = sheet.iterator();
      
      while (rowIterator.hasNext()) {
         Row row = rowIterator.next();

         //For each row, iterate through each columns
         Iterator<Cell> cellIterator = row.cellIterator();
         while (cellIterator.hasNext()) {
            Cell cell       = cellIterator.next();
            String contents = cell.getStringCellValue();

            if (!contents.isEmpty()) {
               if(preprocessArticle)
                  tempList.add(preprocessArticle(contents));
               else
                  tempList.add(contents.split("\\s+"));
            }
         }
      }
      
      int size  = tempList.size();
      this.data = new String[size][];
      
      int i = 0;
      for (String[] articleStringArray : tempList) {
         this.data[i++] = articleStringArray;
      }
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Outputs">
//   public abstract void csvOutput(float outputs) throws IOException;
   public abstract void excelOutput(float outputs) throws IOException;

   protected abstract void output(float outputs, boolean isExcel) throws IOException;   
//</editor-fold>

   /**
    * Returns the words in the article
    * @param article
    * @return 
    */
   private String[] preprocessArticle(String article) {
      return article.toLowerCase()
         .replaceAll(REGEX_WHITE_LIST, " ")
         .replaceAll(" +", " ")              //Replace multiple spaces with a single space
         .split(" ");
   }
   
   /**
    * Removes the stopwords from the given collection
    * @param collection 
    */
   protected void removeStopWords(Collection collection) {
      collection.removeAll(stopwords);
   }
   
   @Override
   public String format(String word) {
      return word.toLowerCase().trim();
   }
}
