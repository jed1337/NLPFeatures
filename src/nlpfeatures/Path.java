package nlpfeatures;

public class Path {
   private final String inputPath;
   private final String outputPath;
   private final String stopwordsPath;

   public Path(String inputPath, String outputPath, String stopwordsPath) {
      this.inputPath     = inputPath;
      this.outputPath    = outputPath;
      this.stopwordsPath = stopwordsPath;
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
