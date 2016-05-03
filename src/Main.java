public class Main {

    public static void test(){
        System.out.println("here");
    }

    public static void main(String[] args) {
//        test();
        Counter counter = new Counter();
        counter.Increase();
        counter.Increase();
        counter.Increase();
        counter.Decrease();
        System.out.println(counter.Get());
    }
}
