package omr.opencv;


import java.util.ArrayList;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;



public class DetectCase {
	public static String dossier="E:\\OMR_ressources\\ImagesOmr\\";
	public static String imgfile ="test1.png";
	public static int espsilon = 5;

	public DetectCase() {

	}
	public static void run(String filename) {
		Mat originalImage = Imgcodecs.imread(filename);
		Mat imgres=originalImage;
		Mat imgSource = new Mat();
		Mat hierarchy = new Mat();
		Mat hierarchy2 = new Mat();
		Mat test = new Mat();
		Imgproc.cvtColor(originalImage, imgSource, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(imgSource, test, 30, 255,Imgproc.THRESH_BINARY_INV);
		Imgproc.Canny(imgSource, imgSource, 50, 50);
		Imgproc.Canny(test, test, 2, 3);
		//Imgproc.GaussianBlur(test, test, new Size(7, 7), 7);
 
		String filenameoutput2 = "result_clean.png";
		Imgcodecs.imwrite(filenameoutput2, imgSource);
		String filenameoutput3 = "result_test.png";
		Imgcodecs.imwrite(filenameoutput3, test);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> contourscoche = new ArrayList<MatOfPoint>();
		Imgproc.findContours(imgSource, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.findContours( test, contourscoche, hierarchy2,Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE) ;
		List<MatOfPoint> allCase = new ArrayList<MatOfPoint>();
		List<MatOfPoint> casecocher = new ArrayList<MatOfPoint>();
		allCase = contourisrect(hierarchy,contours,false);
		casecocher = contourisrect(hierarchy2,contourscoche,true);


		Imgproc.drawContours(imgres, allCase, -1, new Scalar(255,0,0));
		Imgproc.drawContours(imgres, casecocher, -1, new Scalar(0,0,255),Core.FILLED);

		String filenameoutput = "result.png";
		Imgcodecs.imwrite(filenameoutput, imgres);

	}



	public static boolean contien(List<MatOfPoint> liste ,MatOfPoint m) {
		Rect r=new Rect();
		Rect r2=new Rect();
		r2= Imgproc.boundingRect(m);
		for(MatOfPoint test : liste) {
			r= Imgproc.boundingRect(test);
			if(r.x > r2.x -espsilon && r.x < r2.x +espsilon && r.y< r2.y +espsilon && r.y> r2.y -espsilon) {
				return true;
			}
		}

		return false;
	}
	public static List<MatOfPoint> contourisrect(Mat hierarchy, List<MatOfPoint> contours,boolean f ){
		hierarchy.release();
		Rect r= new Rect();
		List<MatOfPoint> finnal = new ArrayList<MatOfPoint>();
		MatOfPoint m=new MatOfPoint();
		int l=0;
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		for (int i = 0; i < contours.size(); i++) {
			//Convert contours from MatOfPoint to MatOfPoint2f
			MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
			//Processing on mMOP2f1 which is in type MatOfPoint2f
			double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;


			//Find Polygons
			Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

			//Convert back to MatOfPoint
			MatOfPoint points = new MatOfPoint(approxCurve.toArray());


			//Rectangle Checks - Points, area, convexity,doublon
			if(f) {

				System.out.println(Imgproc.contourArea(contours.get(i)));

			}



			if (points.total() == 4 && Math.abs(Imgproc.contourArea(points)) < 1200 && Math.abs(Imgproc.contourArea(points)) > 500 && Imgproc.isContourConvex(points) && !contien(finnal,contours.get(i)) ) {
				r=Imgproc.boundingRect(contours.get(i));

				finnal.add(contours.get(i));
				l++;
				



			}


		}
		return finnal;
	}


	public static void main(String[] args) throws Exception {	
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String colle = dossier + imgfile;
		run(colle);
	}
}