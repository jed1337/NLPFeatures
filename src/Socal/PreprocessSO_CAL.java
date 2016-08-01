package Socal;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import Socal.Intensifiers.IntensifierMethod;
import Socal.Intensifiers.IntensifierType;
import Socal.Intensifiers.PrefixIntensifier;
import Socal.Intensifiers.WordBeforeIsIntensifier;
import Socal.Intensifiers.WordBeforeIsANegator;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import nlpfeatures.Article;
import nlpfeatures.Path;
import nlpfeatures.Preprocess;
import nlpfeatures.Sentiment;

/**
 * This class is responsible for preprocessing the input to be used by the SO CAL
 * @author Jed Caychingco
 */
public class PreprocessSO_CAL extends Preprocess {
   
   /** The path to the excel files containing the weights */
   private final String WEIGHT_PATH = "src\\Socal\\Weights\\";
   
   /** The path to the Filipino tagger */
   private final String TAGGER_PATH = "src\\Socal\\Tagger\\filipino.tagger";
   
   /** The tagger to be used */
   private final MaxentTagger TAGGER;

   /** The weights to be used */
   private ArrayList<Weight> weights;
   
   /** The intensifier methods to be used */
   private ArrayList<IntensifierMethod> intMethods;
   
   /**
    * Creates an instance of this class from the 
    * specified path using unigrams
    * @param path The path to the excel file containing article. This serves as the input.
    */
   public PreprocessSO_CAL(Path path){
      this(path, 1); 
   }
   
   /**
    * Creates an instance of this class from the 
    * specified path using the specified ngram count
    * @param path The path to the excel file containing article. This serves as the input.
    * @param ngCount The number of ngrams to use
    */
   public PreprocessSO_CAL(Path path, int ngCount) {
      super(path, ngCount);
      initializeWeights();
      initializeIntensifiers();

      this.TAGGER = new MaxentTagger(TAGGER_PATH);
   }
   
//<editor-fold defaultstate="collapsed" desc="Initializers">
   /**
    * Initializes the weights.
    */
   private void initializeWeights() {
      try {
         this.weights = new ArrayList<>();
         this.weights.add(new Weight(WEIGHT_PATH + "ADJ.xlsx", 'J'));
         this.weights.add(new Weight(WEIGHT_PATH + "ADV.xlsx", 'R'));
         this.weights.add(new Weight(WEIGHT_PATH + "NOUN.xlsx", 'N'));
         this.weights.add(new Weight(WEIGHT_PATH + "VERB.xlsx", 'V'));
      } catch (IOException ex) {
         printErrors(ex);
      }
   }
   
