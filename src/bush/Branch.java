/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author micha
 */
public class Branch {
    private Map<Link, Double> linkflows;
    private double maxflow;
    private Bush bush;
    private Link endlink;
    private Set<Link> minpath;
    

    // this is just a placeholder that says this branch has a specific endlink and a minpath. The actual links in the branch will be determined later.
    public Branch(Bush bush, Link endlink, Set<Link> minpath){
        this.bush = bush;
        linkflows = new HashMap<>();
        
        this.endlink = endlink;
        this.minpath = minpath;
    }
    
    public void init(){
        
        //bush.network.printLinkFlows();
        
        for(Node n : bush.network.nodes){
            n.visited = false;
        }
        
        Stack<Node> unvisited = new Stack<>();
        
        Set<Link> branchlinks = new HashSet<>();
        
        unvisited.add(endlink.getSource());
        
        while(!unvisited.isEmpty()){
            Node j = unvisited.pop();
            
            for(Link ij : j.getIncoming()){
 
                if(bush.contains(ij)){
                    Node i = ij.getSource();
                    
                    branchlinks.add(ij);
                    
                    
                    if(!i.visited){
                        unvisited.push(i);
                        i.visited = true;
                    }
                }
            }
        }


        maxflow = bush.getFlow(endlink);
        // now do Ford-Fulkerson to figure out branch flow on each link
        // the "capacities" are the bush flow on each link
        // due to conservation of flow I don't need to add flow in reverse. DFS will be sufficient.
        
        
        for(Link l : branchlinks){
            linkflows.put(l, 0.0);
        }
        
        Node start = bush.getOrigin();
        Node end = endlink.getSource();
        linkflows.put(endlink, maxflow);
        

        /*
        for(Link l : branchlinks){
            System.out.println(l+" "+bush.getFlow(l));
        }
        System.out.println("end at "+endlink+" "+bush.getFlow(endlink)+" "+maxflow);
        */
        
        double assignedFlow = 0;
        
        // while there is flow left to assign
        // use flow epsilon to avoid numerical error causing infinite loop
        while(maxflow - assignedFlow > Params.flow_epsilon){
            
            //System.out.println(maxflow+" "+assignedFlow);
            
            // DFS find path
            unvisited.clear();
            unvisited.push(start);
            
            for(Node n : bush.network.nodes){
                n.visited = false;
                n.pred2 = null;
            }
            
            start.visited = true;
            
            while(!unvisited.isEmpty()){
                Node i = unvisited.pop();
                

                // once DFS finds a path, stop and add flow. That path will become unusable
                if(i == end){
                    break;
                }
                
                ArrayList<Link> expanded = new ArrayList<>();
                for(Link ij : i.getOutgoing()){
                    // only expand links with positive bush flow - temporary branch flow
                    if(branchlinks.contains(ij) && !ij.getDest().visited && bush.getFlow(ij) - linkflows.get(ij) > Params.flow_epsilon){
                        expanded.add(ij);
                    }
                }
                
                // sort in order of decreasing flow
                Collections.sort(expanded, new Comparator<Link>(){
                    public int compare(Link i, Link j){
                        double flowi = bush.getFlow(i) - linkflows.get(i);
                        double flowj = bush.getFlow(j) - linkflows.get(j);
                        return (int)Math.ceil(flowj - flowi);
                    }
                });
                
                for(Link ij : expanded){
                    Node j = ij.getDest();
                    j.pred2 = ij;
                    j.visited = true;
                    unvisited.push(j);
                }
            }
            
            // trace path and label flows
            Path augmentedPath = bush.tracePath2(start, end);
            
            
            double sendFlow = maxflow - assignedFlow;
            
            for(Link l : augmentedPath){
                sendFlow = Math.min(sendFlow, bush.getFlow(l) - linkflows.get(l));
            }
            
            for(Link l : augmentedPath){
                linkflows.put(l, linkflows.get(l) + sendFlow);
            }
            
       
            assignedFlow += sendFlow;
        }
    }
    
    


    
    public String toString(){
        return endlink+" : "+linkflows.toString()+" ; "+minpath;
    }
    

    
    

    
    public double flowShift(){
        
        
        
        
        double avgTT = getAvgTT(0);
        double minTT = getMinTT(0);
        
        //System.out.println("cost difference is "+avgTT+" "+minTT);
        
        // difference is too small to be worth shifting
        if(avgTT - minTT < minTT * Params.pas_cost_mu){
            
            return 0;
        }
        
        double bot = 0;
        double top = maxflow;
        
        while(top - bot > Params.line_search_gap){
            double mid = (bot+top)/2;
            
            double newTTDiff = getAvgTT(mid) - getMinTT(mid);
            
            //System.out.println(bot+" "+mid+" "+top+" "+getAvgTT(mid)+" "+getMinTT(mid)+" "+newTTDiff);
            
            if(newTTDiff > 0){
                // shift more
                bot = mid;
            }
            else{
                // shift less
                top = mid;
            }
            
        }
        
        propAddFlow(bot);
        
        if(Params.PRINT_PAS_INFO){
            System.out.println("after shift is "+getAvgTT(0)+" "+getMinTT(0)+" "+maxflow);
        }
        
        
        return top;

    }
    
    public double getMinTT(double shift){
        double output = 0;
        
        // this is used in case the min links are also on the branch
        double prop = shift/maxflow;
        
        for(Link l : minpath){
            double newflow = l.getFlow();
            
            // add flow to the minpath
            newflow += shift;
            
            // if link is in branch, some flow will be shifted:
            if(linkflows.containsKey(l)){
                newflow -= prop * linkflows.get(l);
            }
            
            output += l.getTT(newflow);
        }
        
        return output;
        
    }
    
    
    // consider shifting flow from branch to minpath
    public double getAvgTT(double shift){
        double output = 0;
        
        // proportional shift based on how much of the branch flow is on each link
        double prop = shift/maxflow;

        
        for(Link l : linkflows.keySet()){
            
            // subtract the flowshift
            double flowchange = linkflows.get(l)*prop;
            double newflow = l.getFlow() - flowchange;
            
            // if link is on minpath, then add the entire shift to it
            
            if(minpath.contains(l)){
                newflow += shift;
            }


            output += (linkflows.get(l) - flowchange) * l.getTT(newflow);
        }
        
        return output / (maxflow - shift);
    }
    
    public void propAddFlow(double shift){
        double prop = shift/maxflow;

        
        for(Link l : linkflows.keySet()){
            bush.addFlow(l, -linkflows.get(l)*prop);
            
            // this isn't needed: the branch will be discarded after equilibrating.
            if(Params.DEBUG_CHECKS){
                linkflows.put(l, linkflows.get(l) * (1-prop));
            }
        }
        
        for(Link l : minpath){
            bush.addFlow(l, shift);
        }
        
        maxflow -= shift;
    }
}
