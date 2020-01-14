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
public class Zone extends Node implements Dest
{
    private double[] demand; // indexed to zones
    
    protected Bush bush;
    
    public Zone(int id, int numDests)
    {
        super(id);
        demand = new double[numDests];
    }
    
    public double getDemand(Dest s)
    {
        return demand[s.getDestIdx()];
    }
    
    public void setDemand(Dest s, double d)
    {
        demand[s.getDestIdx()] = d;
    }
    
    public int getDestIdx()
    {
        return getIdx();
    }
}
