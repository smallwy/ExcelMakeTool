package core;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConvertXml {

  public static void main(String[] args) {
    String gamePackage = args[0]; // 配置启动参数 拿到xls文件地址 D:\zcWorkspace\配置表\test
    String srcDictPath = args[1];
    System.out.println(gamePackage);
    System.out.println(srcDictPath);
    String path = System.getProperty("user.dir");
    System.out.println(path);
    File fileDir = new File(gamePackage);
    if (fileDir.exists() && fileDir.isDirectory()) {
      File[] files = fileDir.listFiles();
      for (File file : files) {
        int x = 0;
        System.out.println(file);
        Sheet sheet = null;
        Row row = null;
        List<Map<Integer, String>> list = null;
        String cellData = null;
        String fileName = file.getName();
        if (fileName.endsWith(".xlsx") && !fileName.startsWith("~$") && !fileName.startsWith(".")) {
          Workbook workbook = readExcel(file);
          if (workbook != null) {
            // 用来存放表中数据
            list = new ArrayList<Map<Integer, String>>();
            // 获取第一个sheet
            sheet = (Sheet) workbook.getSheetAt(x);
            int rownum = sheet.getPhysicalNumberOfRows();
            row = sheet.getRow(x);
            // 获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = 0; i < rownum; i++) {
              Map<Integer, String> map = new LinkedHashMap<Integer, String>();
              row = sheet.getRow(i);
              if (row != null) {
                for (int j = 0; j < colnum; j++) {
                  cellData = (String) getCellFormatValue(row.getCell(j));
                  map.put(j, cellData);
                }
              } else {
                break;
              }
              list.add(map);
            }
          }
          // 遍历解析出来的list
          for (Map<Integer, String> map : list) {
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
              System.out.print(entry.getValue() + ",");
            }
            System.out.println();
          }
          x++;
        }
      }
    }
  }

  public static Object getCellFormatValue(Cell cell) {
    Object cellValue = null;
    if (cell != null) {
      // 判断cell类型
      switch (cell.getCellType()) {
        case Cell.CELL_TYPE_NUMERIC:
          {
            cellValue = String.valueOf(cell.getNumericCellValue());
            break;
          }
        case Cell.CELL_TYPE_FORMULA:
          {
            // 判断cell是否为日期格式
            if (DateUtil.isCellDateFormatted(cell)) {
              // 转换为日期格式YYYY-mm-dd
              cellValue = cell.getDateCellValue();
            } else {
              // 数字
              cellValue = String.valueOf(cell.getNumericCellValue());
            }
            break;
          }
        case Cell.CELL_TYPE_STRING:
          {
            cellValue = cell.getRichStringCellValue().getString();
            break;
          }
        default:
          cellValue = "";
      }
    } else {
      cellValue = "";
    }
    return cellValue;
  }

  private static void deleteDir(File dir) {
    if (dir.isDirectory()) {
      File[] files = dir.listFiles();
      for (File d : files) {
        deleteDir(d);
      }
    }
    dir.delete();
  }

  public static void mkdirRecursive(String dirPath) {
    File file = new File(dirPath);
    file.mkdirs();
  }

  private static Workbook readExcel(File file) {
    Workbook wb = null;
    try {
      InputStream is = new FileInputStream(file);
      String fileName = file.getName();
      if (fileName.endsWith(".xlsx")) {
        wb = new XSSFWorkbook(is);
      } else if (fileName.endsWith(".xls")) {
        wb = new HSSFWorkbook(is);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return wb;
  }
}
