import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

class CPanel extends JLabel implements MouseWheelListener	{
		  Image displayImage; // Изображението, което ще бъде обработвано

		  BufferedImage biSrc; // Тук се пази изображението, което ще се филтрира

		  BufferedImage biDest; // Помощна променлива, чрез която се извършва филтрирането

		  BufferedImage bi; // Използва се, като reference за това кое изображение ще бъде нарисувано на екрана

		  Graphics2D big; // Графичния обект, чрез който се рисува
		  
		  
		  public int optimalImageWidth; // Широчината, с която ще се рисува изображението
		  public int optimalImageHeight; // Височината, с която ще се рисува изображението
		  public double zoomLevel = 1; // Мащаба на изображението
		  
		  int blurCount = 0; // Колко пъти изображението е било blur-нато
		  int sharpCount = 0; // Колко пъти изображението е било sharp-нато
		  int edgeCount = 0; // Колко пъти изображението е било edge Detect-нато

		  CPanel(String filePath) throws IOException {
		    setBackground(Color.black);
		    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		    optimalImageWidth = (int) screenSize.getWidth()/2;
		    optimalImageHeight = (int) screenSize.getHeight()/2;
		    if(filePath != null) {
			    loadImage(filePath);
			    setSize(optimalImageWidth + 15, optimalImageHeight + 115); 
			    createBufferedImages();
			    addMouseWheelListener(this);
			    bi = biSrc;
			    blurCount = 0; 
				sharpCount = 0; 
				edgeCount = 0;
		    } else {
		    	setSize(optimalImageWidth, optimalImageHeight); 
		    }
		  }

		  /*
		   * Извикваме функцията, когато потребителят отвори ново изображение.
		   * Нулират се промените досега
		   */
		  public void reConstruct(String filePath) throws IOException {
		    setBackground(Color.black);
		    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		    optimalImageWidth = (int) screenSize.getWidth()/2;
		    optimalImageHeight = (int) screenSize.getHeight()/2;
		    if(filePath != null) {
			    loadImage(filePath);
			    setSize(optimalImageWidth + 15, optimalImageHeight + 115); 
			    createBufferedImages();
			    addMouseWheelListener(this);
			    bi = biSrc;
			    blurCount = 0; 
				sharpCount = 0; 
				edgeCount = 0;
		    } else {
		    	setSize(optimalImageWidth, optimalImageHeight); 
		    }
		  }
		  
		  /*
		   * Изображението се зарежда и се избират подходящи размери, с които да се нарисува
		   *  mt - чрез него се следи изображението
		   */
		  public void loadImage(String filePath) throws IOException {
			File sourceimage = new File(filePath);
			displayImage = ImageIO.read(sourceimage);
		    MediaTracker mt = new MediaTracker(this);
		    mt.addImage(displayImage, 1);
		    try {
		      mt.waitForAll();
		    } catch (Exception e) {
		      System.out.println("Exception while loading.");
		    }
		    
		    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		    double width = screenSize.getWidth();
		    double height = screenSize.getHeight();
		    
		    optimalImageWidth = (int) (displayImage.getWidth(this));
		    optimalImageHeight = (int) (displayImage.getHeight(this));
		    
		    if((displayImage.getWidth(this) > (width - width/4)) || (displayImage.getHeight(this) > (height - height/4))) {
			    while((optimalImageWidth > (width - width/4)) || (optimalImageHeight > (height - height/4))) {		    	
				    optimalImageWidth = (int) (displayImage.getWidth(this) * zoomLevel);			    
				    optimalImageHeight = (int) (displayImage.getHeight(this) * zoomLevel);			    
				    zoomLevel-=0.01;
			    }
		    }
		    
		    if (displayImage.getWidth(this) == -1) {
		      System.out.println("No jpg file");
		      System.exit(0);
		    }
		  }

		  /*
		   * Инициализират се променливите, които ще държат филтрираното изображение и ще се използват във филтрирането.
		   * Графичния обект се инициализира. 
		   * Изобразява се на екрана първоначалното изображение.
		   */
		  public void createBufferedImages() {			
		    biSrc = new BufferedImage(optimalImageWidth, optimalImageHeight, BufferedImage.TYPE_INT_RGB);
		    biDest = new BufferedImage(optimalImageWidth, optimalImageHeight, BufferedImage.TYPE_INT_RGB);	
		    big = biSrc.createGraphics();
		    big.drawImage(displayImage, 0, 0, optimalImageWidth, optimalImageHeight, this);
		  }

