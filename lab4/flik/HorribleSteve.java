package flik;

/** Why does 128 == 128 return false?:
 *  This is due to memory optimisations in Java, one of which is autoboxing. Integer (reference type of int) Values
 *  from -128 to 127 are cached, values outside these range aren't cached. This leads to values outside this range from
 *  not being cached, leading to comparison errors. The best way is to always compare values using .equals, which what
 *  I have implemented in Flik or declare the parameters as the primitive int type. */

public class HorribleSteve {
    public static void main(String [] args) throws Exception {
        int i = 0;
        for (int j = 0; i < 500; ++i, ++j) {
            if (!Flik.isSameNumber(i, j)) {
                throw new Exception(
                        String.format("i:%d not same as j:%d ??", i, j));
            }
        }
        System.out.println("i is " + i);
    }
}
