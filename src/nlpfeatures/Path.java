package nlpfeatures;

import java.util.InputMismatchException;

public class Path {
   private final String inputPath;
   private final String outputPath;
   private final String stopwordsPath;

   public Path(String inputPath, String outputPath, String stopwordsPath) {
      this.inputPath     = inputPath;
      this.outputPath    = outputPath;
      this.stopwordsPath = stopwordsPath;
      
      if(this.outputPath==null || this.stopwordsPath==null){
         throw new InputMismatchException("Output path or stopwords path is null. Only the input path can be null");
      }
   }

   public String getInputPath() {
      return inputPath;
   }

   public String getOutputPath() {
      return outputPath;
   }

   public String getStopwordsPath() {
      return stopwordsPath;
   }
}
