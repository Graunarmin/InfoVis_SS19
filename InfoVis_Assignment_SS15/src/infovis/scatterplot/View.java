package infovis.scatterplot;

import infovis.debug.Debug;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class View extends JPanel {
	     private Model model = null;
	     private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0);
	     private Rectangle2D matrixFrame = new Rectangle2D.Double(135,25,650,650);
	     private Line2D horizontalSegmentLine = new Line2D.Double(135,25,785,25);
	     private Line2D verticalSegmentLine = new Line2D.Double(100,30,100,680);

		 public Rectangle2D getMarkerRectangle() {
			return markerRectangle;
		}
		 
		@Override
		public void paint(Graphics g) {

			Graphics2D g2D = (Graphics2D) g;

			//Matrix Frame
			g2D.draw(matrixFrame);

			//draw the horizontal lines into the matrix
			for (int i = 0; i < model.getDim(); i++){	
				g2D.translate(0, 650/model.getDim());
				g2D.draw(horizontalSegmentLine);
			}

			//translate it back
			g2D.translate(0, -650);

			//draw the vertical lines into the matrix
			for (int i = 1; i < model.getDim(); i++){	//Cheating a bit by taking out the last line so it doesn't look ugly
				g2D.translate(725/model.getDim(), 0);	// <------ bekomme es einfach nicht hin das die Abschnitte gleich verteilt sind >.<
				g2D.draw(verticalSegmentLine);

			}
			
			//translate it back
			g2D.translate(-725, 0);
			
			g2D.scale(0.7, 0.7);	//Scales labels down
			
			int i = 0;
			
			//Horizontal labels
			for (String l : model.getLabels()) {	 
				g2D.drawString(l,325+i,25);	//Draws labels starting at position and height
				i += 140;	//moves next label a bit to the right
			}
			
			int j = 0;
			
			//g2D.rotate(1.75);	//Theoretically a way to rotate the labels for the sides but every one rotates differently
			
			//Vertical labels
			for (String l : model.getLabels()) {	
				g2D.drawString(l, 170, 100+j);
				j += 140;
			}
			
			
			for (Range range : model.getRanges()) {
				Debug.print(range.toString() + "+");
				Debug.print(",  ");
				Debug.println("");
			}
			for (Data d : model.getList()) {
				Debug.print(d.toString() + "#");
				Debug.println("");
			}
	        
			
		}
		public void setModel(Model model) {
			this.model = model;
		}
}
