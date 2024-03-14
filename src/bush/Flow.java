/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

/**
 *
 * @author micha
 */
public class Flow 
{
    private double x;
    
    public double x_star;
    
    
    public Flow()
    {
        x = 0;
    }
    
    public void update(double stepsize)
    {
        x = getXPrime(stepsize);
    }
    
    public double getXPrime(double stepsize)
    {
        return x * (1 - stepsize) + x_star * stepsize;
    }
    
    public double getX()
    {
        return x;
    }
    
    public void setX(double x)
    {
        this.x = x;
    }
    
    public void addX(double delta)
    {
        if(x + delta < -Params.flow_epsilon){
            throw new RuntimeException("Negative flow on link - "+(x+delta));
        }
        x += delta;
    }
}
