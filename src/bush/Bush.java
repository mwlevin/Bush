/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author micha
 */
public class Bush
{
    private Zone origin;

    private double[] flow; // use the indices of each link
    private boolean[] contains; 
    
    private Network network;
    
    private Node[] sorted;
    
    public Bush(Zone origin, Network network)
    {
        
        this.network = network;

        this.origin = origin;
        
        
        flow = new double[network.getLinks().length];
        contains = new boolean[network.getLinks().length];
        
        loadDemand();
        
        
        
        
       
    }
    
    
    public boolean contains(Link l)
    {
        return contains[l.getIdx()];
    }
    
    public void topologicalSort()
    {
        for(Node n : network.nodes)
        {
            n.top_order = -1;
            n.temp_mark = false;
        }
        
        


        order = network.nodes.length;
        
        int num_marked = 0;
        
        while(num_marked < network.nodes.length)
        {

            for(Node n : network.nodes)
            {
                if(n.top_order == -1)
                {
                    num_marked += visit(n);
                }
            }
        }

        
        sorted = new Node[network.nodes.length];
        
        for(Node n : network.nodes)
        {
            sorted[n.top_order-1] = n;
        }

    }
    
    
    
    public boolean testTopologicalSort()
    {
        topologicalSort();
        
        for(int idx = 0; idx < flow.length; idx++)
        {
            if(!contains[idx])
            {
                continue;
            }
            Link l = network.links[idx];
            
            if(l.getSource().top_order > l.getDest().top_order)
            {
                return false;
            }
        }
        return true;
    }
    
    public boolean validateTopology()
    {
        for(int idx = 0; idx < flow.length; idx++)
        {
            if(!contains[idx])
            {
                continue;
            }
            Link l = network.links[idx];
            
            if(l.getSource().top_order > l.getDest().top_order)
            {
                return false;
            }
        }
        return true;
    }
    
    int order = 0;
    
    public int visit(Node u)
    {
        if(u.top_order >= 0)
        {
            return 0;
        }
        if(u.temp_mark)
        {
            throw new RuntimeException("Not DAG at "+u);
        }
        
        u.temp_mark = true;
        
        int output = 0;
        
        for(Link uv : u.getBushOutgoing(this))
        {
            output += visit(uv.getDest());
        }
        
        u.temp_mark = false;
        u.top_order = order--;
        output++;
        
        return output;
    }
    
