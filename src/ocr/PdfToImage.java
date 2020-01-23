package ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfToImage {

	public static List<String> listAllFiles(String directory, String extension) {
		// https://www.mkyong.com/java/java-how-to-list-all-files-in-a-directory/
		List<String> files = new ArrayList<String>();
		try (Stream<Path> walk = Files.walk(Paths.get(directory))) {
			// voir simplification si necessaire
			files = walk.map(x -> x.toString()).filter(f -> f.endsWith(extension)).collect(Collectors.toList());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return files;
	}

	public static List<BufferedImage> readAllImages(List<String> fnames) throws IOException {
		File imageFile;
		BufferedImage image;
		List<BufferedImage> images = new ArrayList<>();
		for (String fname : fnames) {
			imageFile = new File(fname);
			image = ImageIO.read(imageFile);
			image = PdfToImage.blackWhiteConvert(image);
			images.add(image);
		}
		return images;
	}

	public static BufferedImage blackWhiteConvert(BufferedImage image) {
		// Convertit une image en image en noir et blanc
		// TODO : voir recursivite
		int width = image.getWidth();
		int height = image.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (image.getRGB(x, y) < 128) {
					image.setRGB(x, y, 0);
				} else {
					image.setRGB(x, y, 255);
				}
			}
		}
		return image;
	}

	public static boolean isBlackWhite(BufferedImage image) {
		// verifie si une image est en noir et blanc
		// TODO : voir recursivite
		int width = image.getWidth();
		int height = image.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if ((image.getRGB(x, y) != 0) || (image.getRGB(x, y) != 255)) {
					return false;
				}
			}
		}
		return true;
	}

	public static List<BufferedImage> convertPagesToBWJPG(PDDocument document, int n) {
		// convertit chaque page d'un document pdf en image noir et blanc
		// retourne une array liste d'images
		ArrayList<BufferedImage> images = new ArrayList<>();
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		try {
			int pageCounter = 0;
			for (PDPage page : document.getPages()) {
				if (pageCounter == n) {
					return images;
				}
//				this.logger.info("page.getRotation() : " + page.getRotation());
//				this.logger.info("pageCounter : " + pageCounter);
				BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter++, 300, ImageType.BINARY); // BINARY =
																											// noir et
																											// blanc
				images.add(bim);
//				this.logger.info("Ajout numero" + pageCounter);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return images;

	}

	public static void saveOnDisk(List<BufferedImage> images, String originalFileDir) {
		// sauvegarde sur le disque les images

		int pageCounter = 0;
		try {
			for (BufferedImage img : images) {
				ImageIO.write(img, "JPEG", new File(originalFileDir + "img_" + pageCounter++ + ".jpg"));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
