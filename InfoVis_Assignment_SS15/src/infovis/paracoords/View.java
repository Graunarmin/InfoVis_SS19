package infovis.paracoords;

import infovis.scatterplot.Model;
import infovis.scatterplot.Data;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.util.ArrayList;


import javax.swing.JPanel;

public class View extends JPanel {
	private Model model = null;

	Font myFont = new Font ("Courier New", 1, 10);

	private static final int WIDTH = 1100;
	private static final int HEIGHT = 600;
	private static final int XOFFST = 50;
	private static final int YOFFST = 50;
	private Line2D axis = new Line2D.Float(XOFFST, YOFFST, XOFFST, YOFFST + HEIGHT);
	private int coordOffset = 0;
	private int clickedX = 0;
	private int clickedY = 0;
//	private boolean pointClicked = false;
//	private boolean lineClicked = false;
	private boolean sthClicked = false;

	public void setClicked(int x, int y){
		clickedX = x;
		clickedY = y;
		sthClicked = true;
	}

	public void setCoordOffset(){
		//for camera data the x coords of the points need to be shifted a wee bit to the right
		if(model.getUsedFile().equals("cameras.ssv")){
			coordOffset = 6;
		}
		else{
			coordOffset = 1;
		}
	}

	@Override
	public void paint(Graphics g) {
		
		//Calculate offset if camera data is used 
		setCoordOffset();
		
		int dim = model.getDim();
		int axisDistance = WIDTH / dim;
		int numberOfEntries = model.getList().size();
		ArrayList<Data> data = model.getList();
		ArrayList<Point2D> dataPoints = new ArrayList<Point2D>();
		ArrayList<ArrayList<Line2D>> connectionLines = new ArrayList<>();

		Graphics2D g2D = (Graphics2D) g;
		
		//Delete everything before update
		g2D.clearRect(0, 0, getWidth(), getHeight());  

		//Draw parallel axes (one for each label) and move them a bit to the right every time 
		for(int a = 0; a < dim; a++){
			g2D.draw(axis);
			g2D.translate(axisDistance, 0);
		}
		//translate it back
		g2D.translate( -WIDTH, 0);

		//Horizontal labels
		int i = 0;
		for (String l : model.getLabels()) {
			g.setFont (myFont);
			drawRotate(g2D,XOFFST+i,YOFFST-5,-15,l);
			i += (WIDTH/dim);	//moves next label a bit to the right
		}

		//x-coordinate is the same for each point on the same axis, for the next axis add axisDistance
		int xCoord = XOFFST + coordOffset;

		//Calculate points for each axis:
		for(int y = 0; y < dim; y++){
			
			//Get y-data 
			ArrayList<Double> yData = new ArrayList<>();
			for(Data d: data) {
				yData.add(d.getValue(y));
			}
			
			//Calculate y-coordinates
			AxisDataPara axisdata = new AxisDataPara(yData, HEIGHT, YOFFST);
			ArrayList<Integer> yCoords = axisdata.getPointYCoordinates();

			//Combine coordinates to create points, store points in dataPoints array
			for(int c = 0; c < yCoords.size(); c++){
				Point2D dataPoint = new Point2D.Double(xCoord, yCoords.get(c));
				dataPoints.add(dataPoint);
			}

			//Move x-coordinates to the right for the next axis 
			xCoord += axisDistance;	
		}
		
		//Points actually don't need to be there for the visualization 
		//draw point array
//		for(Point2D point : dataPoints) {
//			g2D.setColor(Color.BLUE);
//			g2D.drawOval((int) point.getX(), (int) point.getY(), 3, 3);
//			g2D.fillOval((int) point.getX(), (int) point.getY(), 3, 3);
//		}

		//draw lines between points to make a path for each object
		//Startpunkte liegen immer "number of Entries" auseinander:

		for(int point = 0; point < numberOfEntries; point++){

			int j = point;
			ArrayList<Line2D> path = new ArrayList<>();

			//einen Pfad pro Objekt zeichnen:
			for(int counter = 0; counter < dim-1; counter ++){
				g2D.setColor(Color.BLUE);

				Line2D connectionLine = new Line2D.Double(dataPoints.get(j).getX(),dataPoints.get(j).getY(),
						               dataPoints.get(j + numberOfEntries).getX(),dataPoints.get(j + numberOfEntries).getY());
				path.add(connectionLine);
				g2D.draw(connectionLine);
				//zum Punkt der nächsten Kategorie weitergehen:
				j += numberOfEntries;
			}
			connectionLines.add(path);
		}

		// Highlighting a Path:
		if(sthClicked){
			highlightPath(dataPoints, connectionLines, numberOfEntries, dim, g2D);
			sthClicked = false;
		}


	}

	public void highlightPath(ArrayList<Point2D> dataPoints, ArrayList<ArrayList<Line2D>> connectionLines, int numberOfEntries, int dim, Graphics2D g2D){
		// If a point was clicked:
		for(Point2D point: dataPoints){

			//add a little tolerance to the clicked point
			if ((Math.abs(point.getX()-clickedX) <= 5) && (Math.abs(point.getY()-clickedY) <= 5)){

				// every "number-of-entries"th point is the one we need for a path
				for(int p = dataPoints.indexOf(point) % numberOfEntries; p < dataPoints.size(); p += numberOfEntries){

					g2D.setColor(Color.ORANGE);
					//g2D.drawOval((int) dataPoints.get(p).getX(), (int) dataPoints.get(p).getY(), 3, 3);
					//g2D.fillOval((int) dataPoints.get(p).getX(), (int) dataPoints.get(p).getY(), 3, 3);

					//highlight the lines between the right points:
					if(p < dataPoints.size() - numberOfEntries){

						Line2D connectionLine = new Line2D.Double(dataPoints.get(p).getX(),dataPoints.get(p).getY(),
								               dataPoints.get(p + numberOfEntries).getX(),dataPoints.get(p + numberOfEntries).getY());

						g2D.draw(connectionLine);
					}
				}
				break;
			}
		}


		// If instead a line was clicked:
		//add a little tolerance to the clicked point
		Rectangle2D clicked = new Rectangle(clickedX - 3, clickedY - 3, 6, 6);
//		g2D.setColor(Color.RED);
//		g2D.draw(clicked);

		for(ArrayList<Line2D> path: connectionLines){

			//Wenn eine Line aus dem Pfad den geklickten Punkt schneidet:
			for(Line2D line : path){
				if(line.intersects(clicked)){
					//alle Linien dieses Pfads highlighten
					for(Line2D l : path){
						g2D.setColor(Color.ORANGE);
						g2D.draw(l);
					}
					break;
				}
			}
		}
	}
	
	public static void drawRotate(Graphics2D g2d, double x, double y, int angle, String text) {
		//to rotate the labels right
		g2d.translate((float)x,(float)y);
		g2d.rotate(Math.toRadians(angle));
		g2d.drawString(text,0, 0);
		g2d.rotate(-Math.toRadians(angle));
		g2d.translate(-(float)x,-(float)y);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
}
