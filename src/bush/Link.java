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
    

    public Flow x;

    
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
        
        x = new Flow();
    }
    

    public int getIdx()
    {
        return idx;
    }
    
    public int getId()
    {
        return id;
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
        return getTT(x.getX());
    }
    
    public double getDeriv_TT(double x)
    {
        if(beta == 0)
        {
            return 0;
        }
        return fftime * alpha * beta / capacity * Math.pow(x / capacity, beta - 1);
    }
    
    public double getReducedCost()
    {
        return dest.cost - (source.cost + getTT());
    }
    
    public double getDeriv_TT()
    {
        return getDeriv_TT(x.getX());
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
        return fftime * (1 + alpha * Math.pow(x / capacity, beta));
    }
}
