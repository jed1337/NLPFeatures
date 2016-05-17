package nlpfeatures;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PreprocessSO_CAL extends Preprocess {
   private final String WEIGHT_PATH = "src\\weights\\";
   private final String TAGGER_PATH = "src\\tagger\\filipino.tagger";
   private final String SPACE = " ";
   private final String[] articleSentiments;
   private final MaxentTagger tagger;

   private ArrayList<Weight> weights;

   public PreprocessSO_CAL(Path path, int threadCount) {
      super(path, false);
      initializeWeights();

      this.tagger = new MaxentTagger(TAGGER_PATH);
      this.articleSentiments = new String[data.length];
      
      Worker[] workers = initializeWorkerThreads(threadCount);
      startWorkers(workers);
      
      System.out.println(Arrays.toString(this.articleSentiments));
   }
   
//<editor-fold defaultstate="collapsed" desc="Worker Thread Functions">
   /**
    * Sets which data each worker needs to process
    * @param threadCount The number of threads to be used
    * @return
    */
   private Worker[] initializeWorkerThreads(int threadCount) {
      Worker[] workers = new Worker[threadCount];
      
      int partition = this.data.length/threadCount;
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
   
   /**
    * Starts the worker threads and waits for them to finish executing
    * before continuing on with the program
    * 
    * @param workers An array of Worker threads
    */
   private void startWorkers(Worker[] workers) {
      System.out.println("Started tagging");
      
      for (int i = 0; i < workers.length; i++) {
         System.out.println("Worker "+i+": " + workers[i]);
         workers[i].start();
      }
      
      for (Worker worker : workers) {
         try {
            worker.join();
         } catch (InterruptedException ex) {
            printErrors(ex);
         }
      }
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
            int articleWeight = getArticleWeight(i);
            classifyArticle(articleWeight, i);
         }
      }
      
      @Override
      public String toString(){
         return String.format("Start: %d \t End: %d", start, end);
      }
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Tag Article">
   /**
    * Tags an article and gets its weight
    * @param index 
    */
   private int getArticleWeight(int index) {
      String taggedArticle      = this.tagger.tagString(inputToString(data[index]));
      TaggedWords[] taggedWords = setTaggedWords(taggedArticle);
      return getArticleWeight(taggedWords);
   }

   /**
    * Given the weight of the article at the index, the function classifies
    * it into positive, negative or neutral
    * @param articleWeight 
    * @param index 
    */
   private void classifyArticle(int articleWeight, int index) {
      String sentiment;
      if (articleWeight > 0) {
         sentiment = "Positive";
      } else if (articleWeight < 0) {
         sentiment = "Negative";
      } else {
         sentiment = "Neutral";
      }
      System.out.println(String.format("Weight of %d is %d \t = %s", index, articleWeight, sentiment));
      this.articleSentiments[index] = sentiment;
   }
   
   /**
    * Given an already tagged articles, this function returns an array of TaggedWords
    * that contain a word and its appropriate tag
    * 
    * @param taggedArticle
    * @return 
    */
   private TaggedWords[] setTaggedWords(String taggedArticle) {
      String[] articleWords     = taggedArticle.split("\\s+");
      int awLength              = articleWords.length;
      TaggedWords[] taggedWords = new TaggedWords[awLength];
      
      for (int i = 0; i < awLength; i++) {
         taggedWords[i] = new TaggedWords(articleWords[i]);
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
      if (isExcel) {
         ExcelTools.makeExcelOutput(this.articleSentiments, outputPath+"SO_CAL.xlsx");
      } else {
         System.err.println("CSV Not supported yet");
      }
   }
//</editor-fold>
   
   private void initializeWeights() {
      try {
         this.weights = new ArrayList<>();
         this.weights.add(new Weight(WEIGHT_PATH + "ADJ.xlsx", 'J'));
         this.weights.add(new Weight(WEIGHT_PATH + "ADV.xlsx", 'R'));
         this.weights.add(new Weight(WEIGHT_PATH + "NOUN.xlsx", 'N'));
         this.weights.add(new Weight(WEIGHT_PATH + "VERB.xlsx", 'V'));
//      this.weights.add(new Weight(WEIGHT_PATH+"INT.xlsx", '?'));
      } catch (IOException ex) {
         printErrors(ex);
      }
   }
}
