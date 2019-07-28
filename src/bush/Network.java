/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author micha
 */
public class Network 
{
    private Set<Node> nodes;
    private Set<Link> links;
    
    private Demand demand;
    
    public Network(String name) throws IOException
    {
        nodes = new HashSet<>();
        links = new HashSet<>();
        demand = new Demand();
        readNetwork(name);
    }
    public Network(Set<Node> nodes, Set<Link> links, Demand demand)
    {
        this.nodes = nodes;
        this.links = links;
        this.demand = demand;
    }
    
    public Demand getDemand()
    {
        return demand;
    }
    
    
    public void dijkstras(Node r)
    {
        for(Node n : nodes)
        {
            n.cost = Integer.MAX_VALUE;
            n.pred = null;
        }
        
        r.cost = 0;
        
        Set<Node> Q = new HashSet<Node>();
        
        Q.add(r);
        
        while(!Q.isEmpty())
        {
            Node u = null;
            double best = Integer.MAX_VALUE;
            
            for(Node n : Q)
            {
                if(n.cost < best)
                {
                    best = n.cost;
                    u = n;
                }
            }
            
            Q.remove(u);
            
            
            
            for(Link uv : u.getOutgoing())
            {
                double temp = u.cost + uv.getTT();
                Node v = uv.getDest();
                if(temp < v.cost)
                {
                    v.cost = temp;
                    Q.remove(v);
                    Q.add(v);
                    v.pred = uv;
                }
            }
        }
    }
    
    public Tree getSPTree(Node r)
    {
        dijkstras(r);
        
        Tree output = new Tree(r);
        
        for(Node n : nodes)
        {
            output.put(n, n.pred);
        }
        
        return output;
    }
    
    public Bush createBush(Node r)
    {
        dijkstras(r);
        

        Set<Link> links = new HashSet<Link>();
        
        List<Node> nodes = new ArrayList<>();        
        Map<Node, Node> nodemap = new HashMap<>();
        
        nodes.add(r);
        Node clone = new Node(r.getId());
        nodemap.put(r, clone);
        
        
        for(Node n : nodes)
        {
            if(n.pred != null)
            {
                links.add(n.pred);
                
                nodes.add(n);
                clone = new Node(n.getId());
                nodemap.put(n, clone);
            }
        }
        
        for(Link l : links)
        {
            links.add(new Link(l, nodemap.get(l.getSource()), nodemap.get(l.getDest())));
        }
        
        nodemap = null;
        links = null;
        
        
        
        
        Bush output = new Bush(r, nodes, this);
        
        return output;
    }
    
    public void readNetwork(String name) throws IOException
    {
        readNetwork(new File("data/"+name+"/net.txt"), new File("data/"+name+"/trips.txt"));
    }
    
