package services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import service.Calculadora;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {

        when(calcMock.somar(1, 2)).thenReturn(8);
//        Mockito.when(calcSpy.somar(1, 2)).thenReturn(8);
        Mockito.doReturn(5).when(calcSpy).somar(1, 2);
        Mockito.doNothing().when(calcSpy).imprime();

        System.out.println("Mock: " + calcMock.somar(1, 2));
        System.out.println("Spy: " + calcSpy.somar(1, 2));

        System.out.println("Mock");
        calcMock.imprime();
        System.out.println("Spy");
        calcSpy.imprime();
    }

    @Test
    public void teste() {
        Calculadora calc = mock(Calculadora.class);

        ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
        when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);

        assertEquals(5, calc.somar(1, 8));
//        System.out.println(argCapt.getAllValues());
    }
}
