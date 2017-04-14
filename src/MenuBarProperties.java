

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class MenuBarProperties {
	public MenuBarProperties(JMenuBar menuBar) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			menuBar.setOpaque(true);
			menuBar.setUI(new BasicMenuBarUI() {
				public void paint(Graphics g, JComponent c) {
					g.setColor( new Color(223,228,244));
					g.fillRect(0, 0, c.getWidth(), c.getHeight());
				}
			});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
