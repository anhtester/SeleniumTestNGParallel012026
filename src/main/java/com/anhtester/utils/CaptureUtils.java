package com.anhtester.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CaptureUtils {
   public static void createScreenCapture(String imageName) {
      Robot robot = null;
      try {
         robot = new Robot();
      } catch (AWTException e) {
         throw new RuntimeException(e);
      }

      //Get size screen browser
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      System.out.println(screenSize);
      //Khởi tạo kích thước khung hình với kích cỡ trên
      Rectangle screenRectangle = new Rectangle(screenSize);
      //Tạo hình chụp với độ lớn khung đã tạo trên
      BufferedImage image = robot.createScreenCapture(screenRectangle);
      //Lưu hình vào dạng file với dạng png
      File file = new File(imageName + ".png");
      try {
         ImageIO.write(image, "png", file);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      try {
         Thread.sleep(1000);
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }
}
