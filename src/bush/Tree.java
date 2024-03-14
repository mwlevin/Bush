/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A tree is a map of nodes to their predecessor link.
 * The root has no predecessor. 
 * The root node is implicit.
 * @author micha
 */
public class Tree
{
    private Node origin;
    private Link[] map;
    
    public Tree(Node origin, Network network)
    {
        this.origin = origin;
        map = new Link[network.nodes.length];
    }
    
    public Node getOrigin()
    {
        return origin;
    }
    
    public void put(Link link)
    {
        map[link.getDest().getIdx()] = link;
    }
    
    public boolean containsNode(Node node)
    {
        return map[node.getIdx()] != null;
    }
    
    public boolean containsLink(Link link){
        return map[link.getDest().getIdx()] != null && map[link.getDest().getIdx()] == link;
    }
    
    public Link get(Node node)
    {
        return map[node.getIdx()];
    }
    

    
    /**
     * Iterates over the path (links) from Node r to Node s, in backwards order
     */
    
    public Iterable<Link> trace(Node s){
        return trace(origin, s);
    }

    public Iterable<Link> trace(Node i, Node j)
    {
        return new Iterable<Link>()
        {
            public Iterator<Link> iterator()
            {
                return new PathIterator(i, j);
            }
        };
    }
    
    
    public Set<Node> getPathAsNodeSet(Node j){
        return getPathAsNodeSet(origin, j);
    }
    
    public Set<Node> getPathAsNodeSet(Node i, Node j){
        Set<Node> output = new HashSet<>();
        
        output.add(j);
        for(Link curr : trace(i, j))
        {
            output.add(curr.getSource());
        }
        return output;
    }
    
    public Path getPath(Node j){
        return getPath(origin, j);
    }
    
    public Path getPath(Node i, Node j)
    {
        Path output = new Path();
        for(Link curr : trace(i, j))
        {
            output.add(0, curr);
        }
        return output;
    }
    
    class PathIterator implements Iterator<Link>
    {
        private Node curr;
        private Node start;
        
        public PathIterator(Node i, Node j)
        {
            curr = j;
            start = i;
        }
        
        public Link next()
        {
            Link output = get(curr);
            curr = output.getSource();
            return output;
        }
        
        public boolean hasNext()
        {
            return containsNode(curr) && curr != start && get(curr) != null;
        }
    }
}
