package nlpfeatures;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import nlpfeatures.Intensifiers.IntensifierMethod;
import nlpfeatures.Intensifiers.PrefixIntensifier;
import nlpfeatures.Intensifiers.WordBeforeIntensifier;

public class PreprocessSO_CAL extends Preprocess {
   private final String WEIGHT_PATH = "src\\weights\\";
   private final String TAGGER_PATH = "src\\tagger\\filipino.tagger";
   private final String SPACE = " ";
   private final Sentiment[] predictedSentiments;
   private final MaxentTagger tagger;

   private ArrayList<Weight> weights;
   private ArrayList<IntensifierMethod> intensifiers;

   public PreprocessSO_CAL(Path path, int threadCount, int ngCount) {
      super(path, ngCount);
      
      initializeWeights();
      initializeIntensifiers();

      this.tagger = new MaxentTagger(TAGGER_PATH);
      this.predictedSentiments = new Sentiment[articles.size()];
      
      Worker[] workers = initializeWorkerThreads(threadCount);
      startWorkers(workers);
      
      System.out.println(Arrays.toString(this.predictedSentiments));
      
      getFundamentalNumbers();
   }
   
   public PreprocessSO_CAL(Path path, int threadCount){
      this(path, threadCount, 1);
   }
   
//<editor-fold defaultstate="collapsed" desc="Fundamental Numbers">
   private void getFundamentalNumbers(){
      for(Sentiment sen: Sentiment.values()){
         float tp = 0.0f; //True  positive
         float tn = 0.0f; //True  negative
         float fp = 0.0f; //False positive
         float fn = 0.0f; //False negative
         
         for (int i = 0; i < this.articles.size(); i++) {
            Sentiment trueSen = this.articles.get(i).getSentiment(); //Actual Sentiment
            Sentiment predSen = this.predictedSentiments[i];         //Predicted Sentiment
            
            if(trueSen == sen && predSen == sen){
               tp++;
            }
            else if(trueSen != sen && predSen != sen){
               tn++;
            }
            else if(trueSen == sen && predSen != sen){
               fn++;
            }
            else if(trueSen != sen && predSen == sen){
               fp++;
            }
         }

         printFundamentalNumbers(tp, fp, fn, tn, sen);
      }
   }

   private void printFundamentalNumbers(float tp, float fp, float fn, float tn, Sentiment sen) {
      float p   = tp/(tp+fp);            //Precision
      float r   = tp/(tp+fn);            //Recall
      float fs  = (2*p*r)/(p+r);         //F-score
      float acc = (tp+fn)/(tp+fp+tn+fn); //Accuracy
      
      System.out.println("=========="+sen+"==========");
      System.out.println("Accuracy  : " + acc);
      System.out.println("Precision : " + p);
      System.out.println("F-Score   : " + fs);
      System.out.println("Recall    : " + r);
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Worker Thread Functions">
   /**
    * Sets which articles each worker needs to process
    * @param threadCount The number of threads to be used
    * @return
    */
   private Worker[] initializeWorkerThreads(int threadCount) {
      Worker[] workers = new Worker[threadCount];
      
      int partition = this.articles.size()/threadCount;
      for (int i = 0; i < workers.length; i++) {
         int start = i*partition;
         int end   = (i+1)*partition;
         
         //If the number of articles is not divisible by the number of threads
         //Add the remaining articles to the last worker thread
         if(i == (workers.length-1)){
            end += articles.size() % threadCount;
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
    * Tags an article and returns its weight
    * @param index Its index from ArrayList<String> article
    */
   private int getArticleWeight(int index) {
      String taggedArticle      = this.tagger.tagString(inputToString(getDataAtIndex(index)));
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
      Sentiment sentiment;
      if (articleWeight > 0) {
         sentiment = Sentiment.POSITIVE;
      } else if (articleWeight < 0) {
         sentiment = Sentiment.NEGATIVE;
      } else {
         sentiment = Sentiment.NEUTRAL;
      }
      System.out.println(String.format("Weight of %d is %d \t = %s", index, articleWeight, sentiment));
      this.predictedSentiments[index] = sentiment;
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

   /**
    * Todo Clean the code
    * @param taggedWords The words in the article along with their corresponding
    * parts of speech tag
    * @return 
    */
   private int getArticleWeight(TaggedWords[] taggedWords) {
      int articleWeight = 0;
      for (Weight w : weights) {
         articleWeight += Arrays.stream(taggedWords)
            .filter(tw->tw.getTag() == w.getTag()) //The Weight tag is equal to the tag of the current word
            .map(tw->w.getWordValue(tw.getWord())) //Get only the weights of each word (int)
            .reduce(0, (acc, item) -> acc + item); //Collect all the weights, and return their total
      }
      
      articleWeight += addIntensifierWeight(taggedWords);
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
   
//<editor-fold defaultstate="collapsed" desc="Intensifier Code">
   private float addIntensifierWeight(TaggedWords[] taggedWords) {
      Optional<Weight> adjWeight = weights.stream()
         .filter(w->w.getTag()=='J')
         .findFirst();
      
         if(!adjWeight.isPresent()){
            throw new NoSuchElementException("Adjective weight is not present");
         }
         
      float total = 0;
      for(IntensifierMethod intensifier : intensifiers){
         total += intensifier.ifAdjective(taggedWords, adjWeight.get());
      }
      
      if(total != 0){
         System.out.println("total = " + total);
      }
      return total;
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Initializers">
   
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
   
   private void initializeIntensifiers(){
      this.intensifiers = new ArrayList<>();
      this.intensifiers.add(new PrefixIntensifier());
      this.intensifiers.add(new WordBeforeIntensifier());
   }
   
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Outputs">
   @Override
   public void output(float outputs) throws IOException {
      output(outputs, "SO-CAL");
   }
   
   @Override
   public void output(float outputs, String name) throws IOException {
      String fileName = String.format("%s%s", outputPath, name);
      ExcelOutput.output(this.predictedSentiments, fileName+".xlsx");
      
      try {
         FileOutputStream fos      = new FileOutputStream(fileName+".ser");
         ObjectOutputStream oos    = new ObjectOutputStream(fos);
         ArrayList<Sentiment> temp = new ArrayList<>(Arrays.asList(predictedSentiments));
         oos.writeObject(temp);
         
         closeSafely(oos);
         closeSafely(fos);
      } catch (IOException ie) {
         printErrors(ie);
      }
   }
//</editor-fold>
   
   @Override
   protected String[] format(String article){
      return article.split("\\s+");
   }
}
