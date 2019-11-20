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
    private int id;
    

    public Flow x;

    
    private double fftime, capacity, alpha, beta, length;
    private Node source, dest;
    
    public Link(Node source, Node dest, double fftime, double capacity, double alpha, double beta, double length)
    {
        this(Integer.parseInt(source.getId()+""+dest.getId()), source, dest, fftime, capacity, alpha, beta, length);
    }
    
    public Link(int id, Node source, Node dest, double fftime, double capacity, double alpha, double beta, double length)
    {
        this.id = id;
        this.source = source;
        this.dest = dest;
        this.fftime = fftime;
        this.capacity = capacity;
        this.alpha = alpha;
        this.beta = beta;
        this.length = length;
        
        x = new Flow();
        
        source.addLink(this);
        dest.addLink(this);

    }
    

    
    public int getId()
    {
        return id;
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
        return fftime * alpha * beta / capacity * Math.pow(x / capacity, beta - 1);
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
