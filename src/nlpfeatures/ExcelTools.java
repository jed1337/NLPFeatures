package nlpfeatures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelTools{
   //Make this not repeat code
   public static void makeExcelOutput(Sentiment[]sentiments, String outputPath) throws FileNotFoundException, IOException {
      //Keep 100 rows in memory, exceeding rows will be flushed to disk
      SXSSFWorkbook workbook = new SXSSFWorkbook(100);
      Sheet sheet            = workbook.createSheet();
      
      makeOutputRows(sentiments, sheet);
      
      closeCreatedFile(workbook, outputPath);
   }
   
   public static void makeExcelOutput(String[][]data, Set<String> keys, String outputPath) 
      throws FileNotFoundException, IOException {
      
      //Keep 100 rows in memory, exceeding rows will be flushed to disk
      SXSSFWorkbook workbook = new SXSSFWorkbook(100);
      Sheet sheet            = workbook.createSheet();
      
      makeOutputHeader(keys, sheet);
      makeOutputRows(data, keys, sheet);
      
      closeCreatedFile(workbook, outputPath);
   }

   private static void makeOutputHeader(Set<String> keys, Sheet sheet) {
      Row header     = sheet.createRow(0);
      int hCellCount = 0;
      header.createCell(hCellCount++).setCellValue("Article");
      
      for (String ngram : keys) {
         header.createCell(hCellCount++).setCellValue(ngram);
      }
   }
   
   /**
    * Structure:
    * 1. data[0]
    * 2. data[1]
    * 3. data[2]
    * N. data[N-1]
    * @param data
    * @param sheet 
    */
   private static void makeOutputRows(Sentiment[] data, Sheet sheet) {
      for (int i = 1; i <= data.length; i++) {
         Row row        = sheet.createRow(i);
         int rCellCount = 0;
         row.createCell(rCellCount++).setCellValue(i);
         row.createCell(rCellCount++).setCellValue(data[i-1].toString());
      }      
   }
   
   private static void makeOutputRows(String[][] data, Set<String> key, Sheet sheet) {
      for (int i = 0; i < data.length; i++) {
         String article = Arrays.toString(data[i]);
         
         Row row        = sheet.createRow(i + 1);
         int rCellCount = 0;
         row.createCell(rCellCount++).setCellValue(article);
         
         for (String keys : key) {
            row.createCell(rCellCount++).setCellValue(article.contains(keys) ? 1 : 0);
         }//Places 1 if the ngram is present in the article, 0 otherwise
      }
   }
      
   private static void closeCreatedFile(SXSSFWorkbook workbook, String outputPath) throws FileNotFoundException, IOException{
      try (FileOutputStream out = new FileOutputStream(outputPath)) {
         workbook.write(out);
      }
      workbook.dispose(); // dispose of temporary files backing this workbook on disk
   }

   /**
    * Returns the Row Iterator from the excel file in inputPath
    * @param inputPath  The path to an excel file
    * @return
    * @throws FileNotFoundException If the file does not exist
    * @throws IOException 
    */
   public static Iterator<Row> getRowIterator(String inputPath) throws FileNotFoundException, IOException {
      FileInputStream file = new FileInputStream(new File(inputPath));
      //Get the workbook instance for XLS file
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      //Get first sheet from the workbook
      XSSFSheet sheet = workbook.getSheetAt(0);
      
      return sheet.iterator();
   }
}
