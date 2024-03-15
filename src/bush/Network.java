/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author micha
 */
public class Network 
{
    protected Zone[] origins;
    protected Node[] nodes;
    protected Link[] links;
    
    private int numOrigins;
    private int firstDest, lastDest;
    
    private double total_demand;
    
    
    private PASList allPAS;
    private int iter;
    
    public Network(String name) throws IOException
    {
        readNetwork(name);
        
        allPAS = new PASList();
    }

    
    public Node[] getNodes()
    {
        return nodes;
    }
    
    public Link[] getLinks()
    {
        return links;
    }
    
    
    
    public void dijkstras(Node r)
    {
        for(Node n : nodes)
        {
            n.cost = Params.INFTY;
            n.pred = null;
        }
        
        r.cost = 0;
        
        Set<Node> Q = new HashSet<Node>();
        
        Q.add(r);
        
        while(!Q.isEmpty())
        {
            Node u = null;
            double best = Params.INFTY;
            
            for(Node n : Q)
            {
                if(n.cost < best)
                {
                    best = n.cost;
                    u = n;
                }
            }
            
            Q.remove(u);
            
            
            
            for(Link uv : u.getOutgoing())
            {
                double temp = u.cost + uv.getTT();
                Node v = uv.getDest();
                if(temp < v.cost)
                {
                    v.cost = temp;
                    Q.remove(v);
                    Q.add(v);
                    v.pred = uv;
                }
            }
        }
    }
    
    public Tree getSPTree(Node r)
    {
        dijkstras(r);
        
        Tree output = new Tree(r, this);
        
        for(Node n : nodes)
        {
            if(n != r){
                output.put(n.pred);
            }
        }
        
        return output;
    }
    
    public void printLinkFlows(){
        for(Link l : links){
            System.out.println(l.getSource()+"\t"+l.getDest()+"\t"+l.x.getX()+"\t"+l.getTT());
        }
    }
    
    /*
    public Bush createBush(Node r)
    {
        dijkstras(r);
        

        Set<Link> newlinks = new HashSet<Link>();
        
        List<Node> newnodes = new ArrayList<>();        
        Map<Node, Node> nodemap = new HashMap<>();
        
        
        Node clone = new Node(r.getId(), r.getId()+"-"+r.getId());
        nodemap.put(r, clone);
        newnodes.add(clone);
        Node origin_clone = clone;
        origin_clone.cloned = r;
        
        Set<Node> dests = new HashSet<>();
        
        Set<Node> key_dests = demand.getDests(r);
        
        for(Node n : nodes)
        {
            if(n.pred != null)
            {
                newlinks.add(n.pred);
                            
                clone = new Node(n.getId(), n.getId()+"-"+r.getId());
                clone.cloned = n;
                
                nodemap.put(n, clone);
                newnodes.add(clone);
                
                if(key_dests.contains(n))
                {
                    dests.add(clone);
                }
            }
        }
        
        for(Link l : newlinks)
        {
            // the nodes will remember this link, no need to store it
            new Link(l, nodemap.get(l.getSource()), nodemap.get(l.getDest()));
        }
        
        nodemap = null;
        newlinks = null;
        
        
        
        
        Bush output = new Bush(origin_clone, newnodes, dests, this);
        
        return output;
    }
    */
    
    public void readNetwork(String name) throws IOException
    {
        readNetwork(new File("data/"+name+"/net.txt"), new File("data/"+name+"/trips.txt"));
    }
    
