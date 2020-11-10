package myBTreePlus;

/**
 * @author xumg
 * @create 2020-10-19 10:27
 */
public class Menu {

    public static void main(String[] args) {
        BTreePlus<Integer> btPlus=new BTreePlus<>();
        Entry<Integer> en;
        for (int i=0;i<10;i++){
            en=new Entry<>(i,i);
            btPlus.addEntry(en);
        }

        System.out.println();

        btPlus.deleteEntry(7);
        System.out.println();
        btPlus.deleteEntry(3);
        System.out.println();
        btPlus.deleteEntry(9);
        System.out.println();
        btPlus.deleteEntry(6);
        System.out.println();
        btPlus.deleteEntry(5);

        System.out.println();
    }
}
