/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author micha
 */
public class Zone extends Node implements Dest
{
    protected Map<Dest, Double> demand;
    private double totaldemand;
    
    protected Bush bush;
    
    public Zone(int id, int numDests)
    {
        super(id);
        demand = new HashMap<>();

        
        totaldemand = 0;
    }
    /*
    public double[] copyDemand(){
        double[] output = new double[demand.length];
        
        for(int i = 0; i < output.length; i++){
            output[i] = demand[i];
        }
        
        return output;
    }
*/
    
    public Map<Dest, Double> copyDemand(){
        Map<Dest, Double> output = new HashMap<>();
        
        for(Dest s : demand.keySet()){
            output.put(s, demand.get(s));
        }
        
        return output;
    }
    
    public double getDemand(Dest s)
    {
        //return demand[s.getDestIdx()];
        return demand.get(s);
    }
    
    public double getTotalDemand(){
        return totaldemand;
    }
    
    public void setDemand(Dest s, double d)
    {
        totaldemand += d - demand.get(s);
        demand.put(s, d);
    }
    
    public int getDestIdx()
    {
        return getIdx();
    }
}
