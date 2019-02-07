package cyk;

import java.util.function.BiConsumer;

public class CykNavigator {

    public static void forEach(int range, BiConsumer<Integer, Integer> callback){
        for(int i = 0; i < range; i++){
            for (int j = 0; j < range - i; j++){
                callback.accept(i, j);
            }
        }
    }

    public static void forEachReversed(int range, BiConsumer<Integer, Integer> callback){
        for(int i = range - 1; i > -1; i--){
            for (int j = range - i - 1; j > -1; j--){
                callback.accept(i, j);
            }
        }
    }

}
