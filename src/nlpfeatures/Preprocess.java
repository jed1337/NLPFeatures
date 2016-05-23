package nlpfeatures;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public abstract class Preprocess{
   protected final String REGEX_WHITE_LIST   = "[^((a-zA-Z'Ññ\"’\\s-)|([\\r\\n\\t]))]+";
   private final ArrayList<String> stopwords = new ArrayList<>();
   
   protected String outputPath;
   protected ArrayList<Article> articles;

   protected Preprocess(Path path, boolean preprocessArticle) {
      try {
         this.articles   = new ArrayList<>();
         this.outputPath = path.getOutputPath();
         setArticles(path.getInputPath(), preprocessArticle);
         setStopWords(path.getStopwordsPath());
         
      } catch (IOException ex) {
         Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
//<editor-fold defaultstate="collapsed" desc="Getters">

   public String[] getDataAtIndex(int index) {
//      return this.articles[index];
      return articles.get(index).getWords();
   }
   
   public String[] getUniqueWords(int index) {
      return new HashSet<>(Arrays.asList(articles.get(index).getWords()))
         .toArray(new String[0]);
   }

   public Set<String> getUniqueWords(){
      Set<String> uniqueWords = new HashSet<>();
      
//      for(String[] articles: articles){
//         for(String articleWord: articles){
//            uniqueWords.add(articleWord);
//         }
//      }
      
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
    * this.articles[i] = article i
    * this.articles[i][j] = word j in article i
    * 
    * @param inputPath Path of the excel file containing articles
    * @param preprocessArticle true if the article is to be preprocessed before added,
    * false if the raw article, split by whitepsaces, is added instead
    * @throws FileNotFoundException If the file indicated by inputPath is not found
    * @throws IOException 
    */
   private void setArticles(String inputPath, boolean preprocessArticle) throws FileNotFoundException, IOException {
      for (Row row : ExcelOutput.getSheet(inputPath)) {
         Iterator<Cell> cellIterator = row.cellIterator();
         
         try{
            String contents     = cellIterator.next().getStringCellValue();
            Sentiment sentiment = Sentiment.getSentiment(cellIterator.next().getStringCellValue());

            if (!contents.isEmpty()) {
               String[] words = format(contents);
//               if(preprocessArticle)
//                  words = preprocessArticle(contents);
//               else
//                  words = contents.split("\\s+");
               this.articles.add(new Article(words, sentiment));
            }
         }catch(InputMismatchException e){
            printErrors(e);
         }catch(NoSuchElementException e){
            System.out.println("Empty cell (Use delete row)");
         }
      }
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Outputs">
   public abstract void output(float outputs) throws IOException;   
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Utility Functions">
   protected void printErrors(Exception ex) {
      System.err.println(ex.getMessage());
   }
   
   protected void closeSafely(Closeable c){
      try{
         c.close();
      } catch (IOException e) {
         printErrors(e);
      }
   }
//</editor-fold>
   

   /**
    * Removes the stopwords from the given collection
    * @param collection 
    */
   protected void removeStopWords(Collection collection) {
      collection.removeAll(stopwords);
   }

   protected abstract String[] format(String article);
   
//   @Override
//   public String format(String word) {
//      return word.toLowerCase().trim();
//   }
}
