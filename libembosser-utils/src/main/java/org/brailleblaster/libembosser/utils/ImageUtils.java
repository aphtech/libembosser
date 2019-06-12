package org.brailleblaster.libembosser.utils;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.util.Arrays;

public final class ImageUtils {
	/**
	 * Compare two images to check the pixels are the same.
	 * @param img1 The first image to compare.
	 * @param img2 The second image to compare.
	 * @return True if the pixels of the images are identical.
	 */
	public final static boolean imageEquals(Image img1, Image img2) {
		if (img1 == img2) {
			return true;
		}
		if (img1 == null || img2 == null) {
			return false;
		}
		int[] img1Data = null;
		int[] img2Data = null;
		int width1 = 0, width2 = 0, height1 = 0, height2 = 0;
		try {
			PixelGrabber pg = new PixelGrabber(img1, 0, 0, -1, -1, true);
			if (pg.grabPixels()) {
				width1 = pg.getWidth();
				height1 = pg.getHeight();
				img1Data = (int[])pg.getPixels();
			}
			pg = new PixelGrabber(img2, 0, 0, -1, -1, true);
			if (pg.grabPixels()) {
				width2 = pg.getWidth();
				height2 = pg.getHeight();
				img2Data = (int[])pg.getPixels();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Problem getting image pixels to compare images", e);
		}
		return (width1 == width2) && (height1 == height2) && Arrays.equals(img1Data, img2Data);
	}
}
