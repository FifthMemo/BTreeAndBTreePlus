package myBTree;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author xumg
 * @create 2020-10-15 9:22
 */
@Data
@AllArgsConstructor
public class BTree<K,V> {
    //默认度数t为2
    private Integer DEAFULT_T=2;

    private Node<K,V> root;

    private int t = DEAFULT_T;

    private int minSize=t-1;

    private int maxSiez=2*t-1;

    private Comparator<K> kComparator;

    public BTree() {
        Node<K, V> root = new Node<K, V>();
        this.root = root;
        root.setLeaf(true);
    }

    public BTree(int t) {
        this();
        this.t = t;
        minSize=t-1;
        maxSiez=2*t-1;
    }

    public BTree(int t, Comparator<K> kComparator) {
        this(t);
        this.kComparator = kComparator;
    }

    //在正确的节点插入该entry
    public void addEntry(Node<K,V> root,Entry<K,V>entry){
        Result<V> result = root.search(entry.getKey());
        if(result.isExist()){
            return;
        }else{
            root.addEntry(result.getIndex(),entry);
        }
    }
    //添加第一步，判断根节点是否已满
    public void addNode(Entry<K, V> en) {
        if(root.getSize()==maxSiez){
            //根节点已满，请先分裂
            Node<K,V> newRoot=new Node<>();
            newRoot.setLeaf(false);
            newRoot.getChildren().add(0,root);
            splitNode(newRoot,root,0);
            this.root=newRoot;
        }
        addNodeNotFull(root ,en);
    }
    //添加第二步，递归找到所应插入的叶子节点
    public void addNodeNotFull(Node<K,V> root,Entry<K,V> entry){
        if(root.isLeaf()){
            //到达叶子节点
            addEntry(root,entry);
            return;
        }
        Result<V> result = root.search(entry.getKey());
        if(result.isExist()){
            return;
        }
        //不是叶子节点，根据查找出所在子节点下标获取子节点对象
        int index=result.getIndex();
        Node<K,V> searchChild=root.childAt(index);
        //判断子节点是否需要分裂
        if(searchChild.getSize()==2*t-1){
            splitNode(root,searchChild,index);
            //判断插入数据的key与分裂后顶上来的key谁大，决定插入在哪个分裂后的节点
            if(root.compare(root.getEntrys().get(index).getKey(),entry.getKey())>0){
                //entry Key大用分裂后的新节点，其下标为index+1
                searchChild=root.getChildren().get(index+1);
            }
        }
        addNodeNotFull(searchChild,entry);
    }

    public  Entry<K,V> delete(K key){
        Entry<K,V> en=new Entry<>();
        en.setKey(key);
        return delete(root ,en);
    }