		  /*
		   * Sharpen филтъра.
		   * data - пазят се стойностите, с които ще се създаде sharpen матрицата
		   * kernel - матрицата, с която ще бъде филтрирано изображението
		   * convole - Чрез него се извършва филтрирането.
		   */
		  public void sharpen() {
		    float data[] = { -1.0f, -1.0f, -1.0f, -1.0f, 9.0f, -1.0f, -1.0f, -1.0f,
		        -1.0f };
		    Kernel kernel = new Kernel(3, 3, data);
		    ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
		        null);
		    biSrc = convolve.filter(biSrc, biDest);
		    bi = biSrc;		    
		    biDest = new BufferedImage(optimalImageWidth, optimalImageHeight, BufferedImage.TYPE_INT_RGB);
		    sharpCount++;
		  }

		  /*
		   * Blur филтъра.
		   * data - пазят се стойностите, с които ще се създаде blur матрицата
		   * kernel - матрицата, с която ще бъде филтрирано изображението
		   * convole - Чрез него се извършва филтрирането.
		   */
		  public void blur() {
		    float data[] = { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f,
		        0.0625f, 0.125f, 0.0625f };
		    Kernel kernel = new Kernel(3, 3, data);
		    ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
		        null);
		    biSrc = convolve.filter(biSrc, biDest);
		    bi = biSrc;
		    biDest = new BufferedImage(optimalImageWidth, optimalImageHeight, BufferedImage.TYPE_INT_RGB);
		    blurCount++;
		  }

		  /*
		   * Edge Detect филтъра.
		   * data - пазят се стойностите, с които ще се създаде Edge Detect матрицата
		   * kernel - матрицата, с която ще бъде филтрирано изображението
		   * convole - Чрез него се извършва филтрирането.
		   */
		   public void edgeDetect() {
		    float data[] = { 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f,
		       -1.0f };

		    Kernel kernel = new Kernel(3, 3, data);
		    ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
		       null);
		    biSrc = convolve.filter(biSrc, biDest);
		    bi = biSrc;
		    biDest = new BufferedImage(optimalImageWidth, optimalImageHeight, BufferedImage.TYPE_INT_RGB);
		    edgeCount++;
		   }

		  /*
		   * Нулират се всички промени досега
		   */
		  public void reset() {
		    big.setColor(Color.black);
		    big.clearRect(0, 0, bi.getWidth(this), bi.getHeight(this));
		    big.drawImage(displayImage, 0, 0, optimalImageWidth, optimalImageHeight, this);
		    createBufferedImages();
		    bi = biSrc;
		    sharpCount = 0;
		    blurCount = 0;
		    edgeCount = 0;
		  }

		  public void update(Graphics g) {
		    g.clearRect(0, 0, getWidth(), getHeight());
		    paintComponent(g);
		  }
		  
		  /*
		   * Извиква се, когато панелът се пребоядисва (когато repaint() функцията се извика)
		   */
		  public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    Graphics2D g2D = (Graphics2D) g;
		    g2D.drawImage(bi, 0, 0, optimalImageWidth, optimalImageHeight, this);
		  }

		  
		 /*
		  * Извиква се, когато се скролва с мишката. 
		  * Изображението се прерисува съответно с по-големи или с по-малки размери като се запазват всичките му сложени филтри.
		  */
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (notches < 0) { 
				if(zoomLevel <= 2) {
					zoomLevel+=0.01;
				}
	        } else {
	        	if(zoomLevel >= 0.2) {
	        		zoomLevel-=0.01;	
	        	}
	        }					
						
			optimalImageWidth = (int) (displayImage.getWidth(this) * zoomLevel);			    
		    optimalImageHeight = (int) (displayImage.getHeight(this) * zoomLevel);	

		    int tempSharp = sharpCount;
		    int tempBlur = blurCount;
		    int tempEdge = edgeCount;
		    
		    reset();	    
		   
		    for(int i = 0; i < tempSharp; i++) {
		    	sharpen();
		    }
		    
		    for(int i = 0; i < tempBlur; i++) {
		    	blur();
		    }
		    
		    for(int i = 0; i < tempEdge; i++) {
		    	edgeDetect();
		    }
		    
		    this.repaint();
		}		
		  
	}