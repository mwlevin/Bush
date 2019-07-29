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
}