    public Entry<K,V> delete(Node<K,V> root,Entry<K,V> entry){
        Result<V> result = root.search(entry.getKey());
        if(result.isExist()){
            //该节点存在所要删除的数
            if(root.isLeaf()){
                //该节点为叶子节点
                return root.removeEntry(result.getIndex());
            }
            //若不是叶子节点，需要将该数据旋转至子节点直至叶子节点进行删除
            Node<K,V> leftChild=root.childAt(result.getIndex());
            //若左节点已经是最少数量t-1，则需交给右节点
            if(leftChild.getSize() >t-1){
                //左节点数量大于t-1,可进行旋转
                //删除步骤：将删除节点递归与左节点最后一位或右节点最左一位互换，一直换到叶子节点进行删除
                //互换
                root.removeEntry(result.getIndex());
                root.addEntry(result.getIndex(),leftChild.getEntrys().get(leftChild.getSize()-1));;
                leftChild.removeEntry(leftChild.getSize()-1);
                leftChild.addEntry(entry);
                return delete(leftChild,entry);
            }
            Node<K,V> rightNode=root.childAt(result.getIndex()+1);
            if(rightNode.getSize()>t-1){
                //右节点与左节点逻辑相似，只是将最左与删除项进行交换
                root.removeEntry(result.getIndex());
                root.addEntry(result.getIndex(),rightNode.getEntrys().get(0));;
                rightNode.removeEntry(0);
                rightNode.addEntry(0,entry);
                return delete(rightNode,entry);
            }
            //如果左右节点数据数量均已是最小值t-1，则将其合并，将删除项与有节点全部放入左节点
            leftChild.addEntry(entry);
            //左右子节点合并，移除右节点
            root.removeChild(result.getIndex()+1);
            root.removeEntry(result.getIndex());
            //将右节点的数据转移至左节点
            for(Entry<K,V> en:rightNode.getEntrys()){
                leftChild.addEntry(en);
            }
            //如果右节点不是存在子节点，则将其子节点也并入左节点
            if( ! rightNode.isLeaf()){
                for(Node<K,V> node:rightNode.getChildren()){
                    leftChild.insertChild(node);
                }
            }
            //合并后继续递归删除
            return delete(leftChild,entry);
        }else{
            //删除项不在本节点中,在其子节点中
            if(root.isLeaf()){
                //如果该节点已经是叶子节点,则表明不在其中
                System.out.println("所要删除数据不在本树中");
            }
            Node<K,V> searchChild=root.childAt(result.getIndex());
            //获取所在子节点，判断数量大于t-1则递归删除
            if(searchChild.getSize()>t-1){
                return delete(searchChild,entry);
            }
            //所在子节点数量已是最少的t-1，不能直接进入自己点删除，需准备旋转或合并
            //先找最亲近的兄弟借钱->兄弟有钱，旋转周转，都没钱一起去抢父母
            Node<K,V> brotherNode=null;
            //兄弟节点所在下标
            int brotherIndex=-1;
            if(result.getIndex()<root.getSize()){
                //存在右兄弟
                Node<K, V> rightBrother = root.childAt(result.getIndex() + 1);
                if(rightBrother.getSize()>t-1){
                    //如果右边兄弟有钱
                    brotherNode=rightBrother;
                    brotherIndex=result.getIndex()+1;
                }
            }
            if(brotherNode==null){
                //右兄弟没钱或者没有右兄弟，去找左兄弟
                if(result.getIndex()>0){
                    //存在左兄弟
                    Node<K, V> leftBrother = root.childAt(result.getIndex() - 1);
                    if(leftBrother.getSize()>t-1){
                        //左兄弟有钱
                        brotherNode=leftBrother;
                        brotherIndex=result.getIndex()-1;
                    }
                }
            }
            //左右兄弟能借钱
            if(brotherNode != null){
                if(brotherIndex < result.getIndex()){
                    //左兄弟借的钱,把父节点中左兄弟下标的数据拿到自己0下标位置，左兄弟最右侧数据塞给父节点
                    searchChild.addEntry(0,root.getEntrys().get(brotherIndex));
                    root.removeEntry(brotherIndex);
                    root.addEntry(brotherIndex,brotherNode.getEntrys().get(brotherNode.getSize()-1));
                    brotherNode.removeEntry(brotherNode.getSize()-1);
                    //如果左兄弟有孩子，孩子要过户,左孩子最大的孩子拿来给自己当最小的
                    if( ! brotherNode.isLeaf()){
                        searchChild.insertChild(0,brotherNode.childAt(brotherNode.getSize()));
                        brotherNode.removeChild(brotherNode.getSize()-1);
                    }
                }else{
                    //右兄弟借的钱，与左兄弟类似，拿右边,放最大
                    searchChild.addEntry(searchChild.getSize(),root.getEntrys().get(result.getIndex()));
                    root.removeEntry(result.getIndex());
                    root.addEntry(result.getIndex(),brotherNode.getEntrys().get(0));
                    brotherNode.removeEntry(0);
                    //如果右兄弟有孩子，同样要过户
                    if( ! brotherNode.isLeaf()){
                        //右兄弟最小的孩子拿来给自己当最大的
                        searchChild.insertChild(searchChild.getSize(),brotherNode.childAt(0));
                        brotherNode.removeChild(0);
                    }
                }
                //旋转结束，继续递归删除
                return delete(searchChild,entry);
            }
            //左右兄弟都没钱，把父节点拖一起借钱
            if(result.getIndex() < root.getSize()){
                //存在右兄弟
                Node<K,V> rightBrother=root.childAt(result.getIndex()+1);
                //本节点添加与右节点所在父节点的中间数据，父节点移除该数据，父节点移除该数据右子节点
                searchChild.addEntry(root.getEntrys().get(result.getIndex()));
                root.removeEntry(result.getIndex());
                root.removeChild(result.getIndex()+1);
                for(Entry<K,V> en:rightBrother.getEntrys()){
                    searchChild.addEntry(en);
                }
                if( ! rightBrother.isLeaf()){
                    //右兄弟不是叶子节点，把它的孩子都抢过来
                    for(Node<K,V> child:rightBrother.getChildren()){
                        searchChild.insertChild(child);
                    }
                }
            }else{
                //没有右兄弟，找左兄弟
                Node<K, V> leftBrother = root.childAt(result.getIndex() - 1);
                searchChild.addEntry(0,root.getEntrys().get(result.getIndex()-1));
                root.removeEntry(result.getIndex()-1);
                root.removeChild(result.getIndex()-1);
                //
                for(int i=leftBrother.getSize()-1;i>=0;i--){
                    searchChild.addEntry(0,leftBrother.getEntrys().get(i));
                }
                //如果左节点右孩子，过户
                if( ! leftBrother.isLeaf()){
                    for(int i=leftBrother.getSize();i>=0;i--){
                        searchChild.insertChild(leftBrother.getChildren().get(i));
                    }
                }
            }
            if (root == this.root && root.getSize() ==0) {
                this.root = searchChild;
            }
            return delete(searchChild,entry);
        }
    }


