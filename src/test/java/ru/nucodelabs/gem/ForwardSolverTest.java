package ru.nucodelabs.gem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.files.Sonet;
import ru.nucodelabs.gem.ForwardSolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static java.lang.Math.pow;

public class ForwardSolverTest {

    @Test
    void test() {
        int Nraz = 40;
        int Nlay = 3;
        double Raz[] = new double[Nraz]; /* Distances           */
        double Ro[] = new double[Nlay];  /* Layers resistivites */
        double Th[] = new double[Nlay];  /* Layers thicknesses  */
        double RoK[] = new double[Nraz]; /* Apparent resistivity*/
        int i;
        double sr;
        sr = 4./(double)(Nraz-1);
        System.out.println(sr);

        /* Generating distances */
        for( i=0; i<Nraz; i++ ) Raz[i] = pow(10., i*sr);
        Raz[0] = .2;

        /* Generateing thicknesses & resistivites */
        Th[0] = 1.; Ro[0] = 1000.;
        Ro[1] = 100.; Ro[2] = 1.;
        for(i=1; i<Nlay; i++ ){
            Th[i] = Th[i-1]+2.;
//    Ro[i] = Ro[i-1]/pow(10.,i);
        }
        Th[Nlay-1] = 1.e+10;

        RoK = ForwardSolver.solve(Ro, Th, Nlay, Raz, Nraz);
        System.out.println(RoK);
    }
    @Test
    void test_CHITA_STT() throws FileNotFoundException {
        File file = new File ("src/test/resources/CHITA.STT");
        Sonet.STTFile stt = Sonet.readSTT(file);
        Assertions.assertEquals(stt.getAB_2().get(20), 1.5e+003);
    }
}
