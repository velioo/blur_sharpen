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
		  Image displayImage; // �������������, ����� �� ���� �����������

		  BufferedImage biSrc; // ��� �� ���� �������������, ����� �� �� ��������

		  BufferedImage biDest; // ������� ����������, ���� ����� �� �������� ������������

		  BufferedImage bi; // �������� ��, ���� reference �� ���� ��� ����������� �� ���� ���������� �� ������

		  Graphics2D big; // ��������� �����, ���� ����� �� ������
		  
		  
		  public int optimalImageWidth; // ����������, � ����� �� �� ������ �������������
		  public int optimalImageHeight; // ����������, � ����� �� �� ������ �������������
		  public double zoomLevel = 1; // ������ �� �������������
		  
		  int blurCount = 0; // ����� ���� ������������� � ���� blur-����
		  int sharpCount = 0; // ����� ���� ������������� � ���� sharp-����
		  int edgeCount = 0; // ����� ���� ������������� � ���� edge Detect-����

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
		   * ��������� ���������, ������ ������������ ������ ���� �����������.
		   * ������� �� ��������� ������
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
		   * ������������� �� ������� � �� ������� ��������� �������, � ����� �� �� ��������
		   *  mt - ���� ���� �� ����� �������������
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
		   * ������������� �� ������������, ����� �� ������ ������������ ����������� � �� �� ��������� ��� ������������.
		   * ��������� ����� �� ������������. 
		   * ���������� �� �� ������ �������������� �����������.
		   */
		  public void createBufferedImages() {			
		    biSrc = new BufferedImage(optimalImageWidth, optimalImageHeight, BufferedImage.TYPE_INT_RGB);
		    biDest = new BufferedImage(optimalImageWidth, optimalImageHeight, BufferedImage.TYPE_INT_RGB);	
		    big = biSrc.createGraphics();
		    big.drawImage(displayImage, 0, 0, optimalImageWidth, optimalImageHeight, this);
		  }

		  /*
		   * Sharpen �������.
		   * data - ����� �� �����������, � ����� �� �� ������� sharpen ���������
		   * kernel - ���������, � ����� �� ���� ���������� �������������
		   * convole - ���� ���� �� �������� ������������.
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
		   * Blur �������.
		   * data - ����� �� �����������, � ����� �� �� ������� blur ���������
		   * kernel - ���������, � ����� �� ���� ���������� �������������
		   * convole - ���� ���� �� �������� ������������.
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
		   * Edge Detect �������.
		   * data - ����� �� �����������, � ����� �� �� ������� Edge Detect ���������
		   * kernel - ���������, � ����� �� ���� ���������� �������������
		   * convole - ���� ���� �� �������� ������������.
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
		   * ������� �� ������ ������� ������
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
		   * ������� ��, ������ ������� �� ����������� (������ repaint() ��������� �� ������)
		   */
		  public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    Graphics2D g2D = (Graphics2D) g;
		    g2D.drawImage(bi, 0, 0, optimalImageWidth, optimalImageHeight, this);
		  }

		  
		 /*
		  * ������� ��, ������ �� ������� � �������. 
		  * ������������� �� ��������� ��������� � ��-������ ��� � ��-����� ������� ���� �� �������� �������� �� ������� ������.
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