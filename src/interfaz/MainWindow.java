package interfaz;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import logic.ImageOperator;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public class MainWindow {

	/**
	 * Launch the application.
	 * @param args
	 */
	static Display display = Display.getDefault();
	static Shell shlOperacionesConImagenes = new Shell();
	private static  Image resize(Image image, int width, int height) 
	{
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0,
		image.getBounds().width, image.getBounds().height,
		0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}
	protected static void showInfoDialog(String title,String message){
		  MessageBox mb=new MessageBox(shlOperacionesConImagenes,SWT.OK | SWT.ICON_INFORMATION);
		  mb.setText(title);
		  mb.setMessage(message);
		  mb.open();
	}
	public static void main(String[] args) {
		
		shlOperacionesConImagenes.setSize(832, 566);
		shlOperacionesConImagenes.setText("Operaciones con imagenes");
		
		Label imagen1 = new Label(shlOperacionesConImagenes, SWT.BORDER);
		imagen1.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		imagen1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		imagen1.setBounds(21, 31, 350, 300);
		imagen1.setText("imagen1");
		
		Image imagen = new Image(display, "/Users/emilianocarrillo/Desktop/imgs/4a.png");
    	imagen=resize(imagen,imagen1.getBounds().width,imagen1.getBounds().height);
    	imagen1.setImage(imagen);
		
		Label imagen2 = new Label(shlOperacionesConImagenes, SWT.BORDER);
		imagen2.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		imagen2.setText("imagen2");
		imagen2.setBounds(456, 31, 350, 300);
		
		imagen = new Image(display, "/Users/emilianocarrillo/Desktop/imgs/5b.png");
    	imagen=resize(imagen,imagen1.getBounds().width,imagen1.getBounds().height);
    	imagen2.setImage(imagen);
		
		Combo combo = new Combo(shlOperacionesConImagenes, SWT.NONE);
		combo.setItems(new String[] {"+", "-", "*", "#"});
		combo.setBounds(387, 179, 51, 23);
		combo.setText("Operator");
		combo.select(0);
		
		Button btnLoadImage1 = new Button(shlOperacionesConImagenes, SWT.NONE);
		btnLoadImage1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JFileChooser selector=new JFileChooser();
                int estado=selector.showOpenDialog(null);
                File archivoelegido=selector.getSelectedFile();
                String ruta=archivoelegido.getPath();
               
                
                if(archivoelegido.exists())
                     System.out.println("bien imagen 1");
                else
                {
                	showInfoDialog("Advertencia","Archivo no existe");
                }
                if(estado==JFileChooser.APPROVE_OPTION);
                {           	
                	Image imagen = new Image(display,ruta);
                	imagen=resize(imagen,imagen1.getBounds().width,imagen1.getBounds().height);
                	imagen1.setImage(imagen);
                }
			}
		});
		btnLoadImage1.setBounds(119, 348, 108, 25);
		btnLoadImage1.setText("Cargar imagen 1");
		
		Button btnLoadImage2 = new Button(shlOperacionesConImagenes, SWT.NONE);
		btnLoadImage2.setText("Cargar imagen 2");
		btnLoadImage2.setBounds(589, 348, 124, 25);
		
		Button btnOperar = new Button(shlOperacionesConImagenes, SWT.NONE);
		btnOperar.setBounds(374, 457, 75, 25);
		btnOperar.setText("Operar");
		
		btnOperar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ImageOperator op = new ImageOperator(imagen1.getImage(), imagen2.getImage());
				String currentOperator = combo.getItem(combo.getSelectionIndex());
				op.operate(currentOperator);
				
				// TODO: Cambiar en donde se muestra la img resultante
				//imagen2.setImage(op.getResultingImage());
			}
		});

		shlOperacionesConImagenes.open();
		shlOperacionesConImagenes.layout();
		while (!shlOperacionesConImagenes.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
