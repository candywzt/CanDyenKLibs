package candyenk.java;

public class Test {
    public static void main(String[] args) throws Exception {
        float posX = (float) (1 / 5);
        float posY = (float) (1 / 6);
        int x = (int) (1200 * posX);//默认X位置:1/5宽
        int y = (int) (2550 * posY);//默认Y位置:1/6高
        System.out.println(x);
        System.out.println(y);
        System.out.println(posX);
        System.out.println(posY);
        System.out.println(1200/5);
    }
}