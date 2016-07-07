package nlpfeatures.ngram;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import nlpfeatures.Article;
import nlpfeatures.Path;
import nlpfeatures.Preprocess;
import nlpfeatures.ngram.NgramFilters.NgramFilters;
import org.apache.commons.lang3.mutable.MutableFloat;

public class PreprocessNgram extends Preprocess{
   private final HashMap<String, Float> ngrams;
   private final NgramFilters[] ngramFilters;
   
   /**
    * Generates an excel file containing the articles from the articlePath and 
    * 0s and 1s if an ngram in the corpus is found in an article
    * 
    * This creates a separate excel file for each configuration of 
    * ngram and threshold specified in [nStart-nEnd] and [tStart-tEnd]
    * 
    * @param path
    * @param ngramCount Containins the ngram configurations to be used
    * @param removeThreshold Contains the remove threasholds to be used
    * @param ngramFilters Used to filter the ngrams received
    * @throws IOException 
    */
   public PreprocessNgram(Path path, int ngramCount, int[] removeThreshold, 
                          NgramFilters... ngramFilters) throws IOException {
      super(path, ngramCount);
      
      this.ngrams       = getNgrams(ngramFilters);
      this.ngramFilters = ngramFilters;
   }
   
//<editor-fold defaultstate="collapsed" desc="Csv Tools">
   private void makeCSVOutput(String outputFileName, Collection<String> wordList)           throws IOException {
      try (FileWriter fw = new FileWriter(outputFileName+".csv")) {
         String NEW_LINE = "\n";
//<editor-fold defaultstate="collapsed" desc="Old Code">
//
//         //Header
//         append(fw, "Article");
//         for(String word : wordList){
//            append(fw, "\""+word+"\"");
//         }
//         append(fw, "Sentiment");
//
//         //Row
//         for (Article article : super.articles) {
//            fw.append(NEW_LINE);
//            append(fw, "\""+article+"\"");
//
//            for (String word : wordList) {
//               append(fw, article.getFullArticle().contains(word) ? "1" : "0");
//            }//Places 1 if the ngram is present in the article, 0 otherwise
//
//            append(fw, article.getSentiment().name());
//         }
//</editor-fold>
         StringJoiner sj = new StringJoiner(",");
         
         //Header
         sj.add("Article");
         for (String word : wordList) {
            sj.add("\""+word+"\"");
         }
         sj.add("Sentiment");
         
         //Row
         for (Article article : super.articles) {
            sj.add(NEW_LINE);
            sj.add("\""+article+"\"");
            
            for (String word : wordList) {
               sj.add(article.getFullArticle().contains(word)? "1" : "0");
            }
         }
         
         //generate whatever data you want
         fw.flush();
      }
   }
   
   private void append(FileWriter fw, String word) throws IOException {
      char COMMA    = ',';
      
      fw.append(word);
      fw.append(COMMA);
   }
//</editor-fold>

   @Override
   public void output(int num) throws IOException {
      //Used to format the Negators
      StringJoiner sj = new StringJoiner(",", "[", "]");
      for (NgramFilters ngramFilter : ngramFilters) {
         sj.add(ngramFilter.getName());
      }
         
      String outputFileName =
         String.format("%s%dGram_%dRm_%s", outputPath, super.getNgCount(), num, sj.toString());

      removeInvalidWords(ngrams, hashMap->removeLowCountNgrams(hashMap, num));
      makeCSVOutput(outputFileName, ngrams.keySet());
   }

   @Override
   protected String[] format(String article) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    * Populates the ngram list
    * @param articles Each String in articles corresponds to an article
    * @param ngramCount The number of ngrams reuired (bigram, trigram, etc.)
    */
   private HashMap<String, Float> getNgrams(NgramFilters... ngFilters) {
      HashMap<String, Float> tempNG = new HashMap<>();
      
      super.articles.stream()
         .map(a->a.getWords())
         .forEach(sa->{
            for (String s : sa) {
               if(passesNgramFilters(s, ngFilters)){
                  Float count = tempNG.get(s);

                  if (count == null) { // New ngram, make its count 1
                     tempNG.put(s, 0.0f);
                  } else {             // Existing ngram, increment its count
                     tempNG.replace(s, count+1.0f);
                  }
               }
            }
         });
      return tempNG;
   }
   
//<editor-fold defaultstate="collapsed" desc="Remove Invalid">
   /**
    * Removes the ngrams whose count is <= to the removeThreshold
    * @param removeThreshold
    */
   private void removeLowCountNgrams(HashMap<String, Float> ngrams, int removeThreshold) {
      ngrams.values().removeIf(v -> v <= removeThreshold);
   }
//</editor-fold>
}
