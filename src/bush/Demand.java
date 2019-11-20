/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author micha
 */
public class Demand extends HashMap<Node, HashMap<Node, Double>>
{
    private double total;
    
    public Demand()
    {
        
    }
    
    public Map<Node, Double> getDemand(Node r)
    {
        return get(r);
    }
    
    public double getTotal()
    {
        return total;
    }
    
    public double getDemand(Node r, Node s)
    {
        if(containsKey(r))
        {
            Map<Node, Double> temp = get(r);
            
            if(temp.containsKey(s))
            {
                return temp.get(s);
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return 0;
        }
    }
    
    public Set<Node> getOrigins()
    {
        return keySet();
    }
    
    public Set<Node> getDests(Node r)
    {
        if(!containsKey(r))
        {
            return new HashSet<Node>();
        }
        else
        {
            return get(r).keySet();
        }
    }
    
    public void addDemand(Node r, Node s, double d)
    {
        HashMap<Node, Double> temp;
        
        if(containsKey(r))
        {
            temp = get(r);
        }
        else
        {
            put(r, temp = new HashMap<>());
        }
        
        if(temp.containsKey(s))
        {
            total -= temp.get(s);
        }
        total += d;
        temp.put(s, d);
    }
}
