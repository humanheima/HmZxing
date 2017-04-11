package com.hm.hmzxinglibrary.encode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hm.hmzxinglibrary.util.ScreenUtil;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by dumingwei on 2017/4/10.
 * 生成条形码，二维码
 */
public class HmEncoder {

    private static final String TAG = "Encoder";

    /**
     * 生成条形码
     *
     * @param content 文字内容
     * @param width   条形码宽度
     * @return
     */
    public static Bitmap createBarCode(Context context, String content, int width) {
        if (TextUtils.isEmpty(content) || content.length() != 13) {
            return null;
        }
        int height = width / 2;
        Bitmap resultBitMap;
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            BitMatrix bitMatrix = new EAN13Writer().encode(content, BarcodeFormat.EAN_13, width, height, hints);
            int[] pixels = new int[width * width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //生成条条形码下面的数字
            Bitmap codeBitmap = createNumberBitmap(content, width, context);
            //把条形码和数字混合生成一张bitmap
            resultBitMap = mixtureBitmap(bitmap, codeBitmap);
            return resultBitMap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap mixtureBitmap(Bitmap first, Bitmap second) {
        if (first == null || second == null) {
            return null;
        }
        int marginW = 20;
        int marginH = 8;
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth() + marginW * 2,
                first.getHeight() + second.getHeight() + marginH * 2, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, marginH, null);
        cv.drawBitmap(second, marginW, first.getHeight()+marginH, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newBitmap;
    }

    /**
     * 生成条形码底部的数字
     *
     * @param content
     * @param width
     * @param context
     * @return
     */
    private static Bitmap createNumberBitmap(String content, int width, Context context) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        tv.setLayoutParams(layoutParams);
        tv.setText(content);
        tv.setTextColor(Color.BLACK);
        tv.setWidth(width);
        tv.setTextSize(14);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(ScreenUtil.dp2px(context, 48), View.MeasureSpec.AT_MOST));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.setDrawingCacheEnabled(true);
        tv.buildDrawingCache();
        return tv.getDrawingCache();
    }


    public static Bitmap createQRCode(int bitmapWidth, String content) {
        if (content == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = "UTF-8";
        hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, encoding);
        //生成的二维码的边距
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix result;
        try {
            result = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, bitmapWidth, bitmapWidth, hints);
        } catch (WriterException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生成带logo的二维码 logo默认为二维码的1/5
     *
     * @param content     生成二维码的文字
     * @param bitmapWidth 二维码的尺寸
     * @param bitmapLogo  logo
     * @return bitmap
     */
    public static Bitmap createQrCodeWithLogo(int bitmapWidth, String content, Bitmap bitmapLogo) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //设置空白边距
        hints.put(EncodeHintType.MARGIN, 0);
        //这是容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try {
            Bitmap scaleLogo = getScaleLogo(bitmapLogo, bitmapWidth, bitmapWidth);
            int offsetX = bitmapWidth / 2;
            int offsetY = bitmapWidth / 2;
            int scaleWidth = 0;
            int scaleHeight = 0;
            if (scaleLogo != null) {
                scaleWidth = scaleLogo.getWidth();
                scaleHeight = scaleLogo.getHeight();
                //图片的左上角的坐标
                offsetX = (bitmapWidth - scaleWidth) / 2;
                offsetY = (bitmapWidth - scaleHeight) / 2;
            }
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, bitmapWidth, bitmapWidth, hints);
            int[] pixels = new int[bitmapWidth * bitmapWidth];
            for (int y = 0; y < bitmapWidth; y++) {
                for (int x = 0; x < bitmapWidth; x++) {
                    if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                        //logo的绘制范围
                        int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                        if (pixel == 0) {
                            if (bitMatrix.get(x, y)) {
                                pixel = 0xff000000;
                            } else {
                                pixel = 0xffffffff;
                            }
                        }
                        pixels[y * bitmapWidth + x] = pixel;
                    } else {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * bitmapWidth + x] = 0xff000000;
                        } else {
                            pixels[y * bitmapWidth + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapWidth, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapWidth);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
        if (logo == null) return null;
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
        matrix.postScale(scaleFactor, scaleFactor);
        return Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
    }
}
