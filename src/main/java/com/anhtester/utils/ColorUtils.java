package com.anhtester.utils;

import java.awt.Color;
import java.awt.Robot;

public final class ColorUtils {

   private ColorUtils() {
   }

   public static Color getColor(int x, int y) {
      try {
         Robot robot = new Robot();
         return robot.getPixelColor(x, y);
      } catch (Exception e) {
         throw new RuntimeException("Cannot get pixel color", e);
      }
   }

   public static String getHexColor(int x, int y) {
      Color color = getColor(x, y);

      return String.format("#%02X%02X%02X",
              color.getRed(),
              color.getGreen(),
              color.getBlue());
   }

   public static boolean isColorMatched(
           int x,
           int y,
           Color expected) {

      return getColor(x, y).equals(expected);
   }

   public static boolean isColorMatched(
           int x,
           int y,
           Color expected,
           int tolerance) {

      Color actual = getColor(x, y);

      return Math.abs(actual.getRed() - expected.getRed()) <= tolerance
              && Math.abs(actual.getGreen() - expected.getGreen()) <= tolerance
              && Math.abs(actual.getBlue() - expected.getBlue()) <= tolerance;
   }

   public static boolean isColorMatched(
           int x,
           int y,
           String expectedHex) {

      return getHexColor(x, y)
              .equalsIgnoreCase(expectedHex);
   }
}