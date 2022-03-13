package pdl.backend;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;

import javax.lang.model.util.ElementScanner6;

import org.ddogleg.optimization.ConvergeOptParam;
import org.ejml.interfaces.SolveNullSpace;

import boofcv.alg.color.ColorHsv;
import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.factory.background.ConfigBackgroundBasic;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.border.BorderType;
import boofcv.struct.convolve.Kernel1D_S32;
import boofcv.struct.convolve.Kernel2D_S32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;



public class Color {

public static void changeLum(Planar<GrayU8> input, int delta) {
    //marche mais changer sortie main !! (mettre : image au lieu de output)
    for (int i = 0; i < input.getNumBands(); ++i){
      GrayLevelProcessing.changeLum(input.getBand(i), delta);
    }
}

public static void meanFilterSimple(Planar<GrayU8> input, Planar<GrayU8> output, int size) {
    for (int i = 0; i < input.getNumBands(); ++i){
      Convolution.meanFilterSimple(input.getBand(i), output.getBand(i), 3);
    }
}

public static void meanFilterWithBorders(Planar<GrayU8> input, Planar<GrayU8> output, int size, BorderType borderType) {
    for (int i = 0; i < input.getNumBands(); ++i){
      Convolution.meanFilterWithBorders(input.getBand(i), output.getBand(i), 3, BorderType.SKIP );
    }
}

public static void convolution(Planar<GrayU8> input, Planar<GrayU8> output, int[][] kernel) {
    for (int i = 0; i < input.getNumBands(); ++i){
      Convolution.convolution(input.getBand(i), output.getBand(i), kernel);
	}
}

public static void convertGrey (Planar<GrayU8> input, Planar<GrayU8> output){
	for (int y = 0; y < input.height; ++y) {
		for (int x = 0; x < input.width; ++x) {
			int valPix = 0;
			// Calcul nouvelle couleur
			for (int i = 0; i < input.getNumBands(); ++i){
				int gl = input.getBand(i).get(x, y);
				if(i == 0) 		//Rouge
					valPix += (int) gl * 0.3;
				else if (i == 1)//Vert
					valPix += (int) gl * 0.59;
				else 			//Bleu
					valPix += (int) gl * 0.11;
			}
			// Affectation pour chaque couleur du pixel
			for (int i = 0; i < input.getNumBands(); ++i){
				output.getBand(i).set(x, y, valPix);
			}
		}
	}
}


static float max(float r, float g, float b) {
	float max = 0;
	if (max < r) max = r;
	if (max < g) max = g;
	if (max < b) max = b;
	return max;
 }
static float min(float r, float g, float b) {
	float min = 1;
	if (min > r) min = r;
	if (min > g) min = g;
	if (min > b) min = b;
	return min;
 }

public static void rgbToHsv(int r, int g, int b, float[] hsv){
	// borne [0-1]
	float rf = r ;/// 255;
	float gf = g ;/// 255;
	float bf = b ;/// 255;
	float max = max(rf,gf,bf);
	float min = min(rf,gf,bf);
	float diff = max - min;
	///
	if(hsv.length == 3){
		//t
		float c = 0;
		if(max == min) hsv[0] = 0;
		else if (max == r){
			c = (gf - bf) / diff;
			hsv[0] = ((60 * c) + 360) % 360;
		}
		else if (max == g){
			c = (bf - rf) / diff;
			hsv[0] = ((60 * c) + 120) % 360;
		}
		else {
			c = (rf - gf) / diff;
			hsv[0] = ((60 * c) + 240) % 360;
		}
		//s
		hsv[1] = (1 - (min/max));
		if(max == 0) hsv[1] = 0;
		//v
		hsv[2] = max ;
		//System.out.println("h = " + hsv[0] + " || r = " + hsv[1] +  " || r = " + hsv[2]);
	}



}

public static void convertRgbToHsv (Planar<GrayU8> input, Planar<GrayU8> output){
	int r=0,g=0,b=0;
	for (int y = 0; y < input.height; ++y) {
		for (int x = 0; x < input.width; ++x) {
			r = input.getBand(0).get(x, y);
			g = input.getBand(1).get(x, y);
			b = input.getBand(2).get(x, y);
			float[] valPix = new float[3];
			rgbToHsv(r, g, b, valPix);
			for (int i = 0; i < input.getNumBands(); i++) {
				output.getBand(i).set(x, y, (int) (valPix[i]));
			}
		}
	}
}


public static int[] hsvToRgb(float h, float s, float v, int[] rgb){
	if(rgb.length == 3){
		float ti = (h/60) %6;
		float f = (h/60) * ti;
		float l = v * (1 - s);
		float m = v * (1 -(1 - f));
		float n = v * (1 -(1 - f) * s);

		if(ti == 0)
			rgb[0] = (int) v;rgb[1] = (int) n;rgb[2] = (int) l;
		if(ti == 1)
			rgb[0] = (int) m;rgb[1] = (int) v;rgb[2] = (int) l;
		if(ti == 2)
			rgb[0] = (int) l;rgb[1] = (int) v;rgb[2] = (int) n;
		if(ti == 3)
			rgb[0] = (int) l;rgb[1] = (int) m;rgb[2] = (int) v;
		if(ti == 4)
			rgb[0] = (int) n;rgb[1] = (int) l;rgb[2] = (int) v;
		if(ti == 5)
			rgb[0] = (int) v;rgb[1] = (int) l;rgb[2] = (int) m;
	}
	return rgb;
}

public static void convertHvsToRgb (Planar<GrayU8> input, Planar<GrayU8> output){
	int max = 0;
	int min = 255;
	// Calcul min / max
	for (int y = 0; y < input.height; ++y) {
		for (int x = 0; x < input.width; ++x) {
			for (int i = 0; i < input.getNumBands(); i++) {
				int gl = input.getBand(i).get(x, y);
				if(gl > max)
					max = gl;
				if(gl < min)
					min = gl;
			}
		}
	}
	int rgb[] = {0,0,0};
	
	int h=0,s=0,v=0;
	for (int y = 0; y < input.height; ++y) {
		for (int x = 0; x < input.width; ++x) {
			h = input.getBand(0).get(x, y);
			s = input.getBand(1).get(x, y);
			v = input.getBand(2).get(x, y);
			rgb = hsvToRgb(h, s, v, rgb);
			for (int i = 0; i < input.getNumBands(); i++) {
				int gl = rgb[i];
				output.getBand(i).set(x, y, gl);
			}
		}
	}
}
/*
  public static void main(final String[] args) {
	// load image
	if (args.length < 2) {
		System.out.println("missing input or output image filename");
		System.exit(-1);
	}
	final String inputPath = args[0];
	BufferedImage input = UtilImageIO.loadImage(inputPath);
	Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
    Planar<GrayU8> output = image.createSameShape();

	int[][] kernel = {{1,2,3,2,1},
					 {2,6,8,6,2},
					 {3,8,10,8,3},
					 {2,6,8,6,2},
					 {1,2,3,2,1}};

  	int[][] kernel1 = {{1,1,1,1,1},
					  {1,1,1,1,1},
					  {1,1,1,1,1},
					  {1,1,1,1,1},
					  {1,1,1,1,1}};
	
		
		//changeLum(image, 50);
		//convolution(image, output, kernel1);
		//convolution(image, output, kernel);
		//meanFilterSimple(image, output, 10);
		//convertGrey(image, output);
		//convertRgbToHsv(image, output);
		convertHvsToRgb(image, output);
		

		// save output image
		final String outputPath = args[1];
		UtilImageIO.saveImage(output, outputPath);
		System.out.println("Image saved in: " + outputPath);
	  }
*/
}
