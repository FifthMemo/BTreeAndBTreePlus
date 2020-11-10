package myBTree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xumg
 * @create 2020-10-15 9:16
 */
@Data
@AllArgsConstructor
public class Node<K,V> {
    private List<Entry<K,V>> entrys;

    private List<Node<K,V>> children;

    private boolean isLeaf;

    private Comparator<K> kComparator;

    public int compare(K key1, K key2){
        return this.kComparator==null? ((Comparable<K>)key2).compareTo(key1) : kComparator.compare(key1,key2);
    }

    public Node() {
        this.entrys = new LinkedList<Entry<K, V>>();
        this.children = new LinkedList<Node<K, V>>();
        this.isLeaf = false;
    }

    public Node(Comparator<K> kComparator) {
        this();
        this.kComparator = kComparator;
    }

    public Node(LinkedList<Entry<K, V>> entrys, LinkedList<Node<K, V>> children, boolean isLeaf) {
        this.entrys = entrys;
        this.children = children;
        this.isLeaf = isLeaf;
    }

    public int getSize(){
        return this.entrys.size();
    }
    public void addEntry(Entry<K,V> entry){
        this.entrys.add(entry);
    }
    public void addEntry(int index,Entry<K,V> entry){
        this.entrys.add(index,entry);
    }
    //返回指定下标的子节点
    public Node<K,V> childAt(int index){
        return children.get(index);
    }

    //添加子节点/ 删除合并节点时会用到
    public void insertChild(Node<K,V> child){
        children.add(child);
    }
    //向指定下标添加子节点 / 删除旋转过户孩子时用到
    public void insertChild(int index,Node<K,V> child){
        children.add(index,child);
    }

    //移除指定下标的数据
    public Entry<K,V> removeEntry(int index){
        Entry<K,V> re=entrys.get(index);
        entrys.remove(index);
        return re;
    }
    //移除指定下标的子节点
    public void removeChild(int index){
        children.remove(index);
    }

    //二分查找所要插入的entry或所在子节点所在的下标
    public Result<V> search(K key){
        int begin=0;
        int end=this.getSize()-1;
        if(end<0){
            return new Result<V>(false,null,begin);
        }
        int mid=(begin+end)/2;
        boolean isExist=false;
        int index=0;
        V value=null;
        while(begin<end){
            mid=(begin+end)/2;
            Entry midEntry=this.entrys.get(mid);
            int com=compare((K)midEntry.getKey(),key);
            if(com == 0){
                break;
            }else{
                if(com>0){
                    //key>midEntry.getKey()
                    begin=mid+1;
                }else{
                    end=mid-1;
                }
            }
        }
        //查找结束
        if(begin<end){
            //在该节点中找到了此entry
            isExist=true;
            index=mid;
            value=this.entrys.get(mid).getValue();
        }else{
            K midKey=this.entrys.get(begin).getKey();
            int com =compare(midKey,key);
            if(com == 0){
                isExist=true;
                index=begin;
                value=this.entrys.get(begin).getValue();
            }else{
                if(com>0){
                    //key>midkey
                    isExist=false;
                    index=begin+1;
                    value=null;
                }else{
                    isExist=false;
                    index=begin;
                    value=null;
                }
            }
        }
        return new Result<V>(isExist,value,index);
    }
}
