package ngram;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringJoiner;
import nlpfeatures.Article;
import nlpfeatures.Path;
import nlpfeatures.Preprocess;
import Ngram.NgramFilters.NgramFilters;
import java.util.Arrays;

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
    * @param ngramFilters Used to filter the ngrams received
    * @throws IOException 
    */
   public PreprocessNgram(Path path, int ngramCount, NgramFilters... ngramFilters) throws IOException {
      super(path, ngramCount);
      
      this.ngrams       = getNgrams(ngramFilters);
      this.ngramFilters = ngramFilters;
   }

   @Override
   public void output(int removeThreshold) throws IOException {
      //Used to format the Negators
      StringJoiner sj = new StringJoiner(",", "[", "]");
      sj.setEmptyValue("");
      for (NgramFilters ngramFilter : ngramFilters) {
         sj.add(ngramFilter.getName());
      }
         
      String outputFileName =
         String.format("%s%dGram_%dRm_%s", outputPath, super.getNgCount(), removeThreshold, sj.toString());

      HashMap<String, Float> tempNgrams = new HashMap<>(ngrams);
      removeInvalidWords(tempNgrams, hashMap->removeLowCountNgrams(hashMap, removeThreshold));
      makeCSVOutput(outputFileName, tempNgrams.keySet());
   }
   
//<editor-fold defaultstate="collapsed" desc="Csv Tools">
   private void makeCSVOutput(String outputFileName, Collection<String> wordList)throws IOException {
      try (FileWriter fw = new FileWriter(outputFileName+".csv")) {
         String NEW_LINE = "\n";
         StringJoiner sj = new StringJoiner(",");
         
         //Header
         sj.add("Article");
         for (String word : wordList) {
            sj.add("\""+word+"\"");
         }
         sj.add("Sentiment");
         
         //Row
         int articleNum = 0;
         for (Article article : super.articles) {
//            sj.add(NEW_LINE +"\""+article.getFullArticle()+"\"");
            sj.add(NEW_LINE + (++articleNum));
            
            for (String word : wordList) {
               sj.add(article.getFullArticle().contains(word)? "1" : "0");
            }
            
            sj.add(article.getActualSentiment().toString());
         }
         
         //Append the Strings to the file writer
         fw.append(sj.toString());
         
         //generate whatever data you want
         fw.flush();
      }
   }
//</editor-fold>
   
   @Override
   protected String[] format(String article) {
      return article.toLowerCase()
        .replaceAll(REGEX_WHITE_LIST, " ")
        .split("\\s+");
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
         .map(a->a.getWords())            //Returns a stream of String[]
         .flatMap(aw->Arrays.stream(aw))  //Returns a stream of String
         .filter(s->passesNgramFilters(s, ngFilters))
         .forEach(s->{
            Float count = tempNG.get(s);

            if (count == null) { // New ngram, make its count 1
               tempNG.put(s, 0.0f);
            } else {             // Existing ngram, increment its count
               tempNG.replace(s, count+1.0f);
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
