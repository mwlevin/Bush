/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author micha
 */
public class Bush
{
    private Zone origin;

    private double[] flow; // use the indices of each link
    private boolean[] contains; 
    
    private Network network;
    
    private PASList relevantPAS;
    
    private ArrayList<Node> sorted;
    
    public Bush(Zone origin, Network network)
    {
        
        this.network = network;

        this.origin = origin;
        origin.bush = this;
        
        relevantPAS = new PASList();
        
        sorted = new ArrayList<>();
        flow = new double[network.getLinks().length];
        contains = new boolean[network.getLinks().length];
        
        loadDemand();
        
        
        
        
       
    }
    
    
    
    
    public double getFlow(Link l){
        return flow[l.getIdx()];
    }
    
    
    public boolean contains(Link l)
    {
        return contains[l.getIdx()];
    }
    
    
    public void topologicalSort()
    {
        for(Node n : network.nodes)
        {
            n.in_degree = n.getBushIncoming(this).size();
            n.visited = false;
            n.top_order = -1;
        }
        
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(origin);
        origin.visited = true;
        
        
        sorted.clear();
        
        int idx = 0;
        
        while(!queue.isEmpty())
        {
            Node vertex = queue.remove();
            sorted.add(vertex);
            vertex.top_order = idx;
            idx++;
            
            for(Link ij : vertex.getBushOutgoing(this))
            {
                Node j = ij.getDest();
                
                
                if(!j.visited)
                {
                    j.in_degree--;
                    
                    if(j.in_degree == 0)
                    {
                        queue.add(j);
                        j.visited = true;
                    }
                }
            }
        }

        // check for nodes that were not completed
        for(Node n : network.nodes)
        {
            if(n.in_degree < n.getBushIncoming(this).size() && !n.visited)
            {
                System.out.println(origin+"\t"+n+"\t"+n.getBushIncoming(this)+"\t"+n.getBushOutgoing(this)+"\t"+n.top_order);
                
                for(Link l : n.getBushOutgoing(this))
                {
                    System.out.println("\t"+l.getDest()+"\t"+l.getDest().getBushIncoming(this)+"\t"+l.getDest().top_order);
                }
                
                for(Link l : n.getBushOutgoing(this))
                {
                    System.out.println("\t\t"+l+"\t"+flow[l.getIdx()]+"\t"+l.getReducedCost());
                }
                for(Link l : n.getBushIncoming(this))
                {
                    System.out.println("\t\t"+l+"\t"+flow[l.getIdx()]+"\t"+l.getReducedCost());
                }
                throw new RuntimeException("Not DAG origin "+origin);
            }
        }
    }
    
    
    
    public boolean testTopologicalSort()
    {
        topologicalSort();
        
        for(int idx = 0; idx < flow.length; idx++)
        {
            if(!contains[idx])
            {
                continue;
            }
            Link l = network.links[idx];
            
            if(l.getSource().top_order > l.getDest().top_order)
            {
                return false;
            }
        }
        return true;
    }
    
