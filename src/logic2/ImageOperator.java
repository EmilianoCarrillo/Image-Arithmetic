package logic2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

import logic.*;

class Coordenada{
	public int x;
	public int y;
}

interface Operator<T, V>{
	int operate(int p1, int p2);
}

class Chunk extends Thread{
	public Coordenada cpos = new Coordenada();
	public int height;
	public int width;
	ImageData chunkData1, chunkData2;
	ImageData resultData;
	String operator;
	
	Chunk(int x, int y, ImageData chunkData1, ImageData chunkData2, String operator, ImageData resultData){
		this.operator = operator;
		cpos.x = x;
		cpos.y = y;
		height = chunkData1.height;
		width = chunkData1.width;
		this.chunkData1 = chunkData1;
		this.chunkData2 = chunkData2;
		this.resultData = resultData;
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
	
	 public void run() { 
	    System.out.println ("Thread " + Thread.currentThread().getId());
	    
	    int[] lineData1 = new int[width];
		int[] lineData2 = new int[width];
		
		
	    for (int y = 0; y < height; y++) {
	    	
	    	chunkData1.getPixels(0,y,width,lineData1,0);
	    	chunkData2.getPixels(0,y,width,lineData2,0);
	       
	    	for (int x = 0; x<width; x++){
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
	    		
	            resultData.setPixel(cpos.x+x, cpos.y+y, pixelResult.pixelValue);
	        }
	    }
	    
	    ImageLoader imageLoader = new ImageLoader();
	    imageLoader.data = new ImageData[] {resultData};
	    String workingDir = System.getProperty("user.dir");
	    imageLoader.save((workingDir+"/resultados/res" + Thread.currentThread().getId() +".jpg"), SWT.IMAGE_JPEG);
	 } 
	 
	 ImageData getResultChunkData() {
		 return resultData;
	 }
	 
}

public class ImageOperator {
	Image img1, img2;
	ImageData resultData = null;
	int chunkRows;
	int chunkCols;
	int chunkCounter; //rows x cols
	
	public ImageOperator(Image img1, Image img2) {
		this.img1 = img1;
		this.img2 = img2;
	}
	
	ImageData getChunkData(ImageData wholeData, int xInicio, int yInicio, int width, int height) {
		ImageData chunkData = new ImageData(width, height, wholeData.depth, wholeData.palette);
		
		int i = 0;
		int j = 0;
		
		for(int y = yInicio; y < height + yInicio; y++) {
			for(int x = xInicio; x < width + xInicio; x++) {
				chunkData.setPixel(i, j, wholeData.getPixel(x, y));
				i++;
			}
			i=0;
			j++;
		}
		
		return chunkData;
	}
	
	void buildResultData(int xInicio, int yInicio, ImageData chunkResultData) {
		for(int y = yInicio; y < chunkResultData.height + yInicio; y++) {
			for(int x = xInicio; x < chunkResultData.width + xInicio; x++) {
				resultData.setPixel(x, y, chunkResultData.getPixel(x, y));
			}
		}
	}
	
	public void operate(String operator, int rows, int cols) throws InterruptedException{
		
		chunkRows = Math.floorDiv(300, rows);
		chunkCols = Math.floorDiv(300, cols);
		chunkCounter = rows*cols;
		
		//Input Images
		ImageData dataPic1 = img1.getImageData();
		ImageData dataPic2 = img2.getImageData();
		
		// Resulting Image
		resultData = new ImageData(300, 300, dataPic1.depth, dataPic1.palette);
		
		// Chuncks data
		ImageData chunkData1 = null;
		ImageData chunkData2 = null;
	
		// CHUNKS **************************************************
		for(int y = 0; y < cols*chunkCols; y+=chunkCols) {
			for(int x = 0; x < rows*chunkRows; x+=chunkRows) {
				
				chunkData1 = getChunkData(dataPic1, x, y, chunkCols, chunkRows);
				chunkData2 = getChunkData(dataPic2, x, y, chunkCols, chunkRows);
			
				
				Chunk chunk = new Chunk(x, y, chunkData1, chunkData2, operator, resultData);
				chunk.start();
				chunk.join();
			}
		}
		
		// Save Image
	    ImageLoader imageLoader = new ImageLoader();
	    imageLoader.data = new ImageData[] {resultData};
	    String workingDir = System.getProperty("user.dir");
	    imageLoader.save(workingDir+"/resultados/res.jpg", SWT.IMAGE_JPEG);
		
		
	}

	public Image getResultingImage() {
		Display display = Display.getDefault();
		Image resultingImage = new Image(display, resultData);
		return resultingImage;
	}
	
	
}










