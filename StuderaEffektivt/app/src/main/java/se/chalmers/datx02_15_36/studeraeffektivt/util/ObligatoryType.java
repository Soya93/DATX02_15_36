package se.chalmers.datx02_15_36.studeraeffektivt.util;

/**
 * Created by haxmaj0 on 2015-07-07.
 */
public enum ObligatoryType {
    HANDIN ("Inlämningsuppgift"),
    LAB ("Labbuppgift"),
    MINIEXAM ("Dugga"),
    EXAM ("Tentamen");

    private final String name;

    private ObligatoryType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    public String toString(){
        return name;
    }
}