    public void readNetwork(File net, File trips) throws IOException
    {
        Map<Integer, Node> keynodes = new HashMap<Integer, Node>();
        
        Scanner filein = new Scanner(net);
        
        int numLinks = 0;
        int firstThruNode = 0;
        int numNodes = 0;

        
        int numZones = 0;
        
        // metadata
        while(true)
        {
            String line = filein.nextLine();
            
            String key = line.substring(line.indexOf('<')+1, line.indexOf('>'));
            String value = line.substring(line.indexOf('>')+1).trim();
            
            if(key.equalsIgnoreCase("NUMBER OF ZONES"))
            {
                numZones = Integer.parseInt(value);
            }
            else if(key.equalsIgnoreCase("NUMBER OF NODES"))
            {
                numNodes = Integer.parseInt(value);
            }
            else if(key.equalsIgnoreCase("NUMBER OF LINKS"))
            {
                numLinks = Integer.parseInt(value);
            }
            else if(key.equalsIgnoreCase("FIRST THRU NODE"))
            {
                firstThruNode = Integer.parseInt(value);
            }
            else if (key.equalsIgnoreCase("END OF METADATA"))
            {
                break;
            }
        }
        
        
        numOrigins = numZones;
        
        int numNewDests = firstThruNode-1;
        
        firstDest = (firstThruNode - 1);
        lastDest = numOrigins + numNewDests - 1;
        
        
                
        numNodes += numNewDests;
        
        nodes = new Node[numNodes];
        origins = new Zone[numOrigins];
        
        
        int dest_idx = 0;
        
        for(int i = 0; i < numOrigins; i++)
        {
            int id = i+1;
            Node node = new Zone(id, getNumDests());
            nodes[i] = node;
            origins[i] = (Zone)node;
            keynodes.put(id, node);
            
            if(id >= firstThruNode)
            {
                dest_idx++;
            }
        }
        
        
        for(int i = 0; i < numNewDests; i++)
        {
            int id = - (i + 1);
            Node node = new DestNode(id, dest_idx++);
            nodes[i + numOrigins] = node;
            keynodes.put(id, node);
        }
        
        for(int i = 0; i < numNodes - numOrigins - numNewDests; i++)
        {
            int id = i + numOrigins + 1;
            
            Node node = new Node(id);
            nodes[i + numOrigins + numNewDests] = node;
            keynodes.put(id, node);
            
        }
        
        while(filein.nextLine().trim().length() == 0);

        
        links = new Link[numLinks];
        
        for(int n = 0; n < numLinks; n++)
        {
            int idi = filein.nextInt();
            int idj = filein.nextInt();
            double Q = filein.nextDouble();
            double len = filein.nextDouble();
            double tf = filein.nextDouble();
            double a = filein.nextDouble();
            double b = filein.nextDouble();
            filein.nextLine();
            
            
            Node i = keynodes.get(idi);
            Node j;
            
            if(idj < firstThruNode)
            {
                j = keynodes.get(-idj);
            }
            else
            {
                j = keynodes.get(idj);
            }
            
            Link l;
            
            if(b == 4)
            {
                l = new BPRLink4(i, j, tf, Q, a, b, len);
            }
            else
            {
                l = new Link(i, j, tf, Q, a, b, len);
            }
            
            links[n] = l;
        }
        
        
        filein.close();
        
        
        
        for(Node n : nodes)
        {
            List<Link> inc = new ArrayList<>();
            List<Link> out = new ArrayList<>();
            
            
            for(Link l : links)
            {
                if(l.getSource() == n)
                {
                    out.add(l);
                }
                else if(l.getDest() == n)
                {
                    inc.add(l);
                }
            }
            
            n.setIncoming(inc.toArray(new Link[0]));
            n.setOutgoing(out.toArray(new Link[0]));
        }
        filein = new Scanner(trips);
        
        double mu = 0;
        double sigma = 0;
        
        while(true)
        {
            String line = filein.nextLine();
            
            String key = line.substring(line.indexOf('<')+1, line.indexOf('>'));
            String value = line.substring(line.indexOf('>')+1).trim();
            
            if (key.equalsIgnoreCase("END OF METADATA"))
            {
                break;
            }
        }
        
        
        Zone r = null;
        
        while(filein.hasNext())
        {
            String next = filein.next();
            
            if(next.equalsIgnoreCase("Origin"))
            {
                r = (Zone)keynodes.get(filein.nextInt());
            }
            else
            {
                int ids = Integer.parseInt(next);
                Dest s;
                
                if(ids < firstThruNode)
                {
                    s = (Dest)keynodes.get(-ids);
                }
                else
                {
                    s = (Zone)keynodes.get(ids);
                }
                

                filein.next(); 
                String temp = filein.next();
                if(temp.charAt(temp.length()-1)==';')
                {
                    temp = temp.substring(0, temp.length()-1);
                }
                else
                {
                    filein.next();
                }
                
                double p = Double.parseDouble(temp);
                
                r.setDemand(s, p);
                total_demand += p;
            }
        }
        filein.close();
        
        System.out.println("Demand: "+Math.round(total_demand));

    }    

    
    public int getFirstDest()
    {
        return firstDest;
    }
    
    public int getLastDest()
    {
        return lastDest;
    }
    
    public int getFirstOrigin()
    {
        return 0;
    }
    
    public int getNumDests()
    {
        return (lastDest - firstDest) + 1;
    }
    
