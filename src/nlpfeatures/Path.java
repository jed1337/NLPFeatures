package nlpfeatures;

import java.util.InputMismatchException;

/**
 * A class containing the paths to files used. Used for organizational purposes.
 * @author Jed Caychingco
 */
public class Path {
   /** The path to an excel file containing articles */
   private final String inputPath;
   
   /** The output path */
   private final String outputPath;
   
   /** The path to a text file containing stopwords */
   private final String stopwordsPath;

   /**
    * Creates an instance of this class with the specified paths
    * @param inputPath A path to an excel file containing articles
    * @param outputPath The output path
    * @param stopwordsPath The path to a text file containing stopwords
    */
   public Path(String inputPath, String outputPath, String stopwordsPath) {
      this.inputPath     = inputPath;
      this.outputPath    = outputPath;
      this.stopwordsPath = stopwordsPath;
      
      if(this.outputPath==null || this.stopwordsPath==null){
         throw new InputMismatchException("Output path or stopwords path is null. Only the input path can be null");
      }
   }

   /**
    * Returns the input path
    * @return 
    */
   public String getInputPath() {
      return inputPath;
   }

   /** 
    * Returns the output path
    * @return 
    */
   public String getOutputPath() {
      return outputPath;
   }

   /**
    * The stop words path
    * @return 
    */
   public String getStopwordsPath() {
      return stopwordsPath;
   }
}
