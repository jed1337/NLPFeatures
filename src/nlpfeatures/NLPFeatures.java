package nlpfeatures;

import Socal.PreprocessSO_CAL;
import java.io.IOException;
import ngram.PreprocessNgram;

public class NLPFeatures {

   /**
    * The main class
    * @param args
    * @throws IOException 
    */
   public static void main(String[] args) throws IOException {
//      Path path = new Path ("src\\Input\\Cagampan390-Test.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\CagampanComplete.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      Path path = new Path ("src\\Input\\Berna-Complete-unquoted.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Sample Potch.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path (null, "src\\Output\\", "src\\Input\\Stopwords.txt");
      
//<editor-fold defaultstate="collapsed" desc="Tf idf">
//      Preprocess p = new PreprocessTF_IDF(path);
//
//      for (int i = 0; i < 10; i++) {
//         p.output(i*10);
//      }
//</editor-fold>
      
//<editor-fold defaultstate="collapsed" desc="So cal">
//      Preprocess p = new PreprocessSO_CAL(path);
//      p.output(4);
//</editor-fold>

      for(int i=1; i<=5; i++){
         Preprocess p = new PreprocessNgram(path, i);
         
         for(int j=5; j<=25; j+=5){
            p.output(j);
            System.out.printf("Finished %d gram %d rm\n", i, j);
         }
      }
   }
}
