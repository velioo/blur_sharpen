

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileSaveClose {

	/*
	 * Функцията създава нов файл на избрано от потребителя място в системата. 
	 * image - изображението, което ще бъде запазено
	 */
	public void saveImage(BufferedImage image) throws IOException {
		
		JFileChooser chooser = new JFileChooser(){
			private static final long serialVersionUID = 1L;

			@Override
		    public void approveSelection(){
		        File f = getSelectedFile();
		        if(f.exists() && getDialogType() == SAVE_DIALOG){
		            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
		            switch(result){
		                case JOptionPane.YES_OPTION:
		                    super.approveSelection();
		                    return;
		                case JOptionPane.NO_OPTION:
		                    return;
		                case JOptionPane.CLOSED_OPTION:
		                    return;
		                case JOptionPane.CANCEL_OPTION:
		                    cancelSelection();
		                    return;
		            }
		        }

		        super.approveSelection();
		    }        
		};
		
		
		chooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".jpg", "jpg");
		chooser.addChoosableFileFilter(filter);
		filter = new FileNameExtensionFilter(".jpeg", "jpeg");
		chooser.addChoosableFileFilter(filter);
		filter = new FileNameExtensionFilter(".png", "png");
		chooser.addChoosableFileFilter(filter);
		filter = new FileNameExtensionFilter(".bmp", "bmp");
		chooser.addChoosableFileFilter(filter);
		chooser.setMultiSelectionEnabled(false);
		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {		
			
				String splittedFileName [] = {"", ""};
				String temp;				
				String fFilter = chooser.getFileFilter().getDescription();
				String absolutePath;
				boolean fileRenamed = false;
				
				if(chooser.getSelectedFile().getName().contains(".")) {
					temp = chooser.getSelectedFile().getName();
					splittedFileName = temp.split("\\.");	
					
					temp = chooser.getSelectedFile().getAbsolutePath();
					String filePath = temp.substring(0,temp.lastIndexOf(File.separator));
					
					absolutePath = filePath + "\\" + splittedFileName[0] + fFilter;
					fileRenamed = true;
			   } else {
				   absolutePath = chooser.getSelectedFile().getAbsolutePath();
			   }
			
			 String extension = chooser.getFileFilter().getDescription().substring(1,chooser.getFileFilter().getDescription().length());
				
			 if(fileRenamed == true) {
				 ImageIO.write(image , extension, new File(absolutePath));
			 } else {
				 ImageIO.write(image , extension, new File(absolutePath + chooser.getFileFilter().getDescription()));
			 }
			 
				
		}
	}
}