    //节点存储达到上限，它裂开了
    public void splitNode(Node<K,V> root,Node<K,V> child ,int index){
        //自我分裂出的新宝宝
        Node<K,V> newBaby=new Node<>(this.kComparator);
        //原分裂节点若为叶子节点或非叶子节点，新宝宝与之相同
        newBaby.setLeaf(child.isLeaf());
        //将后半部分养大的孩子扔给新节点 中间值下标为t-1
        for(int i=t;i<child.getSize();i++){
            newBaby.addEntry(child.getEntrys().get(i));
        }
        //将原分裂节点的中间元素拿出来准备扔给父母级节点
        Entry<K,V> midEntry=child.getEntrys().get(t-1);
        //将原分裂节点的中间entry及右边全部删除
        for(int i=maxSiez-1;i>=t-1;i--){
            child.getEntrys().remove(i);
        }
        //如果分裂的节点不是叶子节点，则原分裂节点的孩子们要去找新的父母
        if( ! child.isLeaf()){
            //中间节点右边子节点下标为t，将t及之后的子节点塞给新节点
            for(int i=t;i<maxSiez+1;i++){
                newBaby.getChildren().add(child.getChildren().get(i));
            }
            //将塞过的子节点删除，从后向前删否则+1会导致节点漏删
            for(int i=maxSiez;i>=t;i--){
                child.getChildren().remove(i);
            }
        }
        //将中间元素加入原分裂节点的父节点
        addEntry(root,midEntry);
        //将分裂出的新节点挂在原分裂节点的父节点,原分裂节点所在下标为index，所以新增节点下标为index+1
        root.getChildren().add(index+1,newBaby);
    }

    public void outPut(){
        Queue<Node<K,V>> queue = new LinkedList<>();
        queue.offer(root);
        while(!queue.isEmpty()){
            Node<K,V> node = queue.poll();
            for(int i=0;i<node.getSize();i++){
                System.out.println(node.getEntrys().get(i)+",");
            }
            System.out.println();

            if(!node.isLeaf()){
                for(int i=0;i<node.getChildren().size();++i){
                    queue.offer(node.childAt(i));
                }
            }
        }
    }
}
