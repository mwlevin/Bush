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
        // associate it with both the forward and backward end links for searching purposes.
        if(!set.containsKey(p.getEndLinkFwd())){
            set.put(p.getEndLinkFwd(), new HashSet<>());
        }
        
        set.get(p.getEndLinkFwd()).add(p);
        
        
        
        if(!set.containsKey(p.getEndLinkBwd())){
            set.put(p.getEndLinkBwd(), new HashSet<>());
        }
        
        set.get(p.getEndLinkBwd()).add(p);
    }
    
    public void remove(PAS p){
        set.get(p.getEndLinkFwd()).remove(p);
        
        if(set.get(p.getEndLinkFwd()).size() == 0){
            set.remove(p.getEndLinkFwd());
        }
        
        set.get(p.getEndLinkBwd()).remove(p);
        
        if(set.get(p.getEndLinkBwd()).size() == 0){
            set.remove(p.getEndLinkBwd());
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
        private PAS next;
        private Link key;
        
        public PASListIterator(){
            keyiterator = set.keySet().iterator();
            
            refreshIterator();
        }
        
        public PAS next(){
            PAS output = next;
            refreshIterator();
            
            return output;
        }
        
        public void refreshIterator(){
            next = null;
            
            outer: while(true){
                while(setiterator != null && setiterator.hasNext()){
                    next = setiterator.next();

                    if(next.getEndLinkFwd() == key){
                        break outer;
                    }
                }

                while(setiterator == null || !setiterator.hasNext()){
                    if(keyiterator.hasNext()){
                        key = keyiterator.next();
                        setiterator = set.get(key).iterator();
                    }
                    else{
                        next = null;
                        key = null;
                        break outer;
                    }
                }
            }
        }
        
        public boolean hasNext(){
            return next != null;
        }
    }
}
