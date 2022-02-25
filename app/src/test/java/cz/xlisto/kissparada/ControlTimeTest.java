package cz.xlisto.kissparada;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created Xlisto by 11.01.2021
 **/
public class ControlTimeTest {

    @Test
    public void isAllows() {
        ControlTime controlTime = ControlTime.getInstance();
        //3=úterý
        controlTime.setDayStop(3);
        controlTime.setStop(20,00);
        controlTime.setStart(22,00);
        assertArrayEquals(new boolean[]{true},new boolean[]{controlTime.isAllows()});
    }
}