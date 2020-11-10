package myBTreePlus;

import lombok.Data;

/**
 * @author xumg
 * @create 2020-10-16 9:24
 */
@Data
public class BTreePlus<V> {
    //最大存储数 阶数 m
    private int m;
    //头节点
    private Node<V> head;

    private Node<V> root;
    private int maxSize;
    private int minSize;

    public BTreePlus() {
        this.m = 3;
        this.root = new Node<>();
        this.maxSize = m;
        this.minSize = m%2 == 0 ? m/2 : m/2+1;
    }

    public BTreePlus(int m) {
        this.m = m;
        this.root = new Node<>();
        this.maxSize = m;
        this.minSize = m%2 == 0 ? m/2 : m/2+1;
    }

    //添加新数据
    public void addEntry(Entry<V> entry){
        //先判断头节点是否存在，不存在则为该树第一个数据
        //直接插入头节点情况
        if(head == null){
            root.getKeys().add(entry.getKey());
            root.getEntrys().add(entry);
            head=root;
            return;
        }else if(root.getKeysSize() < maxSize && root.isLeaf()){
            root.getKeys().add(entry.getKey());
            root.getEntrys().add(entry);
            return;
        }
        //判断根节点状态，若已存满则分裂
        if(root.getKeysSize() == maxSize){
            //分裂
            Node<V> newRoot = new Node<>();
            newRoot.setLeaf(false);
            newRoot.addChild(root);
            newRoot.addKey(root.keyAt(root.getKeysSize()-1));
            // 分分分
            splitNode(newRoot,root,0);
            this.root=newRoot;
        }
        addNotFull(this.root,entry);
    }

    private void addNotFull(Node<V> root,Entry<V> entry) {
        Result<V> result = root.search(entry.getKey());
        if(root.isLeaf()){
            if(result.isExit()){
                System.out.println("该项已存在");
                return;
            }
            root.addKey(entry.getKey() , result.getIndex());
            root.addEntry(entry,result.getIndex());
            //如果所添加key小于最大则直接插入
            if(entry.compareTo(root.keyAt(root.getKeysSize() - 1)) >= 0){
                //比最大数大，需更新父节点对应值
                doWhileUpdateBigestkey(root,entry.getKey());
            }
            return;
        }
        //该节点不是叶子节点
        //获取对应子节点对象
        Node<V> searchChild=root.childAt(result.getIndex());
        //判断节点是否需要分裂
        if(searchChild.getKeysSize() == maxSize){
            // 分分分
            splitNode(root,searchChild,result.getIndex());
            //判断所插入对象是否比分裂后的这个对象最大值大,是则声明应当插入下一个刚分裂出的新节点
            if(entry.compareTo(searchChild.keyAt(searchChild.getKeysSize()-1)) > 0){
                searchChild=root.childAt(result.getIndex()+1);
            }
        }
        addNotFull(searchChild,entry);
    }


    //分裂节点
    public void splitNode(Node<V> root,Node<V> child,int index){
        //先分出一个空白宝宝准备作为大孩子
        Node<V> newBoby=new Node<>();
        newBoby.setLeaf(child.isLeaf());
        newBoby.setParentNode(root);
        if(child.isLeaf()){
            //如果是叶子节点,设置叶子链表
            newBoby.setNextNode(child.getNextNode());
            newBoby.setPreviousNode(child);
            child.setNextNode(newBoby);
        }
        //将原分裂节点后半部分孩子全部扔给新节点
        for(int i=minSize;i<maxSize;i++){
            newBoby.addKey(child.keyAt(i));
            if(child.isLeaf()){
                newBoby.addEntry(child.entryAt(i));
            }
        }
        //移除原分裂节点中后半部分的元素
        for(int i=maxSize-1;i>=minSize;i--){
            child.deleteKey(i);
            if(child.isLeaf()){
                child.deleteEntry(i);
            }
        }
        //将分裂后的俩个最大值拿出来准备更新及塞入父节点
        Integer newKey1=child.keyAt(child.getKeysSize()-1);
        Integer newKey2=newBoby.keyAt(newBoby.getKeysSize()-1);
        //如果分裂的节点不是叶子节点，它的孩子也要过户
        if( ! child.isLeaf()){
            for(int i=minSize;i<maxSize;i++){
                newBoby.addChild(child.childAt(i));
                child.childAt(i).setParentNode(newBoby);
            }
            //将过户的孩子扔掉
            for(int i=maxSize-1;i>=minSize;i--){
                child.deleteChild(i);
            }
        }
        //更新父节点中的值及子节点
        root.updateKey(index,newKey1);
        root.addKey(newKey2,index+1);
        root.addChild(newBoby,index+1);
    }


