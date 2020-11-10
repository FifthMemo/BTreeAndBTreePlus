package myBTree;


/**
 * @author xumg
 * @create 2020-10-15 9:30
 */
public class Menu {
    public static void main(String[] args) {
        BTree<Integer,Integer> tree=new BTree<>();
        for(int i=0;i<15;i++){
//            int ran=new Random().nextInt(10000);
            Entry<Integer,Integer> en=new Entry<>(i,i);
            tree.addNode(en);
        }
        System.out.println("----------------------");
        tree.outPut();
        System.out.println("----------------------");

        tree.delete(5);
        System.out.println("----------------------");
        tree.delete(7);
        System.out.println("----------------------");
        tree.delete(6);
    }
}