    public int getLastOrigin()
    {
        return numOrigins-1;
    }
    
    public int getNumNodes()
    {
        return nodes.length;
    }
    
    public int getNumLinks()
    {
        return links.length;
    }
    

    public void frankWolfe(int max_iter, double min_gap)
    {
        
        double gap = Params.INFTY;
        iter = 0;
        
        System.out.println("Iter\tStep size\tAEC");
        
        do
        {
            iter++;
            
            calcSearchDirection();
            double lambda = calcStepSize(iter);
            update(lambda);
            
            gap = calcGap();
            
            System.out.println(iter+"\t"+String.format("%.4f", lambda)+"\t"+String.format("%.4f", gap));
        }
        while((gap > min_gap || iter == 1) && iter < max_iter);

    }
    
    public double calcSearchDirection()
    {
        double tstt = 0.0;
        double sptt = 0.0;
        
        for(Link l : links)
        {
            tstt += l.x.getX() * l.getTT();
            l.x.x_star = 0;
        }
        
        for(int idr = getFirstOrigin(); idr <= getLastOrigin(); idr++)
        {
            Zone r = (Zone)nodes[idr];
            
            Tree tree = getSPTree(r);

            for(int ids = getFirstDest(); ids <= getLastDest(); ids++)
            {
                Node s = nodes[ids];
                
                double d = r.getDemand((Dest)s);
                
                sptt += d * s.cost;
                
                for(Link l : tree.trace(s))
                {
                    l.x.x_star += d;
                }
            }
        }
        
        return (tstt - sptt) / total_demand;
    }
    
    public void update(double lambda)
    {
        for(Link l : links)
        {
            l.x.update(lambda);
        }
    }
    
    public double calcStepSize(int iter)
    {
        if(iter == 1)
        {
            return 1;
        }
        else
        {
            double bot = 0;
            double top = 1;
            double mid = 0.5;
            
            while(top - bot > 0.001)
            {
                //System.out.println(bot+" "+mid+" "+top);
                mid = (bot + top)/2;
                
                double val = getDeriv(mid);
                
                if(val < 0)
                {
                    bot = mid;
                }
                else if(val > 0)
                {
                    top = mid;
                }
                else
                {
                    break;
                }
            }
            
            return mid;
        }
    }
    
    public double getDeriv(double lambda)
    {
        double output = 0.0;
        
        for(Link ij : links)
        {
            output += ij.getTT(ij.x.getXPrime(lambda)) * (ij.x.x_star - ij.x.getX());
        }
        
        return output;
    }
    
    public double getObjVal(double lambda)
    {
        double output = 0;
        
        for(Link ij : links)
        {
            output += ij.getInt_TT(ij.x.getXPrime(lambda));
        }
        
        return output;
    }
    
    
    public void tapas(int max_iter, double min_gap){
        
        // find initial solution using AON
        for(Zone r : origins){
            new Bush(r, this);
        }
        
        // repeat iteratively:
        for(iter = 1; iter <= max_iter; iter++){
            // for every origin
            for(Zone r : origins){
                // remove all cyclic flows !!!
                // find tree of least cost routes
                r.bush.checkPAS();
                // for every link used by the origin which is not part of the tree
                    // if there is an existing effective PAS
                        // make sure the origin is listed as relevant
                    // else
                        // construct a new PAS
                        
                        
                    // choose a random subset of active PASs
                        // shift flow within each chosen PAS
            }
            
            
            // for every active PAS
            boolean modified = false;
            for(int shiftIter = 0; shiftIter <= Params.tapas_equilibrate_iter; shiftIter++){
                // check if it should be eliminated
                removePAS();
                // perform flow shift to equilibrate costs
                modified = equilibratePAS();
                // redistribute flows between origins by the proportionality condition
                
                // in the case that no flow shifting occurred, do not try to equilibrate more
                if(!modified){
                    break;
                }
            }
            
                
            double tstt = getTSTT();
            double sptt = getSPTT();
            double gap = (tstt - sptt)/tstt;
            
            System.out.println(iter+"\t"+String.format("%.2f", tstt)+"\t"+String.format("%.4f", gap));
            
            if(gap < min_gap){
                break;
            }
            
            // there's an issue where PAS are labeled as not cost effective because the difference in cost is small, less than 5% of the reduced cost
            // for low network gaps, this is causing PAS to not flow shift
            // when the gap is low, increase the flow shift sensitivity
            if(gap < Params.pas_cost_mu)
            {
                Params.pas_cost_mu /= 10;
            }
        }
        
        // final proportinality iterations:
            // for every active PAS
                // redistribute flows between origins by the proportionality condition
    }

