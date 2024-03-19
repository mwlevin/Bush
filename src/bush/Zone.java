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
    protected double[] demand; // indexed to zones
    private double totaldemand;
    
    protected Bush bush;
    
    public Zone(int id, int numDests)
    {
        super(id);
        demand = new double[numDests];
        totaldemand = 0;
    }
    
    public double[] copyDemand(){
        double[] output = new double[demand.length];
        
        for(int i = 0; i < output.length; i++){
            output[i] = demand[i];
        }
        
        return output;
    }
    
    public double getDemand(Dest s)
    {
        return demand[s.getDestIdx()];
    }
    
    public double getTotalDemand(){
        return totaldemand;
    }
    
    public void setDemand(Dest s, double d)
    {
        int i = s.getDestIdx();
        totaldemand += d - demand[i];
        demand[i] = d;
    }
    
    public int getDestIdx()
    {
        return getIdx();
    }
}
