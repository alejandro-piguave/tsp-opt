package main_package;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;


public class DisplayPanel extends JPanel {
	private int  node_size=5,max_gen,pop_size,elite_size, current_algorithm,start_temperature;
	private boolean is_running, show_result;
	private double mut_ratio,cooling_rate,absolute_temperature;
	private long start_time;
	
	private Path bestPath, currentPath;
	private ArrayList<Node> nodes;
	private String output_lines[];
	private JTextArea outputLabel;
	private Random random;
	
	public DisplayPanel(int max_gen, int pop_size, int elite_size, double mut_ratio,
			int start_temperature,double cooling_ratio, double absolute_temperature) {
		this.max_gen=max_gen;
		this.pop_size=pop_size;
		this.elite_size=elite_size;
		this.mut_ratio=mut_ratio;
		
		this.start_temperature=start_temperature;
		this.cooling_rate=cooling_ratio;
		this.absolute_temperature=absolute_temperature;
		
		nodes = new ArrayList<Node>();
		random = new Random();	

		setBackground(Color.WHITE);
		
        Timer timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(output_lines!=null)
            		updateOutput();
            	repaint();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

	
	private void runSimulatedAnnealing() {
		if(bestPath==null) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i=0; i<nodes.size(); i++) 
				list.add(new Integer(i));
        
			Collections.shuffle(list);
			int path[] = new int[nodes.size()];
			for (int i=0; i<nodes.size(); i++) 
				path[i] =list.get(i);
        
			bestPath = new Path(path);
			currentPath=new Path(path);
		}
		output_lines=new String[] {"","","",""};
        double temperature=start_temperature;
        boolean improved=true;
        Path tempBest=new Path(bestPath.route);

