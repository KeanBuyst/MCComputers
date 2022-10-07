package mcc.computer.view;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MinecraftFont;

import java.awt.*;

public class View {
    private final MapCanvas canvas;

    /**
     * Values representing drawable area.
     */
    protected int width = 127,height = 127,offsetX,offsetY;

    public View(MapCanvas canvas) {
        this.canvas = canvas;
    }
    public void setPixel(int x, int y, Color color){
        if (isDrawable(x,y)){
            x += offsetX;
            y += offsetY;
            canvas.setPixel(x,y, MapPalette.matchColor(color));
        }
    }
    public Color getPixel(int x,int y){
        x += offsetX;
        y += offsetY;
        return MapPalette.getColor(canvas.getPixel(x,y));
    }
    public void drawRect(Color color,int x, int y, int width, int height){
        for (int Y = y; Y <= y+height; Y++){
            for (int X = x; X <= x+width; X++){
                setPixel(X,Y,color);
            }
        }
    }

    public void fill(Color color){
        for (int y = 0; y <= height; y++){
            for (int x = 0; x <= width; x++){
                setPixel(x,y,color);
            }
        }
    }
    public void drawText(String text,int x,int y){
        canvas.drawText(x+offsetX,y+offsetY, MinecraftFont.Font,text);
    }
    private boolean isDrawable(int x,int y){
        return x <= width && x >= 0 && y <= height && y >= 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    @SuppressWarnings("deprecation")
    public Byte[] getByteMap(){
        Byte[] map = new Byte[128*128];
        for (int i = 0; i < map.length; i++){
            int y = i / 128;
            int x = i % 128;
            map[i] = canvas.getPixel(x,y);
        }
        return map;
    }
    @SuppressWarnings("deprecation")
    public void setByteMap(Byte[] map){
        for (int i = 0; i < map.length; i++){
            int y = i / 128;
            int x = i % 128;
            canvas.setPixel(x,y,map[i]);
        }
    }
}
