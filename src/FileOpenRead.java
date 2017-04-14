

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileOpenRead {
	
	/*
	 * Функцията отваря файл, избран от потребителя и връща неговият абсолютен път 
	 */
	@SuppressWarnings("resource")
	public String OpenImage() {
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.jpg,*.jpeg,*.png,*.bmp", "jpg", "jpeg", "png", "bmp");
		chooser.setFileFilter(filter);
		chooser.setMultiSelectionEnabled(false);
		int option = chooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			String filePath = chooser.getSelectedFile().getAbsolutePath();		
			return filePath;
		}
		return null;
	}
}