    public void algorithmB(int max_iter, double min_gap)
    {
        // initial feasible bush
        for(int idr = getFirstOrigin(); idr <= getLastOrigin(); idr++)
        {
            Zone r = (Zone)nodes[idr];
            r.bush = new Bush(r, this);

        }
        
        double gap = Params.INFTY;
        
        System.out.println("Iter\tAEC");
        
        iter = 0;
        
        do
        {
            iter++;
            
            for(int idr = getFirstOrigin(); idr <= getLastOrigin(); idr++)
            {
                Bush bush = ((Zone)nodes[idr]).bush;
                bush.equilibrate(0.1);
            }
            
            gap = calcAEC();
            
            System.out.println(iter+"\t"+String.format("%.6f", gap));
        }
        while(gap > min_gap && iter < max_iter);

    }

    public double getTSTT(){
        double tstt = 0;
        for(Link l : links)
        {
            tstt += l.x.getX() * l.getTT();
            
            if((""+tstt).equals("NaN"))
            {
                System.out.println(l.x.getX()+"\t"+l.getTT()+"\t"+tstt+"\t"+(""+l.getTT()).equals("NaN"));
                System.exit(0);
            }
        }
        return tstt;
    }
    
    public double getSPTT(){
        double sptt = 0.0;
        
        for(int idr = getFirstOrigin(); idr <= getLastOrigin(); idr++)
        {
            Zone r = (Zone)nodes[idr];
            dijkstras(r);
            
            for(int ids = getFirstDest(); ids <= getLastDest(); ids++)
            {
                Node s = nodes[ids];
                sptt += r.getDemand((Dest)s) * s.cost;
            }
        }
        return sptt;
    }
    
    public double calcAEC()
    {
        double tstt = getTSTT();
        double sptt = getSPTT();
        
        
        return (tstt - sptt) / total_demand;
    }
    
    public double calcGap()
    {
        double tstt = getTSTT();
        double sptt = getSPTT();
        
        
        return (tstt - sptt) / tstt;
    }
    
    
    public Node findNode(int id)
    {
        for(Node n : nodes)
        {
            if(n.getId() == id)
            {
                return n;
            }
        }
        return null;
    }
    
    public Link findLink(int id)
    {
        for(Link l : links)
        {
            if(l.getId() == id)
            {
                return l;
            }
        }
        
        return null;
    }
    
    public Link findLink(Node i, Node j)
    {
        if(i == null || j == null)
        {
            return null;
        }
        
        for(Link l : i.getOutgoing())
        {
            if(l.getDest() == j)
            {
                return l;
            }
        }
        return null;
    }
    
    public Link findLink(int idi, int idj)
    {
        return findLink(findNode(idi), findNode(idj));
    }
    
    public PAS findPAS(Link ij, Bush b){
        
        if(!allPAS.containsKey(ij)){
            return null;
        }
        
        PAS best = null;
        double max = Params.bush_gap;
        
        
        for(PAS p : allPAS.get(ij)){
            double temp = p.maxFlowShift(b);
            

            if(temp > max && p.isCostEffective()){
                max = temp;
                best = p;
            }
        }
        
        return best;
    }
    
    public void addPAS(PAS p){
        allPAS.add(p);
        p.setLastIterFlowShift(iter);
    }
 
    public boolean equilibratePAS(){
        boolean output = false;
        
        for(PAS p : allPAS){
            if(p.flowShift()){
                p.setLastIterFlowShift(iter);
                output = true;
            }
        }
        
        return output;
    }
    
    public void printPAS(){
        for(PAS p : allPAS){
            System.out.println(p.getEndLink()+" - "+p+" "+p.getCostDifference()+" "+p.isCostEffective());
        }
    }
    
    public void removePAS(PAS p){
        allPAS.remove(p);
            
        for(Zone r : p.getRelevantOrigins()){
            r.bush.removePAS(p);
        }
    }
    
    public void removePAS(){
        List<PAS> removed = new ArrayList<>();
        
        for(PAS p : allPAS){
            if(p.getLastIterFlowShift() < iter-2){
                removed.add(p);
            }
        }
        
        for(PAS p : removed){
            removePAS(p);
        }
    }
}
