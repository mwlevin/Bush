/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bush;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author michaellevin
 */
public class PAS {
    private HashMap<Link, Integer> links;
    
    // set of origins relevant to this PAS
    private Set<Zone> relevant;
    
    // the last iteration where flow was shifted
    private int lastIterFlowShift;
    
    private Node start;
    private Link endLink;
    
    public PAS(){
        links = new HashMap<>();
        relevant = new HashSet<>();
    }
    
    public int getLastIterFlowShift(){
        return lastIterFlowShift;
    }
    
    public void setLastIterFlowShift(int iter){
        lastIterFlowShift = iter;
    }
    
    
    public void setEndLink(Link e){
        endLink = e;
    }
    
    public void addRelevantOrigin(Zone r){
        relevant.add(r);
        r.bush.addRelevantPAS(this);
    }
    
    public void setStart(Node s){
        start = s;
    }
    
    public Node getEnd(){
        return endLink.getDest();
    }
    
    public Link getEndLink(){
        return endLink;
    }
    
    public Node getStart(){
        return start;
    }
    
    public void addForwardLink(Link l){
        links.put(l, 1);
    }
    
    public void addBackwardLink(Link l){
        links.put(l, -1);
    }
    
    // max flow that can be shifted both in forward and negative directions
    
    
    private double getTT(double topshift){
        double output = 0;
        
        for(Link l : links.keySet()){
            // multiply by links.get(l) to indicate sign of shift (+ or -)
            output += links.get(l) * l.getTT(l.getFlow() + topshift * links.get(l));
        }
        
        return output;
    }
    
    public double getReducedCost(){
        return getEnd().cost - start.cost;
    }
    
    public boolean isFlowEffective(){
        // min flow of high cost segment
        // high cost segment is backwards links
        double minflow = Double.MAX_VALUE; 
        double flowlastsegment = 0;
        
        for(Link l : links.keySet()){
            // only look at high cost segment
            if(links.get(l) == -1){
                double totalFlow = 0;
                for(Zone r : relevant){
                    totalFlow += r.bush.getFlow(l);
                }
                
                minflow = Math.min(minflow, totalFlow);
                if(l.getDest() == getEnd()){
                    flowlastsegment = totalFlow;
                }
            }
        }
        
        return minflow >= Params.pas_flow_mu * flowlastsegment;
        
    }
    
    public boolean isCostEffective(){
        double cdiff = 0;
        
        // add cost on the high-cost segment (backwards link); subtract cost on the least-cost segment (forward links)
        for(Link l : links.keySet()){
            cdiff += -l.getTT() * links.get(l);
        }
        
        
        return cdiff >= Params.pas_cost_mu * getReducedCost();
    }
    
    public Map<Bush, Double> maxFlowShift(){
        
        Map<Bush, Double> maxFlowPerBush = new HashMap<>();
        
        for(Zone r : relevant){

            double max = Double.MAX_VALUE;
            
            for(Link l : links.keySet()){
                // check flow on link if l in backwards direction
                if(links.get(l) == -1){

                    max = Math.min(max, r.bush.getFlow(l));
                }
            }
            
            maxFlowPerBush.put(r.bush, max);
        }
        
        return maxFlowPerBush;
    
    }
    
    public void flowShift(){
        
        double overallMaxShift = 0;
        
        Map<Bush, Double> maxFlowShift = maxFlowShift();
        
        for(Bush b : maxFlowShift.keySet()){
            overallMaxShift += maxFlowShift.get(b);
        }

        
        double bot = 0;
        double top = overallMaxShift;
        
        while(top - bot > 0.01){
            double mid = top - bot;
            
            double check = getTT(mid);
            
            if(check < 0){
                bot = mid;
            }
            else{
                top = mid;
            }
        }
        
        for(Link l : links.keySet()){
            for(Bush b : maxFlowShift.keySet()){
                // proportion allocated to bush is bush max shift / total max shift
                // multiply by links.get(l) to indicate sign of shift (+ or -)
                b.addFlow(l, maxFlowShift.get(b) / overallMaxShift * top * links.get(l));
            }
        }
    }
    
    public String toString(){
        String output = "[";
        for(Link l : links.keySet()){
            if(links.get(l) > 0){
                output += l+", ";
            }
        }
        
        for(Link l : links.keySet()){
            if(links.get(l) < 0){
                output += "-"+l+", ";
            }
        }
        
        output = output.substring(0, output.length()-2);
        output += "]";
        
        return output;
    }
    
}
