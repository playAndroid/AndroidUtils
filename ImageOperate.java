package geminihao.com.androidutils.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageOperate {

	public static Bitmap zoomImage(Bitmap bgimage) {

		return zoomImage(bgimage, 640, 960);

	}

	public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {

		if (bgimage != null) {
			int width = bgimage.getWidth();
			int height = bgimage.getHeight();

			Matrix matrix = new Matrix();
			float scaleWidth = 1.0f;
			float scaleHeight = 1.0f;
			if (newWidth < width) {
				scaleWidth = ((float) newWidth) / width;
			}
			if (newHeight < height) {
				scaleHeight = ((float) newHeight) / height;
			}
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
			return bitmap;
		} else {
			return null;
		}

	}

	public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {

		if (bytes != null)
			if (opts != null) {
				opts.inPreferredConfig= Config.RGB_565;
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			} else {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Config.RGB_565;
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);
			}
		return null;
	}

	public static byte[] getBytesFromInputStream(InputStream is, int bufsiz) throws IOException {
		int total = 0;
		byte[] bytes = new byte[4096];
		ByteBuffer bb = ByteBuffer.allocate(bufsiz);

		while (true) {
			int read = is.read(bytes);
			if (read == -1)
				break;
			bb.put(bytes, 0, read);
			total += read;
		}

		byte[] content = new byte[total];
		bb.flip();
		bb.get(content, 0, total);

		return content;
	}

}
