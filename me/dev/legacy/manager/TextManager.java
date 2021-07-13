package me.dev.legacy.manager;

import java.awt.Color;
import java.awt.Font;
import me.dev.legacy.Client;
import me.dev.legacy.features.Feature;
import me.dev.legacy.features.gui.font.CustomFont;
import me.dev.legacy.features.modules.client.FontMod;
import me.dev.legacy.util.MathUtil;
import me.dev.legacy.util.Timer;
import net.minecraft.util.math.MathHelper;

public class TextManager extends Feature {
   private final Timer idleTimer = new Timer();
   public int scaledWidth;
   public int scaledHeight;
   public int scaleFactor;
   private CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, false);
   private boolean idling;

   public TextManager() {
      this.updateResolution();
   }

   public void init(boolean startup) {
      FontMod cFont = (FontMod)Client.moduleManager.getModuleByClass(FontMod.class);

      try {
         this.setFontRenderer(new Font((String)cFont.fontName.getValue(), (Integer)cFont.fontStyle.getValue(), (Integer)cFont.fontSize.getValue()), (Boolean)cFont.antiAlias.getValue(), (Boolean)cFont.fractionalMetrics.getValue());
      } catch (Exception var4) {
      }

   }

   public void drawStringWithShadow(String text, float x, float y, int color) {
      this.drawString(text, x, y, color, true);
   }

   public float drawString(String text, float x, float y, int color, boolean shadow) {
      if (Client.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
         if (shadow) {
            this.customFont.drawStringWithShadow(text, (double)x, (double)y, color);
         } else {
            this.customFont.drawString(text, x, y, color);
         }

         return x;
      } else {
         mc.field_71466_p.func_175065_a(text, x, y, color, shadow);
         return x;
      }
   }

   public void drawRainbowString(String text, float x, float y, int startColor, float factor, boolean shadow) {
      Color currentColor = new Color(startColor);
      float hueIncrement = 1.0F / factor;
      String[] rainbowStrings = text.split("§.");
      float currentHue = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), (float[])null)[0];
      float saturation = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), (float[])null)[1];
      float brightness = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), (float[])null)[2];
      int currentWidth = 0;
      boolean shouldRainbow = true;
      boolean shouldContinue = false;

      for(int i = 0; i < text.length(); ++i) {
         char currentChar = text.charAt(i);
         char nextChar = text.charAt(MathUtil.clamp(i + 1, 0, text.length() - 1));
         if ((String.valueOf(currentChar) + nextChar).equals("§r")) {
            shouldRainbow = false;
         } else if ((String.valueOf(currentChar) + nextChar).equals("§+")) {
            shouldRainbow = true;
         }

         if (shouldContinue) {
            shouldContinue = false;
         } else {
            if ((String.valueOf(currentChar) + nextChar).equals("§r")) {
               String escapeString = text.substring(i);
               this.drawString(escapeString, x + (float)currentWidth, y, Color.WHITE.getRGB(), shadow);
               break;
            }

            this.drawString(String.valueOf(currentChar).equals("§") ? "" : String.valueOf(currentChar), x + (float)currentWidth, y, shouldRainbow ? currentColor.getRGB() : Color.WHITE.getRGB(), shadow);
            if (String.valueOf(currentChar).equals("§")) {
               shouldContinue = true;
            }

            currentWidth += this.getStringWidth(String.valueOf(currentChar));
            if (!String.valueOf(currentChar).equals(" ")) {
               currentColor = new Color(Color.HSBtoRGB(currentHue, saturation, brightness));
               currentHue += hueIncrement;
            }
         }
      }

   }

   public int getStringWidth(String text) {
      return Client.moduleManager.isModuleEnabled(FontMod.getInstance().getName()) ? this.customFont.getStringWidth(text) : mc.field_71466_p.func_78256_a(text);
   }

   public int getFontHeight() {
      if (Client.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
         String text = "A";
         return this.customFont.getStringHeight(text);
      } else {
         return mc.field_71466_p.field_78288_b;
      }
   }

   public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
      this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
   }

   public Font getCurrentFont() {
      return this.customFont.getFont();
   }

   public void updateResolution() {
      this.scaledWidth = mc.field_71443_c;
      this.scaledHeight = mc.field_71440_d;
      this.scaleFactor = 1;
      boolean flag = mc.func_152349_b();
      int i = mc.field_71474_y.field_74335_Z;
      if (i == 0) {
         i = 1000;
      }

      while(this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
         ++this.scaleFactor;
      }

      if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
         --this.scaleFactor;
      }

      double scaledWidthD = (double)(this.scaledWidth / this.scaleFactor);
      double scaledHeightD = (double)(this.scaledHeight / this.scaleFactor);
      this.scaledWidth = MathHelper.func_76143_f(scaledWidthD);
      this.scaledHeight = MathHelper.func_76143_f(scaledHeightD);
   }

   public String getIdleSign() {
      if (this.idleTimer.passedMs(500L)) {
         this.idling = !this.idling;
         this.idleTimer.reset();
      }

      return this.idling ? "_" : "";
   }
}
