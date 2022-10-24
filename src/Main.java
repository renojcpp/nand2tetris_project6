public class Main {
    public static void main(String[] args) {
       for (String arg : args) {
           Parser p = new Parser(arg);
           p.write();
       }
    }
}