    public void readNetwork(File net, File trips) throws IOException
    {
        Map<Integer, Node> keynodes = new HashMap<Integer, Node>();
        
        Scanner filein = new Scanner(net);
        
        int numLinks = 0;
        int firstThruNode = 0;
        int numNodes = 0;
        int numZones = 0;
        
        // metadata
        while(true)
        {
            String line = filein.nextLine();
            
            String key = line.substring(line.indexOf('<')+1, line.indexOf('>'));
            String value = line.substring(line.indexOf('>')+1).trim();
            
            if(key.equalsIgnoreCase("NUMBER OF ZONES"))
            {
                numZones = Integer.parseInt(value);
            }
            else if(key.equalsIgnoreCase("NUMBER OF NODES"))
            {
                numNodes = Integer.parseInt(value);
            }
            else if(key.equalsIgnoreCase("NUMBER OF LINKS"))
            {
                numLinks = Integer.parseInt(value);
            }
            else if(key.equalsIgnoreCase("FIRST THRU NODE"))
            {
                firstThruNode = Integer.parseInt(value);
            }
            else if (key.equalsIgnoreCase("END OF METADATA"))
            {
                break;
            }
        }
        
        for(int i = 1; i <= numNodes; i++)
        {
            Node node = new Node(i);
            nodes.add(node);
            keynodes.put(i, node);
            
        }
        
        while(filein.nextLine().trim().length() == 0);
        
        for(int n = 0; n < numLinks; n++)
        {
            int r = filein.nextInt();
            int s = filein.nextInt();
            double Q = filein.nextDouble();
            double len = filein.nextDouble();
            double tf = filein.nextDouble();
            double a = filein.nextDouble();
            int b = (int)Math.round(filein.nextDouble());
            filein.nextLine();
            
            if(s < firstThruNode)
            {
                s = -s;
            }
            links.add(new Link(keynodes.get(r), keynodes.get(s), tf, Q, a, b, len));
        }
        
        
        filein.close();
        
        filein = new Scanner(trips);
        
        double mu = 0;
        double sigma = 0;
        
        while(true)
        {
            String line = filein.nextLine();
            
            String key = line.substring(line.indexOf('<')+1, line.indexOf('>'));
            String value = line.substring(line.indexOf('>')+1).trim();
            
            if (key.equalsIgnoreCase("END OF METADATA"))
            {
                break;
            }
        }
        
        demand = new Demand();
        
        Node r = null;
        
        while(filein.hasNext())
        {
            String next = filein.next();
            
            if(next.equalsIgnoreCase("Origin"))
            {
                r = keynodes.get(filein.nextInt());
            }
            else
            {
                Node s = keynodes.get(Integer.parseInt(next));
                

                filein.next(); 
                String temp = filein.next();
                if(temp.charAt(temp.length()-1)==';')
                {
                    temp = temp.substring(0, temp.length()-1);
                }
                else
                {
                    filein.next();
                }
                
                double p = Double.parseDouble(temp);
                
                if(p > 0)
                {
                    demand.addDemand(r, s, p);
                }
            }
        }
        filein.close();
        
        System.out.println("Demand: "+Math.round(demand.getTotal()));

    }    

    public void frankWolfe(int max_iter, double min_gap)
    {
        double gap = Integer.MAX_VALUE;
        int iter = 0;
        
        System.out.println("Iter\tStep size\tGap");
        
        do
        {
            iter++;
            
            gap = calcSearchDirection();
            double lambda = calcStepSize(iter);
            update(lambda);
            
            System.out.println(iter+"\t"+String.format("%.4f", lambda)+"\t"+String.format("%.4f", gap));
        }
        while((gap > min_gap || iter == 1) && iter < max_iter);
    }
    
    public double calcSearchDirection()
    {
        double tstt = 0.0;
        double sptt = 0.0;
        
        for(Link l : links)
        {
            tstt += l.x.getX() * l.getTT();
            l.x.x_star = 0;
        }
        
        for(Node r : demand.getOrigins())
        {
            Tree tree = getSPTree(r);

            for(Node s : demand.getDests(r))
            {
                double d = demand.getDemand(r, s);
                
                sptt += d * s.cost;
                
                for(Link l : tree.iterator(s))
                {
                    l.x.x_star += d;
                }
            }
        }
        
        return (tstt - sptt) / demand.getTotal();
    }
    
    public void update(double lambda)
    {
        for(Link l : links)
        {
            l.x.update(lambda);
        }
    }
    
    public double calcStepSize(int iter)
    {
        if(iter == 1)
        {
            return 1;
        }
        else
        {
            double bot = 0;
            double top = 1;
            double mid = 0.5;
            
            while(top - bot > 0.001)
            {
                //System.out.println(bot+" "+mid+" "+top);
                mid = (bot + top)/2;
                
                double val = getDeriv(mid);
                
                if(val < 0)
                {
                    bot = mid;
                }
                else if(val > 0)
                {
                    top = mid;
                }
                else
                {
                    break;
                }
            }
            
            return mid;
        }
    }
    
    public double getDeriv(double lambda)
    {
        double output = 0.0;
        
        for(Link ij : links)
        {
            output += ij.getTT(ij.x.getXPrime(lambda)) * (ij.x.x_star - ij.x.getX());
        }
        
        return output;
    }
    
    public double getObjVal(double lambda)
    {
        double output = 0;
        
        for(Link ij : links)
        {
            output += ij.getInt_TT(ij.x.getXPrime(lambda));
        }
        
        return output;
    }
    
    
    public void algorithmB()
    {
        
    }
}
