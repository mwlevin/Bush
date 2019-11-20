/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author micha
 */
public class Node implements Comparable<Node>
{
    private int id;
    private String name;
    public Node cloned;
    
    public int top_order;
    
    public double cost;
    public Link pred;
    
    
    public boolean temp_mark;
    
    private Set<Link> incoming, outgoing;
    
    public Node(int id)
    {
        this(id, ""+id);
    }
    

    public Node(int id, String name)
    {
        this.id = id;
        this.name = name;
        incoming = new HashSet<>();
        outgoing = new HashSet<>();
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
    
    public void addLink(Link l)
    {
        if(l.getSource() == this)
        {
            outgoing.add(l);
        }
        else if(l.getDest() == this)
        {
            incoming.add(l);
        }
    }
    
    public String toString()
    {
        return ""+name;
    }
    
    public Set<Link> getIncoming()
    {
        return incoming;
    }
    
    public Set<Link> getOutgoing()
    {
        return outgoing;
    }
    
    
    public Set<Link> getBushOutgoing(Bush b)
    {
        Set<Link> output = new HashSet<>();
        
        for(Link l : outgoing)
        {
            if(b.contains(l))
            {
                output.add(l);
            }
        }
        
        return output;
    }
    
    public Set<Link> getBushIncoming(Bush b)
    {
        Set<Link> output = new HashSet<>();
        
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
