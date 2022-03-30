package pdl.backend;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;

public class Convolution {

  public static void meanFilterSimple(GrayU8 input, GrayU8 output, int size) {
    int d = size/2;
    for (int y = d; y < input.height-d; ++y) {
			for (int x = d; x < input.width-d; ++x) {
        int r = 0;
        for (int u = -d; u <= d; ++u) {
          for (int v = -d; v <= d; ++v) {
            r = r + input.get(x+u, y+v);
          }
        }
				output.set(x, y, r/(size*size));
			}
		}
  }

  public static Boolean checkMax(int u, int umax, int v, int vmax){
    return (u>=0 && u<=umax && v>=0 && v<=vmax);
  }

  public static int checkInside(int x, int max){
    if(x < 0)
      return 0;
    if(x > max)
      return max;
    return x;
  }

  public static int reflect(int x, int max){
    if(x < 0)
      return -x;
    if(x > max)
      return max-(x - max);
    return x;
  }
public static void negatif(GrayU8 input, GrayU8 output){
  for (int x=0; x<input.getWidth(); x++){
    for (int y = 0; y < input.height; y++) {
      int r=input.get(x, y);
      output.set(x, y, 255-r);
    }
  }
}
public static void mirorHor(GrayU8 input, GrayU8 output){
  for (int x=0,j=input.width-1; x<input.getWidth(); x++, j--){
    for (int y = 0,k=input.height-1; y < input.height; y++,k--) {
      int r=input.get(x, y);
      output.set(j, k, r);
    }
  }
}
public static void mirorVer(GrayU8 input, GrayU8 output){
  for (int x=0,j=input.width-1; x<input.getWidth(); x++, j--){
    for (int y = 0,k=input.height-1; y < input.height; y++,k--){
      int r=input.get(x, y);
      //int m= input.get(j, y);
      //output.set(j, y, r);
      output.set(j, y, r);
    }
  }
}
  public static void meanFilterWithBorders(GrayU8 input, GrayU8 output, int size, BorderType borderType) {
  
    if(borderType == BorderType.SKIP){
      meanFilterSimple(input, output, size);
    }else{
      
      int d = size/2;

      for (int y = 0; y < input.height; ++y) {
		  	for (int x = 0; x < input.width; ++x) {
          int r = 0;

          for (int u = -d; u <= d; ++u) {
            for (int v = -d; v <= d; ++v) {

              if(borderType == BorderType.NORMALIZED){
                if(checkMax(x+u,input.width-1,y+v,input.height-1))
                  r = r + input.get(x+u, y+v);

              }else if(borderType == BorderType.EXTENDED){
                int w = checkInside(x+u, input.width-1);
                int h = checkInside(y+u, input.height-1);
                r = r + input.get(w, h);

              }else if(borderType == BorderType.REFLECT){
                int w = reflect(x+u, input.width-1);
                int h = reflect(y+u, input.height-1);
                r = r + input.get(w, h);
              }
            }
          }
				  output.set(x, y, r/(size*size));
			  }
		  }
    }
  }
  
  public static void convolution(GrayU8 input, GrayU8 output, int[][] kernel) {
    int size = kernel.length;
    int d = size/2;

    int coef = 0;
    for (int i = 0; i < kernel.length; i++) {
      for (int j = 0; j < kernel.length; j++) {
        coef += kernel[i][j];
      }
    }

    if(coef == 0){
      coef = 1;
    }
    for (int y = d; y < input.height-d; ++y) {
			for (int x = d; x < input.width-d; ++x) {
        int r = 0;
        for (int u = -d; u <= d; ++u) {
          for (int v = -d; v <= d; ++v) {
            r = r + input.get(x+u, y+v) * kernel[u+d][v+d] ;
          }
        }
        int valPix = r / coef;
        if(valPix <0)
				  output.set(x, y, 0);
        else if(valPix >255)
          output.set(x, y, 255);
        else
          output.set(x, y, valPix);
          

			}
		}
  }

  public static void gradientImageSobel(GrayU8 input, GrayU8 output){
    int[][] sobelHorizontal = {{-1,0,1},{-2,0,2},{-1,0,1}};
    int[][] sobelVertical = {{-1,-2,-1},{0,0,0},{1,2,1}};

    GrayU8 Gx = input.createSameShape();
    GrayU8 Gy = input.createSameShape();

    convolution(input, Gx, sobelHorizontal);
    convolution(input, Gy, sobelVertical);
    
    for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
        int G = (int) Math.sqrt(Gx.get(x,y)*Gx.get(x,y) + Gy.get(x,y)*Gy.get(x,y) );
        output.set(x, y, G);
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
    GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
    GrayU8 output = input.createSameShape();

    //meanFilterWithBorders(input,output,10,BorderType.SKIP);
    //meanFilterSimple(input,output,5);
    int[][] kernel = {{1,2,3,2,1},
                      {2,6,8,6,2},
                      {3,8,10,8,3},
                      {2,6,8,6,2},
                      {1,2,3,2,1}};

    int[][] kernel1 = {{1,1,1},
                      {1,1,1},
                      {1,1,1}};

    
    // Appel 

    //meanFilterSimple(input, output, 3);
    //meanFilterWithBorders(input, output, 3, BorderType.SKIP);
    //meanFilterWithBorders(input, output, 3, BorderType.NORMALIZED);
    //meanFilterWithBorders(input, output, 3, BorderType.EXTENDED);
    //meanFilterWithBorders(input, output, 3, BorderType.REFLECT);
    //convolution(input, output, kernel1);
    //convolution(input, output, kernel);
    //gradientImageSobel(input, output);
    

    // save output image
    
    final String outputPath = args[1];
    UtilImageIO.saveImage(output, outputPath);
    System.out.println("Image saved in: " + outputPath);
  }
*/
}