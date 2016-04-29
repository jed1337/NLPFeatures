package nlpfeatures;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreprocessSO_CAL extends Preprocess {
   private final String WEIGHT_PATH = "src\\weights\\";
   private final String TAGGER_PATH = "src\\tagger\\filipino.tagger";
   private final String SPACE = " ";
   private final String[] articleSentiments;
   private final MaxentTagger tagger;

   private ArrayList<Weight> weights;

   public PreprocessSO_CAL(String inputPath, String outputPath, String stopwordsPath, int threadCount) {
      super(inputPath, outputPath, stopwordsPath);
      initializeWeights();

      this.tagger = new MaxentTagger(TAGGER_PATH);
      this.articleSentiments = new String[data.length];
      
      Worker[] workers = initializeWorkerThreads(threadCount);
      
      System.out.println("Started tagging");
      
      startWorkers(workers);
      
   }
   
//<editor-fold defaultstate="collapsed" desc="Worker Thread Functions">
   private Worker[] initializeWorkerThreads(int threadCount) {
      Worker[] workers = new Worker[threadCount];
      
      int partition = data.length/threadCount;
      for (int i = 0; i < workers.length; i++) {
         int start = i*partition;
         int end   = (i+1)*partition;
         
         //If the number of articles is not divisible by the number of threads
         //Add the remaining articles to the last worker thread
         if(i == (workers.length-1)){
            end += data.length % threadCount;
         }
         
         workers[i] = new Worker(start, end);
      }
      
      return workers;
   }
   
   private void startWorkers(Worker[] workers) {
      for (int i = 0; i < workers.length; i++) {
         System.out.println("Worker "+i+": " + workers[i]);
         workers[i].start();
      }
      
      for (Worker worker : workers) {
         try {
            worker.join();
         } catch (InterruptedException ex) {
            Logger.getLogger(PreprocessSO_CAL.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      
      System.out.println(Arrays.toString(this.articleSentiments));
   }
   
   private class Worker extends Thread{
      private final int start;
      private final int end;
      
      public Worker(int start, int end) {
         this.start = start;
         this.end = end;
      }
      
      @Override
      public void run() {
         for(int i=start; i<end; i++){
            System.out.println("i = " + i);
            tagArticle(i);
         }
      }
      
      @Override
      public String toString(){
         return String.format("Start: %d \t End: %d", start, end);
      }
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Tag Article">
   
   private void tagArticle(int index) {
      TaggedWords[] taggedWords = setTaggedWords(getTaggedWords(this.data[index]));
      
      int articleWeight = getArticleWeight(taggedWords);
      
      String sentiment;
      if (articleWeight > 0) {
         sentiment = "Positive";
      } else if (articleWeight < 0) {
         sentiment = "Negative";
      } else {
         sentiment = "Neutral";
      }
      System.out.println(String.format("Weight of %d is %d \t = %s", index, articleWeight, sentiment));
      articleSentiments[index] = sentiment;
   }
   
   private String[] getTaggedWords(String[] toTag) {
      System.out.println("Tagging");
      String[] words = tagger.tagString(inputToString(toTag)).split(SPACE);
      System.out.println("Done tagging");
      return words;
   }
   
   private TaggedWords[] setTaggedWords(String[] words) {
      TaggedWords[] taggedWords = new TaggedWords[words.length];
      int i = 0;
      for (String word : words) {
         taggedWords[i++] = new TaggedWords(word);
      }
      return taggedWords;
   }
   
   private int getArticleWeight(TaggedWords[] taggedWords) {
      int articleWeight = 0;
      for (Weight w : weights) {
         articleWeight += Arrays.stream(taggedWords)
                 .filter(tw->tw.getTag() == w.getTag()) //The Weight tag is equal to the tag of the current word
                 .map(tw->w.getWordValue(tw.getWord())) //Get only the weights of each word (int)
                 .reduce(0, (acc, item)->acc + item);   //Collect all the weights, and return their total
      }
      return articleWeight;
   }
   
   private String inputToString(String[] toTag) {
      StringBuilder sb = new StringBuilder();
      for (String s : toTag) {
         sb.append(s);
         sb.append(SPACE);
      }
      return sb.toString();
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="SO_CAL Outputs">
   
   @Override
   public void excelOutput(float outputs) throws IOException {
      output(outputs, true);
   }
   
   @Override
   protected void output(float outputs, boolean isExcel) throws IOException {
      for(int i=0;i<articleSentiments.length;i++){
         System.out.println(i+"\t"+articleSentiments[i]);
      }
//      if (isExcel) {
//         ExcelTools.makeExcelOutput(data, keys, outputPath + percentage + "%.xslx");
//      } else {
//         CSVTools.makeCSVOutput(data, keys, outputPath + percentage + "%.csv");
//      }
   }
//</editor-fold>
   
   private void initializeWeights() {
      this.weights = new ArrayList<>();
      this.weights.add(new Weight(WEIGHT_PATH + "ADJ.xlsx", 'J'));
      this.weights.add(new Weight(WEIGHT_PATH + "ADV.xlsx", 'R'));
//      this.weights.add(new Weight(WEIGHT_PATH+"INT.xlsx", '?'));
      this.weights.add(new Weight(WEIGHT_PATH + "NOUN.xlsx", 'N'));
      this.weights.add(new Weight(WEIGHT_PATH + "VERB.xlsx", 'V'));
   }
}
