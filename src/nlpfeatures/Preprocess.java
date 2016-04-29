package nlpfeatures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class Preprocess implements FormatString{
   private final String REGEX_WHITE_LIST     = "[^((a-zA-Z'Ññ\"’\\s-)|([\\r\\n\\t]))]+";
   private final ArrayList<String> stopwords = new ArrayList<>();
   
   protected String outputPath;
   protected String[][] data;

   public Preprocess(String inputPath, String outputPath, String stopwordsPath) {
      try {
         this.outputPath = outputPath;
         setData(inputPath);
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
   
   private Iterator<Row> getInputPathIterator(String path) throws FileNotFoundException, IOException {
      FileInputStream file  = new FileInputStream(new File(path));
      //Get the workbook instance for XLS file
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      //Get first sheet from the workbook
      XSSFSheet sheet       = workbook.getSheetAt(0);
      //Iterate through each rows from first sheet
      Iterator<Row> rowIterator = sheet.iterator();
      return rowIterator;
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

   private void setData(String inputPath) throws FileNotFoundException, IOException {
      List<String[]> tempList = new ArrayList<>();
      
      Iterator<Row> rowIterator = getInputPathIterator(inputPath);
      
      while (rowIterator.hasNext()) {
         Row row = rowIterator.next();

         //For each row, iterate through each columns
         Iterator<Cell> cellIterator = row.cellIterator();
         while (cellIterator.hasNext()) {
            Cell cell       = cellIterator.next();
            String contents = cell.getStringCellValue();

            if (!contents.isEmpty()) {
               tempList.add(preprocessArticle(contents));
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
//   
   public abstract void excelOutput(float outputs) throws IOException;

   protected abstract void output(float outputs, boolean isExcel) throws IOException;   
//</editor-fold>

   private String[] preprocessArticle(String article) {
      return article.toLowerCase()
         .replaceAll(REGEX_WHITE_LIST, " ")
         .replaceAll(" +", " ")              //Replace multiple spaces with a single space
         .split(" ");
   }
   
   protected void removeStopWords(Collection collection) {
      collection.removeAll(stopwords);
   }
   
   @Override
   public String format(String word) {
      return word.toLowerCase().trim();
   }
}