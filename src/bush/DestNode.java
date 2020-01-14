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
public class DestNode extends Node implements Dest
{
    private int dest_idx;
    
    public DestNode(int id, int dest_idx)
    {
        super(id);
        this.dest_idx = dest_idx;
    }
    
    public int getDestIdx()
    {
        return dest_idx;
    }
}
