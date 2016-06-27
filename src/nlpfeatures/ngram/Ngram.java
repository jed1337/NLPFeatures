package nlpfeatures.ngram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import nlpfeatures.ngram.NgramFilters.Negators;
import nlpfeatures.ngram.NgramFilters.NgramFilters;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Ngram {
   private final String REGEX_WHITE_LIST = "[^\\-a-zA-Z'Ññ\"\'’\\s]+";
   
   private final String QUOTES = "(\"[^\"]+\")|(\\(([^)]+)\\))";
   private final String SPACES = "\\s+";
   private HashMap<String, MutableInt> ngrams;

   /**
    * Generates an excel file containing the articles from the articlePath and 
    * 0s and 1s if an ngram in the corpus is found in an article
    * 
    * @param corpusPath
    * @param articlePath
    * @param outputPath A folder where the outputs will be placed
    * @param ngramCount A number stating the ngram to check (2: bigram, 3: trigram, etc)
    * @param removeThreshold Removes ngrams whose occurrence in the corpus is less than or equal to it
    * @throws java.io.IOException
    */
   public Ngram(String corpusPath, String articlePath, String outputPath, 
                     int ngramCount, int removeThreshold) 
                     throws IOException {
      
      this(corpusPath, articlePath, outputPath, ngramCount, ngramCount, removeThreshold, removeThreshold);
   }
   
   /**
    * Generates an excel file containing the articles from the articlePath and 
    * 0s and 1s if an ngram in the corpus is found in an article
    * 
    * This creates a separate excel file for each configuration of 
    * ngram and threshold specified in [nStart-nEnd] and [tStart-tEnd]
    * 
    * @param corpusPath
    * @param articlePath
    * @param outputPath A folder where the outputs will be placed
    * @param nStart A number stating the ngram to check (2: bigram, 3: trigram, etc)
    * @param nEnd A number stating up to what ngram to check
    * @param tStart Removes ngrams whose occurrence in the corpus is less than or equal to it
    * @param tEnd A number stating up to what threshold to take into account
    * @throws IOException 
    */
   public Ngram(String corpusPath, String articlePath, String outputPath, 
                     int nStart, int nEnd, int tStart, int tEnd)
                     throws IOException {
      
      ArrayList<String> corpus = getExcelLines(corpusPath);
         
      for (int i = nStart; i <= nEnd; i++) {
         for (int j = tStart; j <= tEnd; j++) {
            this.ngrams = new HashMap<>();
            String outputFileName = outputPath + "_" + i + "Gram_" + j + "rm";
            
            for(String article : corpus){
               addToNgrams(article.toLowerCase().split(REGEX_WHITE_LIST), i, new Negators());
            }
            removeLowCountNgrams(j);
            remove1LetterNgams();

//            makeExcelOutput(getExcelLines(articlePath), outputFileName);
            makeCSVOutput(getExcelLines(articlePath), outputFileName, this.ngrams.keySet());
         }
      }
   }

   private void makeCSVOutput(ArrayList<String> articles, 
         String outputFileName, Collection<String> wordList) 
         throws IOException {
      
      try (FileWriter fw = new FileWriter(outputFileName+".csv")) {
         char NEW_LINE = '\n';
         
         //Header
         append(fw, "Article");
         for(String word : wordList){
            append(fw, word);
         }
         
         //Row
         for (String article : articles) {
            fw.append(NEW_LINE);
            append(fw, "\""+article+"\"");
            
            for (String word : wordList) {
               append(fw, article.contains(word) ? "1" : "0");
            }//Places 1 if the ngram is present in the article, 0 otherwise
         }
         
         //generate whatever data you want
         fw.flush();
      }
   }

   private void append(FileWriter fw, String ngram) throws IOException {
      char COMMA    = ',';
      
      fw.append(ngram);
      fw.append(COMMA);
   }

   /**
    * Reads an Excel File and returns an ArrayList<String>
    * containing its lines.
    * 
    * @param path Path of the Excel file to be read
    * @return Returns an ArrayList<String> containing the lines of the Excel File
    * @throws FileNotFoundException
    * @throws IOException 
    */
   private ArrayList<String> getExcelLines(String path) throws FileNotFoundException, IOException{
      ArrayList<String> temp = new ArrayList<>();
      
      //Get the workbook instance for XLS file
      try (FileInputStream file = new FileInputStream(new File(path))) {
         //Get first sheet from the workbook
         XSSFSheet sheet = new XSSFWorkbook(file).getSheetAt(0);

         //Iterate through each rows from first sheet
         Iterator<Row> rowIterator = sheet.iterator();
         while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            //For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
               String articleContents = cellIterator.next().getStringCellValue().toLowerCase();
               if (!articleContents.isEmpty()) {
                  articleContents = articleContents.replaceAll("“|”", "\"");
                  articleContents = articleContents.replaceAll(QUOTES, "");
                  articleContents = articleContents.replaceAll(",", "");
                  articleContents = articleContents.replaceAll(SPACES, " ");
                  temp.add(articleContents);
               }
            }
         }
      }
      return temp;
   }
   
   /**
    * Populates the ngram list
    * @param articles Each String in articles corresponds to an article
    * @param ngramCount The number of ngrams reuired (bigram, trigram, etc.)
    */
   private void addToNgrams(String[] articles, int ngramCount, NgramFilters... ngFilters) {
      for (String articleSection : articles) {
         String[] words = formatString(articleSection);
         
         for (int j = 0; j < words.length - ngramCount + 1; j++) {
            String ngram     = concatWords(words, j, j + ngramCount);
            
            if(passesNgramFilters(ngram, ngFilters)){
               MutableInt count = this.ngrams.get(ngram);
               
               if (count == null) { // New ngram, make its count 1
                     this.ngrams.put(ngram, new MutableInt());
               } else {             // Existing ngram, increment its count
                  count.increment();
               }
            }
         }
      }
   }
   
   private boolean passesNgramFilters(String ngram, NgramFilters... ngFilters){
      for (NgramFilters ngFilter : ngFilters) {
         if(!ngFilter.contains(ngram)){
            return false;
         }
      }
      return true;
   }

   /**
    * Formats an article section (1 sentence, etc.)
    * @param articleSection
    * @return A string array. Each String in the array contains 1 word
    */
   private String[] formatString(String articleSection) {
      return Arrays.stream(articleSection.split(" "))
         .map(String::toLowerCase)        //lowercase all array elements
         .map(String::trim)               //Trim all array elements
         .filter(s -> (s.length() > 0))   //Remove ""s
         .toArray(String[]::new);         //Convert back to an array
   }
   
   /**
    * Concatenates the words in the given array from the start index to
    * before the end index "[start, end)"
    * 
    * @param words Array of words to concatenate
    * @param start Start index
    * @param end End index
    * @return 
    */
   public String concatWords(String[] words, int start, int end) {
      StringBuilder sb = new StringBuilder();
      for (int i = start; i < end; i++) {
         sb.append(i > start ? " " : "").append(words[i]);
      }
      return sb.toString();
   }
   
   /**
    * Returns the ngrams along with their count
    * @return 
    */
   public HashMap<String, MutableInt> getCorpus() {
      return ngrams;
   }
   
//<editor-fold defaultstate="collapsed" desc="Remove Invalid">
   /**
    * Removes the ngrams whose count is <= to the removeThreshold
    * @param removeThreshold
    */
   private void removeLowCountNgrams(int removeThreshold) {
      ngrams.values().removeIf((MutableInt v) -> v.get() <= removeThreshold);
   }
   
   /**
    * Removes ngrams that consists of only 1 letter
    */
   private void remove1LetterNgams(){
      //Implement Don't remove if I or O
      ngrams.keySet().removeIf((key)->key.length() <=1);
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Static methods">
   public static String[] getNgrams(String[] words, int ngramCount) {
      ArrayList<String> ngramList = new ArrayList<>();
      for (int i = 0; i < words.length - ngramCount + 1; i++) {
         StringBuilder sb = new StringBuilder();
         for (int j = i; j < i+ngramCount; j++) {
            sb.append(j > i ? " " : "");
            sb.append(words[j]);
         }
         ngramList.add(sb.toString());
      }
      return ngramList.toArray(new String[0]);
   }
//</editor-fold>
}
