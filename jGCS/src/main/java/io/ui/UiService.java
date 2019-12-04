package io.ui;

public class UiService {

    private static UiService instance;

    private UiService() {
    }

    public static UiService getInstance() {
        if (instance == null)
            instance = new UiService();
        return instance;
    }

    public void info(Object object){
        System.out.println(object);
    }

    public void info(String format, Object... args){
        info(String.format(format, args));
    }

    public void error(Object object){
        System.out.println(String.format("ERROR! %s", object));
    }

    public void error(String format, Object... args){
        error(String.format(format, args));
    }

    public void fatal(Object object){
        System.out.println(String.format("FATAL! %s", object));
    }

    public void fatal(String format, Object... args){
        error(String.format(format, args));
    }


    public void state(Object object){
        System.out.print("\r");
        System.out.print(object);
    }

    public void state(String format, Object... args){
        state(String.format(format, args));
    }

    public void closeState(){
        System.out.println();
    }

}
