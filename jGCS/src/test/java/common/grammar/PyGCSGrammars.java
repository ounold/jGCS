package common.grammar;

public final class PyGCSGrammars {

    private PyGCSGrammars() {
    }

    public static final String TOMITA1 = "$->AA;A->AA;A->a";
    public static final String TOMITA2 = "$->AB;$->CC;C->AB;C->CC;A->a;B->b";
    public static final String TOMITA3 = "$->AE;$->$A;E->A$;H->$B;$->B$;$->HB;A->a;B->b";
    public static final String TOMITA4 = "$->H$;$->BB;$->$A;$->A$;H->$A;A->a;B->b";
    public static final String TOMITA5 = "F->BJ;$->AA;C->$A;$->$$;C->A$;$->AC;$->CC;$->OJ;$->JJ;C->A$;$->BB;R->$A;O->BR;$->FA;J->AB;A->a;B->b";
    public static final String TOMITA6 = "P->$K;K->BB;$->LP;L->$B;B->AA;$->AB;$->$$;$->BK;$->BK;$->BA;A->a;B->b";
    public static final String TOMITA7 = "$->BB;$->BR;$->$A;R->$B;$->AB;A->a;B->b";
    public static final String AB = "E->$B;$->$$;$->BA;$->BO;O->$A;$->AB;$->AE;B->b;A->a";
    public static final String ANBN = "O->A$;$->OB;$->AB;A->a;B->b";
    public static final String BRA1 = "$->AB;A->A$;$->$$;B->b;A->a";
    public static final String BRA3 = "$->CF;D->$D;$->AD;$->BE;F->$F;K->$E;$->$$;$->BK;D->d;F->f;A->a;C->c;E->e;B->b";
    public static final String PAL2 = "$->BB;H->$B;O->A$;$->OA;$->AA;$->BH;B->b;A->a";
    public static final String RANDOM_RULES = "$->BA;C->BA;C->BB;B->C$;$->AA;$->BB;D->AB;A->$C;B->AA;B->$A";

}
