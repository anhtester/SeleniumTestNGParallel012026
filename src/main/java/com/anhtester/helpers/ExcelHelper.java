package com.anhtester.helpers;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class ExcelHelper {

   //Màu nền cho trạng thái Passed (xanh lá vừa) và Failed (đỏ vừa)
   private static final byte[] COLOR_PASSED = new byte[]{(byte) 0x4C, (byte) 0xAF, (byte) 0x50};
   private static final byte[] COLOR_FAILED = new byte[]{(byte) 0xE5, (byte) 0x39, (byte) 0x35};

   private FileInputStream fis;
   private FileOutputStream fileOut;
   private Workbook wb;
   private Sheet sh;
   private Cell cell;
   private Row row;
   private CellStyle cellstyle;
   private Color mycolor;
   private String excelFilePath;
   private Map<String, Integer> columns = new HashMap<>();

   //Cache lại style để không tạo mới mỗi lần ghi cell (Excel giới hạn 64000 style)
   private CellStyle passedStyle;
   private CellStyle failedStyle;
   private CellStyle defaultStyle;

   public void setExcelFile(String ExcelPath, String SheetName){
      try {
         File f = new File(ExcelPath);

         if (!f.exists()) {
            System.out.println("File doesn't exist.");
         }

         fis = new FileInputStream(ExcelPath);
         wb = WorkbookFactory.create(fis);
         sh = wb.getSheet(SheetName);

         if (sh == null) {
            throw new Exception("Sheet name doesn't exist.");
         }

         this.excelFilePath = ExcelPath;

         //Workbook mới thì style cũ không dùng lại được
         passedStyle = null;
         failedStyle = null;
         defaultStyle = null;

         //adding all the column header names to the map 'columns'
         sh.getRow(0).forEach(cell ->{
            columns.put(cell.getStringCellValue(), cell.getColumnIndex());
         });

      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }

   public String getCellData(int columnIndex, int rowIndex) {
      try {
         cell = sh.getRow(rowIndex).getCell(columnIndex);
         String CellData = null;
         switch (cell.getCellType()) {
            case STRING:
               CellData = cell.getStringCellValue();
               break;
            case NUMERIC:
               if (DateUtil.isCellDateFormatted(cell)) {
                  CellData = String.valueOf(cell.getDateCellValue());
               } else {
                  CellData = String.valueOf((long) cell.getNumericCellValue());
               }
               break;
            case BOOLEAN:
               CellData = Boolean.toString(cell.getBooleanCellValue());
               break;
            case BLANK:
               CellData = "";
               break;
         }
         return CellData;
      } catch (Exception e) {
         return "";
      }
   }

   //Gọi ra hàm này nè
   public String getCellData(String columnName, int rowIndex) {
      return getCellData(columns.get(columnName), rowIndex);
   }

   //set by column index
   public void setCellData(String text, int columnIndex, int rowIndex) {
      try {
         row = sh.getRow(rowIndex);
         if (row == null) {
            row = sh.createRow(rowIndex);
         }
         cell = row.getCell(columnIndex);

         if (cell == null) {
            cell = row.createCell(columnIndex);
         }
         cell.setCellValue(text);

         //Passed/pass -> nền xanh lá, Failed/fail -> nền đỏ, còn lại -> không tô nền
         cell.setCellStyle(getStyleByStatus(text));

         fileOut = new FileOutputStream(excelFilePath);
         wb.write(fileOut);
         fileOut.flush();
         fileOut.close();
      } catch (Exception e) {
         e.getMessage();
      }
   }

   //set by column name
   public void setCellData(String text, String columnName, int rowIndex) {
      setCellData(text, columns.get(columnName), rowIndex);
   }

   //Chọn style theo giá trị ghi vào cell (không phân biệt hoa thường)
   private CellStyle getStyleByStatus(String text) {
      String value = text == null ? "" : text.trim().toLowerCase();

      if (value.equals("passed") || value.equals("pass")) {
         if (passedStyle == null) {
            passedStyle = createStatusStyle(COLOR_PASSED);
         }
         return passedStyle;
      }

      if (value.equals("failed") || value.equals("fail")) {
         if (failedStyle == null) {
            failedStyle = createStatusStyle(COLOR_FAILED);
         }
         return failedStyle;
      }

      if (defaultStyle == null) {
         defaultStyle = createStatusStyle(null);
      }
      return defaultStyle;
   }

   //rgb = null thì không tô nền
   private CellStyle createStatusStyle(byte[] rgb) {
      XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
      style.setAlignment(HorizontalAlignment.CENTER);
      style.setVerticalAlignment(VerticalAlignment.CENTER);

      if (rgb == null) {
         style.setFillPattern(FillPatternType.NO_FILL);
         return style;
      }

      style.setFillForegroundColor(new XSSFColor(rgb, null));
      style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

      Font font = wb.createFont();
      font.setBold(true);
      font.setColor(IndexedColors.WHITE.getIndex());
      style.setFont(font);

      return style;
   }
}