   /**
    * Initializes the intensifiers
    */
   private void initializeIntensifiers(){
      this.intMethods = new ArrayList<>();
      this.intMethods.add(new PrefixIntensifier());
      this.intMethods.add(new WordBeforeIsIntensifier());
      this.intMethods.add(new WordBeforeIsANegator());
   }   
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Fundamental Numbers">
   /**
    * Prints the fundamental numbers: Precision, Recall, F-Score, Accuracy
    * @param articles The list used to get the fundemental numbers
    */
   private String getFundamentalNumbersString(ArrayList<Article> articles){
      StringJoiner sj = new StringJoiner("\n====================\n");
      for(Sentiment sen: Sentiment.values()){
         float tp = 0.0f; //True  positive
         float tn = 0.0f; //True  negative
         float fp = 0.0f; //False positive
         float fn = 0.0f; //False negative
         
         for (Article article : articles) {
            Sentiment trueSen = article.getActualSentiment(); //Actual Sentiment
            Sentiment predSen = article.getPredictedSentiment("SO_CAL");         //Predicted Sentiment
            
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
         float p   = tp/(tp+fp);            //Precision
         float r   = tp/(tp+fn);            //Recall
         float fs  = (2*p*r)/(p+r);         //F-score
         float acc = (tp+fn)/(tp+fp+tn+fn); //Accuracy

//         System.out.println("=========="+sen+"==========");
         sj.add(String.format("Sentiment: \t%s\nAccuracy: \t%s\nPrecision: \t%s\nF-Score: \t%s\nRecall: \t%s\n"
            ,sen,acc, p, fs, r));
//         System.out.println("Accuracy  : " + acc);
//         System.out.println("Precision : " + p);
//         System.out.println("F-Score   : " + fs);
//         System.out.println("Recall    : " + r);
      }
      return sj.toString();
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Intensifier Code">
   /**
    * Loops through the tagged words and intensifies the words that
    * can be intensified
    * @param taggedWords A list of words to be checked for intensification
    * @return The total value of the intensification made 
    * using all the intensifier methods in this class.
    */
   private float addIntensifierWeight(TaggedWords[] taggedWords) {
      float total = 0;
      for(IntensifierMethod intMethod : this.intMethods){
         total += intMethod.addIntensification(taggedWords, this.weights);
      }
      return total;
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Tag Article">
   /**
    * Tags an article and returns its weight
    * @param index Its index from ArrayList of String super.articles
    * @return The weight of the article
    */
   private int getArticleWeight(int index) {
//      String taggedArticle = TAGGER.tagString(getFullArticleAt(index));
      return getArticleWeight(getFullArticleAt(index));
   }
   
   /**
    * Tags an article and returns its weight
    * This method can function without the use of super.articles
    * @param articleContents an article's contents
    * @return The weight of the article
    */
   public int getArticleWeight(String articleContents) {
      String taggedArticle = TAGGER.tagString(articleContents);
      TaggedWords[] taggedWords = getTaggedWords(taggedArticle);
      return getArticleWeightFromTW(taggedWords);
   }

   /**
    * Given an already tagged articles, this function returns an array of TaggedWords
    * that contain a word and its appropriate tag
    * 
    * @param article
    * @return 
    */
   private TaggedWords[] getTaggedWords(String article) {
      String[] articleWords     = article.split("\\s+");
      int awLength              = articleWords.length;
      TaggedWords[] taggedWords = new TaggedWords[awLength];
      
      for (int i = 0; i < awLength; i++) {
         taggedWords[i] = new TaggedWords(articleWords[i]);
      }
      return taggedWords;
   }
   
   /**
    * @param taggedWords The words in the article along with their corresponding
    * parts of speech tag
    * @return 
    */
   private int getArticleWeightFromTW(TaggedWords[] taggedWords) {
      int articleWeight = 0;
      for (Weight w : weights) {
         articleWeight += Arrays.stream(taggedWords)
            .filter(tw->tw.getTag() == w.getTag())       //The Weight tag is equal to the tag of the current word
            .mapToInt(tw->w.getWordValue(tw.getWord()))  //Get only the weights of each word (int)
            .sum();
      }
      
      articleWeight += addIntensifierWeight(taggedWords);
      return articleWeight;
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Classify Article">
   /**
    * Given the weight of the article at the index, the function classifies
    * it into positive, negative or neutral
    * @param articleWeight
    * @param index
    */
   private void classifyArticle(int articleWeight, int index) {
      Sentiment sentiment;
      sentiment = getSentimentFromWeight(articleWeight);
      System.out.println(String.format("Weight of %d is %d \t = %s", index, articleWeight, sentiment));
      articles.get(index).addPredictedSentiment("SO_CAL", sentiment);
   }

   /**
    * Returns the appropriate sentiment given the article's weight.
    * @param articleWeight
    * @return Positive if the weight's greater than 0,
    * Negative if it is less than 0,
    * and Neutral if it is equal to 0
    */
   public Sentiment getSentimentFromWeight(int articleWeight) {
      Sentiment sentiment;
      if (articleWeight > 0) {
         sentiment = Sentiment.POSITIVE;
      } else if (articleWeight < 0) {
         sentiment = Sentiment.NEGATIVE;
      } else {
         sentiment = Sentiment.NEUTRAL;
      }
      return sentiment;
   }
//</editor-fold>
      
//<editor-fold defaultstate="collapsed" desc="Worker Thread">
   /**
    * An inner class used to parallelize the classification of articles
    */
   private class Worker extends Thread {
      /**
       * The start index of {@code ArrayList<Weight> weights} to classify
       */
      private final int START;

      /**
       * The end index of {@code ArrayList<Weight> weights} to classify
       */
      private final int END;
      
      /**
       * Creates an instance of this class with the specified start and end index
       * @param start
       * @param end 
       */
      public Worker(int start, int end) {
         this.START = start;
         this.END = end;
      }
      
      /**
       * Classifies each article within the range of [START, END)
       */
      @Override
      public void run() {
         for (int i = START; i < END; i++) {
            int articleWeight = getArticleWeight(i);
            classifyArticle(articleWeight, i);
         }
      }
      
      /**
       * @return Start: {@code <START>} End: {@code <END>}
       */
      @Override
      public String toString() {
         return String.format("Start: %d \t End: %d", START, END);
      }
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Worker Thread Functions">
   /**
    * Sets which articles each worker needs to process
    * @param threadCount The number of threads to be used
    * distributing the load to the Workers
    * @return an array of Workers bearing approximately equal load
    */
   private Worker[] getWorkerThreads(int threadCount, int aSize) {
      Worker[] workers = new Worker[threadCount];
      
      final int partition = aSize/threadCount;
      
      for (int i = 0; i < workers.length; i++) {
         int start = i*partition;
         int end   = (i+1)*partition;
         
         //If the number of articles is not divisible by the number of threads
         //Add the remaining articles to the last worker thread
         if(i == (workers.length-1)){
            end += aSize % threadCount;
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
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Output">
   /**
    * Clssifies the articles and then creates an Excel and .srt file of the result
    * @param threadCount Run this classifier with the following number of threads
    * @throws IOException 
    */
   @Override
   public void output(int threadCount) throws IOException {
      setupWorkerThreads(threadCount);
      
      tempMeasureIntensifier();
      
      List<Sentiment> predictedSentiments = 
         super.articles.stream()
            .map(a->a.getPredictedSentiment("SO_CAL"))
            .collect(Collectors.toList());
      
      String fileName = String.format("%sSO-CAL", outputPath);
//      ExcelOutput.output(predictedSentiments, fileName+".xlsx");
      
      createSrtFile(fileName, predictedSentiments);
   }

   /**
    * Ad hoc function to view the stats of the intensifier method
    * @throws IOException 
    */
   private void tempMeasureIntensifier() throws IOException {
      String fn = String.format("\n\n%s \t %s\n", IntensifierType.SUPERLATIVE, IntensifierType.COMPARATIVE);
      fn       += getFundamentalNumbersString(articles);
      
      Files.write(Paths.get(outputPath+"SuperCompare.txt"), fn.getBytes(), StandardOpenOption.APPEND);
   }
   
   /**
    * Uses a number of threads to parallelize the classification
    * @param threadCount The number of threads to use
    */
   private void setupWorkerThreads(int threadCount) {
      Worker[] workers = getWorkerThreads(threadCount, articles.size());
      startWorkers(workers);
   }
   
   /**
    * Creates an .srt file of the predicted sentiments
    * @param fileName The File name to be used. 
    * The program automatically appends ".ser" at the end.
    * @param predictedSentiments The list of predicted sentiments
    */
   private void createSrtFile(String fileName, List<Sentiment> predictedSentiments) {
      try {
         FileOutputStream fos      = new FileOutputStream(fileName+".ser");
         ObjectOutputStream oos    = new ObjectOutputStream(fos);
         oos.writeObject(predictedSentiments);
         
         closeSafely(oos);
         closeSafely(fos);
      } catch (IOException ie) {
         printErrors(ie);
      }
   }
//</editor-fold>

   /**
    * Returns the words in an article
    * @param article The article to be formatted
    * @return 
    */
   @Override
   protected String[] format(String article){
      return article.split("\\s+");
   }
}
