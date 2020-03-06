package logic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

class argb{
	public int alpha;
	public int red;
	public int green;
	public int blue;
	public int pixelValue;
	
	public argb(int pv) {
		alpha= (pv >> 24) & 0x000000FF;
        red = (pv >> 16) & 0x000000FF;
        green = (pv >>8 ) & 0x000000FF;
        blue = (pv) & 0x000000FF;
	}
	
	public void buildPixelValue() {
		pixelValue = (alpha<<24) | (red<<16) | (green<<8) | (blue);
	};

	public argb() {}
};

interface Operator<T, V>{
	int operate(int p1, int p2);
}

public class ImageOperator {
	Image img1;
	Image img2;
	Image imgR;
	String operator;
	
	public ImageOperator(Image img1, Image img2) {
		this.setImages(img1, img2);
	}
	public void setImages(Image img1, Image img2) {
		this.img1 = img1;
		this.img2 = img2;
	}
	public Image getResultingImage() {
		return imgR;
	}
	
	public void operate(String operator) {
		//Input Images
		ImageData dataPic1 = img1.getImageData();
		ImageData dataPic2 = img2.getImageData();
		
		// Resulting Image
		Display display = Display.getDefault();
		Image resultingImage = new Image(display, img1.getBounds());
		ImageData dataResultingImage = resultingImage.getImageData();
		
		int[] lineData1 = new int[dataPic1.width];
		int[] lineData2 = new int[dataPic2.width];
		
		int height = dataPic1.height;
		int width = dataPic1.width;		
		
	    for (int y = 0; y < height; y++) {
	    	
	    	dataPic1.getPixels(0,y,width,lineData1,0);
	    	dataPic2.getPixels(0,y,width,lineData2,0);
	       
	    	for (int x=0; x<width; x++){
	            int pixelValue1 = lineData1[x];
	            int pixelValue2 = lineData2[x];
	            
	            argb p1 = new argb(pixelValue1);
	            argb p2 = new argb(pixelValue2);
	            
	            argb pixelResult = new argb();
	            
	            Operator<Integer, Integer> op = null;
	            
	            // Change behavior depending on the type of operator
	            switch(operator) {
	    		case "+":
	    			op = this::addPixels;
	    			break;
	    		case "-":
	    			op = this::substractPixels;
	    			break;
	    		case "*":
	    			op = this::multiplyPixels;
	    			break;
	    		case "#":
	    			op = this::combinePixels;
	    			break;
	    		}
	            
	            pixelResult.alpha = op.operate(p1.alpha,p2.alpha);
	    		pixelResult.red = op.operate(p1.red,p2.red);
	    		pixelResult.green = op.operate(p1.green,p2.green);
	    		pixelResult.blue = op.operate(p1.blue,p2.blue);
	            pixelResult.buildPixelValue();
	    		
	            dataResultingImage.setPixel(x,y, pixelResult.pixelValue);
	           
	        }
	    }
	    
	    // Save Image
	    imgR = new Image(Display.getDefault(), dataResultingImage);
	    ImageLoader imageLoader = new ImageLoader();
	    imageLoader.data = new ImageData[] {dataResultingImage};
	    imageLoader.save("/Users/emilianocarrillo/Desktop/res.jpg", SWT.IMAGE_JPEG);
		
	}
	
	public int addPixels(int pa,int pb) {
		return Math.min(255, (pa+pb));	
	}
	
	public int substractPixels(int pa,int pb) {
		return 255 / 2 + (pa-pb) / 2;	
	}	
	
	public int multiplyPixels(int pa,int pb) {
		int max = 255*255;
		int res = (int)Math.floor((pa*pb)*255/max);
		return 	res;
	}
	
	public int combinePixels(int pa,int pb) {
		double a = 0.8;
		double b = 1 - a;
		double max = a*b*255*255;
		int res = (int)Math.floor((a*pa*b*pb)*255/max);
		return res;	
	}
	
}
