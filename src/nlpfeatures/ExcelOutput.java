package nlpfeatures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelOutput{
   
   //Make this not repeat code
   public static void output(List<Sentiment> sentiments, String outputPath) throws FileNotFoundException, IOException {
      //Keep 100 rows in memory, exceeding rows will be flushed to disk
      SXSSFWorkbook workbook = new SXSSFWorkbook(100);
      Sheet sheet            = workbook.createSheet();
      
      makeRows(sentiments, sheet);
      
      closeFile(workbook, outputPath);
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
   private static void makeRows(List<Sentiment> data, Sheet sheet) {
      for (int i = 1; i <= data.size(); i++) {
         Row row        = sheet.createRow(i);
         int rCellCount = 0;
         row.createCell(rCellCount++).setCellValue(i);
         row.createCell(rCellCount++).setCellValue(data.get(i-1).toString());
      }      
   }
      
   private static void closeFile(SXSSFWorkbook workbook, String outputPath) throws FileNotFoundException, IOException{
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
   public static XSSFSheet getSheet(String inputPath) throws FileNotFoundException, IOException {
      FileInputStream file = new FileInputStream(new File(inputPath));
      //Get the workbook instance for XLS file
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      //Get first sheet from the workbook
      XSSFSheet sheet = workbook.getSheetAt(0);
      
      return sheet;
   }
}
