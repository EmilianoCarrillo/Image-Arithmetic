package logic3;
import interfaz.MainWindow;
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
	int myId;
	public int height;
	public int width;
	ImageData chunkData1, chunkData2;
	ImageData resultData;
	String operator;
	Lock lock;
	double coeficient;
	public static int chunkCounter;
	public static boolean[][] chunkMatrix;
	public static int rows=0,cols=0;
	
	Chunk(int id,int x, int y, ImageData chunkData1, ImageData chunkData2, String operator, ImageData resultData, Lock lock,double coef){
		this.operator = operator;
		cpos.x = x;
		cpos.y = y;
		height = chunkData1.height;
		width = chunkData1.width;
		this.chunkData1 = chunkData1;
		this.chunkData2 = chunkData2;
		this.resultData = resultData;
		this.lock = lock;
		this.coeficient=coef;
		this.myId=id;
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
		double a = coeficient;
		double b = 1 - a;
		double max = a*b*255*255;
		int res = (int)Math.floor((a*pa*b*pb)*255/max);
		return res;	
	}
	public void nonCriticalRegion() throws InterruptedException
	{
		System.out.println(myId + " no esta en la CR" );
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] {resultData};
		String workingDir = System.getProperty("user.dir");
		imageLoader.save((workingDir+"/resultados/res" + Thread.currentThread().getId() +".jpg"), SWT.IMAGE_JPEG);
		Image label=MainWindow.imagen3.getImage();
		ImageData dataLabel=label.getImageData();
		ImageData chunkDataLabel = new ImageData(width, height, dataLabel.depth, dataLabel.palette);
		
		for (int y = 0; y < height; y++) {
	    	for (int x = 0; x<width; x++){
	    		chunkDataLabel.setPixel(cpos.x+x, cpos.y+y, resultData.getPixel(x, y));
	        }
	    }
		Display display = Display.getDefault();
		Image resultingImage = new Image(display, chunkDataLabel);
		MainWindow.imagen3.setImage(resultingImage);
		Thread.sleep(1000);
	}
	public void CriticalRegion() throws InterruptedException
	{
		System.out.println(myId + " está en la CR . . ." );
		// Aquí va el código que trabaja con el recurso compartido
		
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
	    
		//Thread.sleep(1000);
	}
	 public void run() 
	 { 
		while(true) 
		{
			if(chunkCounter==0)
				return;
			lock.requestCR(myId);
			int r=-1;
			int c=-1;
			try {
				
				for(int i=0;i<rows;i++)
				{
					for(int j=0;j<cols;j++)
					{
						if(!chunkMatrix[i][j])
						{
							r=i;
							c=j;
							break;
						}
					}
					if(r!=-1 || c!=-1)
						break;
				}
				if(r==-1 || c==-1)
					return;
				chunkMatrix[r][c]=true;
				chunkCounter--;
				CriticalRegion();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			lock.releaseCR(myId);
			
			try {
				nonCriticalRegion();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	
	double coeficient;

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
	
	public void operate(String operator, int rows, int cols, double coef) throws InterruptedException{
		Chunk.chunkMatrix = new boolean[rows][cols];
		coeficient=coef;
		int TOTAL_WIDTH=300;
		int TOTAL_HEIGHT=300;
		
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < cols; x++) {
				Chunk.chunkMatrix[y][x] = false;
			}
		}
		
		
		chunkRows = Math.floorDiv(TOTAL_HEIGHT, rows);
		chunkCols = Math.floorDiv(TOTAL_WIDTH, cols);
		chunkCounter = rows*cols;
		Chunk.chunkCounter=this.chunkCounter;
		Chunk.rows=rows;
		Chunk.cols=cols;
		
		//Input Images
		ImageData dataPic1 = img1.getImageData();
		ImageData dataPic2 = img2.getImageData();
		
		// Resulting Image
		resultData = new ImageData(TOTAL_WIDTH, TOTAL_HEIGHT, dataPic1.depth, dataPic1.palette);
		
		// Chunks data
		ImageData chunkData1 = null;
		ImageData chunkData2 = null;
		
		// LOCK
		Lock lock = new Bakery(chunkCounter);
		int contThreads=0;
		// CHUNKS **************************************************
		for(int y = 0; y < rows*chunkRows; y+=chunkRows) {
			for(int x = 0; x < cols*chunkCols; x+=chunkCols) {
				
				int chunkRowstmp=chunkRows;
				int chunkColstmp=chunkCols;
				if((y+1)/chunkRows==rows-1) 
					chunkRowstmp=TOTAL_HEIGHT-y;
				
				if((x+1)/chunkCols==cols-1) 
					chunkColstmp=TOTAL_WIDTH-x;
			
				chunkData1 = getChunkData(dataPic1, x, y, chunkColstmp, chunkRowstmp);
				chunkData2 = getChunkData(dataPic2, x, y, chunkColstmp, chunkRowstmp);
				
				Chunk chunk = new Chunk(contThreads,x, y, chunkData1, chunkData2, operator, resultData, lock,coeficient);
				contThreads++;
				chunk.start();
				
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










