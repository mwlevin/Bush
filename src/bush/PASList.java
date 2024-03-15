/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bush;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author michaellevin
 */
public class PASList implements Iterable<PAS>{
    
    private HashMap<Link, HashSet<PAS>> set;
    
    public PASList(){
        set = new HashMap<>();
    }
    
    
    
    public void add(PAS p){
        if(!set.containsKey(p.getEndLink())){
            set.put(p.getEndLink(), new HashSet<>());
        }
        
        set.get(p.getEndLink()).add(p);
    }
    
    public void remove(PAS p){
        set.get(p.getEndLink()).remove(p);
        
        if(set.get(p.getEndLink()).size() == 0){
            set.remove(p.getEndLink());
        }
    }
    
    public boolean containsKey(Link a){
        return set.containsKey(a);
    }
    
    public Set<PAS> get(Link l){
        return set.get(l);
    }
    
    public boolean hasPAS(Link a){
        return set.containsKey(a) && set.get(a).size() > 0;
    }
    
    public Map<Link, HashSet<PAS>> getPAS(){
        return set;
    }
    
    
    
    
    public Iterator<PAS> iterator(){
        return new PASListIterator();
    }
    
    class PASListIterator implements Iterator<PAS>
    {
        private Iterator<PAS> setiterator;
        private Iterator<Link> keyiterator;
        
        public PASListIterator(){
            keyiterator = set.keySet().iterator();
        }
        
        public PAS next(){
            refreshIterator();
            
            if(setiterator == null){
                return null;
            }
            else{
                return setiterator.next();
            }
        }
        
        public void refreshIterator(){
            
            while(setiterator == null || !setiterator.hasNext()){
                if(keyiterator.hasNext()){
                    setiterator = set.get(keyiterator.next()).iterator();
                }
                else{
                    break;
                }
            }
        }
        
        public boolean hasNext(){
            refreshIterator();
            
            return setiterator != null && setiterator.hasNext();
        }
    }
}
