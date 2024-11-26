package top.sacz.timtool.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawableUtil {
    //Bitmap.CompressFormat.PNG Bitmap.CompressFormat.JPEG
    public static void drawableToFile(Drawable drawable, String filePath, Bitmap.CompressFormat format) {
        if (drawable == null) return;
        try {
            File file = new File(filePath);

            if (file.exists()) file.delete();
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();

            FileOutputStream out;
            out = new FileOutputStream(file);
            ((BitmapDrawable) drawable).getBitmap().compress(format, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将本地文件转换为 Drawable
     */
    public static Drawable readDrawableFromFile(Context context, String file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        Drawable drawable = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            drawable = new BitmapDrawable(context.getResources(), bitmap);
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return drawable;
    }
}
