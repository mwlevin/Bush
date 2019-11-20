/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A tree is a map of nodes to their predecessor link
 * The root has no predecessor. 
 * The root node is implicit.
 * @author micha
 */
public class Tree extends HashMap<Node, Link>
{
    private Node origin;
    
    public Tree(Node origin)
    {
        this.origin = origin;
    }
    
    public Node getOrigin()
    {
        return origin;
    }
    
    public Iterable<Link> iterator(Node s)
    {
        return new Iterable<Link>()
        {
            public Iterator<Link> iterator()
            {
                return new PathIterator(s);
            }
        };
    }
    
    /**
     * Iterates over the path (links) from Node r to Node s, in backwards order
     */

    public Iterable<Link> trace(Node s)
    {
        return new Iterable<Link>()
        {
            public Iterator<Link> iterator()
            {
                return new PathIterator(s);
            }
        };
    }
    
    public Path getPath(Node s)
    {
        Path output = new Path();
        for(Link curr : trace(s))
        {
            output.add(0, curr);
        }
        return output;
    }
    
    class PathIterator implements Iterator<Link>
    {
        private Node curr;
        public PathIterator(Node s)
        {
            curr = s;
        }
        
        public Link next()
        {
            Link output = get(curr);
            curr = output.getSource();
            return output;
        }
        
        public boolean hasNext()
        {
            return containsKey(curr) && get(curr) != null;
        }
    }
}
