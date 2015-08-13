package com.android.enjack.util;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {


	/**
	 * 图片的信息，包括长宽等。
	 *
	 * */
	public static class Info{
		public int width = 0;
		public int height = 0;
	}

	/**
	 * 加载位图，特别是大分辨率的位图。使用这个方法加载可以防止OOM。
	 *
	 * @param res
	 * @param	resId
	 * @param	samplesize
	 * 	采样率，必须是大于等于1的整数，google建议是2的指数。
	 * 	得到的位图是原图片的(1/samplesize*samplesize)大小。
	 *
	 * @return Bitmap
	 *
	 * @see #load(String file, int samplesize)
	 * */
	public static Bitmap load(Resources res, int resId, int samplesize){
		BitmapFactory.Options opt = new BitmapFactory.Options();  
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;  
		opt.inInputShareable = true;
		opt.inSampleSize = samplesize;
		opt.inJustDecodeBounds = false;//按照inSampleSize的采样率，读取图片到内存
		/*decodeStream直接调用JNI>>nativeDecodeAsset()来完成decode，
		无需再使用java层的createBitmap，从而节省了java层的空间。*/
		InputStream is = res.openRawResource(resId);
		return BitmapFactory.decodeStream(is,null,opt);
	}


	/**
	 * 加载位图，特别是大分辨率的位图。使用这个方法加载可以防止OOM。
	 *
	 * @param file
	 * 	文件的路径。
	 * @param	samplesize
	 * 	采样率，必须是大于等于1的整数，google建议是2的指数。
	 * 	得到的位图是原图片的(1/samplesize*samplesize)大小。
	 *
	 * @return	Bitmap
	 *
	 * @see #load(Resources res, int resId, int samplesize)
	 * */
	public static Bitmap load(String file, int samplesize){
		BitmapFactory.Options opt = new BitmapFactory.Options();  
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;  
		opt.inInputShareable = true;
		opt.inSampleSize = samplesize;
		opt.inJustDecodeBounds = false;//����inSampleSize�Ĳ����ʣ���ȡͼƬ���ڴ�
		return BitmapFactory.decodeFile(file, opt);
	}


	/**
	 * 收回位图。
	 * */
	public static void recycle(Bitmap bitmap){
		if(bitmap!=null){
			if(!bitmap.isRecycled()){
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
		}
	}


	/**
	 * 不加载图片到内存，得到图片的长宽。
	 *
	 * @param	res
	 * @param	resId
	 *
	 * @return
	 * 	Info
	 *
	 * @see Info
	 * @see #getWidthHeight(String file)
	 * */
	public static Info getWidthHeight(Resources res, int resId){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;//֮��ȡ��Ϣ��������ͼƬ���ڴ档
		BitmapFactory.decodeResource(res, resId, opt);
		Info info = new BitmapUtil.Info();
		info.width = opt.outWidth;
		info.height = opt.outHeight;
		return info;
	}


	/**
	 * 不加载图片到内存，得到图片的长宽。
	 *
	 * @param	file
	 * 	文件路径.
	 *
	 * @return
	 * 	Info
	 *
	 * @see Info
	 * @see #getWidthHeight(Resources res, int resId)
	 * */
	public static Info getWidthHeight(String file){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;//֮��ȡ��Ϣ��������ͼƬ���ڴ档
		BitmapFactory.decodeFile(file, opt);
		Info info = new BitmapUtil.Info();
		info.width = opt.outWidth;
		info.height = opt.outHeight;
		return info;
	}


	/**
	 * 重新调整bitmap大小。
	 * @param bitmap
	 * 	需要处理的bitmap。
	 * @param	w
	 * 	新的width。
	 * @param	h
	 * 	新的height。
	 *
	 * @return	调整大小后的bitmap。
	 * */
	public static Bitmap resize(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }


	/**
	 * 把位图转换成jpg格式保存。
	 *
	 * @param	compress
	 * 	压缩精度，最大100.
	 *
	 * @param	filename
	 * 	jpg的文件名，包含路径。
	 * */
	public static void toJPG(Bitmap bitmap, int compress, String filename){
		File f = new File(filename);
		FileOutputStream fOut = null;
		try {
            fOut = new FileOutputStream(f);
	    } catch (FileNotFoundException e) {
	            e.printStackTrace();
	    }
		bitmap.compress(Bitmap.CompressFormat.JPEG, compress, fOut);
		try {
            fOut.flush();
	    } catch (IOException e) {
	            e.printStackTrace();
	    }
		try {
            fOut.close();
	    } catch (IOException e) {
	            e.printStackTrace();
	    }
	}


	/**
	 * 把位图转换成png格式保存。
	 *
	 * @param	compress
	 * 	压缩精度，最大100.
	 *
	 * @param	filename
	 * 	jpg的文件名，包含路径。
	 * */
	public static void toPNG(Bitmap bitmap, int compress, String filename){
		File f = new File(filename);
		FileOutputStream fOut = null;
		try {
            fOut = new FileOutputStream(f);
	    } catch (FileNotFoundException e) {
	            e.printStackTrace();
	    }
		bitmap.compress(Bitmap.CompressFormat.PNG, compress, fOut);
		try {
            fOut.flush();
	    } catch (IOException e) {
	            e.printStackTrace();
	    }
		try {
            fOut.close();
	    } catch (IOException e) {
	            e.printStackTrace();
	    }
	}


	/**
	 * 裁剪矩形位图。
	 * @param	left
	 * 	以位图左上角为原点的坐标。不可超出位图范围。
	 * @param	top
	 * @param	right
	 * @param	bottom
	 *
	 * @return
	 * 	裁剪后的位图。
	 * */
	public static Bitmap clipRect(Bitmap bitmap, int left, int top, int right, int bottom){
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if(left<0 || top<0 || right>w || bottom>h || right-left<=0 || bottom-top<=0)
			return null;
		Bitmap bm = Bitmap.createBitmap(right-left, bottom-top, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		c.translate(-left, -top);
		Paint p = new Paint();
		p.setAntiAlias(true);
		c.drawBitmap(bitmap, 0, 0, p);
		return bm;
	}


	/**
	 * 裁剪出一个圆形的位图。边角透明。
	 *
	 * @param	bitmap
	 * 	要裁剪的位图。
	 * @param	x
	 * 	圆心的x坐标。
	 * @param	y
	 * 	圆心的y坐标。
	 *
	 * @return
	 * 	圆形位图。
	 * */
	public static Bitmap clipCircle(Bitmap bitmap, int x, int y, int radius){
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if(x<=0 || y<=0 || x>=w || y>=h || 
				x-radius<0 || y-radius<0 ||
				x+radius>w || y+radius>h)
			return null;
		Bitmap bm = Bitmap.createBitmap(radius*2, radius*2, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint();
		p.setAntiAlias(true);
		c.drawARGB(0, 0, 0, 0);//����͸������
		c.drawCircle(radius, radius, radius, p);
		p.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// ��������ͼƬ�ཻʱ��ģʽ,�ο�http://trylovecatch.iteye.com/blog/1189452
		c.translate(-(x-radius), -(y-radius));
		c.drawBitmap(bitmap, 0, 0, p);
		return bm;
	}

	/**
	 * 调节三种颜色分量的比例，从而达到改变色温、色调、色相。
	 *
	 * @param bitmap
	 * 	位图
	 * @param r
	 * 	红色分量。0表示维持不变。大于0增加比例，小于0减小比例。
	 * @param	g
	 * 	绿色分量。
	 * @param	b
	 * 	蓝色分量。
	 *
	 *
	 * @return
	 * 	处理后的位图。
	 * */
	public static Bitmap rgbRatio(Bitmap bitmap, float r, float g, float b){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		ColorMatrix cm = new ColorMatrix();
		cm.set(new float[] { 1, 0, 0, 0, r, 
				0, 1,  0, 0, g,
                0, 0, 1, 0, b, 
                0, 0, 0, 1, 0 });
		Paint p = new Paint();
		p.setColorFilter(new ColorMatrixColorFilter(cm));
		Canvas c = new Canvas(bmp);
		c.drawBitmap(bitmap, 0, 0, p);
		return bmp;
	}


	/**
	 * 调节对比度。
	 *
	 * @param bitmap
	 * 	位图。
	 * @param	con
	 * 	对比度。等于1保持，大于1增大对比度，小于1减小对比度。
	 * 效果不是很好，需要找出con和brightness的最佳对应关系。
	 * 对比度越大，饱和度同时也越高，该问题待处理。
	 *
	 * @return
	 * 	处理后的位图。
	 * */
	public static Bitmap contrast(Bitmap bitmap, float con){
		float brightness = 0;
		if(con>0.9999 && con<1.0001)
			brightness = 0;
		else if(con>1.0001)
			brightness = -(con-1)*100;
		else if(con<0.9999)
			brightness = (1-con)*200;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		ColorMatrix cm = new ColorMatrix();
		cm.set(new float[] { con, 0, 0, 0, brightness, 
				0,  con, 0, 0, brightness,
                0, 0, con, 0, brightness, 
                0, 0, 0, 1, 0 });  

        Paint p = new Paint();  
        p.setColorFilter(new ColorMatrixColorFilter(cm)); 
        Canvas c = new Canvas(bmp);
		c.drawBitmap(bitmap, 0, 0, p);
		return bmp;
	}


	/**
	 * 调节亮度。通过改变颜色矩阵第五列颜色分量调节亮度。
	 *
	 * @param bitmap
	 * 	位图。
	 * @param	bri
	 * 	bri=0保持当前亮度，bri>0增加亮度，bri<0减小亮度。
	 *
	 * @return
	 * 	处理后的位图。
	 * */
	public static Bitmap brightness(Bitmap bitmap, float bri){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		ColorMatrix cm = new ColorMatrix();
		cm.set(new float[] { 1, 0, 0, 0, bri, 
				0, 1,  0, 0, bri,
                0, 0, 1, 0, bri, 
                0, 0, 0, 1, 0 });
		Paint p = new Paint();
		p.setColorFilter(new ColorMatrixColorFilter(cm));
		Canvas c = new Canvas(bmp);
		c.drawBitmap(bitmap, 0, 0, p);
		return bmp;
	}


	/**
	 * 调节饱和度。
	 *
	 * @param	bitmap
	 * 	位图。
	 * @param	sat
	 * 	饱和度，0即黑白。1即保持不变。
	 *
	 * @return
	 * 	处理后的位图。
	 * */
	public static Bitmap saturation(Bitmap bitmap, float sat){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(sat);
		Paint p = new Paint();
		p.setColorFilter(new ColorMatrixColorFilter(cm));
		Canvas c = new Canvas(bmp);
		c.drawBitmap(bitmap, 0, 0, p);
		return bmp;
	}



	/**
	 * 位图模糊处理，通过缩小图片的方式加快模糊处理的速度。
	 * 缩放图片后可大大提高模糊的处理速度，可达1-10ms左右。
	 * 甚至有时候缩放所话费的时间大于模糊处理的时间。如果是经常使用的图片，先做好缩放，
	 * 然后缓存起来，之后使用fastBlur设置scaleFactor=1.
	 *
	 * @param	bitmap
	 * 	需要处理的位图。
	 * @param	scaleFactor
	 * 	缩小的倍数。
	 * @param	radius
	 * 	模糊半径。
	 *
	 * @see #blur(Bitmap, int, boolean)
	 *
	 * @return	处理后的位图。
	 * */
	public static Bitmap fastBlur(Bitmap bitmap, int scaleFactor, int radius){
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap overlay;
		if(1!=scaleFactor)
			overlay = resize(bitmap, (int)(w/scaleFactor), (int)(h/scaleFactor));
		else
			overlay = bitmap;
        return blur(overlay, (int)radius, false);
	}



	/**
	 * 图像模糊处理，采用高斯模糊和box模糊，效果比box模糊好看，性能比高斯模糊快速。
	 *
	 * @param	sentBitmap
	 * 	需要处理的bitmap。
	 * @param	radius
	 * 	模糊半径，数值越大，模糊效果越明显。
	 * @param	canReuseInBitmap
	 * 	是否可以更改传入的bitmap？？？有些bitmap不能更改，比如resouce里面的bitmap。
	 *
	 * @return	模糊后的bitmap。
	 * */
	public static Bitmap blur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}
