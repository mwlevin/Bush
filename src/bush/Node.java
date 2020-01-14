/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author micha
 */
public class Node implements Comparable<Node>
{
    private static int next_idx = 0;
    
    
    private int idx;
    
    private int id;
  

    
    public int top_order;
    
    public double cost;
    public Link pred;
    
    
    public boolean temp_mark;
    
    private Link[] incoming, outgoing;
    
    public Node(int id)
    {
        idx = next_idx++;
        this.id = id;
    }
    
    public int getIdx()
    {
        return idx;
    }
    
    public int compareTo(Node rhs)
    {
        return top_order - rhs.top_order;
    }
    
    public boolean equals(Object o)
    {
        Node rhs = (Node)o;
        return rhs.getId() == getId();
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setIncoming(Link[] inc)
    {
        incoming = inc;
    }
    
    public void setOutgoing(Link[] out)
    {
        outgoing = out;
    }
    
    public String toString()
    {
        return ""+id;
    }
    
    public Link[] getIncoming()
    {
        return incoming;
    }
    
    public Link[] getOutgoing()
    {
        return outgoing;
    }
    
    
    public List<Link> getBushOutgoing(Bush b)
    {
        List<Link> output = new ArrayList<>();
        
        for(Link l : outgoing)
        {
            if(b.contains(l))
            {
                output.add(l);
            }
        }
        
        return output;
    }
    
    public List<Link> getBushIncoming(Bush b)
    {
        List<Link> output = new ArrayList<>();
        
        for(Link l : incoming)
        {
            if(b.contains(l))
            {
                output.add(l);
            }
        }
        
        return output;
    }
    
    public int hashCode()
    {
        return id;
    }
}
