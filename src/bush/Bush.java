/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.ArrayList;
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
public class Bush extends Network
{
    private Node origin;

    private Map<Node, Double> demand;
    
    public Bush(Node origin_, Network network)
    {
        super(network);
        
        demand = new HashMap<>();
        
        Map<Integer, Node> nodesidmap = new HashMap<>();
        
        for(Node n : network.getNodes())
        {
            Node clone = new Node(n.getId(), n.getId()+"-"+origin_.getId());
            nodesidmap.put(clone.getId(), clone);
            nodes.add(clone);
        }
        
        Demand demand_ = network.getDemand();
        for(Node s : demand_.getDests(origin_))
        {
            demand.put(nodesidmap.get(s.getId()), demand_.getDemand(origin_, s));
        }
        
        this.origin = nodesidmap.get(origin_.getId());
        
        for(Link l : network.getLinks())
        {
            links.add(new Link(l, nodesidmap.get(l.getSource().getId()), nodesidmap.get(l.getDest().getId())));
        }
        
        
        loadDemand();
    }
    
    public void topologicalSort()
    {
        for(Node n : nodes)
        {
            n.top_order = -1;
            n.temp_mark = false;
        }
        
        order = nodes.size();
        
        int num_marked = 0;
        
        while(num_marked < nodes.size())
        {
            for(Node n : nodes)
            {
                if(n.top_order == -1)
                {
                    num_marked += visit(n);
                }
            }
        }
        
        
        
        Collections.sort(nodes);

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
            throw new RuntimeException("Not DAG");
        }
        
        u.temp_mark = true;
        
        int output = 0;
        
        for(Link uv : u.getBushOutgoing())
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
        for(Node u : nodes)
        {
            u.cost = Integer.MAX_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        origin.cost = 0;
        
        for(Node u : nodes)
        {
            for(Link uv : u.getBushOutgoing())
            {
                if(uv.bush_x == 0)
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
        
        for(Node n : nodes)
        {
            output.put(n, n.pred);
        }
        
        return output;
    }
    
    public Tree minPath()
    {
        for(Node u : nodes)
        {
            u.cost = Integer.MAX_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        origin.cost = 0;
        
        for(Node u : nodes)
        {
            for(Link uv : u.getBushOutgoing())
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
        
        for(Node n : nodes)
        {
            output.put(n, n.pred);
        }
        
        return output;
    }
    
    public Tree maxUsedPath()
    {
        for(Node u : nodes)
        {
            u.cost = Integer.MIN_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        origin.cost = 0;
        
        for(Node u : nodes)
        {
            for(Link uv : u.getBushOutgoing())
            {
                if(uv.bush_x == 0)
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
        
        for(Node n : nodes)
        {
            output.put(n, n.pred);
        }
        
        return output;
    }
    
    public void swapFlows()
    {
        topologicalSort();
        
        Tree min = minPath();
        Tree max = maxUsedPath();


        // start and end of common path segments
        Node m = null;
        Node n = null;
        
        for(Node s : demand.keySet())
        {
            n = s;
            
            while(n != origin)
            {
                Iterable<Link> min_iter = min.iterator(n);
                Iterable<Link> max_iter = max.iterator(n);

                Set<Node> visited_min = new HashSet<Node>();


                for(Link l : min_iter)
                {
                    visited_min.add(l.getSource());
                }
                
                System.out.println(visited_min);

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

                System.out.println(n+" "+m+" "+min_path+" "+max_path);
                
                swapFlow(min_path, max_path);
                n = m;
                
            }
        }
    }
    
    /**
     * return whether flow was swapped
     */
    public boolean swapFlow(Path min_path, Path max_path)
    {
        if(min_path.equals(max_path))
        {
            return false;
        }
        
        double max_moved = Integer.MAX_VALUE;
        
        double stepsize = 0.2;
        
        for(Link l : max_path)
        {
            max_moved = Math.min(l.bush_x, max_moved);
        }
        
        
        
        double difference = max_path.getTT() - min_path.getTT();
        
        if(max_moved < 0.1 || difference < 0.1)
        {
            return false;
        }

        while(difference > 0.1)
        {
            double deriv = max_path.getDeriv_TT() + min_path.getDeriv_TT();

            double y = Math.min(max_moved, stepsize * difference / deriv);
            
            System.out.println(deriv+" "+y);

            for(Link l : max_path)
            {
                l.x.addX(-y);
                l.bush_x -= y;
            }
            
            for(Link l : min_path)
            {
                l.x.addX(y);
                l.bush_x += y;
            }


            max_moved -= y;

            difference = max_path.getTT() - min_path.getTT();
        }
        

        return true;
        
    }
    
    public void improveBush()
    {
        
    }
    
    public void loadDemand()
    {
        dijkstras(origin);
        
        for(Node s : demand.keySet())
        {
            double d = demand.get(s);
            
            Node curr = null;
            
            for(Node n : nodes)
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
                uv.x.addX(d);
                uv.bush_x += d;
                uv.inBush = true;
                curr = uv.getSource();
            }
        }
    }
    
    // acyclic shortest path
    public void shortestPath()
    {
        topologicalSort();
        
        
        
        for(Node u : nodes)
        {
            u.cost = Integer.MAX_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        for(Node u : nodes)
        {
            for(Link uv : u.getBushOutgoing())
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
    
    
}