    //删除
    public void deleteEntry(Integer key){
        if(head==null){
            System.out.println("该树为空");
            return;
        }
        Result<V> result = root.deleteSearch(key);
        if(root.keyAt(root.getKeysSize()-1)<key){
            //如果比最大值大，则不存在
            System.out.println("所删除节点不存在");
            return;
        }
        if(root.isLeaf()){
            //根节点为叶子节点
            if( ! result.isExit()){
                System.out.println("所删除节点不存在");
                return;
            }else{
                //存在于根叶节点，直接删除
                root.deleteKey(result.getIndex());
                root.deleteEntry(result.getIndex());
            }
        }else{
            //根节点不为叶子节点
            deleteFromRoot(root,key);
        }
    }
    //循环至叶子节点进行删除
    public void deleteFromRoot(Node<V> root,Integer key){
        Result<V> result = root.deleteSearch(key);
        if(root.isLeaf()){
            if( !result.isExit()){
                System.out.println("所选择数据不存在");
                return;
            }
            if(root.getKeysSize() > minSize) {
                root.getKeys().remove(key);
                root.deleteEntry(result.getIndex());
                if (root.keyAt(root.getKeysSize() - 1) < key) {
                    //所删除节点为最大
                    doWhileUpdateBigestkeyDelete(root.getParentNode(), root.keyAt(root.getKeysSize()-1));
                }
            }else{
                mergeNode(root.getParentNode(), root, root.getParentNode().getKeys().indexOf(root.keyAt(root.getKeysSize()-1)));
            }
            return;
        }
        Node<V> searchChild = root.childAt(result.getIndex());
        if(key > searchChild.keyAt(searchChild.getKeysSize() - 1)){
            System.out.println("所选择数据不存在");
            return;
        }
        if(searchChild.getKeysSize() == minSize){
            mergeNode(root, searchChild, result.getIndex());
        }
        deleteFromRoot(searchChild,key);
    }

