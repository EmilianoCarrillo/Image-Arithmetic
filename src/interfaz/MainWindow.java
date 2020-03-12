package interfaz;

import logic3.ImageOperator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;

public class MainWindow {

	/**
	 * Launch the application.
	 * @param args
	 */
	static Display display = Display.getDefault();
	static Shell shlOperacionesConImagenes = new Shell();
	private static Text text;
	private static Text text_1;
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
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	public static void main(String[] args) {
		
		shlOperacionesConImagenes.setSize(800, 800);
		shlOperacionesConImagenes.setText("Operaciones con imagenes");
		
		Label imagen1 = new Label(shlOperacionesConImagenes, SWT.BORDER);
		imagen1.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		imagen1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		imagen1.setBounds(21, 31, 300, 300);
		imagen1.setText("imagen1");
		
		/*Image imagen = new Image(display, "/Users/emilianocarrillo/Desktop/imgs/3a.png");
    	imagen=resize(imagen,imagen1.getBounds().width,imagen1.getBounds().height);
    	imagen1.setImage(imagen);*/
		
		Label imagen2 = new Label(shlOperacionesConImagenes, SWT.BORDER);
		imagen2.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		imagen2.setText("imagen2");
		imagen2.setBounds(456, 31, 300, 300);
		
		/*imagen = new Image(display, "/Users/emilianocarrillo/Desktop/imgs/3b.png");
    	imagen=resize(imagen,imagen1.getBounds().width,imagen1.getBounds().height);
    	imagen2.setImage(imagen);*/
    	
    	
    	Label imagen3 = new Label(shlOperacionesConImagenes, SWT.BORDER);
		imagen3.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		imagen3.setText("imagen3");
		imagen3.setBounds(250, 450, 300, 300);
		
		/*imagen = new Image(display, "/Users/emilianocarrillo/Desktop/imgs/default.png");
    	imagen=resize(imagen,imagen1.getBounds().width,imagen1.getBounds().height);
    	imagen3.setImage(imagen);*/
		
		Combo combo = new Combo(shlOperacionesConImagenes, SWT.NONE);
		combo.setItems(new String[] {"+", "-", "*", "#"});
		combo.setBounds(387, 179, 51, 23);
		combo.setText("Operator");
		combo.select(0);
		
		text = new Text(shlOperacionesConImagenes, SWT.BORDER);
		text.setBounds(300, 350, 76, 21);
		
		text_1 = new Text(shlOperacionesConImagenes, SWT.BORDER);
		text_1.setBounds(400, 352, 76, 21);
		
		Label label = new Label(shlOperacionesConImagenes, SWT.NONE);
		label.setBounds(385, 358, 20, 15);
		label.setText("X");
		
		Scale scale = new Scale(shlOperacionesConImagenes, SWT.NONE);
		scale.setBounds(332, 250, 115, 42);
		
		scale.setMaximum(0);
		scale.setMaximum(100);
		scale.setIncrement(1);
		scale.setVisible(false);
		
		scale.setSelection(30);
		combo.addModifyListener( new ModifyListener () {

			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub
				String operator= combo.getText();
				if(operator.equals("#"))
				{
					scale.setVisible(true);
				}
				else
					scale.setVisible(false);
			}
			
		});
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
		btnLoadImage2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JFileChooser selector=new JFileChooser();
                int estado=selector.showOpenDialog(null);
                File archivoelegido=selector.getSelectedFile();
                String ruta=archivoelegido.getPath();
               
                if(archivoelegido.exists())
                     System.out.println("bien imagen 2");
                else
                {
                	showInfoDialog("Advertencia","Archivo no existe");
                }
                if(estado==JFileChooser.APPROVE_OPTION);
                {
                	
                	Image imagen = new Image(display,ruta);
                	imagen=resize(imagen,imagen2.getBounds().width,imagen2.getBounds().height);
                	imagen2.setImage(imagen);
                }
			}
		});
		btnLoadImage2.setText("Cargar imagen 2");
		btnLoadImage2.setBounds(545, 348, 124, 25);
		
		Button btnOperar = new Button(shlOperacionesConImagenes, SWT.NONE);
		btnOperar.setBounds(374, 400, 75, 25);
		btnOperar.setText("Operar");
		
		
		
		btnOperar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(text.getText().isEmpty() || text_1.getText().isEmpty())
				{
					showInfoDialog("Advertencia","Debe especificar las divisiones de la cuadricula");
					return;
				}
				if(!isNumeric(text.getText())) {
					showInfoDialog("Advertencia","Ingrese un numero valido en el primer campo");
					return;
				}
				if(!isNumeric(text_1.getText())) {
					showInfoDialog("Advertencia","Ingrese un numero valido en el segundo campo");
					return;
				}
				int c1;
				int c2;
				c1=Integer.parseInt(text.getText());
				c2=Integer.parseInt(text_1.getText());
				ImageOperator op = new ImageOperator(imagen1.getImage(), imagen2.getImage());
				String currentOperator = combo.getItem(combo.getSelectionIndex());
				double coeficient=-1.0;
				if(currentOperator.equals("#"))
				{
					int tmp=scale.getSelection();
					coeficient=(double)((double)(tmp*1.0)/100.00);
					if(coeficient==-1.0)
						coeficient=0.8;
					
				}
				//*** logic 1 *****
				//op.operate(currentOperator);
				//***** logic 2 *****
				/*
				try {
					op.operate(currentOperator, 10, 10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				*/
				//***** logic 3 *****
				
				try {
					op.operate(currentOperator, c1, c2,coeficient);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				
				imagen3.setImage(op.getResultingImage());
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