    public boolean containsNode(List<Node> nodes, int id)
    {
        for(Node n : nodes)
        {
            if(n.getId() == id)
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkReducedCosts()
    {
        return checkReducedCosts(false);
    }
    
    public boolean checkReducedCosts(boolean print)
    {
        minPath();
        
        boolean output = true;
        for(Link l : network.links)
        {
            if(flow[l.getIdx()] > Params.bush_gap && l.getReducedCost() <- Params.bush_gap)
            {
                if(print)
                {
                    System.out.println("Negative reduced cost origin "+origin);
                    System.out.println(l+"\t"+flow[l.getIdx()]+"\t"+l.getSource().cost+"\t"+l.getTT()+"\t"+l.getDest().cost + "\t"+
                            (l.getDest().cost - (l.getSource().cost + l.getTT())));
                }
                output = false;
            }
        }
        return output;
    }
    
    public boolean validateTopology()
    {
        for(int idx = 0; idx < flow.length; idx++)
        {
            if(!contains[idx])
            {
                continue;
            }
            Link l = network.links[idx];
            
            if(l.getSource().top_order > l.getDest().top_order)
            {
                return false;
            }
        }
        return true;
    }
    
    
    
    public Tree minUsedPath()
    {
        for(Node u : network.nodes)
        {
            u.cost = Params.INFTY;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        origin.cost = 0;
        
        for(Node u : sorted)
        {
            for(Link uv : u.getBushOutgoing(this))
            {
                if(flow[uv.getIdx()] == 0)
                {
                    continue;
                }
                
                Node v = uv.getDest();
                double temp = uv.getTT() + u.cost;
                
                if(temp < v.cost)
                {
                    v.cost = temp;
                    v.pred = uv;
                }
            }
        }
        
        Tree output = new Tree(origin, network);
        
        for(Node n : sorted)
        {
            output.put(n.pred);
        }
        
        return output;
    }
    
    public Tree minPath()
    {
        for(Node u : network.nodes)
        {
            u.cost = Params.INFTY;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        
        for(Node u : sorted)
        {
            
            for(Link uv : u.getBushOutgoing(this))
            {
                
                Node v = uv.getDest();
                
                double temp = uv.getTT() + u.cost;
                
                if(temp < v.cost)
                {
                    v.cost = temp;
                    v.pred = uv;
                }
            }

        }


        Tree output = new Tree(origin, network);
        
        for(Node n : sorted)
        {
            output.put(n.pred);
        }
        
        return output;
    }
    
    
    // look for links that need to be included in a PAS
    public void checkPAS(){
        
        Tree minPathTree = network.getSPTree(origin);
        
        
        
        // look for all used links not part of the tree of least cost routes
        for(Link l : network.links){
            if(getFlow(l) > 0 && !minPathTree.containsLink(l)){
                // we need a PAS!
                if(!hasRelevantPAS(l)){
                    
                    // should check if we can borrow one from network
                    PAS fromNetwork = network.findPAS(l, this);
                    if(fromNetwork == null){
                        System.out.println("Create PAS for "+l);
                        createPAS(minPathTree, l);
                    }
                    else{
                        System.out.println("Take PAS for "+l);
                        fromNetwork.addRelevantOrigin(origin);
                    }
                }
            }
        }
        
    }
    
    public boolean hasRelevantPAS(Link a){
        if(!relevantPAS.containsKey(a)){
            return false;
        }
        for(PAS p : relevantPAS.get(a)){
            if(p.isCostEffective() && p.isFlowEffective()){
                return true;
            }
        }
        
        return false;
    }
    
    public void removePAS(PAS p){
        relevantPAS.remove(p);
    }
    
    
    
    
    
    // create a PAS for link a
    public PAS createPAS(Tree minPathTree, Link a){
        
        
        
        PAS output = new PAS();
        
        // min path to a.dest
        Set<Node> minPath = minPathTree.getPathAsNodeSet(a.getDest());
        
        System.out.println("minPath is "+minPath);
        
        // store trace to avoid repeating breadth first search
        Map<Link, Link> trace = new HashMap<>();
        
        Queue<Link> unvisited = new LinkedList<>();
        
        unvisited.add(a);
        
        Link firstSimilar = null;
        
        while(!unvisited.isEmpty()){
            Link jk = unvisited.remove();
            Node j = jk.getSource();
            
            if(minPath.contains(j)){
                firstSimilar = jk;
                break;
            }
            
            
            
            for(Link ij : j.getIncoming()){
                if(getFlow(ij) > 0){
                    unvisited.add(ij);
                    trace.put(ij, jk);
                }
            }
        }
        
        System.out.println("firstSimilar is "+firstSimilar);
        
        // trace firstSimilar to a in min path tree: this is the forward side of the PAS
        for(Link l : minPathTree.trace(firstSimilar.getSource(), a.getDest())){
            output.addForwardLink(l);
        }
        
        // trace firstSimilar to a in used flow bush: this is the backward side of the PAS
        Link curr = firstSimilar;
        output.addBackwardLink(firstSimilar);
        do{
            curr = trace.get(curr);
            output.addBackwardLink(curr);
        }
        while(curr != a);
        
        output.setStart(firstSimilar.getDest());
        output.setEndLink(a);
        output.addRelevantOrigin(origin);
        
        System.out.println("PAS is "+output);
        
        network.addPAS(output);
        
        return output;
    }
    
    public PASList getRelevantPAS(){
        return relevantPAS;
    }
    
    
    
    public Tree maxUsedPath()
    {
        for(Node u : network.nodes)
        {
            u.cost = -Params.INFTY;
            u.pred = null;
        }
        
        origin.cost = 0;
        
        origin.cost = 0;
        
        for(Node u : sorted)
        {
            for(Link uv : u.getBushOutgoing(this))
            {
                if(flow[uv.getIdx()] == 0)
                {
                    continue;
                }
                
                Node v = uv.getDest();
                double temp = uv.getTT() + u.cost;
                
                if(temp > v.cost)
                {
                    v.cost = temp;
                    v.pred = uv;
                }
            }
        }
              
        Tree output = new Tree(origin, network);
        
        for(Node n : sorted)
        {
            output.put(n.pred);
        }
        
        return output;
    }
    
    public void equilibrate(double bush_gap)
    {
        topologicalSort();
        
        if(Params.printBushEquilibrate)
        {
            System.out.println("Origin "+origin);
        }
        
        int swapIter = 0;
        
        double difference = Params.INFTY;
        
        for(int bushIter = 1; !(bushIter > 2 && swapIter < 2); bushIter++)
        {
            difference = 0.0;
            
            swapIter = 0;
            
            
            
            do
            {
                
                difference = swapFlows();
                
                if(Params.printBushEquilibrate)
                {
                    System.out.println("\t\tSwap: "+String.format("%.2f", difference));
                }
                
                swapIter ++;
            }
            while(!checkReducedCosts());
            //while(difference > bush_gap);
            
            if(!checkReducedCosts(true))
            {
                Tree min = minPath();
                Tree max = maxUsedPath();
                
                int id = 11;
                Path minPath = min.getPath(network.findNode(id));
                System.out.println(minPath+"\t"+minPath.getTT());
                Path maxPath = max.getPath(network.findNode(id));
                System.out.println(maxPath +"\t"+maxPath.getTT()+"\t"+getMaxFlow(maxPath));
                
                System.out.println(swapFlow(minPath, maxPath));
                
                System.out.println(minPath+"\t"+minPath.getTT());
                System.out.println(maxPath +"\t"+maxPath.getTT()+"\t"+getMaxFlow(maxPath));
                
                checkReducedCosts();
                
                System.exit(0);
            }
            
            if(Params.printBushEquilibrate)
            {
                System.out.println("\t"+bushIter+"\t"+difference+"\t"+testTopologicalSort());
            }
            
            improveBush();
        }
        
        
        //validateDemand();
    }
    
    public double getMaxFlow(Path path)
    {
        double max_moved = Params.INFTY;
        for(Link l : path)
        {
            max_moved = Math.min(flow[l.getIdx()], max_moved);
        }
        return max_moved;
    }
    
    public void removeCyclicFlows(){
        
    }
    
    public double swapFlows()
    {

        Tree max = maxUsedPath();
        Tree min = minPath();
        

        // start and end of common path segments
        Node m = null;
        Node n = null;
        
        double max_diff = 0.0;
        
        for(int ids = network.getFirstDest(); ids <= network.getLastDest(); ids++)
        {
            
            Node s = network.nodes[ids];
            
            
            if(origin.getDemand((Dest)s) == 0)
            {
                continue;
            }
            
            Path min_path = min.getPath(s);
            Path max_path = max.getPath(s);
            
            max_diff = Math.max(max_diff, swapFlow(min_path, max_path));
            
            /*
            
            n = s;
            while(n != origin)
            {
                
                Iterable<Link> min_iter = min.iterator(n);
                Iterable<Link> max_iter = max.iterator(n);

                Set<Node> visited_min = new HashSet<Node>();


                for(Link l : min_iter)
                {
                    visited_min.add(l.getSource());
                }
                
                
                for(Link l : max_iter)
                {
                    if(visited_min.contains(l.getSource()))
                    {
                        m = l.getSource();
                        break;
                    }
                }
                
                if(m==null)
                {
                    System.out.println("origin="+origin);
                    
                    
                    System.out.println("n="+n+" "+n.getBushIncoming(this)+"\t"+n.top_order+"\t"+n.cost);
                    
                    
                    int id = 164;
                    Node node = network.findNode(id);
                    System.out.println(node+"\t"+node.getBushIncoming(this)+"\t"+node.getBushOutgoing(this)+"\t"+
                            node.top_order+"\t"+node.cost+"\t"+sorted.contains(node)+"\t"+node.in_degree);
                    id = 162;
                    node = network.findNode(id);
                    System.out.println(node+"\t"+node.getBushIncoming(this)+"\t"+node.getBushOutgoing(this)+"\t"+
                            node.top_order+"\t"+node.cost+"\t"+sorted.contains(node)+"\t"+node.in_degree);
                    System.out.println(visited_min);
                    
                    Link link = network.findLink(162, 164);
                    System.out.println(link+"\t"+link.getTT()+"\t"+flow[link.getIdx()]);
                    
                    link = network.findLink(164, 162);
                    System.out.println(link+"\t"+link.getTT()+"\t"+flow[link.getIdx()]);
                    
                    max_iter = max.iterator(n);
                    
                    for(Link l : max_iter)
                    {
                        System.out.print(l+" ");
                    }
                    System.out.println();
                    
                    throw new RuntimeException("m is null");
                }
                

                min_iter = min.iterator(n);
                max_iter = max.iterator(n);

                Path min_path = new Path(true);
                Path max_path = new Path(true);

                for(Link l : min_iter)
                {
                    min_path.add(l);
                    if(l.getSource() == m)
                    {
                        break;
                    }
                }

                for(Link l : max_iter)
                {
                    max_path.add(l);
                    if(l.getSource() == m)
                    {
                        break;
                    }
                }

                //System.out.println(n+" "+m+" "+min_path+" "+max_path);
                
                max_diff = Math.max(max_diff, swapFlow(min_path, max_path));
                
                
                n = m;
                
            }
            */
        }
        
        return max_diff;
    }
    
    /**
     * return how much flow was swapped
     */
    public double swapFlow(Path min_path, Path max_path)
    {
        if(min_path.equals(max_path))
        {
            return 0.0;
        }
        
        double max_moved = getMaxFlow(max_path);
        
        double stepsize = 1;
        
        
        
        //System.out.println(max_path);
        
        double difference = max_path.getTT() - min_path.getTT();
        
        /*
        if(origin.getId() == 1)
        {
            System.out.println(origin+"\t"+min_path.get(min_path.size()-1).getDest()+"\t"+max_moved);
            System.out.println("\t"+min_path.getTT()+"\t"+difference);
            for(Link l : min_path)
            {
                System.out.println("\t\t"+l+"\t"+l.getReducedCost());
            }
            System.out.println("\t"+max_path.getTT()+"\t"+difference);
            for(Link l : max_path)
            {
                System.out.println("\t\t"+l+"\t"+l.getReducedCost());
            }
        }
        */

        while(max_moved > 0  && difference > Params.bush_gap)
        {
 
            double deriv = max_path.getDeriv_TT() + min_path.getDeriv_TT();
            
            double y;
            if(max_moved < Params.bush_gap)
            {
                y = max_moved;
            }
            else
            {
                y = Math.min(max_moved, stepsize * difference / deriv);
            }

            
            //System.out.println(deriv+" "+y+" "+difference+" "+stepsize+" "+max_moved);

            for(Link l : max_path)
            {
                addFlow(l, -y);
            }
            
            for(Link l : min_path)
            {
                addFlow(l, y);
            }


            max_moved -= y;

            difference = max_path.getTT() - min_path.getTT();


        }
        

        return difference;
        
    }
    
    public boolean checkCost(Link l)
    {
        return l.getReducedCost() >= 0;
        
    }
    
    public void improveBush()
    {

        minPath();
        
        List<Link> remove = new ArrayList<>();
        
        for(int idx = 0; idx < flow.length; idx++)
        {
            Link l = network.links[idx];
            
            if(flow[idx] < Params.bush_gap && !checkCost(l))
            {
                remove.add(l);
            }
        }
        
        for(Link l : remove)
        {
            flow[l.getIdx()] = 0;
            contains[l.getIdx()] = false;
        }
        

        for(int idx = 0; idx < network.links.length; idx++)
        {
            Link l = network.links[idx];
            if(!contains(l) && checkCost(l))
            {
                contains[idx] = true;
            }
        }
        
        topologicalSort();
    }
    
    public void loadDemand()
    {
        network.dijkstras(origin);
        
        for(int ids = network.getFirstDest(); ids <= network.getLastDest(); ids++)
        {
            Node s = network.nodes[ids];
            
            double d = origin.getDemand((Dest)s);
            
            Node curr = s;
            
            /*
            for(Node n : network.nodes)
            {
                if(n.getId() == s.getId())
                {
                    curr = n;
                    break;
                }
            }
            */
            
            
            while(curr != origin)
            {
                Link uv = curr.pred;
                addFlow(uv, d);
                curr = uv.getSource();
            }
        }
    }
    
    
    
    
    public boolean validateDemand(){

        for(Node s : sorted){
            if(s != origin && (s instanceof Dest)){
                double d = origin.getDemand((Dest)s);
                
                double actual = 0;
                
                for(Link id : s.getIncoming()){
                    actual += flow[id.getIdx()];
                }
                
                for(Link dj : s.getOutgoing()){
                    actual -= flow[dj.getIdx()];
                }
                
                if(Math.abs(d - actual) > Params.bush_gap){
                    throw new RuntimeException("Origin "+origin+": demand to "+s+" is "+d+" but flow is "+actual);
                    //return false;
                }
            }
        }
        
        return true;
    }
    
    public void addFlow(Link l, double x)
    {

        if((""+x).equals("NaN"))
        {
            throw new RuntimeException("flow="+x);
        }
        l.x.addX(x);
        flow[l.getIdx()] += x;
        
        contains[l.getIdx()] = flow[l.getIdx()] > Params.bush_gap;
    }
    
    // maybe store this as a map<Link, list<PAS>> mapping end link of PAS
    public void addRelevantPAS(PAS p){
        relevantPAS.add(p);
    }
    
    
}
