package omr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import config.Config;
import config.Groupe;
import config.Question;
import config.Reponse;
import copies.Copie;
import generation.pdf.CopiePdfImpl;
import generation.pdf.FusionCorrige;
import ocr.PdfToImage;

public class DetectCase {
	public static String dossier = "C:\\Users\\nairo\\eclipse-workspace\\opencv\\file\\";
	public static String imgfile = "FusionCorrige-1.png";
	public static int espsilon = 5;
	public static int espsilon2 = 7;

	public static List<Point> run(Mat m, int numpage) {
		Mat originalImage = m;
		Mat imgres = originalImage;
		Mat imgSource = new Mat();
		Mat hierarchy = new Mat();
		Mat hierarchy2 = new Mat();
		Mat test = new Mat();
		Imgproc.cvtColor(originalImage, imgSource, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(imgSource, test, 30, 255, Imgproc.THRESH_BINARY_INV);
		Imgproc.Canny(imgSource, imgSource, 50, 50);
		Imgproc.Canny(test, test, 2, 3);

//		String filenameoutput2 = "result_clean.png";
//		Imgcodecs.imwrite(filenameoutput2, imgSource);
//		String filenameoutput3 = "result_test.png";
//		Imgcodecs.imwrite(filenameoutput3, test);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> contourscoche = new ArrayList<MatOfPoint>();
		Imgproc.findContours(test, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.findContours(test, contourscoche, hierarchy2, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		List<MatOfPoint> allCase = new ArrayList<MatOfPoint>();
		List<MatOfPoint> casecocher = new ArrayList<MatOfPoint>();
		allCase = contourisrect(hierarchy, contours, false);
		casecocher = contourisrect(hierarchy2, contourscoche, true);

		List<MatOfPoint> finnal = new ArrayList<MatOfPoint>();

		for (MatOfPoint mat : allCase) {
			if (!contien(casecocher, mat)) {
				finnal.add(mat);
			}
		}
		Imgproc.drawContours(imgres, finnal, -1, new Scalar(0, 0, 255), Core.FILLED);
		String filenameoutput = "result" + numpage + ".png";
		Imgcodecs.imwrite(filenameoutput, imgres);
		List<Point> p = new ArrayList<Point>();
		// TODO : enlever la prochaine ligne
		// p.add(new Point(numpage,numpage));
		Rect r = new Rect();
		for (MatOfPoint matpoint : finnal) {
			r = Imgproc.boundingRect(matpoint);
			p.add(new Point(r.x, r.y));

		}
		return p;

	}

	public static boolean contien(List<MatOfPoint> liste, MatOfPoint m) {
		Rect r = new Rect();
		Rect r2 = new Rect();
		r2 = Imgproc.boundingRect(m);
		for (MatOfPoint test : liste) {
			r = Imgproc.boundingRect(test);
			if ((r.x > (r2.x - espsilon)) && (r.x < (r2.x + espsilon)) && (r.y < (r2.y + espsilon))
					&& (r.y > (r2.y - espsilon))) {
				return true;
			}
		}

		return false;
	}

	public static List<MatOfPoint> contourisrect(Mat hierarchy, List<MatOfPoint> contours, boolean f) {
		hierarchy.release();
		// Rect r= new Rect();
		List<MatOfPoint> finnal = new ArrayList<MatOfPoint>();
		// MatOfPoint m=new MatOfPoint();
		// int l=0;
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		for (int i = 0; i < contours.size(); i++) {
			// Convert contours from MatOfPoint to MatOfPoint2f
			MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
			// Processing on mMOP2f1 which is in type MatOfPoint2f
			double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;

			// Find Polygons
			Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

			// Convert back to MatOfPoint
			MatOfPoint points = new MatOfPoint(approxCurve.toArray());

			// Rectangle Checks - Points, area, convexity,doublon
			if (f) {

				// System.out.println(Imgproc.contourArea(contours.get(i)));

			}

			int max;
			int min;
			if (f) {
				min = 500;
				max = 1500;
			} else {
				max = 2100;
				min = 1500;
			}
			// Rect rect = new Rect();
			// rect = Imgproc.boundingRect(points);
			// TODO: enlever avant points.total() == 4
			if (/* rect.y>100 && */(points.total() == 4) && (Math.abs(Imgproc.contourArea(points)) < max)
					&& (Math.abs(Imgproc.contourArea(points)) > min) && Imgproc.isContourConvex(points)
					&& !contien(finnal, contours.get(i))) {
				// r=Imgproc.boundingRect(contours.get(i));

				finnal.add(contours.get(i));
				// l++;

			}

		}
		return finnal;
	}

	public static Mat img2Mat(BufferedImage in) {
		Mat out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
		byte[] data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
		int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
		for (int i = 0; i < dataBuff.length; i++) {
			data[i * 3] = (byte) ((dataBuff[i]));
			data[(i * 3) + 1] = (byte) ((dataBuff[i]));
			data[(i * 3) + 2] = (byte) ((dataBuff[i]));
		}
		out.put(0, 0, data);
		return out;
	}

	public static Map<Question, List<Reponse>> getAnsweredReponses(CopiePdfImpl pdf) throws IOException {
		Map<Question, List<Reponse>> result = new HashMap<>(); // association des questions avec leurs reponses cochees
		// on recupere le document
		PDDocument document = pdf.getPdDocument();

		List<BufferedImage> pdfImg = PdfToImage.convertPagesToBWJPG(document, document.getNumberOfPages());

		// on recupere la copie

		List<Mat> listeImage = new ArrayList<Mat>();

		Mat buff = new Mat();
		for (BufferedImage buf : pdfImg) {
			buff = img2Mat(buf);
			listeImage.add(buff);

		}
		Copie copie = pdf.getCopie();
		List<Question> questions = copie.getQuestions();

		int currentpage = -1;
		Mat currentMat = new Mat();
		List<Point> currentPoint = new ArrayList<Point>();

		for (Question q : questions) {
			// pour chaque question de la copie
			// System.out.println("-----------------------QUESTION : " + q.getTitre());
			List<Reponse> goodAnswers = new ArrayList<>();
			List<Reponse> reponses = q.getReponses();

			for (Reponse r : reponses) {
				if (currentpage != r.getNumPage()) {
					currentpage = r.getNumPage();
					currentMat = listeImage.get(r.getNumPage());
					currentPoint = run(currentMat, r.getNumPage());
				}

				// pour chaque reponse de la question
				// System.out.println("-----------REPONSE : " + r.getIntitule());

				Point point = r.getP();
				// System.out.print(point +"|");// on recupere ses coordonnees
				if (isSquareBlack(point, currentPoint, pdf, currentMat)) {
					// si la case est cochee
					goodAnswers.add(r); // on l'ajoute a la liste correspondant a la question
				}
			}
			result.put(q, goodAnswers); // on ajoute la question et ses reponses cochees
		}
		return result;
	}

	public static boolean isSquareBlack(Point n, List<Point> liste, CopiePdfImpl pdf, Mat image) {
		int x = ajustXCoords(n, pdf.getFormat(), image);
		int y = ajustYCoords(n, pdf.getFormat(), image) - 40;

		for (Point test : liste) {

			if ((x < (test.x + espsilon2)) && (x > (test.x - espsilon2)) && (y < (test.y + espsilon2))
					&& (y > (test.y - espsilon2))) {
				System.out.println(true);
				return true;
			}
		}
		System.out.println(false);
		return false;

	}

	public static Point ajustPointCoords(Point point, PDRectangle format, Mat img) {
		point.setLocation(ajustXCoords(point, format, img), ajustYCoords(point, format, img));
		return point;
	}

	public static int ajustXCoords(Point point, PDRectangle format, Mat img) {

		Size s = img.size();

		return (int) ((s.width * point.getX()) / format.getWidth());
	}

	public static int ajustYCoords(Point point, PDRectangle format, Mat img) {
		Size s = img.size();
		return (int) (((s.height)) - ((s.height * point.getY()) / format.getHeight()));
	}

	public static void main(String[] args) throws Exception {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String sourcePath = "./config/source.txt";
		Config config;
		Copie copie;
		CopiePdfImpl copiePdf;
		Random r = new Random();
		List<Question> listeQuestions = new ArrayList<>();
		config = Config.getInstance(sourcePath); // ne prend pas en compte le Singleton
		config.readConfig();
		for (Groupe g : config.getGroupes()) {
			List<Question> tempQuestions = (ArrayList<Question>) g.getQuestions().clone();

			while (tempQuestions.size() > g.getNbQtChoisie()) {
				tempQuestions.remove(r.nextInt(tempQuestions.size()));
			}
			listeQuestions.addAll(tempQuestions);
		}
		copie = new Copie(listeQuestions, "A4");
		copiePdf = new FusionCorrige(copie);
		copiePdf.generatePDF(config);

		// this.config = Config.getInstance(this.sourcePath);

		// TODO: voir seuils < 50
		Map<Question, List<Reponse>> result = new HashMap<>();
		for (Question q : copie.getQuestions()) {
			List<Reponse> goodAnswers = new ArrayList<>();
			for (Reponse re : q.getReponses()) {
				if (re.isJuste()) {
					goodAnswers.add(re);
				}
			}
			result.put(q, goodAnswers);
		}

		Map<Question, List<Reponse>> goodAnswers = result;
		copiePdf.save("./pdf/TestOMR/testOmrTemplate.pdf");

		Map<Question, List<Reponse>> answered;

		long debut = System.currentTimeMillis();
		answered = DetectCase.getAnsweredReponses(copiePdf);
		System.out.println((System.currentTimeMillis() - debut) + "ms");

		assertEquals(goodAnswers, answered);
	}
}