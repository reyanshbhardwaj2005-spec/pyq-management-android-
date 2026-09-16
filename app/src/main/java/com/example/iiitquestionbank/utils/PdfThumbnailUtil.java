package com.example.iiitquestionbank.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.IOException;

public class PdfThumbnailUtil {
    public static Bitmap getThumbnail(File pdfFile) {

        PdfRenderer renderer = null;
        PdfRenderer.Page page = null;
        ParcelFileDescriptor descriptor = null;

        try {

            descriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(descriptor);

            if (renderer.getPageCount() == 0) {
                return null;
            }

            page = renderer.openPage(0);
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.WHITE);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {

            if (page != null) {
                page.close();
            }

            if (renderer != null) {
                renderer.close();
            }

            if (descriptor != null) {
                try {
                    descriptor.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}