    public Tree minUsedPath()
    {
        for(Node u : network.nodes)
        {
            u.cost = Integer.MAX_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        origin.cost = 0;
        
        for(Node u : sorted)
        {
            for(Link uv : u.getBushOutgoing(this))
            {
                if(flow[uv.getIdx()] == 0)
                {
                    continue;
                }
                
                Node v = uv.getDest();
                double temp = uv.getTT() + u.cost;
                
                if(temp < v.cost)
                {
                    v.cost = temp;
                    v.pred = uv;
                }
            }
        }
        
        Tree output = new Tree(origin);
        
        for(Node n : sorted)
        {
            output.put(n, n.pred);
        }
        
        return output;
    }
    
    public Tree minPath()
    {
        for(Node u : sorted)
        {
            u.cost = Integer.MAX_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        origin.cost = 0;
        
        for(Node u : sorted)
        {
            for(Link uv : u.getBushOutgoing(this))
            {
                
                Node v = uv.getDest();
                double temp = uv.getTT() + u.cost;
                
                if(temp < v.cost)
                {
                    v.cost = temp;
                    v.pred = uv;
                }
            }
        }
        
        Tree output = new Tree(origin);
        
        for(Node n : sorted)
        {
            output.put(n, n.pred);
        }
        
        return output;
    }
    
    public Tree maxUsedPath()
    {
        for(Node u : sorted)
        {
            u.cost = Integer.MIN_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        origin.cost = 0;
        
        for(Node u : sorted)
        {
            for(Link uv : u.getBushOutgoing(this))
            {
                if(flow[uv.getIdx()] == 0)
                {
                    continue;
                }
                
                Node v = uv.getDest();
                double temp = uv.getTT() + u.cost;
                
                if(temp > v.cost)
                {
                    v.cost = temp;
                    v.pred = uv;
                }
            }
        }
              
        Tree output = new Tree(origin);
        
        for(Node n : sorted)
        {
            output.put(n, n.pred);
        }
        
        return output;
    }
    
    public void equilibrate(double bush_gap)
    {
        if(Params.printBushEquilibrate)
        {
            System.out.println("Origin "+origin);
        }
        
        int swapIter = 0;
        
        double difference = Integer.MAX_VALUE;
        
        for(int bushIter = 1; !(bushIter > 2 && swapIter < 2); bushIter++)
        {
            difference = 0.0;
            
            swapIter = 0;
            
            
            
            do
            {
                
                difference = swapFlows();
                
                if(Params.printBushEquilibrate)
                {
                    System.out.println("\t\tSwap: "+String.format("%.2f", difference));
                }
                
                swapIter ++;
            }
            while(difference > bush_gap);
            
            if(Params.printBushEquilibrate)
            {
                System.out.println("\t"+bushIter+"\t"+difference+"\t"+testTopologicalSort());
            }
            
            improveBush();
        }
    }
    
    public double swapFlows()
    {

        topologicalSort();
        
        
        Tree min = minPath();
        Tree max = maxUsedPath();
        

        // start and end of common path segments
        Node m = null;
        Node n = null;
        
        double max_diff = 0.0;
        
        for(int ids = 0; ids < network.getNumZones(); ids++)
        {
            
            Zone s = (Zone)network.nodes[ids];
            n = s;
            
            if(origin.getDemand(s) == 0)
            {
                continue;
            }
            
            
            while(n != origin)
            {
                
                Iterable<Link> min_iter = min.iterator(n);
                Iterable<Link> max_iter = max.iterator(n);

                Set<Node> visited_min = new HashSet<Node>();


                for(Link l : min_iter)
                {
                    visited_min.add(l.getSource());
                }
                
                
                for(Link l : max_iter)
                {
                    if(visited_min.contains(l.getSource()))
                    {
                        m = l.getSource();
                        break;
                    }
                }
                
                

                min_iter = min.iterator(n);
                max_iter = max.iterator(n);

                Path min_path = new Path(true);
                Path max_path = new Path(true);

                for(Link l : min_iter)
                {
                    min_path.add(l);
                    if(l.getSource() == m)
                    {
                        break;
                    }
                }

                for(Link l : max_iter)
                {
                    max_path.add(l);
                    if(l.getSource() == m)
                    {
                        break;
                    }
                }

                //System.out.println(n+" "+m+" "+min_path+" "+max_path);
                
                max_diff = Math.max(max_diff, swapFlow(min_path, max_path));
                n = m;
                
                if(m==null)
                {
                    throw new RuntimeException("m is null");
                }
            }
        }
        
        return max_diff;
    }
    
    /**
     * return whether flow was swapped
     */
    public double swapFlow(Path min_path, Path max_path)
    {
        if(min_path.equals(max_path))
        {
            return 0.0;
        }
        
        double max_moved = Integer.MAX_VALUE;
        
        double stepsize = 1;
        
        for(Link l : max_path)
        {
            max_moved = Math.min(flow[l.getIdx()], max_moved);
        }
        
        //System.out.println(max_path);
        
        double difference = max_path.getTT() - min_path.getTT();
        


        while(max_moved >= Params.bush_gap && difference >= Params.bush_gap)
        {
            double deriv = max_path.getDeriv_TT() + min_path.getDeriv_TT();
            

            double y = Math.min(max_moved, stepsize * difference / deriv);
            
            //System.out.println(deriv+" "+y+" "+difference+" "+stepsize+" "+max_moved);

            for(Link l : max_path)
            {
                addFlow(l, -y);
            }
            
            for(Link l : min_path)
            {
                addFlow(l, y);
            }


            max_moved -= y;

            difference = max_path.getTT() - min_path.getTT();


        }
        

        return difference;
        
    }
    
    public boolean checkCost(Link l)
    {
        return l.getSource().cost + l.getTT() <= l.getDest().cost;
    }
    
    public void improveBush()
    {

        
        //Tree maxPath = maxUsedPath();
        //Tree minPath = minPath();
        
        
        //System.out.println(minPath.getPath(network.findNode(21)));
        //System.out.println(minPath.getPath(network.findNode(22)));
        
        
        
        
        shortestPath();
        
        List<Link> remove = new ArrayList<>();
        
        for(int idx = 0; idx < flow.length; idx++)
        {
            Link l = network.links[idx];
            
            if(flow[idx] == 0 && !checkCost(l))
            {
                remove.add(l);
            }
        }
        
        for(Link l : remove)
        {
            flow[l.getIdx()] = 0;
            contains[l.getIdx()] = false;
        }
        

        for(int idx = 0; idx < network.links.length; idx++)
        {
            Link l = network.links[idx];
            if(!contains(l) && checkCost(l))
            {
                contains[idx] = true;
            }
        }
    }
    
    public void loadDemand()
    {
        network.dijkstras(origin);
        
        for(int ids = 0; ids < network.getNumZones(); ids++)
        {
            Zone s = (Zone)network.nodes[ids];
            
            double d = origin.getDemand(s);
            
            Node curr = null;
            
            for(Node n : network.nodes)
            {
                if(n.getId() == s.getId())
                {
                    curr = n;
                    break;
                }
            }
            
            
            while(curr != origin)
            {
                Link uv = curr.pred;
                addFlow(uv, d);
                curr = uv.getSource();
            }
        }
    }
    
    // acyclic shortest path
    public void shortestPath()
    {
        topologicalSort();
        
        
        
        for(Node u : sorted)
        {
            u.cost = Integer.MAX_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        for(Node u : sorted)
        {
            for(Link uv : u.getBushOutgoing(this))
            {
                Node v = uv.getDest();
                double temp = uv.getTT() + u.cost;
                
                if(temp < v.cost)
                {
                    v.cost = temp;
                    v.pred = uv;
                }
                
                //System.out.println("testing "+uv+" "+u.cost+" "+temp+" "+nodes.contains(v)+" "+v.cost);
            }
        }
    }

    
    public void addFlow(Link l, double x)
    {
        l.x.addX(x);
        flow[l.getIdx()] += x;
        contains[l.getIdx()] = true;
    }
    
    
}
