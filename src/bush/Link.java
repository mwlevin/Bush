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
public class Link 
{
    private static int next_idx = 0;
    
    
    private int idx;
    
    private int id;
    

    private double x;
    public double x_star;

    
    private double fftime, capacity, alpha, beta, length;
    private Node source, dest;
    
    public Link(Node source, Node dest, double fftime, double capacity, double alpha, double beta, double length)
    {
        this(Integer.parseInt(source.getId()+""+((int)Math.abs(dest.getId()))), source, dest, fftime, capacity, alpha, beta, length);
    }
    
    public Link(int id, Node source, Node dest, double fftime, double capacity, double alpha, double beta, double length)
    {
        idx = next_idx++;
        this.id = id;
        this.source = source;
        this.dest = dest;
        this.fftime = fftime;
        this.capacity = capacity;
        this.alpha = alpha;
        this.beta = beta;
        this.length = length;
        
        x = 0;
    }
    
    public boolean hasHighReducedCost(double percent){
        double reducedCost = dest.cost - source.cost;
        double tt = getTT();
        
        return tt - reducedCost > tt*percent;
    }
    
    public int getIdx()
    {
        return idx;
    }
    
    public int getId()
    {
        return id;
    }
    
    public double getFlow(){
        return x;
    }
    
    
    public double getAlpha()
    {
        return alpha;
    }
    
    public double getBeta()
    {
        return beta;
    }
    
    public double getFFTime()
    {
        return fftime;
    }
    
    public double getCapacity()
    {
        return capacity;
    }
    
    public double getTT()
    {
        return getTT(x);
    }
    
    public double getDeriv_TT(double x)
    {
        if((""+x).equals("NaN"))
        {
            throw new RuntimeException("Flow is NaN");
        }
        
        if(x < -Params.bush_gap)
        {
            throw new RuntimeException("Flow is negative");
        }
        
        if(x < 0)
        {
            x = 0;
        }
        
        if(beta == 0)
        {
            return 0;
        }
        
  
        double output = fftime * alpha * beta / capacity * Math.pow(x / capacity, beta - 1);
        
        if((""+output).equals("NaN"))
        {
           throw new RuntimeException("Deriv is NaN"); 
        }
        
        return output;
    }
    
    public double getReducedCost()
    {
        return dest.cost - (source.cost + getTT());
    }
    
    public double getDeriv_TT()
    {
        return getDeriv_TT(x);
    }
    
    public double getInt_TT(double x)
    {
        return fftime * x + alpha * fftime / (beta + 1) * Math.pow(x / capacity, beta+1);
    }
    
    
    public Node getSource()
    {
        return source;
    }
    
    public Node getDest()
    {
        return dest;
    }
    
    public int hashCode()
    {
        return id;
    }
    
    public String toString()
    {
        return "["+source+","+dest+"]";
    }
    
    public double getTT(double x)
    {   
        if(x < -Params.bush_gap)
        {
            throw new RuntimeException("Flow is negative");
        }
        
        if(x < 0)
        {
            x = 0;
        }
        return fftime * (1 + alpha * Math.pow(x / capacity, beta));
    }
    
    public void update(double stepsize)
    {
        x = getXPrime(stepsize);
    }
    
    public double getXPrime(double stepsize)
    {
        return x * (1 - stepsize) + x_star * stepsize;
    }
    
    public void addX(double delta)
    {
        if(x + delta < -Params.flow_epsilon){
            throw new RuntimeException("Negative flow on link - "+(x+delta));
        }
        x += delta;
    }
    
    public void setFlow(double x){
        this.x = x;
    }
}
