public class DoubleMain {

    public static void main(String[] args) {
        Continuation cont = Stepper.continuation(3);
        while (!cont.isDone()) {
            cont.run();
        }
    }
}
