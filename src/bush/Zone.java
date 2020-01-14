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
public class Zone extends Node 
{
    private double[] demand; // indexed to zones
    
    protected Bush bush;
    
    public Zone(int id, int numZones)
    {
        super(id);
        demand = new double[numZones];
    }
    
    public double getDemand(Zone s)
    {
        return demand[s.getIdx()];
    }
    
    public void setDemand(Zone z, double d)
    {
        demand[z.getIdx()] = d;
    }
}
