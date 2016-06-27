package nlpfeatures;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import nlpfeatures.ngram.Ngram;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class Preprocess{
   protected final String REGEX_WHITE_LIST = "[^\\-a-zA-Z'Ññ\"\'’\\s]+";
   final ArrayList<String> stopwords = new ArrayList<>();
   
   protected final int ngCount;
   protected final ArrayList<Article> articles;
   protected final String outputPath;
   
   protected Preprocess(Path path, int ngCount){
      this.ngCount    = ngCount;
      this.articles   = new ArrayList<>();
      this.outputPath = path.getOutputPath();
         
      try {
         setArticles(path.getInputPath(), ngCount);
         setStopWords(path.getStopwordsPath());
         
      } catch (IOException ex) {
         Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

//<editor-fold defaultstate="collapsed" desc="Getters">
   public String[] getDataAtIndex(int index) {
      return articles.get(index).getWords();
   }
   
   public String[] getUniqueWords(int index) {
      return new HashSet<>(
         Arrays.asList(getDataAtIndex(index)))
         .toArray(new String[0]);
   }

   public Set<String> getUniqueWords(){
      Set<String> uniqueWords = new HashSet<>();
      
      for(Article article: articles){
         for(String word: article.getWords()){
            uniqueWords.add(word);
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
    * Sets the articles of String[][] this.articles
    * this.articles[i]    = article i
    * this.articles[i][j] = word j in article i
    * 
    * @param inputPath Path of the excel file containing articles
    * false if the raw article, split by whitepsaces, is added instead
    * @throws FileNotFoundException If the file indicated by inputPath is not found
    * @throws IOException 
    */
   private void setArticles(String inputPath, int ngCount) throws FileNotFoundException, IOException {
      for (Row row : ExcelOutput.getSheet(inputPath)) {
         Iterator<Cell> cellIterator = row.cellIterator();
         
         try{
            String contents     = cellIterator.next().getStringCellValue();
            Sentiment sentiment = Sentiment.getSentiment(cellIterator.next().getStringCellValue());

            if (!contents.isEmpty()) {
               //Format is an abstract method
               //Ngram.getNgrams, gets the ngCount ngrams from format(contents);
               String[] formattedWords = Ngram.getNgrams(format(contents), ngCount);
               
               this.articles.add(new Article(formattedWords, sentiment));
            }
         }catch(InputMismatchException e){
            printErrors(e);
         }
      }
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Abstract methods">
   public abstract void output(float outputs) throws IOException;
   public abstract void output(float outputs, String name) throws IOException;
   
   /**
    * Specifies how the cell containing the whole article shall be formatted
    * before being placed in this.articles
    * @param article A cell from the excel file containing the article
    * @return 
    */
   protected abstract String[] format(String article);
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Utility Functions">
   /**
    * A standard function for printing Exceptions
    * @param ex 
    */
   protected void printErrors(Exception ex) {
      System.err.println(ex.getMessage());
   }
   
   /**
    * Used for brevity purposes
    * @param closeable the object to be closed
    */
   protected void closeSafely(Closeable closeable){
      try{
         closeable.close();
      } catch (IOException e) {
         printErrors(e);
      }
   }
   
   /**
    * Invokes closeSafely on each of the closeables
    * @param closeables an array of Closeable objects
    */
   protected void closeSafely(Closeable... closeables){
      for (Closeable closeable : closeables) {
         closeSafely(closeable);
      }
   }
   //</editor-fold>
}
