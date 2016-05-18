package nlpfeatures;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CSVOutput {
   private static final char NEW_LINE = '\n';
   
   public static void output(ArrayList<Article> articles, Set<String> keys, String outputPath)
           throws FileNotFoundException, IOException {
      FileWriter writer = new FileWriter(outputPath);

      makeHeader(keys, writer);
      makeRows(keys, articles, writer);

      closeFile(writer);
   }

   private static void append(StringBuilder sb, String text) throws IOException{
      char COMMA    = ',';
      sb.append(text);
      sb.append(COMMA);
   }
   
   private static void makeHeader(Set<String> keys, FileWriter writer) throws IOException {
      StringBuilder sb = new StringBuilder();
      append(sb, "Article");
      
      for (String key : keys) {
         append(sb, key);
      }
      sb.append(NEW_LINE);
      writer.append(sb);
   }

   private static void makeRows(Set<String> keys, ArrayList<Article> articles, FileWriter writer) throws IOException {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < articles.size(); i++) {
         String article = Arrays.toString(articles.get(i).getWords());
         append(sb, "Article "+i);

         for (String key : keys) {
            append(sb, article.contains(key) ? "1" : "0");
         }
         sb.append(NEW_LINE);
      }
      writer.append(sb);
   }

   private static void closeFile(FileWriter writer) throws IOException {
      writer.flush();
      writer.close();
   }
}
