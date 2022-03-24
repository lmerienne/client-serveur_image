package pdl.backend;
import boofcv.struct.image.GrayU8;

public class GrayLevelProcessing {

	public static void threshold(GrayU8 input, int t) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (gl < t) {
					gl = 0;
				} else {
					gl = 255;
				}
				input.set(x, y, gl);
			}
		}
	}

	public static void changeLum(GrayU8 input, int delta) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if(gl + delta > 255)
					input.set(x, y, 255);
				else if(gl + delta < 0)
					input.set(x, y, 0);
				else
					input.set(x, y, gl + delta);
			}
		}
	}

	public static void changeDyn(GrayU8 input) {
		int max = 0;
		int min = 255;
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if( max < gl)
					max = gl;
				if( min > gl)
					min = gl;
			}
		}
		System.out.println(max);
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				int n = (255 / (max - min)) * (gl - min);
				input.set(x,y,n);
			}
		}

		System.out.println("dynamic pas opti : " + System.nanoTime());
	}

	public static void changeDyn2(GrayU8 input,int max, int min ) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				int n = (255 / (max - min)) * (gl - min);
				input.set(x,y,n);
			}
		}

		System.out.println("dynamic opti bof : " + System.nanoTime());
	}

	public static int[] lut(GrayU8 input,int max, int min ) {
		int[] tab = new int[256];

		for (int i = 0; i < tab.length; i++) {
			int n = (255 / (max - min)) * (i - min);
			tab[i] = n;
		}
		return tab;
	}

	public static void changeDyn3(GrayU8 input,int max, int min ) {
		int[] tab = lut(input, max, min);
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				input.set(x, y, tab[gl]);
			}
		}
		System.out.println("dynamic opti max : " + System.nanoTime());
		
	}

	public static int[] calculHisto(GrayU8 input) {
		int[] tab = new int[256];
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				tab[gl] = tab[gl] + 1;
			}
		}
		return tab;
	}

	public static int histoCumul(GrayU8 input,int[] tab,int k) {
		int count = 0;
		for (int i = 0; i < k; i++) {
			count += tab[i];
		}
		return count;
	}

	public static void histo(GrayU8 input) {
		int[] tab = calculHisto(input);
		int N = input.height * input.width;
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				int value = (histoCumul(input,tab, gl) * 255) / N;
				input.set(x, y, value);
			}
		}
	}


/*
    public static void main( String[] args ) {

		// load image
		if (args.length < 2) {
		System.out.println("missing input or output image filename");
		System.exit(-1);
		}
		final String inputPath = args[0];
		GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
		if(input == null) {
		System.err.println("Cannot read input file '" + inputPath);
		System.exit(-1);
		}
	   
		// processing
		
		//Appel :

		//threshold(input, 128);
		//changeLum(input, 50);
		//changeDyn(input);
		//changeDyn2(input,255,0);
		//changeDyn3(input,255,0);
		//histo(input);
		
		
		
		// save output image
	   
		final String outputPath = args[1];
		UtilImageIO.saveImage(input, outputPath);
		System.out.println("Image saved in: " + outputPath);
		}
	   
	   
	   
*/
}