package com.demo;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DemoApplication {
    static final int DELTA = 3;

    public static boolean isEligible(BufferedImage img, int x, int y){

        int left  = x-1;
        while (  left < 0 &&  x -left < 2* DELTA) {
            if( img.getRGB(left,y) == Color.WHITE.getRGB()) {
                break;
            }
            left --;
        }
        if( left < 0) {
            return false;
        }
        int right = x + 1;

        while ( right < img.getWidth() && right - left < 2 * DELTA) {
            if( img.getRGB(right,y) == Color.WHITE.getRGB()) {
                break;
            }
            right++;
        }
        if( right > img.getWidth()) {
            return false;
        }
        int top = y -1;
        while (top >0 && y - top < 2 * DELTA) {
            if( img.getRGB(x,top) == Color.WHITE.getRGB()) {
                break;
            }
            top --;
        }
        if( top < 0) {
            return false;
        }
        int bottom = y+1;
        while (bottom < img.getHeight() && bottom -top < 2* DELTA) {
            if( img.getRGB( x,bottom) == Color.WHITE.getRGB()) {
                break;
            }
            bottom++;
        }
        if( bottom > img.getHeight()) {
            return false;
        }


        int width = right -left;
        int height =  bottom - top;
        if( width >= DELTA && height >= DELTA) {
            return true;
        }
        return false;

    }

    public static BufferedImage cleanImage(BufferedImage source){
        BufferedImage clone = new BufferedImage(source.getWidth(),
                source.getHeight(), source.getType());
        Graphics2D g2d = clone.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        for(int i=0;i<clone.getWidth();i++){
            for(int j=0;j<clone.getHeight();j++){
                int rgb = clone.getRGB(i,j);
                if( rgb == Color.WHITE.getRGB()){
                    continue;
                }
                if( isEligible(clone, i,j)) {
                    continue;
                }
                else {
                    clone.setRGB(i,j,Color.WHITE.getRGB());
                }

            }
        }

        return clone;

    }
    public static String cleanResult(String result){
        StringBuilder sb = new StringBuilder();
        for(int i =0;i<result.length();i++){
            if( Character.isAlphabetic( result.charAt(i)) || Character.isDigit( result.charAt(i))) {
                sb.append( result.charAt(i));
            }
        }
        return sb.toString();
    }
    public static void main(String[] args) throws IOException, TesseractException {
        BufferedImage image = ImageIO.read( new File("sample/captcha-sample-1.png"));
        BufferedImage clean = cleanImage(image);
        ImageIO.write(clean, "png",new File("sample/clean.png"));
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        String result = tesseract.doOCR( clean);
        result = cleanResult(result);

        System.out.println("result : "+result);
    }
}
