package nlpfeatures;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * The root of the preprocessing class tree. It is used to 
 * apply the preprocessing that is unique to all of its subclasses. <br>
 * It also contains constants used by its subclasses.
 * @author Jed Caychingco
 */
public abstract class Preprocess{
   /** Used to filter out certain symbols */
   protected final String REGEX_WHITE_LIST = "[^\\-a-zA-Z'Ññ\"\'’\\s]+";
   
   /** A list of words to remove since they have no bearing on the end sentiment. */
   protected final ArrayList<String> stopwords = new ArrayList<>();
   
   /** The number of ngrams to take into consideration */
   protected final int ngCount;
   
   /** The corpus */
   protected final ArrayList<Article> articles;
   
   /** Where the output is to be written to */
   protected final String outputPath;
   
   /**
    * Creates an instance of this class from the 
    * specified path using the specified ngram count
    * @param path The path to the excel file containing article. This serves as the input.
    * @param ngCount The number of ngrams to use
    */
   protected Preprocess(Path path, int ngCount){
      this.ngCount    = ngCount;
      this.articles   = new ArrayList<>();
      this.outputPath = path.getOutputPath();
      
      try {
         setStopWords(path.getStopwordsPath());
         setArticles(path.getInputPath());
      } catch (IOException ex) {
         Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
       }
   }

//<editor-fold defaultstate="collapsed" desc="Protected Getters">
   /** 
    * Returns the ngram count
    * @return 
    */
   protected int getNgCount() {
      return ngCount;
   }

   /**
    * Returns the ngrams at the specified index
    * @param index 
    * @return 
    */
   protected String[] getNgramsAt(int index) {
      return this.articles.get(index).getWords();
   }
   
   /**
    * Returns the article at the specified index
    * @param index 
    * @return 
    */
   protected String getFullArticleAt(int index){
      return this.articles.get(index).getFullArticle();
   }
   
   /**
    * Returns the unique words in the article at the
    * specified index
    * @param index 
    * @return 
    */
   protected String[] getUniqueWords(int index) {
      return new HashSet<>(
         Arrays.asList(getNgramsAt(index)))
         .toArray(new String[0]);
   }

   /**
    * Returns the unique words in the entire corpus
    * @return 
    */
   protected Set<String> getUniqueWords(){
      Set<String> uniqueWords = new HashSet<>();
      for(Article article: articles){
         for(String word: article.getWords()){
            uniqueWords.add(word);
         }
      }
      
      return uniqueWords;
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Private Setters">
   /**
    * Sets the stop words from the specified path
    * @param stopwordsPath Contains a .txt file containing stop words
    * @throws FileNotFoundException If the file cannot be found
    * @throws IOException 
    */
   private void setStopWords(String stopwordsPath) throws IOException{
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
   private void setArticles(String inputPath) throws IOException {
      if(inputPath==null){
         System.out.println("Input path is null.");
      }
      else{
         for (Row row : ExcelOutput.getSheet(inputPath)) {
            Iterator<Cell> cellIterator = row.cellIterator();

            String contents  = cellIterator.next().getStringCellValue();
            String sentiment = cellIterator.next().getStringCellValue();

            addArticle(contents, sentiment);
         }
         this.articles.removeIf(a->a.getActualSentiment()==Sentiment.NONE);
      }
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Add articles">
   /**
    * Add an article with its specific contents. It gets a sentiment of NONE
    * @param contents 
    */
   protected void addArticle(String contents){
      addArticle(contents, Sentiment.NONE);
   }
   
   /**
    * Add an article with its specific content and sentiment.
    * @param contents
    * @param sentiment 
    */
   protected void addArticle(String contents, String sentiment) {
      addArticle(contents, Sentiment.getSentiment(sentiment));
   }
   
   /**
    * Add an article with its specific content and sentiment.
    * @param contents
    * @param sentiment 
    */
   protected void addArticle(String contents, Sentiment sentiment) {
      if (contents.isEmpty()) {
         throw new InvalidParameterException("The article's contents is empty");
      }
      //Format is an abstract method
      this.articles.add(new Article(contents, sentiment, ()->format(contents), this.ngCount));
   }
   
   /**
    * Used to add articles in bulk
    * @param articles 
    */
   protected void addArticles(ArrayList<Article> articles){
      for (Article article : articles) {
         addArticle(article.getFullArticle(), article.getActualSentiment());
      }
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Output">
   /**
    * Creates the output at {@code outputPath}. Its subclasses determine
    * what kind of file it is, and what its contents are.
    * @param num Each subclass makes different use of this number 
    * @throws IOException 
    */
   public abstract void output(int num) throws IOException;
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Abstract Functions">
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
    * @param closeables an array of Closeable objects
    */
   protected void closeSafely(Closeable... closeables){
      for (Closeable closeable : closeables) {
         try {
            closeable.close();
         } catch (IOException e) {
            printErrors(e);
         }
      }
   }
   //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Utility Removers">
   /**
    * A general purpose utility function that calls the other removers
    * @param corpusWords 
    * @param externalRemovers Added custom removers. These are executed first.
    */
   protected void removeInvalidWords(HashMap<String, Float> corpusWords, Consumer<HashMap<String, Float>>... externalRemovers) {
      for (Consumer<HashMap<String, Float>> externalRemover : externalRemovers) {
         externalRemover.accept(corpusWords);
      }
      
      removeInvalidSymbols(corpusWords);
      remove1LetterWords(corpusWords);
   }
   
   /**
    * Remove the invalid symbols from the start and end of the word
    * Example: "--anyos" becomes "anyos", "((frj))" becomes "frj"
    * @param corpusWords A HashMap containing invalid symbols
    */
   protected void removeInvalidSymbols(HashMap<String, Float> corpusWords){
      final String RS = "[^a-zA-ZÑñ]"; //Not a letter
      
      Pattern pattern = Pattern.compile(
         String.format("(%s+.*)|(.*%s+)", RS, RS));   //Starts or ends with a symbol
      Pattern start   = Pattern.compile(RS+".*");     //Starts with a symbol
      Pattern end     = Pattern.compile(".*"+RS);     //Ends with a symbol.
      
      //Temp contains words from corpusWords that start or end with a symbol
      Map<String,Float> temp = corpusWords.entrySet().stream()
         .filter(entry->pattern.matcher(entry.getKey()).matches())               //Only takes those that match pattern
         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));     //Turn it to a Map
      
      corpusWords.entrySet().removeAll(temp.entrySet());

      //Place the words from temp back to corpusWords without their symbol
      //on their start or end
      temp.entrySet().forEach(entry->{
         String key = entry.getKey();
         
         //Removes symbols from the beginning
         while(start.matcher(key).matches()){
            key = key.substring(1);
         }
         
         //Removes symbols from the end
         while(end.matcher(key).matches()){
            key = key.substring(0, key.length()-1);
         }
         
         //Return the clean values back to the CorpusWords
         corpusWords.put(key, entry.getValue());
      });
   }
      
   /**
    * Remove words of length 1 except for "I" and "O"
    * @param corpusWords 
    */
   protected void remove1LetterWords(HashMap<String, Float> corpusWords){
      corpusWords.keySet().removeIf(k->{
         return k.length() <= 1 && !(k.equalsIgnoreCase("i")||k.equalsIgnoreCase("o"));
      });
   }
//</editor-fold>
}
