/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author michaellevin
 */
public class PAS {
    private List<Link> forwardlinks;
    private List<Link> backwardlinks;
    
    // set of origins relevant to this PAS
    private Set<Zone> relevant;
    
    // the last iteration where flow was shifted
    private int lastIterFlowShift;
    
    private Node start;
    
    public PAS(){
        forwardlinks = new ArrayList<>();
        backwardlinks = new ArrayList<>();
        relevant = new HashSet<>();
    }
    
    public Set<Zone> getRelevantOrigins(){
        return relevant;
    }
    
    public int getLastIterFlowShift(){
        return lastIterFlowShift;
    }
    
    public void setLastIterFlowShift(int iter){
        lastIterFlowShift = iter;
    }
    
    
    
    public void addRelevantOrigin(Zone r){
        relevant.add(r);
        r.bush.addRelevantPAS(this);
    }
    
    public void setStart(Node s){
        start = s;
    }
    
    public Node getEnd(){
        return forwardlinks.get(0).getDest();
    }
    
    public Link getEndLinkFwd(){
        return forwardlinks.get(0);
    }
    
    public Link getEndLinkBwd(){
        return backwardlinks.get(backwardlinks.size()-1);
    }
    
    public Node getStart(){
        return start;
    }
    
    public void addForwardLink(Link l){
        forwardlinks.add(l);
    }
    
    public void addBackwardLink(Link l){
        backwardlinks.add(l);
    }
    
    // max flow that can be shifted both in forward and negative directions
    
    
    public double getTT(double topshift){
        double output = 0;
        
        for(Link l : forwardlinks){
            output += l.getTT(l.getFlow() + topshift);
        }
        
        for(Link l : backwardlinks){
            output -= l.getTT(l.getFlow() - topshift);
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
        
        for(Link l : backwardlinks){
            // only look at high cost segment
            double totalFlow = 0;
            for(Zone r : relevant){
                totalFlow += r.bush.getFlow(l);
            }

            minflow = Math.min(minflow, totalFlow);
            if(l.getDest() == getEnd()){
                flowlastsegment = totalFlow;
            }
        }
        
        return minflow >= Params.pas_flow_mu * flowlastsegment;
        
    }
    
    
    public boolean isCostEffective(){
        
        double forwardcost = getForwardCost();
        double backwardcost = getBackwardCost();
        
        double costdiff = backwardcost - forwardcost;
        
        // maybe the forward and backward costs will be reversed sometimes
        return Math.abs(costdiff) > Params.pas_cost_mu * forwardcost;
    }
    
    public boolean isCostEffective(Link a){
        
        double forwardcost = getForwardCost();
        double backwardcost = getBackwardCost();
        
        double costdiff = 0;
        
        
        if(a == getEndLinkBwd()){
            costdiff = backwardcost - forwardcost;
        }
        else if(a == getEndLinkFwd()){
            costdiff = forwardcost - backwardcost;
        }
        
        // maybe the forward and backward costs will be reversed sometimes
        return costdiff > Params.pas_cost_mu * forwardcost;
    }
    
    public double getForwardCost(){
        double forwardcost = 0;
        
        for(Link l : forwardlinks){
            forwardcost += l.getTT();
        }
        
        return forwardcost;
    }
    
    public double getBackwardCost(){
        double backwardcost = 0;
        
        for(Link l : backwardlinks){
            backwardcost += l.getTT();
        }
        
        return backwardcost;
    }
    
    public double getCostDifference(){
        return Math.abs(getBackwardCost() - getForwardCost());
    }
    
    public double maxFlowShift(Bush b){
        double max = Double.MAX_VALUE;
            
        for(Link l : backwardlinks){
            // check flow on link if l in backwards direction
            max = Math.min(max, b.getFlow(l));

        }
        return max;
    }
    
    public double maxBackwardFlowShift(Bush b){
        double max = Double.MAX_VALUE;
            
        for(Link l : forwardlinks){
            // check flow on link if l in backwards direction
            max = Math.min(max, b.getFlow(l));

        }
        return max;
    }
    
    public Map<Bush, Double> maxFlowShift(){
        
        Map<Bush, Double> maxFlowPerBush = new HashMap<>();
        
        for(Zone r : relevant){
            maxFlowPerBush.put(r.bush, maxFlowShift(r.bush));
        }
        
        return maxFlowPerBush;
    
    }
    
    public Map<Bush, Double> maxBackwardFlowShift(){
        
        Map<Bush, Double> maxFlowPerBush = new HashMap<>();
        
        for(Zone r : relevant){
            maxFlowPerBush.put(r.bush, maxBackwardFlowShift(r.bush));
        }
        
        return maxFlowPerBush;
    
    }
    
    public boolean flowShift(){
        
        double forwardcost = getForwardCost();
        double backwardcost = getBackwardCost();
        
        double costdiff = backwardcost - forwardcost;
        
        // maybe the forward and backward costs will be reversed sometimes
        if(Math.abs(costdiff) < Params.pas_cost_mu * forwardcost){
            return false;
        }
        
        int backwards = (backwardcost > forwardcost ? 1:-1);

        
        double overallMaxShift = 0;
        
        Map<Bush, Double> maxFlowShift;
        
        if(backwards > 0){
            maxFlowShift = maxFlowShift();
        }
        else{
            maxFlowShift = maxBackwardFlowShift();
        }
        
        for(Bush b : maxFlowShift.keySet()){
            overallMaxShift += maxFlowShift.get(b);
        }
        
        if(overallMaxShift < Params.pas_flow_mu){
            return false;
        }

        //System.out.println("max shift "+overallMaxShift+" "+backwards+" "+backwardcost+" "+forwardcost);
        
        double bot = 0;
        double top = overallMaxShift;
        
        while(top - bot > 0.01){
            double mid = (top + bot)/2;
            
            double check = getTT(mid * backwards);
            
            //System.out.println("\t"+bot+" "+top+" "+mid+" "+check);
            
            if(check*backwards < 0){
                bot = mid;
            }
            else{
                top = mid;
            }
        }
        

        for(Link l : forwardlinks){
            for(Bush b : maxFlowShift.keySet()){
                // proportion allocated to bush is bush max shift / total max shift
                b.addFlow(l, maxFlowShift.get(b) / overallMaxShift * top * backwards);
            }
        }
        
        for(Link l : backwardlinks){
            for(Bush b : maxFlowShift.keySet()){
                // proportion allocated to bush is bush max shift / total max shift
                b.addFlow(l, -maxFlowShift.get(b) / overallMaxShift * top * backwards);
            }
        }
        
        //System.out.println("after shift "+getTT(0));
        
        return true;
    }
    
    public String toString(){
        String output = "[";
        for(Link l : forwardlinks){
            output += l+", ";
        }
        
        for(Link l : backwardlinks){
            output += "-"+l+", ";
        }
        
        output = output.substring(0, output.length()-2);
        output += "]";
        
        return output;
    }
    
}
