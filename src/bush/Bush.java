/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author micha
 */
public class Bush 
{
    private Node origin;
    
    private List<Node> nodes;
    
    private Network network;
    
    public Bush(Node origin, List<Node> nodes, Network network)
    {
        this.origin = origin;
        this.nodes = nodes;
    }
    
    public void topologicalSort()
    {
        for(Node n : nodes)
        {
            n.top_order = -1;
        }
        
        List<Node> Q = new LinkedList<>();
        
        Q.add(origin);
        
        int id = 0;
        
        while(!Q.isEmpty())
        {
            Node u = Q.remove(0);
            u.top_order = id++;
            
            for(Link uv : u.getOutgoing())
            {
                Node v = uv.getDest();
                if(v.top_order == -1)
                {
                    Q.add(v);
                }
            }
        }
        
        Collections.sort(nodes);
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
            for(Link uv : u.getOutgoing())
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
            for(Link uv : u.getOutgoing())
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
    
    // acyclic shortest path
    public void shortestPath()
    {
        for(Node u : nodes)
        {
            u.cost = Integer.MAX_VALUE;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        for(Node u : nodes)
        {
            for(Link uv : u.getOutgoing())
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
    }
    
    
}
