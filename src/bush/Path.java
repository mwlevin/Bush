/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.ArrayList;

/**
 * 
 * @author micha
 */
public class Path extends ArrayList<Link>
{
    private boolean backwards;
    
    
    public Path()
    {
        backwards = false;
        
    }
    
    public boolean contains(Node n){
        for(Link l : this){
            if(l.getDest() == n){
                return true;
            }
        }
        
        if(size() > 0 && get(0).getSource() == n){
            return true;
        }
        
        return false;
    }
    
    public Path(boolean backwards)
    {
        this.backwards = backwards;
    }
    
    public double getDeriv_TT()
    {
        double output = 0.0;
        
        for(Link l : this)
        {
            output += l.getDeriv_TT();
        }
        
        return output;
    }
    
    public void addFlow(double y)
    {
        for(Link l : this)
        {
            l.x.addX(y);
        }
    }
    
    public double getTT()
    {
        double output = 0.0;
        
        for(Link l : this)
        {
            output += l.getTT();
        }
        
        return output;
    }
    
    
    public String toString()
    {
        if(size() == 0)
        {
            return "[]";
        }
        String output = "[";
        if(!backwards)
        {
            for(Link l : this)
            {
                output += l.getSource()+", ";
            }

            output += get(size()-1).getDest()+"]";
        }
        else
        {
            for(int i = size()-1; i >= 0; i--)
            {
                output += get(i).getSource()+", ";
            }
            
            output += get(size()-1).getDest()+"]";
        }
        return output;
    }

}
