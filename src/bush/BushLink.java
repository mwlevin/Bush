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
public class BushLink
{
    private Node bush_source, bush_dest; // references to the nodes
    private Link link;
    
    public double x; // this is origin flow!!
    
    public BushLink(Link l, Node bush_source, Node bush_dest)
    {
        this.link = l;
        this.bush_source = bush_source;
        this.bush_dest = bush_dest;
    }
    
    public Node getSource()
    {
        return bush_source;
    }
    
    public Node getDest()
    {
        return bush_dest;
    }
    
    public Link getLink()
    {
        return link;
    }
}
