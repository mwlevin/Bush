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
    protected Node[] nodes;
    protected Link[] links;
    
    protected int numZones;
    
    private double total_demand;

    public Network(String name) throws IOException
    {
        readNetwork(name);
    }

    
    public Node[] getNodes()
    {
        return nodes;
    }
    
    public Link[] getLinks()
    {
        return links;
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
    
    /*
    public Bush createBush(Node r)
    {
        dijkstras(r);
        

        Set<Link> newlinks = new HashSet<Link>();
        
        List<Node> newnodes = new ArrayList<>();        
        Map<Node, Node> nodemap = new HashMap<>();
        
        
        Node clone = new Node(r.getId(), r.getId()+"-"+r.getId());
        nodemap.put(r, clone);
        newnodes.add(clone);
        Node origin_clone = clone;
        origin_clone.cloned = r;
        
        Set<Node> dests = new HashSet<>();
        
        Set<Node> key_dests = demand.getDests(r);
        
        for(Node n : nodes)
        {
            if(n.pred != null)
            {
                newlinks.add(n.pred);
                            
                clone = new Node(n.getId(), n.getId()+"-"+r.getId());
                clone.cloned = n;
                
                nodemap.put(n, clone);
                newnodes.add(clone);
                
                if(key_dests.contains(n))
                {
                    dests.add(clone);
                }
            }
        }
        
        for(Link l : newlinks)
        {
            // the nodes will remember this link, no need to store it
            new Link(l, nodemap.get(l.getSource()), nodemap.get(l.getDest()));
        }
        
        nodemap = null;
        newlinks = null;
        
        
        
        
        Bush output = new Bush(origin_clone, newnodes, dests, this);
        
        return output;
    }
    */
    
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
        
        nodes = new Node[numNodes];
        
        for(int i = 0; i < numNodes; i++)
        {
            int id = i+1;
            
            Node node;
            if(i < numZones)
            {
                node = new Zone(id, numZones);
            }
            else
            {
                node = new Node(id);
            }
            nodes[i] = node;
            keynodes.put(id, node);
            
        }
        
        while(filein.nextLine().trim().length() == 0);
        
        
        links = new Link[numLinks];
        
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

            links[n] = new Link(keynodes.get(r), keynodes.get(s), tf, Q, a, b, len);
        }
        
        
        filein.close();
        
        
        
        for(Node n : nodes)
        {
            List<Link> inc = new ArrayList<>();
            List<Link> out = new ArrayList<>();
            
            
            for(Link l : links)
            {
                if(l.getSource() == n)
                {
                    out.add(l);
                }
                else if(l.getDest() == n)
                {
                    inc.add(l);
                }
            }
            
            n.setIncoming(inc.toArray(new Link[0]));
            n.setOutgoing(out.toArray(new Link[0]));
        }
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
        
        
        Zone r = null;
        
        while(filein.hasNext())
        {
            String next = filein.next();
            
            if(next.equalsIgnoreCase("Origin"))
            {
                r = (Zone)keynodes.get(filein.nextInt());
            }
            else
            {
                Zone s = (Zone)keynodes.get(Integer.parseInt(next));
                

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
                
                r.setDemand(s, p);
                total_demand += p;
            }
        }
        filein.close();
        
        System.out.println("Demand: "+Math.round(total_demand));

    }    

    
    public int getNumZones()
    {
        return numZones;
    }
    
    public int getNumNodes()
    {
        return nodes.length;
    }
    
    public int getNumLinks()
    {
        return links.length;
    }
    
    public void frankWolfe()
    {
        frankWolfe(100000, Params.epsilon);
    }
    
    public void frankWolfe(int max_iter, double min_gap)
    {
        long time = System.nanoTime();
        
        double gap = Integer.MAX_VALUE;
        int iter = 0;
        
        System.out.println("Iter\tStep size\tAEC");
        
        do
        {
            iter++;
            
            calcSearchDirection();
            double lambda = calcStepSize(iter);
            update(lambda);
            
            gap = calcAEC();
            
            System.out.println(iter+"\t"+String.format("%.4f", lambda)+"\t"+String.format("%.4f", gap));
        }
        while((gap > min_gap || iter == 1) && iter < max_iter);
        
        System.out.println("Time: "+String.format("%.2f", (System.nanoTime() - time)/1.0e9)+" s");
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
        
        for(int idr = 0; idr < numZones; idr++)
        {
            Zone r = (Zone)nodes[idr];
            
            Tree tree = getSPTree(r);

            for(int ids = 0; ids < numZones; ids++)
            {
                Zone s = (Zone)nodes[ids];
                
                double d = r.getDemand(s);
                
                sptt += d * s.cost;
                
                for(Link l : tree.iterator(s))
                {
                    l.x.x_star += d;
                }
            }
        }
        
        return (tstt - sptt) / total_demand;
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
    

    public void algorithmB(int max_iter)
    {
        long time = System.nanoTime();

        // initial feasible bush
        for(int idr = 0; idr < numZones; idr++)
        {
            Zone r = (Zone)nodes[idr];
            r.bush = new Bush(r, this);

        }
        
        double gap = Integer.MAX_VALUE;
        
        System.out.println("Iter\tAEC");
        
        int iter = 0;
        
        do
        {
            iter++;
            
            for(int idr = 0; idr < numZones; idr++)
            {
                Bush bush = ((Zone)nodes[idr]).bush;
                bush.equilibrate();
            }
            
            gap = calcAEC();
            
            System.out.println(iter+"\t"+String.format("%.3f", gap));
        }
        while(gap > Params.epsilon && iter <= max_iter);

        System.out.println("Time: "+String.format("%.2f", (System.nanoTime() - time)/1.0e9)+" s");
    }
    
    public double calcAEC()
    {
        double tstt = 0.0;
        
        for(Link l : links)
        {
            tstt += l.x.getX() * l.getTT();
        }
        
        double sptt = 0.0;
        
        for(int idr = 0; idr < numZones; idr++)
        {
            Zone r = (Zone)nodes[idr];
            dijkstras(r);
            
            for(int ids = 0; ids < numZones; ids++)
            {
                Zone s = (Zone)nodes[ids];
                sptt += r.getDemand(s) * s.cost;
            }
        }
        
        return (tstt - sptt) / total_demand;
    }
    
    
    public Node findNode(int id)
    {
        for(Node n : nodes)
        {
            if(n.getId() == id)
            {
                return n;
            }
        }
        return null;
    }
    
 
}
