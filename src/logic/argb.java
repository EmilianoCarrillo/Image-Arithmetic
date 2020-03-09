package logic;

public class argb {
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