        while (is_running && (improved || temperature > absolute_temperature)) {
        	improved = false;
        	for(int i=1;i<bestPath.route.length-1;i++) {
        		float elapsedTime=(float)(System.currentTimeMillis()-start_time)/1000f;
    			output_lines[0]="Execution time: "+String.format("%.03f", elapsedTime)+" s";

    			output_lines[1]="Temperature:\n"+temperature;
    			

        			int new_path[] = new int[nodes.size()];
        			int j = Math.max(i, random.nextInt(nodes.size()));
        			while(j==i) {
        				j = Math.max(i, random.nextInt(nodes.size()));
        			}
        			for(int k=0;k<i;k++)
        				new_path[k]=bestPath.route[k];
        			for(int k=i, h=j;k<=j;k++,h--)
        				new_path[k]=bestPath.route[h];
        			if(j<tempBest.route.length-1)
        				for(int k=j+1;k<nodes.size();k++)
        					new_path[k]=bestPath.route[k];
        			
        			currentPath=new Path(new_path);
        			if(currentPath.distance<=tempBest.distance) {
        				improved=true;
        				tempBest=new Path(new_path);
        	  			float lastBestSolTime = (System.currentTimeMillis()-start_time)/1000f;
        	  			output_lines[2]="Best length: "+String.format("%.02f", tempBest.distance)+",";
        	  			output_lines[3]="found in: "+String.format("%.03f", lastBestSolTime)+" s";
        	  			
        	  			
        	  			//System.out.println(count+"&"+lastBestSolTime+"s&"+bestPath.distance+"\\\\ \n\\hline");
        			}
        			
        			if(currentPath.distance <= bestPath.distance 
        					|| random.nextDouble()<=Math.exp(bestPath.distance-currentPath.distance)/temperature) {
        				bestPath = new Path(new_path);
        			}


        	}
        	temperature*=(1-cooling_rate);
        	
        }
    	bestPath=tempBest;
    	//System.out.println("=================================================================");
	}
	private void run2OptAlgorithm() {
		int count=1;
		if(bestPath==null) {
			//long start_time=System.currentTimeMillis();
			output_lines=new String[] {"","",""};
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i=0; i<nodes.size(); i++) 
				list.add(new Integer(i));
        
			Collections.shuffle(list);
			int path[] = new int[nodes.size()];
			for (int i=0; i<nodes.size(); i++) 
				path[i] =list.get(i);
        
			bestPath = new Path(path);
		}
        boolean improved = true;
        
        while (is_running && improved) {
        	improved = false;
        	for(int i=1;i<bestPath.route.length-1;i++) {
        		float elapsedTime=(float)(System.currentTimeMillis()-start_time)/1000f;
    			output_lines[0]="Execution time: "+String.format("%.03f", elapsedTime)+" s";
        		for (int j=i+1; j<bestPath.route.length;j++) {
        			int new_path[] = new int[nodes.size()];
        			for(int k=0;k<i;k++)
        				new_path[k]=bestPath.route[k];
        			for(int k=i, h=j;k<=j;k++,h--)
        				new_path[k]=bestPath.route[h];
        			if(j<bestPath.route.length-1)
        				for(int k=j+1;k<nodes.size();k++)
        					new_path[k]=bestPath.route[k];
        			
        			currentPath=new Path(new_path);
        			if(currentPath.distance<bestPath.distance) {
        				improved=true;
        	  			bestPath = new Path(new_path);

        	  			float lastBestSolTime = (System.currentTimeMillis()-start_time)/1000f;
        	  			output_lines[1]="Best length: "+String.format("%.02f", bestPath.distance)+",";
        	  			output_lines[2]="found in: "+String.format("%.03f", lastBestSolTime)+" s";
        	  			
        	  			//System.out.println(count+"&"+lastBestSolTime+"s&"+bestPath.distance+"\\\\ \n\\hline");
        	  			count++;
        			}
        		}
        	}
        }
    	//System.out.println("=================================================================");
	}
	private void runGeneticAlgorithm() {
		//long start_time=System.currentTimeMillis();
		output_lines=new String[] {"","","","","",""};
		ArrayList<Path> population = new ArrayList<Path>();
		int currentGen=1;
		
    		ArrayList<Integer> numbers = new ArrayList<>();
    		for(int i=0;i<nodes.size();i++)
    			numbers.add(i);
    		
    		for(int i=0;i<pop_size;i++){
    			Collections.shuffle(numbers);
    			int[] indexes = new int[numbers.size()];
    			for (int j=0;j<numbers.size();j++)
    				indexes[j]=numbers.get(j);
    			population.add(new Path(indexes));
    		}
    		Collections.sort(population);
    		bestPath = population.get(0);
    		currentPath = population.get(0);


    	while(is_running && currentGen<=max_gen) {
    		float elapsedTime=(float)(System.currentTimeMillis()-start_time)/1000f;
			output_lines[0]="Execution time: "+String.format("%.03f", elapsedTime)+" s";
			output_lines[1]="Generation #"+currentGen;
    		Collections.sort(population);
    		if(population.get(0).distance<bestPath.distance) {
    			bestPath = new Path(population.get(0).route);
    			float lastBestSolTime = (System.currentTimeMillis()-start_time)/1000f;
	  			output_lines[2]="Best length: "+String.format("%.02f", bestPath.distance)+",";
	  			output_lines[3]="found in: "+String.format("%.03f", lastBestSolTime)+" s,";
	  			output_lines[4]="in generation #"+currentGen;
	  			
	  			//System.out.println(count+"&"+lastBestSolTime+"s&"+bestPath.distance+"\\\\ \n\\hline");
    		}
			currentPath = new Path(population.get(0).route);

    		float totalFitness = calcFitness(population);
    		ArrayList<Integer> bestIndexes = getBestIndexes(population,totalFitness);
    		ArrayList<Path> children = nextGeneration(population,bestIndexes);
    		population.clear();
    		population.addAll(children);
        	currentGen++;
    		}

	}
    
	private void runRandomGreedy(){
		output_lines=new String[] {"","",""};
		int startingNode=random.nextInt(nodes.size());
		bestPath = null;
		currentPath = null;

			int path[] = new int[nodes.size()];
			path[0]=startingNode;
			ArrayList<Node> pool= new ArrayList<Node>(nodes);
			pool.remove(nodes.get(startingNode));
			Node lastNode = nodes.get(startingNode);
			for(int i =1; i<nodes.size();i++) {
				double min_distance=Double.MAX_VALUE;
				Node nearestNode = null;
				for(Node n:pool) {
					double d =lastNode.distanceTo(n);
					if(d < min_distance) {
						nearestNode = n;
						min_distance=d;
					}
				}
				path[i]=nodes.indexOf(nearestNode);
				lastNode = nearestNode;
				pool.remove(nearestNode);
				
//				int temp_path[]=new int[i+1];
//				System.arraycopy( path, 0, temp_path, 0, i+1 );
//				
//				currentPath=new Path(temp_path);
//				bestPath = new Path(temp_path);
//				try {
//					Thread.sleep(250);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println(temp_path.length);
//				System.out.println(Arrays.toString(temp_path));
			}
	    	Path newPath = new Path(path);
	  			bestPath = newPath;
	  			float solvingTime = (System.currentTimeMillis()-start_time)/1000f;
	  			output_lines[0]="Length: "+String.format("%.02f", bestPath.distance)+",";
	  			output_lines[1]="found in: "+String.format("%.03f", solvingTime)+" s";
	    	  
	}
	private void runFullGreedy() {
		//long start_time=System.currentTimeMillis();
		output_lines=new String[] {"","",""};
		int startingNode=0;
		bestPath = null;
		double bestDistance=Float.MAX_VALUE;
		currentPath = null;
		
		while(is_running && startingNode<nodes.size()) {
			float elapsedTime=(float)(System.currentTimeMillis()-start_time)/1000f;
			output_lines[0]="Execution time: "+String.format("%.03f", elapsedTime)+" s";
			
			int path[] = new int[nodes.size()];
			path[0]=startingNode;
			ArrayList<Node> pool= new ArrayList<Node>(nodes);
			pool.remove(nodes.get(startingNode));
			Node lastNode = nodes.get(startingNode);
			for(int i =1; i<nodes.size();i++) {
				double min_distance=Double.MAX_VALUE;
				Node nearestNode = null;
				for(Node n:pool) {
					double d =lastNode.distanceTo(n);
					if(d < min_distance) {
						nearestNode = n;
						min_distance=d;
					}
				}
				path[i]=nodes.indexOf(nearestNode);
				lastNode = nearestNode;
				pool.remove(nearestNode);
			}

	    	Path newPath = new Path(path);
	    	if(newPath.distance<bestDistance) {
	    		bestDistance = newPath.distance;
	  			bestPath = newPath;
	  			float lastBestSolTime = (System.currentTimeMillis()-start_time)/1000f;
	  			output_lines[1]="Best length: "+String.format("%.02f", bestDistance)+",";
	  			output_lines[2]="found in: "+String.format("%.03f", lastBestSolTime)+" s";
	    	  }
	    	  currentPath=newPath;
	    	  startingNode++;
		}
	
	}
	private void runAllSolAlgorithm() {
		//long start_time=System.currentTimeMillis();


		output_lines=new String[] {"","","",""};
		int lexOrder[] = new int[nodes.size()];
		long solNum = factorial(nodes.size()-1)/2,currentSol=1;
		for(int i=0;i<nodes.size();i++)
			lexOrder[i]=i;
		bestPath = new Path(lexOrder);
		currentPath = new Path(lexOrder);
		
		while(is_running && currentSol<solNum) {
			float elapsedTime=(float)(System.currentTimeMillis()-start_time)/1000f;
			output_lines[0]="Execution time: "+String.format("%.03f", elapsedTime)+" s";
			//output_lines[1]="Path "+(currentSol+1)+"/"+solNum;
	    	int largestI = -1;
	    	  for (int i = 1; i < lexOrder.length - 1; i++) {
	    	    if (lexOrder[i] < lexOrder[i + 1]) {
	    	      largestI = i;
	    	    }
	    	  }

	    	  // STEP 2
	    	  int largestJ = -1;
	    	  for (int j = 1; j < lexOrder.length; j++) {
	    	    if (lexOrder[largestI] < lexOrder[j]) {
	    	      largestJ = j;
	    	    }
	    	  }
	    	  // STEP 3
	    	  swap(lexOrder, largestI, largestJ);
	    	  // STEP 4: reverse from largestI + 1 to the end
	    	  int endArray[] = Arrays.copyOfRange(lexOrder, largestI+1,lexOrder.length);
	    	  for(int i = 0; i < endArray.length / 2; i++)
	    	  {
	    	      int temp = endArray[i];
	    	      endArray[i] = endArray[endArray.length - i - 1];
	    	      endArray[endArray.length - i - 1] = temp;
	    	  }
	    	  System.arraycopy( endArray, 0, lexOrder, lexOrder.length-endArray.length, endArray.length );
	    	  
	    	  if(lexOrder[1]>lexOrder[lexOrder.length-1])
	    		  continue;
	    	  currentPath = new Path(lexOrder);
	    	  if(currentPath.distance<bestPath.distance) {
	  			bestPath = currentPath;
	  			float lastBestSolTime = (System.currentTimeMillis()-start_time)/1000f;
	  			output_lines[2]="Best length: "+String.format("%.02f", bestPath.distance)+",";
	  			output_lines[3]="found in: "+String.format("%.03f", lastBestSolTime)+" s";
	  			
	  			//System.out.println(count+"&"+lastBestSolTime+"s&"+bestPath.distance+"\\\\ \n\\hline");
	    	  }
	    	  currentSol++;
	    	  
		}
	}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2.5f));
        if(is_running && currentPath!=null && bestPath!=null) {
        	g2.setColor(Color.BLACK);
        	paintRoute(g2,currentPath);
            g2.setColor(Color.BLUE);
            paintRoute(g2,bestPath);
            }
        if(show_result) {
            g2.setColor(Color.BLUE);
            paintRoute(g2,bestPath);
            }
        if(nodes.size()>0) {
            g2.setColor(Color.BLACK);
            for(Node node: nodes) {
            	Rectangle2D.Float square = new Rectangle2D.Float(node.x-node_size/2f, node.y-node_size/2f,node_size, node_size);
                g2.fill(square);
            }
        }
    }
    private void paintRoute(Graphics2D g2, Path path) {
        GeneralPath gPath = new GeneralPath();
        gPath.moveTo(nodes.get(path.route[0]).x, nodes.get(path.route[0]).y);
        for(int i=1;i<path.route.length;i++) {
        	gPath.lineTo(nodes.get(path.route[i]).x, nodes.get(path.route[i]).y);
        }
        gPath.lineTo(nodes.get(path.route[0]).x, nodes.get(path.route[0]).y);
        g2.draw(gPath);
    }

    private float calcFitness(ArrayList<Path> currentGeneration) {
    	float fitness_sum=0,currentWorstDistance=0;
    	for(Path path:currentGeneration){

    		if(path.distance>currentWorstDistance)
    			currentWorstDistance=path.distance;
    			
    		}
    	for(Path path:currentGeneration){
    		float fitness =currentWorstDistance-path.distance;
    		path.fitness=fitness;
    		fitness_sum+=fitness;}
    	return fitness_sum;
    }
    
    
    private ArrayList<Path> nextGeneration(ArrayList<Path> currentGeneration,ArrayList<Integer> bestIndexes){
    	ArrayList<Path> children = new ArrayList<Path>();
    	for(int i=0;i<elite_size;i++)
            children.add(currentGeneration.get(i));
    		
        Collections.shuffle(bestIndexes);
    	int  leng = bestIndexes.size()-elite_size;
    	for(int i=0;i<leng;i++){
            Path child = breed(currentGeneration.get(bestIndexes.get(i)), currentGeneration.get(bestIndexes.get(leng-i-1)));
            children.add(child);
    	}
    	mutatePopulation(children);
    
        return children;
    }
    
    private void mutatePopulation(ArrayList<Path> children){
    	for(Path child:children)
    		mutateIndividual(child);

    		
    }
    private void mutateIndividual(Path individual){
    	for(int i=0; i < individual.route.length;i++){
    		if(random.nextFloat()*100<mut_ratio){
    			int genB = random.nextInt(individual.route.length);
    			int temp = individual.route[i];
    			individual.route[i] = individual.route[genB];
    			individual.route[genB] = temp;
    			}
    		}
    	}
    
    private Path breed(Path parent1, Path parent2){
    	int child[], childP1[], childP2[];
        
        int geneA = random.nextInt(parent1.route.length);
        int geneB = random.nextInt(parent2.route.length);
        
        int startGene = Math.min(geneA, geneB);
        int endGene = Math.max(geneA, geneB);
        
        childP1 = new int[endGene-startGene+1];
        childP2 = new int[parent2.route.length-childP1.length];
        for (int i=startGene,j=0;i<= endGene;i++,j++)
            childP1[j]=parent1.route[i];
        
        int a =0;
    	for(int i=0;i<parent2.route.length;i++){
    		boolean foo = false;
    		for(int j=0;j<childP1.length;j++){
    			if(parent2.route[i]==childP1[j]){
    				foo=true;
    				break;}
    		}
    		if(!foo) {
    			childP2[a]=parent2.route[i];
    			a++;
    		}	
    	}
    	
    	child = new int[childP1.length+childP2.length];
    	System.arraycopy(childP1, 0, child, 0, childP1.length);
    	System.arraycopy(childP2, 0, child, childP1.length, childP2.length);return new Path(child);
    }


    public ArrayList<Integer> getBestIndexes(ArrayList<Path> currentGeneration,float totalFitness){
    	float cum_sum[] = new float[currentGeneration.size()];
    	ArrayList<Integer> bestIndexes= new ArrayList<Integer>();
    	for(int i =0;i<currentGeneration.size();i++){
    		Path path = currentGeneration.get(i);
    		float relativeFitness = path.fitness*100/totalFitness;
    		float cumRelativeFitness = relativeFitness;
    		if(i>0)
    			cumRelativeFitness = cum_sum[i-1] + relativeFitness;
    		cum_sum[i] = cumRelativeFitness;
    		}
    	for(int i=0; i<elite_size;i++)
    		bestIndexes.add(i);
    	for(int i=0; i<currentGeneration.size()-elite_size;i++){
    		float pick = 100*random.nextFloat();
            for (i=0;i<currentGeneration.size();i++){
                if (pick <= cum_sum[i]){
                	bestIndexes.add(i);
                    break;
    				}
    			}
    		}
    	return bestIndexes;
    }

	public void setGeneticParameters(int max_gen, int pop_size, int elite_size, double mut_ratio) {
		this.max_gen=max_gen;this.pop_size=pop_size;this.elite_size=elite_size;this.mut_ratio=mut_ratio;
	}
	public void setCurrentAlgorithm(int a) {current_algorithm=a;}

	public int getNumNodes() {return nodes.size();}
    private void swap(int a[],int i,int j) {
  	  int temp = a[i];
  	  a[i] = a[j];
  	  a[j] = temp;
  	}
	public void stopAlgorithm() {
		bestPath=null;
		currentPath=null; 
		output_lines=null;
		is_running=false;
		show_result=false;
	}
	public void setOutputLabel(JTextArea label) {outputLabel = label;}
	private static long factorial(int n){    
		  if (n == 0)    
		    return 1;    
		  else    
		    return(n * factorial(n-1));    
		 }
	private void updateOutput() {
		String output="";
		for(int i=0;i<output_lines.length;i++)
			output+=output_lines[i]+"\n";
		outputLabel.setText(output);
	}
	public void randomNodes(int num) {
		outputLabel.setText("");
		nodes.clear();
		for(int i = 0; i < num;i++) {
			nodes.add(new Node(random.nextInt(getWidth()),random.nextInt(getHeight())));
		}
	}
	public void randomNodes() {
		int temp = nodes.size();
		nodes.clear();
		for(int i = 0; i < temp;i++) {
			nodes.add(new Node(random.nextInt(getWidth()),random.nextInt(getHeight())));
		}
	}
	public int getMax_gen() {
		return max_gen;
	}

	public int getPop_size() {
		return pop_size;
	}

	public int getElite_size() {
		return elite_size;
	}

	public double getMut_ratio() {
		return mut_ratio;
	}
	public void setAnnealingParameters(int temperature, double cooling_ratio, double absolute_temperature) {
		this.start_temperature=temperature;this.cooling_rate=cooling_ratio;this.absolute_temperature=absolute_temperature;
	}
	

	public double getStart_temperature() {
		return start_temperature;
	}

	public double getCooling_ratio() {
		return cooling_rate;
	}

	public double getAbsolute_temperature() {
		return absolute_temperature;
	}

	//so our panel is the corerct size when pack() is called on Jframe
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
    
    private class Path  implements Comparable<Path> {
    	public int route[];
    	public float fitness,distance;
    	public Path(int route[]) {
    		this.route = new int[route.length];
    		System.arraycopy( route, 0, this.route, 0, route.length );
    		this.distance = calcDistance(route);
    	}
    	private float calcDistance(int route[]) {
    		  float sum = 0;
    		  for (int i = 0; i < route.length; i++) {
    		    Node cityA = nodes.get(route[i]);
    		    Node cityB = null;
    			if(i>=route.length-1)
    				cityB = nodes.get(route[0]);
    			else cityB = nodes.get(route[i + 1]);
    		    double d = cityA.distanceTo(cityB);
    		    sum += d;
    		  }
    		  return sum;
    	}
    	@Override
    	public int compareTo(Path o) {
    		// TODO Auto-generated method stub
    		return (int) (this.distance - o.distance);
    	}

    }
	public void runAlgorithm() {

		is_running=true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				start_time=System.currentTimeMillis();
				switch(current_algorithm) {
				case 0: 
					runAllSolAlgorithm();
					break;
				case 1:
					runGeneticAlgorithm();
					break;
				case 2:
					runFullGreedy();
					break;
				case 3:
					runRandomGreedy();
					break;
				case 4:
					run2OptAlgorithm();
					break;
				case 5:
					runRandomGreedy();
					//runGreedyAlgorithm();
					run2OptAlgorithm();
					break;
				case 6:
					runSimulatedAnnealing();
					break;
				case 7:
					runRandomGreedy();
					//runGreedyAlgorithm();
					runSimulatedAnnealing();
					break;   
				}
				if(is_running) {
					is_running=false;
					show_result=true;
					}
			}
		}).start();
	}
}