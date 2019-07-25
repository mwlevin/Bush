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
    
    public int top_order;
    
    public double cost;
    public Link pred;
    
    private Set<Link> incoming, outgoing;
    
    public Node(int id)
    {
        this.id = id;
        incoming = new HashSet<>();
        outgoing = new HashSet<>();
    }
    
    public int compareTo(Node rhs)
    {
        if(cost < rhs.cost)
        {
            return -1;
        }
        else if(cost > rhs.cost)
        {
            return 1;
        }
        else
        {
            return id = rhs.id;
        }
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
        return ""+id;
    }
    
    public Set<Link> getIncoming()
    {
        return incoming;
    }
    
    public Set<Link> getOutgoing()
    {
        return outgoing;
    }
    
    public int hashCode()
    {
        return id;
    }
}
