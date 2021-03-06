package myBTreePlus;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xumg
 * @create 2020-10-16 9:17
 */
@Data
public class Node<V> {
    private List<Integer> keys;
    private List<Node<V>> children;
    private boolean isLeaf;
    private Node<V> previousNode;
    private Node<V> nextNode;
    private Node<V> parentNode;
    private List<Entry<V>> entrys;

    public void addChild(Node<V> child){
        children.add(child);
    }
    public void addChild(Node<V> child,int index){
        children.add(index,child);
    }
    public void addKey(Integer key){
        keys.add(key);
    }
    public void addKey(Integer key,int index){
        keys.add(index,key);
    }
    public void addEntry(Entry<V> entry){
        entrys.add(entry);
    }
    public void addEntry(Entry<V> entry,int index){
        entrys.add(index,entry);
    }

    public Node() {
        this.keys =new LinkedList<>();
        this.children=new LinkedList<>();
        this.entrys=new LinkedList<>();
        this.isLeaf=true;
    }

    //返回子节点大小
    public int getChildrenSize(){
        return this.children.size();
    }

    //返回键值大小
    public int getKeysSize(){
        return this.keys.size();
    }

    //获取指定下标的key
    public Integer keyAt(int index){
        return keys.get(index);
    }

    //获取指定下标的entry
    public Entry<V> entryAt(int index){
        return entrys.get(index);
    }

    //获取指定下标的child
    public Node<V> childAt(int index){
        return children.get(index);
    }

    //更新父节点指定下标的key
    public void updateKey(int index,Integer key){
        this.keys.set(index,key);
    }

    //删除指定节点key
    public void deleteKey(int index){
        this.keys.remove(index);
    }

    //删除指定节点child
    public void deleteChild(int index){
        this.children.remove(index);
    }

    //删除指定节点的entry
    public void deleteEntry(int index){
        this.entrys.remove(index);
    }

    //判断当前节点是否包含key 存在返回true
    public boolean contains(Integer key){
        if(keys.contains(key)){
            return true;
        }
        return false;
    }

    //二分查找所查找key应该在节点哪
    public Result<V> search(Integer key ){
        int begin=0;int end=this.getKeysSize()-1;
        int mid;
        int index=-1;
        Entry<V> entry=null;
        //node为空直接返回
        if(this.getKeysSize() <=0){
            return new Result<>(null,entry,index,false);
        }
        //若节点为叶子节点且不存在
//        if(this.isLeaf() && ! this.getKeys().contains(key)){
//            return new Result<>(null,entry,index,false);
//        }
        while(begin<end){
            mid=(begin+end)/2;
            int com=this.keyAt(mid).compareTo(key);
            if(com==0){
                //中间值为所查找值
                index=mid;
                if(this.isLeaf()){
                    entry=this.entryAt(mid);
                }
                return new Result<>(key,entry,index,true);
            }else if(com <0){
                //key大
                begin=mid+1;
            }else{
                //mid大
                end=mid-1;
            }
        }
        int com = this.keyAt(begin).compareTo(key);
        if(com == 0){
            index=begin;
            if(this.isLeaf()){
                entry=this.entryAt(begin);
            }
            return new Result<>(key,entry,index,true);
        }else if(com < 0){
            //begin所在位置key小与查询key
            index=begin;
            if(this.isLeaf()){
                //如果不是叶子节点，子节点下边等同begin，如果是叶子节点，插入需要+1，因为这种B+树子节点少一个头
                index=begin+1;
            }
            return new Result<>(key,null,index,false);
        }else{
            //begin所在位置key大于查询key
            index=begin;
            return new Result<>(key,null,index,false);
        }
    }

    //二分查找所查找key应该在节点哪
    public Result<V> deleteSearch(Integer key ){
        int begin=0;int end=this.getKeysSize()-1;
        int mid;
        int index=-1;
        Entry<V> entry=null;
        //node为空直接返回
        if(this.getKeysSize() <=0){
            return new Result<>(null,entry,index,false);
        }
        //若节点为叶子节点且不存在
//        if(this.isLeaf() && ! this.getKeys().contains(key)){
//            return new Result<>(null,entry,index,false);
//        }
        while(begin<end){
            mid=(begin+end)/2;
            int com=this.keyAt(mid).compareTo(key);
            if(com==0){
                //中间值为所查找值
                index=mid;
                if(this.isLeaf()){
                    entry=this.entryAt(mid);
                }
                return new Result<>(key,entry,index,true);
            }else if(com <0){
                //key大
                begin=mid+1;
            }else{
                //mid大
                end=mid-1;
            }
        }
        int com = this.keyAt(begin).compareTo(key);
        if(com == 0){
            index=begin;
            if(this.isLeaf()){
                entry=this.entryAt(begin);
            }
            return new Result<>(key,entry,index,true);
        }else if(com < 0){
            //begin所在位置key小与查询key
            index=begin + 1;
            return new Result<>(key,null,index,false);
        }else{
            //begin所在位置key大于查询key
            index=begin;
            return new Result<>(key,null,index,false);
        }
    }

    @Override
    public String toString() {
        return "";
    }
}
