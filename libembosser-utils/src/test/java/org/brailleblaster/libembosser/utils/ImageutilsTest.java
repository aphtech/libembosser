package org.brailleblaster.libembosser.utils;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.testng.annotations.Test;

public class ImageutilsTest {
	@Test
	public void testSameImagesEquals() throws IOException {
		Image img1 = ImageIO.read(getClass().getResourceAsStream("/org/brailleblaster/libembosser/utils/images/image1.png"));
		Image img2 = ImageIO.read(getClass().getResourceAsStream("/org/brailleblaster/libembosser/utils/images/image1.png"));
		assertTrue(ImageUtils.imageEquals(img1, img2));
	}
	@Test
	public void testSameObjectsEquals() throws IOException {
		Image img1 = ImageIO.read(getClass().getResourceAsStream("/org/brailleblaster/libembosser/utils/images/image1.png"));
		assertTrue(ImageUtils.imageEquals(img1, img1));
	}
	@Test
	public void testDifferentImagesNotEquals() throws IOException {
		Image img1 = ImageIO.read(getClass().getResourceAsStream("/org/brailleblaster/libembosser/utils/images/image1.png"));
		Image img2 = ImageIO.read(getClass().getResourceAsStream("/org/brailleblaster/libembosser/utils/images/image2.png"));
		assertFalse(ImageUtils.imageEquals(img1, img2));
	}
}
