import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class GuiBase extends JFrame {
	
	  JFrame frame = this;
	  CPanel displayPanel;
	  FileOpenRead fileOpenRead;
	  FileSaveClose fileSaveClose;
	  JButton sharpenButton, blurringButton, edButton, resetButton;

	  int resizeNewCount = 0;
	  
	  public GuiBase() throws IOException {
	    super();
	    Container container = getContentPane();
	    
	    displayPanel = new CPanel(null);
	    container.add(displayPanel);    
	    
	    JMenuBar menuBar = new JMenuBar();
		MenuBarProperties menuproperties = new MenuBarProperties(menuBar); // по-добре изглеждаща програма	
		this.setJMenuBar(menuBar);		
		JMenu mnFile = new JMenu("  File  ");
		menuBar.add(mnFile);
		fileOpenRead = new FileOpenRead();
		
		JMenuItem mntmOpenFile = new JMenuItem("Open File...");
		
		mntmOpenFile.addActionListener(new ActionListener() { // Изпълнява се когато потребителят избере "Open File..." от менюто
			public void actionPerformed(ActionEvent event) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							String filePath = fileOpenRead.OpenImage(); // взимаме абсолютният път на избрания файл
							if(filePath != null) {
								displayPanel.reConstruct(filePath); // подаваме го на панел, в който ще бъде обработван
								setSize(displayPanel.getWidth(), displayPanel.getHeight()); // Преоразмеряване прозореца спрямо размерите на изображението
							    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
							    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
							    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
							    frame.setLocation(x, y); // центрираме прозореца
								resizedNew(); // извикваме тази функция, иначе вероятно изображението ще бъде нарисувано върху бутоните...
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
			
		});
		
		mnFile.add(mntmOpenFile);	  	
		
		fileSaveClose = new FileSaveClose();

		JMenuItem mntmSaveImageAs = new JMenuItem("Save Image as...");
		
		mntmSaveImageAs.addActionListener(new ActionListener() {// Изпълнява се когато потребителят избере "Save Image as..." от менюто
			public void actionPerformed(ActionEvent event) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							fileSaveClose.saveImage(displayPanel.bi); // запазваме текущото изображение, като го подаваме на инстанцията fileSaveClose
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnFile.add(mntmSaveImageAs);		

	    JPanel panel = new JPanel();
	    panel.setLayout(new GridLayout(2, 2));
	    panel
	        .setBorder(new TitledBorder(
	            "Click a Button to Perform the Associated Operation and Reset..."));

	    sharpenButton = new JButton("Sharpen");
	    sharpenButton.addActionListener(new ButtonListener());
	    blurringButton = new JButton("Blur");
	    blurringButton.addActionListener(new ButtonListener());
	    edButton = new JButton("Edge Detect");
	    edButton.addActionListener(new ButtonListener());
	    resetButton = new JButton("Reset");
	    resetButton.addActionListener(new ButtonListener());

	    panel.add(sharpenButton);
	    panel.add(blurringButton);
	    panel.add(edButton);
	    panel.add(resetButton);

	    container.add(BorderLayout.SOUTH, panel);

	    addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	        System.exit(0);
	      }
	    });
	    
	    setSize(displayPanel.getWidth(), displayPanel.getHeight()); // преоразмеряваме прозореца
	    
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize(); 
	    int x = (int) ((dimension.getWidth() - (this).getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - (this).getHeight()) / 2);
	    (this).setLocation(x, y); // центрирамер прозореца
	    
	    setVisible(true); 
}
	  
	  class ButtonListener implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		      JButton button = (JButton) e.getSource();

		      if (button.equals(sharpenButton)) {
		        displayPanel.sharpen();
		        displayPanel.repaint();
		      } else if (button.equals(blurringButton)) {
		        displayPanel.blur();
		        displayPanel.repaint();
		      } else if (button.equals(edButton)) {
		        displayPanel.edgeDetect();
		        displayPanel.repaint();
		      } else if (button.equals(resetButton)) {
		        displayPanel.reset();
		        displayPanel.repaint();
		      }
		    }
		  }
	  
		public void resizedNew() { // предотвратяваме изображението да бъде нарисувано върху бутоните
			if(resizeNewCount == 0) {
				this.setSize(new Dimension(this.getWidth() + 1,this.getHeight() + 1));
				resizeNewCount++;
			}
			else {
				this.setSize(new Dimension(this.getWidth() - 1,this.getHeight() - 1));
				resizeNewCount--;
			}
		}
}