    /**
     * 合并节点
     */
    private void mergeNode(Node<V> root, Node<V> node, Integer index){
        //需要借钱或合并--看是否存在左右兄弟
        Node<V> brotherNode = null;
        Integer brotherIndex=null;
        if(index < root.getKeysSize()-1){
            //存在右兄弟 准备开口
            Node<V> rightNode = root.childAt(index + 1);
            if(rightNode.getKeysSize() > minSize){
                //右兄弟有钱，准备抢
                brotherNode=rightNode;
                brotherIndex=index+1;
            }
        }
        if(brotherNode == null){
            //没有右兄弟，去看看有没有弟弟
            if(index > 0){
                //有弟弟 开口
                Node<V> leftNode = root.childAt(index - 1);
                if(leftNode .getKeysSize() > minSize){
                    //抢
                    brotherNode=leftNode;
                    brotherIndex=index-1;
                }
            }
        }
        if(brotherNode == null){
            //兄弟都没钱，找父母
            if(index < root.getKeysSize() - 1){
                //和右兄弟合体
                Node<V> rightNode = root.childAt(index + 1);
                root.deleteChild(index + 1);
                root.deleteKey(index);
                for (int i = 0; i < rightNode.getKeysSize(); i++){
                    node.addKey(rightNode.keyAt(i));
                    if(node.isLeaf()){
                        node.addEntry((rightNode.entryAt(i)));
                        node.setNextNode(null);
                    }else{
                        node.addChild(rightNode.getChildren().get(i));
                    }
                }
                if(this.root.getKeysSize() == 1){
                    this.root = node;
                }
            }else{
                //和左兄弟合体
                Node<V> leftNode = root.childAt(index - 1);
                root.deleteChild(index - 1);
                root.deleteKey(index - 1);
                if(head == leftNode){
                    head = node;
                }
                for (int i = leftNode.getKeysSize() - 1; i >= 0; i--){
                    node.addKey(leftNode.keyAt(i));
                    if(node.isLeaf()){
                        node.addEntry(node.getEntrys().get(i));
                        node.setPreviousNode(leftNode.getPreviousNode());
                        leftNode.getPreviousNode().setNextNode(node);
                    }else{
                        node.addChild(leftNode.getChildren().get(i));
                    }
                }
            }
        }else {
            //抢兄弟
            if(brotherIndex > index){
                //右兄弟
                Integer brotherKey = brotherNode.keyAt(0);
                brotherNode.deleteKey(0);
                node.addKey(brotherKey);
                if(node.isLeaf()){
                    //叶子节点
                    node.addEntry(brotherNode.entryAt(0));
                    brotherNode.deleteEntry(0);
                }else{
                    //非叶子节点
                    node.addChild(brotherNode.childAt(0));
                    brotherNode.deleteChild(0);
                }
                //循环更新最大值
                doWhileUpdateBigestkey(root,brotherKey);
            }else {
                //左兄弟
                Integer brotherKey = brotherNode.keyAt(brotherNode.getKeysSize() - 1);
                brotherNode.deleteKey(brotherNode.getKeysSize() - 1);
                node.addKey(brotherKey,0);
                if(node.isLeaf()){
                    //叶子节点
                    node.addEntry(brotherNode.entryAt(brotherNode.getChildrenSize() - 1));
                    brotherNode.deleteEntry(brotherNode.getChildrenSize() - 1);
                }else {
                    //非叶子节点
                    node.addChild(brotherNode.childAt(brotherNode.getChildrenSize() - 1), 0);
                    brotherNode.deleteChild(brotherNode.getKeysSize() - 1);
                }
                root.updateKey(index - 1, brotherKey);
            }
        }
    }

    /**
     * 循环更新父节点，传入更新的叶节点
     */
    private void doWhileUpdateBigestkey(Node<V> entry,Integer key ){
        while(entry != null && entry.getParentNode() != null) {
            Result<V> parentResult = entry.getParentNode().search(key);
            if(entry.getParentNode().keyAt(entry.getParentNode().getKeysSize() - 1) < key) {
                //如果也是父节点的最大值，继续向上变
                entry.getParentNode().updateKey(parentResult.getIndex(), key);
                entry=entry.getParentNode();
            }else{
                //不是父节点的最大key，只更换对应值
                entry.getParentNode().updateKey(parentResult.getIndex(),key);
                entry=null;
            }
        }
    }
    /**
     * 循环更新父节点，传入更新的叶节点删除
     */
    private void doWhileUpdateBigestkeyDelete(Node<V> parent,Integer key ){
        while(parent != null ) {
            Result<V> parentResult = parent.search(key);
            if(parent.keyAt(parent.getKeysSize() - 2) < key) {
                //如果也是父节点的最大值，继续向上变
                parent.updateKey(parentResult.getIndex(), key);
                parent=parent.getParentNode();
            }else{
                //不是父节点的最大key，只更换对应值
                parent.updateKey(parentResult.getIndex(),key);
                parent=null;
            }
        }
    }
}
